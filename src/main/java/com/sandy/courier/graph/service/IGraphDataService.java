package com.sandy.courier.graph.service;

import java.util.List;

import com.sandy.courier.graph.bean.GraphEntityBean;
import com.sandy.courier.graph.bean.GraphPropBean;
import com.sandy.courier.graph.bean.GraphRelationBean;

/**
 * 
 * @Description: @createTime：2020/6/11 14:20
 * @author: chengyu3
 **/
public interface IGraphDataService {

    /**
     * 保存实体
     * 
     * @param typeName
     * @param props
     * @return java.lang.String @createTime：2020/6/19 17:37
     * @author: chengyu3
     */
    String saveEntity(String typeName, List<GraphPropBean> props);

    void batchSaveEntity(List<GraphEntityBean> entityBeans);

    /**
     * 保存关系
     * 
     * @param srcId
     * @param dstId
     * @param relations
     * @return void @createTime：2020/6/19 17:37
     * @author: chengyu3
     */
    void saveRelation(String srcId, String dstId, List<GraphRelationBean> relations);

    /**
     * 保存关系
     * 
     * @param srcNode
     * @param dstNode
     * @param relations
     * @return void @createTime：2020/6/19 17:36
     * @author: chengyu3
     */
    void saveRelation(GraphEntityBean srcNode, GraphEntityBean dstNode, List<GraphRelationBean> relations);

    /**
     * 根据实体id删除实体
     *
     * @return void @createTime：2020/6/19 17:35
     * @author: chengyu3
     */
    void delEntity(String id);

    /**
     * 删除实体属性
     * 
     * @param id
     * @param propertyName
     * @return void @createTime：2020/6/19 17:35
     * @author: chengyu3
     */
    void delProperty(String id, String propertyName);

    /**
     * 删除两点之间的关系
     * 
     * @param srcId
     * @param dstId
     * @param relName
     * @return void @createTime：2020/6/19 17:34
     * @author: chengyu3
     */
    void delRelation(String srcId, String dstId, String relName);

    /**
     * 根据id获取顶点信息
     *
     * @return com.sandy.courier.graph.bean.GraphNodeBean @createTime：2020/6/19
     *         17:32
     * @author: chengyu3
     */
    GraphEntityBean getEntityById(String id);

    /**
     * 根据属性获取实体
     * 
     * @param typeName
     * @param propName
     * @param propValue
     * @return java.util.List<com.sandy.courier.graph.bean.GraphNodeBean> @createTime：2020/6/19
     *         17:31
     * @author: chengyu3
     */
    List<GraphEntityBean> getEntityByProp(String typeName, String propName, Object propValue);

    /**
     * 最短路径
     * 
     * @param srcId
     * @param dstId
     * @param relName
     * @return java.util.List<com.sandy.courier.graph.bean.GraphNodeBean> @createTime：2020/6/19
     *         17:31
     * @author: chengyu3
     */
    List<GraphEntityBean> shortestPath(String srcId, String dstId, String relName);

    /**
     * 关系查询
     * 
     * @param typeName
     * @param propName
     * @param relName
     * @return java.util.List<com.sandy.courier.graph.bean.GraphNodeBean> @createTime：2020/6/19
     *         17:30
     * @author: chengyu3
     */
    List<GraphEntityBean> queryRelations(String typeName, String propName, Object propValue, String relName);
}
