package com.sandy.courier.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Preconditions;

/**
 * @Description:实现任务可指定线程 @createTime：2020/5/24 9:18
 * @author: chengyu3
 **/
public class ShardThreadPoolExecutor extends AbstractExecutorService {

    private final ReentrantLock mainLock = new ReentrantLock();
    /**
     * Wait condition to support awaitTermination
     */
    private final Condition termination = mainLock.newCondition();

    private final Condition shutdown = mainLock.newCondition();
    /**
     * cpu cores
     */
    private Integer poolSize = Runtime.getRuntime().availableProcessors();
    /**
     * thread pool name
     */
    private String poolName = "shard-thread-pool-executor";
    /**
     * task to be execute
     */
    private List<BlockingQueue<Command>> commanders;
    /**
     * blocking queue
     */
    private Class<? extends BlockingQueue> clazz = ArrayBlockingQueue.class;
    /**
     * worker thread
     */
    private Map<Integer, Work> workers = new ConcurrentHashMap<>();

    /**
     * work queue map
     */
    private Map<Work, BlockingQueue<Command>> workQueueMap = new ConcurrentHashMap<>();
    /**
     * close thread pool
     */
    private boolean shutDown;

    /**
     * terminate thread pool
     */
    private boolean shutDownNow;
    /**
     * queue size
     */
    private Integer queueSize = 200;

    public ShardThreadPoolExecutor(int poolSize, String poolName, Class<? extends BlockingQueue> clazz,
            Integer queueSize) {
        Preconditions.checkArgument(poolSize > 0);
        Preconditions.checkArgument(StringUtils.isNotEmpty(poolName));
        this.poolSize = poolSize;
        this.poolName = poolName;
        this.clazz = clazz;
        initCommanders();
    }

    public ShardThreadPoolExecutor() {
        initCommanders();
    }

    /**
     * init blocking queue
     *
     * @param
     * @return void @createTime：2020/5/24 9:54
     * @author: chengyu3
     */
    private void initCommanders() {
        commanders = new ArrayList<>(poolSize);
        for (int i = 0; i < poolSize; i++) {
            try {
                commanders.add((BlockingQueue<Command>) clazz.getConstructor(Integer.TYPE).newInstance(queueSize));
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
    }

    @Override
    public void shutdown() {
        shutDown = true;
        for (Work work : workers.values()) {
            mainLock.lock();
            while (!tryTerminateWork(work)) {
                // can do better
                LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(50));
            }
            mainLock.unlock();
        }
    }

    private boolean tryTerminateWork(Work work) {
        if (work.state == 1 && workQueueMap.get(work).isEmpty()) {
            work.interrupt();
            return true;
        }
        return false;
    }

    @Override
    public List<Runnable> shutdownNow() {
        shutDownNow = true;
        shutDown = true;
        for (Work work : workers.values()) {
            work.thread.interrupt();
        }
        return new ArrayList<>(workers.values());
    }

    @Override
    public boolean isShutdown() {
        return shutDown;
    }

    @Override
    public boolean isTerminated() {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        long nanos = unit.toNanos(timeout);
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            for (;;) {
                if (workers.size() == 0)
                    return true;
                if (nanos <= 0)
                    return false;
                nanos = termination.awaitNanos(nanos);
            }
        } finally {
            mainLock.unlock();
        }
    }

    @Override
    public void execute(Runnable command) {
        if (!(command instanceof Command)) {
            throw new IllegalArgumentException("param must be instance of ShardThreadPoolExecutor Command");
        }
        Command cmd = ((Command) command);
        Object shardObject = cmd.getShardKey();
        if (shardObject == null) {
            throw new IllegalStateException("shardObject is null");
        }

        if (poolSize > workers.size() && !hasWorker(cmd)) {
            // execute new work
            newWork(cmd);
        } else {
            // add to queue
            try {
                getCommandQueue(cmd).put(cmd);
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    private boolean hasWorker(Command command) {
        return workers.containsKey(getBlockQueueIndex(command));
    }

    private BlockingQueue<Command> getCommandQueue(Command command) {
        return commanders.get(getBlockQueueIndex(command));
    }

    private Integer getBlockQueueIndex(Command command) {
        int hashCode = command.getShardKey().hashCode();
        // can do better
        return Math.abs(hashCode % poolSize);
    }

    private void newWork(Command command) {
        Work work = new Work(command);
        work.thread.start();
        workQueueMap.put(work, getCommandQueue(command));
        workers.put(getBlockQueueIndex(command), work);
    }

    private Command getCommand(BlockingQueue<Command> commandDeque) {
        if (shutDownNow) {
            return null;
        }
        try {
            return commandDeque.take();
        } catch (InterruptedException e) {
            return null;
        }
    }

    private void removeWork(Work work) {
        if (isShutdown()) {
            Integer key = null;
            for (Map.Entry<Integer, Work> workEntry : workers.entrySet()) {
                if (work == workEntry.getValue()) {
                    key = workEntry.getKey();
                }
            }
            workers.remove(key);
        }
    }

    public interface Command extends Runnable {
        String getShardKey();
    }

    private class Work extends Thread {
        private Command command;

        private Thread thread;

        private int state = 0;

        public Work(Command command) {
            thread = new Thread(this::run, poolName + "-" + workers.size());
            this.command = command;
        }

        @Override
        public void run() {
            execute(command);
        }

        private void execute(Command command) {
            BlockingQueue<Command> commandDeque = getCommandQueue(command);
            Command cmd = command;
            while (cmd != null || (cmd = getCommand(commandDeque)) != null) {
                try {
                    state = 0;
                    cmd.run();
                    state = 1;
                } finally {
                    cmd = null;
                }
            }
            removeWork(this);
        }
    }
}
