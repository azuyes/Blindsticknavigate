package com.example.blindsticknavigate;

import java.io.IOException;
import java.util.concurrent.Callable;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class POIRequest {
    private String key;
    private String keywords;
    private String lat;
    private String lng;
    private String types;
    private int radius;
    // 多边形搜索区域的顶点坐标
    private String coordinates;
    private String extensions = "base";
    private String output = "JSON";
    private String responseBodyString = "";
    private GetPolygonPois getPolygonPois;

    public class GetPolygonPois implements Callable<String> {
        @Override
        public String call() throws Exception {
            String poi_url = "https://restapi.amap.com/v3/place/polygon?" +
                    "key=" + key +
                    "&polygon=" + getCoordinates() +
//                    "&keywords=" + keywords +
                    "&types=" + types +
                    "&extensions=" + extensions +
                    "&output=" + output;
            return send_request(poi_url);
        }
    }

    /**
     * 创建多边形搜索poi_url
     * @return
     */
    public String polygon_search(){
        String poi_url = "https://restapi.amap.com/v3/place/polygon?" +
                "key=" + this.key +
                "&polygon=" + this.getCoordinates() +
                "&keywords=" + this.keywords +
                "&types=" + this.types +
                "&extensions=" + this.extensions +
                "&output=" + this.output;

        return send_request(poi_url);
    }

    public String send_request(String poi_url){
        try {
            //创建OkHttpClient对象
            OkHttpClient client = new OkHttpClient();
            //创建Request
            Request request = new Request.Builder()
                    .url(poi_url)//访问连接
                    .get()
                    .build();
            //创建Call对象
            Call call = client.newCall(request);
            //通过execute()方法获得请求响应的Response对象
            Response response = call.execute();
            if (response.isSuccessful()) {
                ResponseBody responseBody=response.body();
                responseBodyString=responseBody.string();
                System.out.println("Surroundings response info: "+responseBodyString);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return responseBodyString;
    }

    /*------- 以下为getter setter -------*/
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getTypes() {
        return types;
    }

    public void setTypes(String types) {
        this.types = types;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    public GetPolygonPois getGetPolygonPois() {
        return getPolygonPois;
    }

    public void setGetPolygonPois() {
        this.getPolygonPois = new GetPolygonPois();
    }
}
