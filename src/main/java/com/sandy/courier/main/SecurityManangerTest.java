package com.sandy.courier.main;

/**
 * @Description: @createTime：2020/5/31 13:39
 * @author: chengyu3
 **/
public class SecurityManangerTest {

    public static void main(String[] args) {
        MySM sm = new MySM();
        System.out.println(System.getSecurityManager());
        System.setSecurityManager(sm);// 注释掉测一下
        System.exit(0);
    }

    static class MySM extends SecurityManager {
        public void checkExit(int status) {
            throw new SecurityException("no exit");
        }

    }

}
