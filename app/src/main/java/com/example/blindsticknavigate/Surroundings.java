package com.example.blindsticknavigate;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.blindsticknavigate.ui.surroundings.SensorEventHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class Surroundings {

    private String lng;
    private String lat;
    private Context mcontext;
    private SensorEventHelper mSensorHelper;
    private final int edge = 2000;
    private Handler handler;

    public SensorEventHelper getmSensorHelper() {
        return mSensorHelper;
    }

    public void getSurroundingPois(){
        float mAngle = mSensorHelper.getmAngle();
        String coordinates = cal_lat_lng(Double.valueOf(lat), Double.valueOf(lng), mAngle);
        StringBuilder sb = new StringBuilder();
        sb.append("前方").append(edge).append("米范围内：");

        // 发送poi请求
        POIRequest request = new POIRequest();
        request.setKey(profile.gaodeKey);
        request.setCoordinates(coordinates);
        request.setTypes("050301");
        request.setGetPolygonPois();
        FutureTask<String> polygon=new FutureTask<>(request.getGetPolygonPois());
        new Thread(polygon).start();
        String responseBodyString = "";
        try {
            responseBodyString = polygon.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            JSONObject jo = new JSONObject(responseBodyString);
            JSONArray pois=jo.getJSONArray("pois");
            if(pois.length() == 0)
                sb.append("没有店铺");
            else {
                sb.append("有").append(pois.length()).append("家店铺。");
                for (int i = 0; i < pois.length(); i++) {
                    JSONObject poi = (JSONObject) pois.get(i);
                    sb.append(poi.getString("name"));
//                            .append("距离").append(poi.getString("distance")).append("米。");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            sb.delete(0, sb.length());
            sb.append("查询出错");
        }finally {
            Message msg = Message.obtain();
            msg.what = 1;
            msg.obj = sb.toString();
            handler.sendMessage(msg);
        }
    }

    /**
     * 根据当前当前坐标以及朝向，计算三角形区域其余两点的坐标。
     * @param cur_lat 当前纬度
     * @param cur_lng 当前经度
     * @param mAngle 当前角度
     * @return 三角形顶点坐标组成的字符串，可直接拼接到poi_url中。
     * @author su
     */
    public String cal_lat_lng(Double cur_lat, Double cur_lng, float mAngle){
        StringBuilder coordinates = new StringBuilder();

        double pi = Math.PI;
        double r_earth = 6378000;

        // 三角形区域的角度，目前设置为前方60°.
        double alpha = pi / 3;

        double dxA = edge * Math.sin(mAngle - alpha / 2);
        double dyA = edge * Math.cos(mAngle - alpha / 2);
        double dxB = edge * Math.sin(pi - alpha / 2 - mAngle);
        double dyB = (-1) * edge * Math.cos(pi - alpha / 2 - mAngle);

        double latA  = cur_lat  + (dyA / r_earth) * (180 / pi);
        double lngA = cur_lng + (dxA / r_earth) * (180 / pi) / Math.cos(latA * pi/180);
        double latB  = cur_lat  + (dyB / r_earth) * (180 / pi);
        double lngB = cur_lng + (dxB / r_earth) * (180 / pi) / Math.cos(latB * pi/180);

        coordinates.append(cur_lng).append(",").append(cur_lat).append("|");
        coordinates.append(lngA).append(",").append(latA).append("|");
        coordinates.append(lngB).append(",").append(latB);

        return coordinates.toString();
    }

    /*----- 以下为getter和setter -----*/
    public void setmSensorHelper() {
        this.mSensorHelper = new SensorEventHelper(this.getMcontext());
        mSensorHelper.registerSensorListener();
    }

    public Context getMcontext() {
        return mcontext;
    }

    public void setMcontext(Context mcontext) {
        this.mcontext = mcontext;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }
}
