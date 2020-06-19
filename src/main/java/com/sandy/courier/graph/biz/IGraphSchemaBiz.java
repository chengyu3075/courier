package com.sandy.courier.graph.biz;

import java.util.List;

import com.sandy.courier.common.exception.GraphDbException;
import com.sandy.courier.graph.bean.GraphNodeTypeBean;

/**
 * @Description:图schema管理 @createTime：2020/5/20 10:55
 * @author: chengyu3
 **/
public interface IGraphSchemaBiz {

    /**
     * 删除类型
     * 
     * @param type
     * @return void @createTime：2020/5/20 11:11
     * @author: chengyu3
     */
    void deleteType(String type) throws GraphDbException;

    /**
     * 查询类型
     * 
     * @param type
     * @return java.util.List<com.liepin.courier.graph.bean.GraphTypeBean> @createTime：2020/5/20
     *         11:12
     * @author: chengyu3
     */
    List<GraphNodeTypeBean> findType(String type) throws GraphDbException;

    /**
     * 添加属性
     * 
     * @param propertyInfo
     * @return void @createTime：2020/5/20 15:11
     * @author: chengyu3
     */
    void addProperty(String propertyInfo) throws GraphDbException;

    /**
     * 清除库中所有数据
     * 
     * @param
     * @return void @createTime：2020/5/20 15:51
     * @author: chengyu3
     */
    void dropAllData(boolean includeSchema) throws GraphDbException;
}
