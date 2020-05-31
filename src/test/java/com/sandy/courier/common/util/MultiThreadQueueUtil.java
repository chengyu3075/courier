package com.sandy.courier.common.util;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;

import io.netty.util.concurrent.DefaultThreadFactory;

/**
 * @Description:异步生产消费 @createTime：2019/11/18 9:31
 * @author: chengyu
 **/
public class MultiThreadQueueUtil<E> {
    /**
     * 队列默认大小
     */
    private static final Integer DEFAULT_QUEUE_SIZE = 100;
    /**
     * 打印日志的间隔
     */
    private static final Integer LOG_SEPER = 200;
    /**
     * 生产者计数器
     */
    private final AtomicInteger INPUT_COUNTER = new AtomicInteger();
    /**
     * 消费者计数器
     */
    private final AtomicInteger CONSUME_COUNTER = new AtomicInteger();

    /**
     * 消费者逻辑
     */
    IConsumerBiz<E> consumerBiz;
    /**
     * 消费者线程池
     */
    private ThreadPoolExecutor consumeExecutor;

    /**
     * 消费者启动线程池
     */
    private ThreadPoolExecutor consumeStartExecutor;
    /**
     * 消费者缓冲队列
     */
    private BlockingQueue<E> blockingQueue;

    /**
     * 是否已停止
     */
    private boolean isStopping = false;

    public MultiThreadQueueUtil(IConsumerBiz<E> consumerBiz, Integer queueSize, Integer consumeThreadNum,
            String consumeThreadPoolName) {
        if (queueSize <= 0) {
            throw new IllegalArgumentException("error queueSize:" + queueSize);
        }
        blockingQueue = new ArrayBlockingQueue<E>(queueSize);
        this.consumerBiz = consumerBiz;
        initThreadPool(consumeThreadNum, consumeThreadPoolName);
    }

    public MultiThreadQueueUtil(IConsumerBiz<E> consumerBiz) {
        blockingQueue = new ArrayBlockingQueue<E>(DEFAULT_QUEUE_SIZE);
        this.consumerBiz = consumerBiz;
        initThreadPool();
    }

    /**
     * 初始化默认线程池
     * 
     * @param
     * @return void @createTime：2019/11/18 12:02
     * @author: chengyu
     */
    private void initThreadPool() {
        // 线程池默认大小为CPU核数
        int processorNum = Runtime.getRuntime().availableProcessors();
        initThreadPool(processorNum, consumerBiz.getClass().getSimpleName() + "-consume-thread-pool");
    }

    /**
     * 初始化自定义线程池
     * 
     * @param consumeThreadNum
     * @param consumeThreadPoolName
     * @return void @createTime：2019/11/18 12:02
     * @author: chengyu
     */
    private void initThreadPool(int consumeThreadNum, String consumeThreadPoolName) {
        if (consumeThreadNum <= 0 || StringUtils.isBlank(consumeThreadPoolName)) {
            throw new IllegalArgumentException(
                    String.format("error params,consumeThreadNum:%d,consumeThreadPoolName:%s", consumeThreadNum,
                            consumeThreadPoolName));
        }
        System.out.println(consumerBiz.getClass().getSimpleName() + " consume thread num:" + consumeThreadNum);
        consumeExecutor = new ThreadPoolExecutor(1, consumeThreadNum, 10L, TimeUnit.SECONDS, new SynchronousQueue<>(),
                new DefaultThreadFactory(consumeThreadPoolName));
        consumeExecutor.setRejectedExecutionHandler(new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                if (!executor.isShutdown()) {
                    try {
                        executor.getQueue().put(r);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        });
        consume();
    }

    /**
     * 数据转换
     * 
     * @param e
     * @return void @createTime：2019/11/22 10:22
     * @author: chengyu
     */
    public void transfer(E e) {
        if (isStopping) {
            throw new RuntimeException("task is stopping...");
        }
        // 队列中不存放空元素
        if (e == null) {
            return;
        }
        try {
            blockingQueue.put(e);
            if ((e instanceof List ? INPUT_COUNTER.addAndGet(((List) e).size()) : INPUT_COUNTER.incrementAndGet())
                    % LOG_SEPER == 0) {
                System.out.println(consumerBiz.getClass().getSimpleName() + " input size:" + INPUT_COUNTER.get()
                        + ",queue info,queueSize:" + blockingQueue.size() + ",queue remaining:"
                        + blockingQueue.remainingCapacity());
            }
        } catch (InterruptedException ex) {
            blockingQueue.clear();
            stop();
        }
    }

    /**
     * 停止
     * 
     * @param
     * @return void @createTime：2019/11/18 12:03
     * @author: chengyu
     */
    public void stop() {
        System.out.println(consumerBiz.getClass().getSimpleName() + " begin stop consume...");
        isStopping = true;
        while (blockingQueue.size() > 0) {
            System.out.println(consumerBiz.getClass().getSimpleName() + " stop consume blockingDeque size:"
                    + blockingQueue.size());
            Long waitTime = 1000L;
            try {
                Thread.sleep(waitTime);
            } catch (InterruptedException e) {
                break;
            }
        }
        // 使阻塞打断
        consumeStartExecutor.shutdownNow();
        consumeExecutor.shutdown();
        System.out.println(consumerBiz.getClass().getSimpleName() + ",task finished,input size:" + INPUT_COUNTER.get()
                + ",consume size:" + CONSUME_COUNTER.get());
    }

    /**
     * 多线程消费数据，具体方法由调用者实现
     * 
     * @param
     * @return void @createTime：2019/11/18 12:07
     * @author: chengyu
     */
    private void consume() {
        consumeStartExecutor = new ThreadPoolExecutor(1, 1, 100, TimeUnit.SECONDS, new SynchronousQueue<>(),
                new DefaultThreadFactory(consumerBiz.getClass().getSimpleName() + "-start-consume-thread-pool"));
        consumeStartExecutor.execute(() -> {
            while (!consumeStartExecutor.isTerminating()) {
                try {
                    // 用take防止造成空循环，使cpu使用率升高
                    E e = blockingQueue.take();
                    consumeExecutor.execute(new Consume(e));
                    if (e instanceof List) {
                        CONSUME_COUNTER.addAndGet(((List) e).size());
                    } else {
                        CONSUME_COUNTER.incrementAndGet();
                    }
                } catch (InterruptedException ex) {
                    break;
                }
            }
        });
    }

    public interface IConsumerBiz<E> {
        void consume(E e);
    }

    /**
     * 消息处理类
     */
    private class Consume implements Runnable {
        private E e;

        public Consume(E e) {
            this.e = e;
        }

        @Override
        public void run() {
            consumerBiz.consume(e);
        }
    }

}
