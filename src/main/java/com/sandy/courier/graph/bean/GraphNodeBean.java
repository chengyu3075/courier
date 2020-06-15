package com.sandy.courier.graph.bean;

import java.util.Map;

/**
 * @Description: @createTime：2020/5/20 11:17
 * @author: chengyu3
 **/
public class GraphNodeBean {

    /**
     * 类型名称
     */
    private String typeName;
    /**
     * 唯一键名称
     */
    private String uniqueKeyName;
    /**
     * 属性
     */
    private Map<String, Object> props;

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getUniqueKeyName() {
        return uniqueKeyName;
    }

    public void setUniqueKeyName(String uniqueKeyName) {
        this.uniqueKeyName = uniqueKeyName;
    }

    public Map<String, Object> getProps() {
        return props;
    }

    public void setProps(Map<String, Object> props) {
        this.props = props;
    }

}
