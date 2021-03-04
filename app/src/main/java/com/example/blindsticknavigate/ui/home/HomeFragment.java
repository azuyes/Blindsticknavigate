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
import com.example.blindsticknavigate.MainActivity;
import com.example.blindsticknavigate.R;
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
    ConstraintLayout lyout=null;
    BottomNavigationView navibarlyout=null;
    TextView textView=null;
    TextView twofunctext=null;
    MainActivity mainact =null;
    Businfo businfo=null;

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
                //获取poi兴趣点公交站
                double lng=((MainActivity)getActivity()).lng;
                double lat=((MainActivity)getActivity()).lat;
                businfo=new Businfo(lng,lat);
                businfo.getpoi();
                while(businfo.responseBodystring.length()<=0){
                    continue;
                }

                String info=businfo.responseBodystring;
                try {
                    JSONObject jo = new JSONObject(info);
                    JSONArray pois=jo.getJSONArray("pois");
                    JSONObject neareststop=(JSONObject) pois.get(0);
                    String stopname=neareststop.getString("name");
                    String lines=neareststop.getString("address");

                    //根据线路获取线路id
                    HashMap<String,String> kv= businfo.getstopnameandline(lines,stopname);
                    businfo.getrealtimebusline(kv);
                    while(businfo.lineid.size()<kv.size()){
                        continue;
                    }

                    businfo.getrealtimestopid(businfo.lineid,kv);
                    while(businfo.stopids.size()<kv.size()){
                        continue;
                    }

                    businfo.getrealbuscominginfo();
                    while(businfo.cominginfos.size()<kv.size()){
                        continue;
                    }

                    Log.i("sign","!!");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                Log.i("Home response info",info);
            }
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
    }
    public void setbackcolor(int color){
        lyout.setBackgroundColor(getResources().getColor(color));
        navibarlyout.setBackgroundColor(getResources().getColor(color));
    }
}