package com.sandy.courier.common.util;

import org.junit.jupiter.api.Test;

import com.sandy.courier.CourierApplicationTest;

/**
 * @Description: @createTime：2020/5/24 11:35
 * @author: chengyu3
 **/
public class IShardThreadPoolTest extends CourierApplicationTest {

    @Test
    public void testShardPool() {
        ShardThreadPoolExecutor shardThreadPoolExecutor = new ShardThreadPoolExecutor();
        for (int i = 0; i < 10; i++) {
            String string = "a";
            if (i % 2 == 0) {
                string = "b";
            }
            shardThreadPoolExecutor.execute(new StringCommand(string));
        }
    }

    class StringCommand implements ShardThreadPoolExecutor.Command {

        private String s;

        public StringCommand(String s) {
            this.s = s;
        }

        @Override
        public String getShardKey() {
            return s;
        }

        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName() + ":" + s);
        }
    }
}
