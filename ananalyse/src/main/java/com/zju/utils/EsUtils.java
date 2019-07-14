package com.zju.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;

import java.io.IOException;
import java.util.Map;

/**
 * @author zhangqi
 * @create 2019/7/14
 */
public class EsUtils {

    private static RestClient client;

    private static int connectTimeout = 5000;

    private static int socketTimeout = 60000;

    private static int connectRequestTimeout = 500;

    private static int maxRetryTimeout = 60000;

    static {
        RestClientBuilder clientBuilder = RestClient.builder(new HttpHost("localhost",9200,"http"))
                .setRequestConfigCallback(t->t.setConnectTimeout(connectTimeout).setSocketTimeout(socketTimeout).setConnectionRequestTimeout(connectRequestTimeout)).setMaxRetryTimeoutMillis(maxRetryTimeout);
        client = clientBuilder.build();
    }

    public static Response performRequest(String put,String format) throws IOException {
        return client.performRequest(put,format);
    }


    public static Response performRequest(String put, String endpoint, Map<String,String> params, HttpEntity httpEntity) throws IOException {
        return client.performRequest(put,endpoint,params,httpEntity);
    }
}
