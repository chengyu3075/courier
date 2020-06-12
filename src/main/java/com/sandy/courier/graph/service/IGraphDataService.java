package com.sandy.courier.graph.service;

import java.util.List;
import java.util.Map;

/**
 * @Description: @createTime：2020/6/11 14:20
 * @author: chengyu3
 **/

public interface IGraphDataService {

    String saveEntity(String categoryName, Map<String, Object> values);

    Map<String, String> batchSaveEntities();

    void createRelation(String srcId, String dstId, String relName);

    void delEntity(String entityId);

    void delProperty(String entityId, String propertyName);

    void delRelation(String srcId, String dstId, String relName);

    Map<String, Object> getEntityById(String entityId);

    List<Map<String, Object>> getEntityByProp(String type, String propName, Object propValue);

}
