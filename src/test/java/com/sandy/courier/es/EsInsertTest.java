package com.sandy.courier.es;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpHost;
import org.assertj.core.util.Maps;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.Test;

import com.sandy.courier.CourierApplicationTest;

/**
 * @Description: @createTime：2020/6/10 10:44
 * @author: chengyu3
 **/
public class EsInsertTest extends CourierApplicationTest {

    public static final RestHighLevelClient CLIENT;

    static {
        CLIENT = new RestHighLevelClient(RestClient.builder(new HttpHost("10.110.13.28", 8000, "http"),
                new HttpHost("10.110.13.56", 8000, "http"), new HttpHost("10.110.14.239", 8000, "http")));
    }

    @Test
    public void testInert() throws Exception {
        int threadNum = 200;
        ExecutorService executorService = Executors.newFixedThreadPool(threadNum);
        for (int i = 0; i < threadNum; i++) {
            executorService.execute(() -> {
                try {
                    insert();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.HOURS);
    }

    public void insert() throws Exception {
        BulkRequest bulkRequest = new BulkRequest();
        String index = "chihiro_ecomp";
        IndexRequest indexRequest = new IndexRequest(index).type(index).id("a")
                .source(Maps.newHashMap("name", "name-10"));

        UpdateRequest updateRequest = new UpdateRequest(index, index, "a");
        updateRequest.doc(Maps.newHashMap("age", 17)).upsert(indexRequest);
        bulkRequest.add(updateRequest);
        BulkResponse bulk = CLIENT.bulk(bulkRequest, RequestOptions.DEFAULT);
        System.out.println("bulk:" + bulk.buildFailureMessage());
    }

}
