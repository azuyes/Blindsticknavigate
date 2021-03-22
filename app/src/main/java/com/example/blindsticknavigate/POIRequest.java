package com.example.blindsticknavigate;

import java.io.IOException;

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
    /**
     * 创建多边形搜索poi_url
     * @return
     */
    public String polygon_search(){
        StringBuilder poi_url = new StringBuilder();
        poi_url.append("https://restapi.amap.com/v3/place/polygon?");
        poi_url.append("key=").append(this.key);
        poi_url.append("&polygon=").append(this.getCoordinates());
        poi_url.append("&keywords=").append(this.keywords);
        poi_url.append("&types=").append(this.types);
        poi_url.append("&extensions=").append(this.extensions);
        poi_url.append("&output=").append(this.output);

        String responseBodyString = send_request(poi_url.toString());
        return responseBodyString;
    }

    public synchronized String send_request(String poi_url){
        new Thread(new Runnable() {
            @Override
            public void run() {
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
            }
        }).start();

        while(responseBodyString.length()<=0){
            continue;
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
}
