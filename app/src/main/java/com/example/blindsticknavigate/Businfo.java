package com.example.blindsticknavigate;

import android.app.Activity;
import android.content.Context;
import android.location.LocationManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class Businfo{
    String lng=null;
    String lat=null;
    String poisearchradius="500";//半径（m）
    String typecode="150700";//种类，150700是公交兴趣点
    String poiurl=null;
    String realtimebuslineurl="http://www.bjbus.com/api/api_etaline_list.php?hidden_MapTool=busex2.BusInfo&city=%u5317%u4EAC&pageindex=1&pagesize=30&fromuser=bjbus&datasource=bjbus&clientid=9db0f8fcb62eb46c&webapp=mobilewebapp&what=";
    public String responseBodystring="";
    public List<String> lineid=new ArrayList<String>();
    public List<String> anotherdirectionlineid=new ArrayList<String>();
    public List<String> stopids=new ArrayList<String>();
    public List<String> cominginfos=new ArrayList<String>();

    public Businfo(Double lng,Double lat){
        this.lng=String.valueOf(lng);
        this.lat=String.valueOf(lat);
        this.poiurl="https://restapi.amap.com/v3/place/around?key="+profile.gaodeKey+"&location="+this.lng+","+this.lat+"&keywords=&types="+this.typecode+"&radius="+this.poisearchradius+"&offset=20&page=1&extensions=all";

    }

    public synchronized String getpoi() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //创建OkHttpClient对象
                    OkHttpClient client = new OkHttpClient();
                    //创建Request
                    Request request = new Request.Builder()
                            .url(poiurl)//访问连接
                            .get()
                            .build();
                    //创建Call对象
                    Call call = client.newCall(request);
                    //通过execute()方法获得请求响应的Response对象
                    Response response = call.execute();
                    if (response.isSuccessful()) {
                        //处理网络请求的响应，处理UI需要在UI线程中处理
                        //...
                        ResponseBody responseBody=response.body();
                        responseBodystring=responseBody.string();
                        System.out.println("Businfo response info: "+responseBodystring);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        return responseBodystring;
    }

    public HashMap<String,String> getstopnameandline(String lines,String stopname){
        String[] linearray=lines.split(";");
        HashMap<String,String> kv=new HashMap<String,String>();
        for (String s : linearray) {
            kv.put(s.substring(0, s.length() - 1), stopname);
        }
        return kv;
    }

    public void getrealtimebusline(HashMap<String,String> kv){
        for(Map.Entry<String,String> entry:kv.entrySet()){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        //创建OkHttpClient对象
                        OkHttpClient client = new OkHttpClient();
                        //创建Request
                        Request request = new Request.Builder()
                                .url(realtimebuslineurl+entry.getKey())//访问连接
                                .get()
                                .build();
                        //创建Call对象
                        Call call = client.newCall(request);
                        //通过execute()方法获得请求响应的Response对象
                        Response response = call.execute();
                        if (response.isSuccessful()) {
                            //处理网络请求的响应，处理UI需要在UI线程中处理
                            //...
                            ResponseBody responseBody=response.body();
                            String info=responseBody.string();
                            try {
                                JSONObject jo = new JSONObject(info);
                                JSONObject responese=jo.getJSONObject("response");
                                JSONObject resultset=responese.getJSONObject("resultset");
                                JSONObject data=resultset.getJSONObject("data");
                                JSONArray feature=data.getJSONArray("feature");

                                JSONObject onedirection=(JSONObject) feature.get(0);
                                JSONObject anotherdirection=(JSONObject) feature.get(1);
                                anotherdirectionlineid.add(anotherdirection.getString("lineId"));
                                lineid.add(onedirection.getString("lineId"));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }


    }

    public void getrealtimestopid(List<String> lineid,HashMap<String,String> kv) {
        List<String> stopnames=new ArrayList<String>();
        for(Map.Entry<String,String> entry:kv.entrySet()){
            stopnames.add(entry.getValue().replace("(公交站)",""));
        }
        for(String line : lineid) {
            String stopsurl = "http://www.bjbus.com/api/api_etastation.php?lineId=" + line + "&token=eyJhbGciOiJIUzI1NiIsIlR5cGUiOiJKd3QiLCJ0eXAiOiJKV1QifQ.eyJwYXNzd29yZCI6IjY0ODU5MTQzNSIsInVzZXJOYW1lIjoiYmpidXMiLCJleHAiOjE2MTY1MzY4MDF9.BalA1LZoStafkHzomepuv51bUDdiQY8Q6JiB_l8vIW8";
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        //创建OkHttpClient对象
                        OkHttpClient client = new OkHttpClient();
                        //创建Request
                        Request request = new Request.Builder()
                                .url(stopsurl)//访问连接
                                .get()
                                .build();
                        //创建Call对象
                        Call call = client.newCall(request);
                        //通过execute()方法获得请求响应的Response对象
                        Response response = call.execute();
                        if (response.isSuccessful()) {
                            //处理网络请求的响应，处理UI需要在UI线程中处理
                            //...
                            ResponseBody responseBody = response.body();
                            String info = responseBody.string();
                            try {
                                JSONObject jo = new JSONObject(info);
                                JSONArray stops = jo.getJSONArray("data");
                                for(String stopname:stopnames) {
                                    for (int j = 0; j < stops.length(); j++) {
                                        JSONObject stop = (JSONObject) stops.get(j);
                                        if (stop.get("stopName").equals(stopname)){
                                            stopids.add(stop.getString("stationId"));
                                        }
                                    }
                                }
                                if(stopids.size()==0){

                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    public void getrealbuscominginfo(){
        List<String> conditions=new ArrayList<String>();
        for(int i=0;i<lineid.size();i++){
            conditions.add(lineid.get(i)+"-"+stopids.get(i));
        }
        for(String condition : conditions) {
            String condurl = "http://www.bjbus.com/api/api_etartime.php?conditionstr=" + condition + "&token=eyJhbGciOiJIUzI1NiIsIlR5cGUiOiJKd3QiLCJ0eXAiOiJKV1QifQ.eyJwYXNzd29yZCI6IjY0ODU5MTQzNSIsInVzZXJOYW1lIjoiYmpidXMiLCJleHAiOjE2MTY1MzY4MDF9.BalA1LZoStafkHzomepuv51bUDdiQY8Q6JiB_l8vIW8";
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        
                        //创建OkHttpClient对象
                        OkHttpClient client = new OkHttpClient();
                        //创建Request
                        Request request = new Request.Builder()
                                .url(condurl)//访问连接
                                .get()
                                .build();
                        //创建Call对象
                        Call call = client.newCall(request);
                        //通过execute()方法获得请求响应的Response对象
                        Response response = call.execute();
                        if (response.isSuccessful()) {
                            //处理网络请求的响应，处理UI需要在UI线程中处理
                            //...
                            ResponseBody responseBody = response.body();
                            String info = responseBody.string();
                            try {
                                JSONObject jo = new JSONObject(info);
                                JSONArray data = jo.getJSONArray("data");
                                JSONObject one = (JSONObject)data.get(0);
                                JSONObject datas = one.getJSONObject("datas");
                                JSONArray trip = datas.getJSONArray("trip");
                                JSONObject inf=(JSONObject)trip.get(0);
                                String cominginfo=inf.getString("distance")+"&"+inf.getString("stationLeft");
                                cominginfos.add(cominginfo);

                            } catch (JSONException e) {
                                e.printStackTrace();
                                cominginfos.add(e.getMessage());
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

    }


}
