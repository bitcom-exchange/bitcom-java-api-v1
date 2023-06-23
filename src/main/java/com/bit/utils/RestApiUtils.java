package com.bit.utils;

import okhttp3.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.stream.Collectors;

public class RestApiUtils {
    public static String HTTP_METHOD_GET = "GET";
    public static String HTTP_METHOD_POST = "POST";

    public static String paramsToStr(HashMap<String, Object> params) {
        String mapAsString = params.keySet().stream()
                .map(key -> key + "=" + params.get(key))
                .collect(Collectors.joining("&"));
        return mapAsString;
    }

    public static FormBody paramsToFormBody(HashMap<String, Object> params){
        FormBody.Builder builder = new FormBody.Builder();
        for(String key: params.keySet()) {
            builder.add(key, String.valueOf(params.get(key)));
        }
        return builder.build();
    }

    public static RequestBody paramsToJsonBody(HashMap<String, Object> params){
        RequestBody body = RequestBody.create(
                MediaType.parse("application/json"), Common.objectToJson(params));
        return body;
    }

    public static Response httpRequest(OkHttpClient client, String method, String url, HashMap<String, Object> params,
                                       HashMap<String, String> headers) throws IOException {
        if (params == null) {
            params = new HashMap<>();
        }

        method = method.trim().toUpperCase();

//        System.out.printf("method=%s, url=%s, params=%s\n", method, url, Common.objectToJson(params));

        Request.Builder builder = new Request.Builder();

        if (method.equals(HTTP_METHOD_GET)) {
            var queryString = paramsToStr(params);
            url += "?" + queryString;
            builder = builder.url(url);
        } else {
            builder = builder.url(url);
            builder = builder.post(RestApiUtils.paramsToJsonBody(params));
        }

        if (headers != null) {
            for(String key: headers.keySet()) {
                builder = builder.addHeader(key, headers.get(key));
            }
        }

        var req = builder.build();
        // System.out.printf("req = %s\n", req);
        return client.newCall(req).execute();
    }

}
