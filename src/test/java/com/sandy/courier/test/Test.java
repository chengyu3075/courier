package com.sandy.courier.test;

import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @Description: @createTime：2020/5/25 15:51
 * @author: chengyu3
 **/
public class Test {
    /**
     * 数据未变化标识-带引号
     */
    public static final String NOT_CHANGE_TAG = "'kg_no_change'";
    /**
     * 数据未变化标识
     */
    public static final String KG_NO_CHANGE_TAG = NOT_CHANGE_TAG.replace("'", "");

    public static void main(String[] args) {
        Integer[] array = new Integer[] { 4, 2, 5, 3, 8, 6, 8, 2, 9, 11, 45, 23, 1, 56, 34, 22 };
        System.out.println(Arrays.asList(bubbleSort(array)));

        System.out.println(NOT_CHANGE_TAG + "," + KG_NO_CHANGE_TAG);
    }

    public static Integer[] bubbleSort(Integer[] array) {
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array.length; j++) {
                if (array[i] < array[j]) {
                    int temp = array[i];
                    array[i] = array[j];
                    array[j] = temp;
                }
            }
        }
        return array;
    }

    public static void queueInterruptTest() {
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
