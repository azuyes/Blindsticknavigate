package com.example.blindsticknavigate.ui.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainer;
import androidx.fragment.app.FragmentContainerView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.blindsticknavigate.BleLowEnergy;
import com.example.blindsticknavigate.Businfo;
import com.example.blindsticknavigate.Businformation;
import com.example.blindsticknavigate.GodeLocation;
import com.example.blindsticknavigate.MainActivity;
import com.example.blindsticknavigate.R;
import com.example.blindsticknavigate.Surroundings;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    View rootview=null;
    Boolean isconnect=false;
    Button obstacledetect=null;
    Button redgreenlight=null;
    Button audioreport=null;
    Button busreport=null;
    // “周围商铺”按钮
    Button surroundings = null;
    ConstraintLayout lyout=null;
    BottomNavigationView navibarlyout=null;
    TextView textView=null;
    TextView twofunctext=null;
    MainActivity mainact =null;
    Businfo businfo=null;
    Surroundings sur_tool;

    Handler homefraghandler=new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(@NonNull Message msg) {
            dealmessage(msg);
            return false;
        }
    });


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        mainact=(MainActivity)getActivity();
        rootview=root;
        textView = root.findViewById(R.id.text_home);
        twofunctext=root.findViewById(R.id.twofunctext);
        ImageView bluetoothbutton=root.findViewById(R.id.bluetoothbutton);
        lyout=(ConstraintLayout)rootview.findViewById(R.id.frag_home_layout);
        navibarlyout=(BottomNavigationView)mainact.findViewById(R.id.nav_view);

        redgreenlight=(Button) root.findViewById(R.id.redgreenlight);
        obstacledetect=(Button) root.findViewById(R.id.obstacledetect);
        audioreport=(Button) root.findViewById(R.id.audioreport);
        busreport=(Button) root.findViewById(R.id.busreport);
        // 获取“周围商铺”按钮
        surroundings = (Button)root.findViewById(R.id.surroundings);

        init();

        bluetoothbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity a = (MainActivity)getActivity();
                if(a!=null){
                    if(!isconnect){
                        a.bluetoothconnect();
                        isconnect=true;
                        eyesclosetoopen();
                        textView.setText("蓝牙已连接");
                        ((MainActivity)getActivity()).audio.speak("蓝牙已连接");
                        redgreenlight.setVisibility(View.VISIBLE);
                        obstacledetect.setVisibility(View.VISIBLE);
                        audioreport.setVisibility(View.VISIBLE);
                    }else{
                        a.bluetoothdisconnect();
                        isconnect=false;
                        eyesopentoclose();
                        textView.setText("点击连接蓝牙");
                        ((MainActivity)getActivity()).audio.speak("蓝牙已断开");
                        redgreenlight.setVisibility(View.INVISIBLE);
                        obstacledetect.setVisibility(View.INVISIBLE);
                        twofunctext.setVisibility(View.INVISIBLE);
                        audioreport.setVisibility(View.INVISIBLE);
                        setbackcolor(R.color.white);
                    }

                }
            }
        });

        redgreenlight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setbackcolor(R.color.redgreenlight);
                ((MainActivity)getActivity()).audio.speak("红绿灯检测已开启");
            }
        });

        obstacledetect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setbackcolor(R.color.obstacle);
                ((MainActivity)getActivity()).audio.speak("障碍物检测已开启");
            }
        });

        audioreport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).audio.speakreport();
            }
        });

        busreport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GodeLocation godeLocation=new GodeLocation((MainActivity)getActivity(),homefraghandler);
                godeLocation.startlocate();
                //获取poi兴趣点公交站
                double lng=((MainActivity)getActivity()).lng;
                double lat=((MainActivity)getActivity()).lat;

                Businformation bus=new Businformation(lng,lat,homefraghandler);
//                String getpois=bus.getpoi();
//                String[] linename=getpois.split("&")[0].split(";");
//
//                String[] lineids=bus.getrealtimebusline(getpois);
//                String[] condition=bus.getrealtimestopid(lineids);
//                String[] cominginfos=bus.getbuscominginfo(condition);
//                String readword="";
//                for(int i=0;i<linename.length;i++){
//                    if(cominginfos[i].contains("&")){
//                        readword+=linename[i]+"距您"+cominginfos[i].split("&")[0]+"米"+"还有"+cominginfos[i].split("&")[1]+"站  ";
//
//                    }else{
//                        readword+=linename[i]+"暂无信息  ";
//                    }
//                }
//                ((MainActivity)getActivity()).audio.speak(readword);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        bus.reportword();
                    }
                }).start();
            }
        });

        surroundings.setOnClickListener(v -> {
            // 获取并当前位置经纬度
            double lng=((MainActivity)getActivity()).lng;
            double lat=((MainActivity)getActivity()).lat;
            sur_tool.setLat(String.valueOf(lat));
            sur_tool.setLng(String.valueOf(lng));
            // 获取前方三角形范围内的poi
            sur_tool.getSurroundingPois();
        });

//        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });

        return root;
    }

    @Override
    public void onPause() {
        super.onPause();
        navibarlyout.setBackgroundColor(getResources().getColor(R.color.white));
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    public void eyesclosetoopen(){
        ImageView bluetoothbutton=rootview.findViewById(R.id.bluetoothbutton);
        bluetoothbutton.setImageResource(R.drawable.openeyes);
    }
    public void eyesopentoclose(){
        ImageView bluetoothbutton=rootview.findViewById(R.id.bluetoothbutton);
        bluetoothbutton.setImageResource(R.drawable.closeeyes);
    }
    public void init(){
        redgreenlight.setVisibility(View.INVISIBLE);
        obstacledetect.setVisibility(View.INVISIBLE);
        twofunctext.setVisibility(View.INVISIBLE);
        //        先判断目前mainactivity里的连接状态
        if(mainact.getble()!=null){
            isconnect=true;
            eyesclosetoopen();
            textView.setText("蓝牙已连接");
            redgreenlight.setVisibility(View.VISIBLE);
            obstacledetect.setVisibility(View.VISIBLE);
            audioreport.setVisibility(View.VISIBLE);
        }else{
            isconnect=false;
            eyesopentoclose();
            textView.setText("点击连接蓝牙");
            redgreenlight.setVisibility(View.INVISIBLE);
            obstacledetect.setVisibility(View.INVISIBLE);
            audioreport.setVisibility(View.INVISIBLE);
        }
        setbackcolor(R.color.white);

        // 初始化"周围商铺"功能中用到的context和传感器监听
        sur_tool = new Surroundings();
        sur_tool.setMcontext(getContext());
        sur_tool.setmSensorHelper();
    }
    public void setbackcolor(int color){
        lyout.setBackgroundColor(getResources().getColor(color));
        navibarlyout.setBackgroundColor(getResources().getColor(color));
    }

    private void dealmessage(Message msg){
        switch (msg.what){
            case 1:
                ((MainActivity)getActivity()).audio.speak((String)msg.obj);
                break;
            case 2:
                ((MainActivity)getActivity()).lng=Double.parseDouble(((String)msg.obj).split("&")[0]);
                ((MainActivity)getActivity()).lat=Double.parseDouble(((String)msg.obj).split("&")[1]);
                break;
        }
    }
}