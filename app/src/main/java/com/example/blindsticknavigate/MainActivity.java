package com.example.blindsticknavigate;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.blindsticknavigate.ui.home.HomeFragment;
import com.example.blindsticknavigate.ui.notifications.NotificationsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    BleLowEnergy ble;

    private FragmentManager fmanager;
    private FragmentTransaction ftransaction;
    public NavController navController = null;
    public Audio audio = null;
    LocationManager locationManager = null;
    public double lng=0.0;
    public double lat=0.0;

    private final Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(@NonNull Message msg) {
            dealsignal(msg.what);
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        ActionBar actionBar = this.getSupportActionBar();
        actionBar.setTitle("盲杖应用");

        audio = new Audio(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }

//        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        //获取经度
        lng = bestLocation.getLongitude();
        //获取纬度
        lat = bestLocation.getLatitude();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
////        getMenuInflater().inflate(R.menu.menu, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            //actionbar navigation up 按钮
//            case android.R.id.home:
//                finish();
//                break;
//
//            case R.id.bluetoothconnect:
////                Toast.makeText(this, "蓝牙已连接", Toast.LENGTH_SHORT).show();
//                ble = new BleLowEnergy(this, handler);
//                ble.scanThenConnect();
//                audio.speak("蓝牙已连接");
//                break;
//        }
//        return true;
//    }

//    设置本app字体
    @Override
    public Resources getResources() {
        Resources resources = super.getResources();
        if (resources != null) {
            android.content.res.Configuration configuration = resources.getConfiguration();
            if (configuration != null && configuration.fontScale != 0.8f) {
                configuration.fontScale = 0.8f;
                resources.updateConfiguration(configuration, resources.getDisplayMetrics());
            }
        }
        return resources;
    }



    public void bluetoothconnect(){
        ble = new BleLowEnergy(this, handler);
        ble.scanThenConnect();
    }
    public void bluetoothdisconnect(){
        if(ble!=null){
            ble.disconnect();
        }
        ble=null;
    }
    public BleLowEnergy getble(){
        return ble;
    }

    public void gotofragment(Fragment fr,Class f){
        fmanager = getSupportFragmentManager();
        ftransaction = fmanager.beginTransaction();
        Fragment mTargetFragment = null;
        try {
            mTargetFragment = (Fragment)f.newInstance();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        ftransaction.replace(R.id.configlayout, mTargetFragment);
//        ftransaction.show(mTargetFragment).hide(fr);
        ftransaction.addToBackStack(null); //将当前fragment加入到返回栈中
        ftransaction.commit();
        fmanager=null;
        ftransaction=null;
//        int num=fmanager.getBackStackEntryCount();
//        System.out.println(num);
    }
    public void dealsignal(int type){
//        获取homefragment对象，以便调用他的方法
        HomeFragment hfra=null;
        fmanager = getSupportFragmentManager();
        List<Fragment> fl=fmanager.getFragments();
        NavHostFragment navHostFragment = (NavHostFragment) fl.get(0);
        List<Fragment> childfragments = navHostFragment.getChildFragmentManager().getFragments();
        if(childfragments != null && childfragments.size() > 0){
            for (int j = 0; j < childfragments.size(); j++) {
                Fragment fra = childfragments.get(j);
                if(fra.getClass().isAssignableFrom(HomeFragment.class)){
                    hfra=(HomeFragment)childfragments.get(j);
                }
            }
        }
//        判断信号
        switch (type){
            case profile.AUDIOREPORT:
                hfra.setbackcolor(R.color.white);
                audio.speakreport();
                break;
            case profile.OBSTACLE:
                hfra.setbackcolor(R.color.obstacle);
                audio.speak("开启障碍物检测");
                break;
            case profile.REDGRENNLIGHT:
                hfra.setbackcolor(R.color.redgreenlight);
                audio.speak("开启红绿灯检测");
                break;
        }
    }



}