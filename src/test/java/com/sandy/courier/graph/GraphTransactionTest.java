package com.sandy.courier.graph;

import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.sandy.CourierApplication;
import com.sandy.courier.common.util.KgDgraphClient;

import io.dgraph.DgraphClient;
import io.dgraph.DgraphProto;
import io.dgraph.Transaction;

/**
 * @Description: @createTime：2020/5/26 15:42
 * @author: chengyu3
 **/
public class GraphTransactionTest extends CourierApplication {

    /**
     * 读未提交测试
     * 
     * @param
     * @return void @createTime：2020/5/26 15:44
     * @author: chengyu3
     */
    public void testRu() throws Exception {

    }

    /**
     * 读已提交测试
     * 
     * @param
     * @return void @createTime：2020/5/26 15:43
     * @author: chengyu3
     */
    @Test
    public void testRc() throws Exception {

    }

    @Test
    public void testRr() throws Exception {
        DgraphClient client = KgDgraphClient.getClient();

        // 2.查询一条数据
        String query = "query all($a: string){\n" + "  all(func: eq(name, $a)) {\n" + "    uid\n name\n age\n" + "  }\n"
                + "}\n";
        Map<String, String> vars = Collections.singletonMap("$a", "Michael");
        Transaction transaction = client.newReadOnlyTransaction();
        transaction.setBestEffort(true);
        DgraphProto.Response response = transaction.queryWithVars(query, vars);
        System.out.println("t1:" + response.getJson().toStringUtf8());
        // 2.1异步更新一条数据,采用页面更新

        // 2.2执行相同的查询
        DgraphProto.Response response1 = transaction.queryWithVars(query, vars);
        System.out.println("t1:" + response1.getJson().toStringUtf8());
        // 2.3提交查询事务

        Transaction transaction2 = client.newReadOnlyTransaction();
        DgraphProto.Response response2 = transaction2.queryWithVars(query, vars);
        System.out.println("t2:" + response2.getJson().toStringUtf8());
    }
}
