package com.sandy.courier.graph.bean;

import java.util.List;

/**
 * @Description: @createTime：2020/5/20 11:17
 * @author: chengyu3
 **/
public class GraphEntityBean {

    /**
     * 类型名称
     */
    private String typeName;
    /**
     * 属性
     */
    private List<GraphPropBean> props;

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public List<GraphPropBean> getProps() {
        return props;
    }

    public void setProps(List<GraphPropBean> props) {
        this.props = props;
    }
}
