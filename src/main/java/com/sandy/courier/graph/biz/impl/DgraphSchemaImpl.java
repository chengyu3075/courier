package com.sandy.courier.graph.biz.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sandy.courier.common.exception.GraphDbException;
import com.sandy.courier.graph.bean.GraphNodeTypeBean;
import com.sandy.courier.graph.biz.AbstractDgraphBiz;
import com.sandy.courier.graph.biz.IGraphSchemaBiz;

/**
 * @Description: @createTime：2020/5/20 11:47
 * @author: chengyu3
 **/
@Service
public class DgraphSchemaImpl extends AbstractDgraphBiz implements IGraphSchemaBiz {

    @Override
    public void deleteType(String type) throws GraphDbException {

    }

    @Override
    public List<GraphNodeTypeBean> findType(String type) throws GraphDbException {
        return null;
    }

    @Override
    public void addProperty(String propertyInfo) throws GraphDbException {
        setSchema(propertyInfo);
    }

    @Override
    public void dropAllData(boolean includeSchema) throws GraphDbException {
        dropAll(includeSchema);
    }

}
