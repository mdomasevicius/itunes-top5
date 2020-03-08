package mdomasevicius.itunestop5.componenttests

import groovy.json.JsonSlurper
import okhttp3.*

import static groovy.json.JsonOutput.toJson
import static java.util.concurrent.TimeUnit.MINUTES
import static java.util.concurrent.TimeUnit.SECONDS

class HttpClient {

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private final String baseUri
    private final Map<String, String> defaultHeaders
    private final OkHttpClient httpClient

    HttpClient(String baseUri, Map<String, String> defaultHeaders = [:]) {
        this.baseUri = baseUri
        this.defaultHeaders = defaultHeaders
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
        def bytes = httpResponse.body().bytes()
        return new FluentResponse(httpResponse.code(), bytes ? new JsonSlurper().parse(bytes) : null)
    }

    FluentResponse post(String path, Map payload) {
        def httpResponse = httpClient.newCall(postRequest(path, payload)).execute()
        def body = httpResponse.body().contentLength() ? new JsonSlurper().parse(httpResponse.body().bytes()) : null
        return new FluentResponse(httpResponse.code(), body)
    }

    FluentResponse delete(String path) {
        def httpResponse = httpClient.newCall(deleteRequest(path)).execute()
        return new FluentResponse(httpResponse.code(), null)
    }

    private Request getRequest(String path, Map<String, String> query = [:]) {
        return applyDefaultHeaders(requestBuilder(path, query))
            .get()
            .build()
    }

    private Request postRequest(String path, Map payload) {
        return applyDefaultHeaders(requestBuilder(path))
            .post(RequestBody.create(toJson(payload), JSON))
            .build()
    }

    private Request deleteRequest(String path) {
        return applyDefaultHeaders(requestBuilder(path))
            .delete()
            .build()
    }

    private Request.Builder requestBuilder(String path, Map<String, String> queryMap = [:]) {
        def queryString = queryMap ? "?${queryMap2String(queryMap)}" : ''
        return new Request.Builder().url("${baseUri}${path}${queryString}")
    }

    private Request.Builder applyDefaultHeaders(Request.Builder requestContinuation) {
        defaultHeaders.each { key, value -> requestContinuation.header(key, value) }
        return requestContinuation
    }

    private static String queryMap2String(Map<String, String> queryMap) {
        return queryMap.collect { "${it.key}=${it.value}" }.join('&')
    }
}

class FluentResponse {
    final int status
    final Object body

    FluentResponse(int status, Object body) {
        this.status = status
        this.body = body
    }
}