package com.sandy.courier.common.util;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

/**
 * @Description: @createTime：2020/6/10 12:58
 * @author: chengyu3
 **/
public class KgEsClient {

    public static final RestHighLevelClient CLIENT;

    static {
        CLIENT = new RestHighLevelClient(RestClient.builder(new HttpHost("10.110.13.28", 8000, "http"),
                new HttpHost("10.110.13.56", 8000, "http"), new HttpHost("10.110.14.239", 8000, "http")));
    }
}
