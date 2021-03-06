package com.sandy.courier.graph.bean;

import java.util.List;

/**
 * @Description: @createTime：2020/6/18 10:40
 * @author: chengyu3
 **/
public class GraphRelationBean {
    /**
     * 名称
     */
    public String name;
    /**
     * 属性
     */
    public List<GraphPropBean> props;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<GraphPropBean> getProps() {
        return props;
    }

    public void setProps(List<GraphPropBean> props) {
        this.props = props;
    }

}
