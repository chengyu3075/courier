package com.sandy.courier.graph.bean;

/**
 * @Description:图数据库属性 @createTime：2020/5/20 11:10
 * @author: chengyu3
 **/
public class GraphPropBean {
    /**
     * 属性名称
     */
    private String name;
    /**
     * 属性值
     */
    private Object value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

}
