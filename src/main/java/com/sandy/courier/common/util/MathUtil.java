package com.sandy.courier.common.util;

import java.util.Random;

/**
 * @Description: @createTime：2020/5/28 11:41
 * @author: chengyu3
 **/
public class MathUtil {

    private static Random random = new Random();

    public static int randomInt(int max) {
        return random.nextInt(max);
    }
}
