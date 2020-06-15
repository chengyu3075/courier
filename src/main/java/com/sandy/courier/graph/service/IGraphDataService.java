package com.sandy.courier.graph.service;

import java.util.List;
import java.util.Map;

import com.sandy.courier.graph.bean.GraphNodeBean;

/**
 * @Description: @createTime：2020/6/11 14:20
 * @author: chengyu3
 **/

public interface IGraphDataService {

    String saveEntity(String categoryName, Map<String, Object> value);

    void batchSaveEntities(String categoryName, List<Map<String, Object>> values);

    void saveRelation(String srcId, String dstId, Map<String, Map<String, Object>> relations);

    void mergePropsAndRel(GraphNodeBean srcNode, GraphNodeBean dstNode, Map<String, Map<String, Object>> relations);

    void delEntity(String entityId);

    void delProperty(String entityId, String propertyName);

    void delRelation(String srcId, String dstId, String relName);

    Map<String, Object> getEntityById(String entityId);

    List<Map<String, Object>> getEntityByProp(String type, String propName, Object propValue);

}
