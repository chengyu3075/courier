package com.sandy.courier.graph.biz;

import java.util.List;

import com.sandy.courier.common.exception.GraphDbException;

/**
 * @Description:图数据库数据操作类 @createTime：2020/5/20 11:13
 * @author: chengyu3
 **/
public interface IGraphDataBiz {

    /**
     * 保存rdf信息
     * 
     * @param rdf
     * @return void @createTime：2020/5/20 11:46
     * @author: chengyu3
     */
    void saveWithRdf(String rdf) throws GraphDbException;

    /**
     * 批量保存rdf
     * 
     * @param rdfs
     * @return void @createTime：2020/5/22 18:44
     * @author: chengyu3
     */
    void batchSaveWithRdf(List<String> rdfs) throws GraphDbException;

    /**
     * 保存triple
     * 
     * @param triple
     * @param typeName
     * @param relationName
     * @return void @createTime：2020/5/21 13:26
     * @author: chengyu3
     */
    void saveTripleWithTab(String triple, String typeName, String relationName) throws GraphDbException;

    /**
     * 批量保存
     * 
     * @param triples
     * @param typeName
     * @param relationName
     * @return void @createTime：2020/5/25 16:25
     * @author: chengyu3
     */
    void batchSaveTripleWithTab(List<String> triples, String typeName, String relationName) throws GraphDbException;
}
