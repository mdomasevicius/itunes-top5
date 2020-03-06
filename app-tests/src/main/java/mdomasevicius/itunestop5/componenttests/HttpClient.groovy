package mdomasevicius.itunestop5.componenttests

import groovy.json.JsonSlurper
import groovy.transform.CompileStatic
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.Request

import static java.util.concurrent.TimeUnit.MINUTES
import static java.util.concurrent.TimeUnit.SECONDS

class HttpClient {

    private static final String uriString = "http://localhost:7001"
    private final OkHttpClient httpClient

    HttpClient() {
        this.httpClient  = new OkHttpClient.Builder()
            .connectTimeout(5, SECONDS)
            .retryOnConnectionFailure(true)
            .readTimeout(20, SECONDS)
            .writeTimeout(20, SECONDS)
            .connectionPool(new ConnectionPool(1, 10, MINUTES))
            .build()
    }

    FluentResponse get(String path, Map<String, String> queryMap = [:]) {
        def httpResponse = httpClient.newCall(getRequest(path, queryMap)).execute()
        def body = new JsonSlurper().parse(httpResponse.body().bytes())
        return new FluentResponse(httpResponse.code(), body)
    }

    private static Request getRequest(String path, Map<String, String> query = [:]) {
        return requestBuilder(path, query)
            .get()
            .build()
    }

    private static Request.Builder requestBuilder(String path, Map<String, String> queryMap = [:]) {
        def queryString = queryMap ? "?${queryMap2String(queryMap)}" : ''
        return new Request.Builder().url("${uriString}${path}${queryString}")
    }

    private static String queryMap2String(Map<String, String> queryMap) {
        return queryMap.collect { "${it.key}=${it.value}" }.join('&')
    }
}

@CompileStatic
class FluentResponse {
    final int status
    final Object body

    FluentResponse(int status, Object body) {
        this.status = status
        this.body = body
    }
}