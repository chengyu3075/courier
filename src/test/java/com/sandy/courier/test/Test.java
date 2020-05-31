package com.sandy.courier.test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @Description: @createTime：2020/5/25 15:51
 * @author: chengyu3
 **/
public class Test {

    public static void main(String[] args) {
        BlockingQueue<String> queue = new ArrayBlockingQueue<String>(100);
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("1");
                try {
                    queue.take();
                } catch (InterruptedException e) {
                    System.out.println("interrupt...");
                    e.printStackTrace();
                }
            }
        });
        t1.start();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("begin interrupt");
        t1.interrupt();
    }
}
