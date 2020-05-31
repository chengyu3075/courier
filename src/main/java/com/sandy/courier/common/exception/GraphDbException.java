package com.sandy.courier.common.exception;

/**
 * @Description:图数据库异常类 @createTime：2020/5/20 10:58
 * @author: chengyu3
 **/
public class GraphDbException extends RuntimeException {

    public GraphDbException(String message) {
        super(message);
    }

    public GraphDbException(Throwable throwable) {
        super(throwable);
    }

}
