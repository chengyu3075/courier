package com.sandy.courier.graph;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.protobuf.ByteString;
import com.sandy.courier.CourierApplicationTest;
import com.sandy.courier.common.util.KgDgraphClient;
import com.sandy.courier.graph.biz.IGraphSchemaBiz;

import io.dgraph.DgraphProto;

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
        final String schema = "Animal.name: string @id @index(rune) .";
        graphSchemaBiz.addProperty(schema);
    }

    @Test
    public void testDropAll() throws Exception {
        // 10.110.13.28:9080,10.110.14.239:9080,10.110.13.56:9080
        System.setProperty(KgDgraphClient.DGRAPH_ALPHA_HOST, "localhost:9080");
        graphSchemaBiz.dropAllData(true);
    }

    @Test
    public void testDropType() throws Exception {
        System.setProperty(KgDgraphClient.DGRAPH_ALPHA_HOST, "localhost:9080");
        KgDgraphClient.getClient().alter(DgraphProto.Operation.newBuilder().setDropOp(DgraphProto.Operation.DropOp.TYPE)
                .setDropValue("Customer").build());
    }

    @Test
    public void testSetDropData() throws Exception {
        System.setProperty(KgDgraphClient.DGRAPH_ALPHA_HOST, "localhost:9080");
        KgDgraphClient.getClient().alter(
                DgraphProto.Operation.newBuilder().setDropAttrBytes(ByteString.copyFromUtf8("Product.name")).build());
    }
}
