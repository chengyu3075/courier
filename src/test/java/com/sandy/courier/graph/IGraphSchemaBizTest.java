package com.sandy.courier.graph;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sandy.courier.CourierApplicationTest;
import com.sandy.courier.common.util.KgDgraphClient;
import com.sandy.courier.graph.biz.IGraphSchemaBiz;

/**
 * @Description: @createTime：2020/5/20 15:18
 * @author: chengyu3
 **/
public class IGraphSchemaBizTest extends CourierApplicationTest {

    @Autowired
    private IGraphSchemaBiz graphSchemaBiz;

    @Test
    public void testAddProperty() throws Exception {
        System.setProperty(KgDgraphClient.DGRAPH_ALPHA_HOST, "localhost:9080");
        final String schema = "name: string @index(rune) .";
        graphSchemaBiz.addProperty(schema);
    }

    @Test
    public void testDropAll() throws Exception {
        // 10.110.13.28:9080,10.110.14.239:9080,10.110.13.56:9080
        System.setProperty(KgDgraphClient.DGRAPH_ALPHA_HOST, "localhost:9080");
        graphSchemaBiz.dropAllData(false);
    }
}
