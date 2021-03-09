package com.example.blindsticknavigate;

import android.os.Handler;
import android.os.Message;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class Businformation {
    String lng=null;
    String lat=null;
    String poisearchradius="500";//半径（m）
    String typecode="150700";//种类，150700是公交兴趣点
    String poiurl=null;
    String realtimebuslineurl="http://www.bjbus.com/api/api_etaline_list.php?hidden_MapTool=busex2.BusInfo&city=%u5317%u4EAC&pageindex=1&pagesize=30&fromuser=bjbus&datasource=bjbus&clientid=9db0f8fcb62eb46c&webapp=mobilewebapp&what=";
    public String responseBodystring="";
    public List<String> lineid=null;
    public List<String> anotherdirectionlineid=new ArrayList<String>();
    public List<String> stopids=null;
    public List<String> cominginfos=new ArrayList<String>();
    Handler handler;

    public Businformation(Double lng,Double lat,Handler handler){
        this.lng=String.valueOf(lng);
        this.lat=String.valueOf(lat);
        this.handler=handler;
        this.poiurl="https://restapi.amap.com/v3/place/around?key="+profile.gaodeKey+"&location="+this.lng+","+this.lat+"&keywords=&types="+this.typecode+"&radius="+this.poisearchradius+"&offset=20&page=1&extensions=all";

    }

    class Getpoi implements Callable<String>{
        String res="";
        @Override
        public String call() throws Exception {
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
                    String responseBodystring=responseBody.string();
                    JSONObject jo = new JSONObject(responseBodystring);
                    JSONArray pois=jo.getJSONArray("pois");
                    JSONObject neareststop=(JSONObject) pois.get(0);
                    String stopname=neareststop.getString("name");
                    String lines=neareststop.getString("address");
                    res=lines+"&"+stopname;
//                    System.out.println("Businfo response info: "+responseBodystring);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return res;
        }
    }

    class Getrealtimebusline implements Callable<String>{
        String busid="";
        String linename="";
        public Getrealtimebusline(String linename){
            this.linename=linename;
        }
        @Override
        public String call() throws Exception {
            try {
                //创建OkHttpClient对象
                OkHttpClient client = new OkHttpClient();
                //创建Request
                Request request = new Request.Builder()
                        .url(realtimebuslineurl+linename)//访问连接
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
//                        anotherdirectionlineid.add(anotherdirection.getString("lineId"));
                        busid=onedirection.getString("lineId");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return busid;
        }
    }

    class Getrealtimestopid implements Callable<String>{
        String lineid="";
        String stopname="";
        String stopsurl="";
        String stopid="";
        public Getrealtimestopid(String lineid,String stopname){
            this.lineid=lineid;
            this.stopname=stopname.replace("(公交站)","");
            this.stopsurl="http://www.bjbus.com/api/api_etastation.php?lineId=" + this.lineid + "&token=eyJhbGciOiJIUzI1NiIsIlR5cGUiOiJKd3QiLCJ0eXAiOiJKV1QifQ.eyJwYXNzd29yZCI6IjY0ODU5MTQzNSIsInVzZXJOYW1lIjoiYmpidXMiLCJleHAiOjE2MTY1MzY4MDF9.BalA1LZoStafkHzomepuv51bUDdiQY8Q6JiB_l8vIW8";
        }
        @Override
        public String call() throws Exception {
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

                        for (int j = 0; j < stops.length(); j++) {
                            JSONObject stop = (JSONObject) stops.get(j);
                            if (stop.get("stopName").equals(stopname)){
                                stopid=stop.getString("stationId");
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return stopid;
        }
    }

    class Getrealbuscominginfo implements Callable<String>{
        String lineid="";
        String stopid="";
        String condurl="";
        String cominginfo="无信息";
        public Getrealbuscominginfo(String lineid,String stopid){
            this.lineid=lineid;
            this.stopid=stopid;
            this.condurl="http://www.bjbus.com/api/api_etartime.php?conditionstr=" + lineid+"-"+stopid + "&token=eyJhbGciOiJIUzI1NiIsIlR5cGUiOiJKd3QiLCJ0eXAiOiJKV1QifQ.eyJwYXNzd29yZCI6IjY0ODU5MTQzNSIsInVzZXJOYW1lIjoiYmpidXMiLCJleHAiOjE2MTY1MzY4MDF9.BalA1LZoStafkHzomepuv51bUDdiQY8Q6JiB_l8vIW8";
        }
        public Getrealbuscominginfo(String condition){
            this.condurl="http://www.bjbus.com/api/api_etartime.php?conditionstr=" + condition + "&token=eyJhbGciOiJIUzI1NiIsIlR5cGUiOiJKd3QiLCJ0eXAiOiJKV1QifQ.eyJwYXNzd29yZCI6IjY0ODU5MTQzNSIsInVzZXJOYW1lIjoiYmpidXMiLCJleHAiOjE2MTY1MzY4MDF9.BalA1LZoStafkHzomepuv51bUDdiQY8Q6JiB_l8vIW8";
        }

        @Override
        public String call() throws Exception {
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
                        JSONObject inf=(JSONObject)trip.get(trip.length()-1);
                        cominginfo=inf.getString("distance")+"&"+inf.getString("stationLeft");
//                        cominginfos.add(cominginfo);

                    } catch (JSONException e) {
                        e.printStackTrace();
//                        cominginfos.add(e.getMessage());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return cominginfo;
        }
    }

    public String getpoi() {
        String res="";
        FutureTask<String> poitask=new FutureTask<String>(new Getpoi());
        new Thread(poitask).start();
        try {
            res=poitask.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return res;
    }

    public String[] getrealtimebusline(String lineandstopname){
        String[] linenames=lineandstopname.split("&")[0].split(";");
        String stopname=lineandstopname.split("&")[1];
        String[] lineids=new String[linenames.length];
        List<FutureTask<String>> lineidtasklist=new ArrayList<FutureTask<String>>();
        ExecutorService exs = Executors.newFixedThreadPool(5);
        for(String linename:linenames){
            FutureTask<String> lineidtask=new FutureTask<String>(new Getrealtimebusline(linename.substring(0, linename.length() - 1)));
            exs.submit(lineidtask);
            lineidtasklist.add(lineidtask);
        }
        for(int i=0;i<lineidtasklist.size();i++){
            String lineid="";
            try {
                lineid=lineidtasklist.get(i).get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            lineids[i]=lineid+"&"+stopname;
        }
        return lineids;
    }

    public String[] getrealtimestopid(String[] lineidsandstopnames){

        //TODO  选择公交朝向
        String[] condition=new String[lineidsandstopnames.length];
        List<FutureTask<String>> stopidtasklist=new ArrayList<FutureTask<String>>();
        ExecutorService exs = Executors.newFixedThreadPool(5);
        for(String linename:lineidsandstopnames){
            FutureTask<String> stopidtask=new FutureTask<String>(new Getrealtimestopid(linename.split("&")[0],linename.split("&")[1]));
            exs.submit(stopidtask);
            stopidtasklist.add(stopidtask);
        }
        for(int i=0;i<stopidtasklist.size();i++){
            String stopid="";
            try {
                stopid=stopidtasklist.get(i).get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            condition[i]=lineidsandstopnames[i].split("&")[0]+"-"+stopid;
        }
        return condition;
    }

    public String[] getbuscominginfo(String[] conditions){
        String[] cominginfos=new String[conditions.length];
        List<FutureTask<String>> cominginfotasklist=new ArrayList<FutureTask<String>>();
        ExecutorService exs = Executors.newFixedThreadPool(5);
        for(String condition:conditions){
            FutureTask<String> cominginfotask=new FutureTask<String>(new Getrealbuscominginfo(condition));
            exs.submit(cominginfotask);
            cominginfotasklist.add(cominginfotask);
        }
        for(int i=0;i<cominginfotasklist.size();i++){
            String cominginfo="";
            try {
                cominginfo=cominginfotasklist.get(i).get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            cominginfos[i]=cominginfo;
        }
        return cominginfos;
    }

    public String reportword(){
        String readword = "";
        try {
            String getpois = this.getpoi();
            String[] linename = getpois.split("&")[0].split(";");
            String stopname = getpois.split("&")[1];
            String[] lineids = this.getrealtimebusline(getpois);
            String[] condition = this.getrealtimestopid(lineids);
            String[] cominginfos = this.getbuscominginfo(condition);
            readword = "您现在位于" + stopname + " ";
            for (int i = 0; i < linename.length; i++) {
                if (cominginfos[i].contains("&")) {
                    readword += linename[i] + "距您" + cominginfos[i].split("&")[0] + "米" + "还有" + cominginfos[i].split("&")[1] + "站  ";

                } else {
                    readword += linename[i] + "暂无信息  ";
                }
            }
        }catch(Exception e){
            e.printStackTrace();
            readword="查询出错";
        }finally {
            Message msg = Message.obtain();
            msg.what = 1;
            msg.obj = readword;
            handler.sendMessage(msg);
            return readword;
        }
    }

}
