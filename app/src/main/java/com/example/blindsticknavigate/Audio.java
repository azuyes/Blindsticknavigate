package com.example.blindsticknavigate;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Calendar;
import java.util.Locale;

public class Audio {
    private TextToSpeech textToSpeech = null;//创建自带语音对象
    public Audio(Context c){
        //实例化自带语音对象
        textToSpeech = new TextToSpeech(c, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == textToSpeech.SUCCESS) {

                    textToSpeech.setPitch(1.0f);//方法用来控制音调
                    textToSpeech.setSpeechRate(1.0f);//用来控制语速

                    //判断是否支持下面两种语言
                    int result1 = textToSpeech.setLanguage(Locale.US);
                    int result2 = textToSpeech.setLanguage(Locale.SIMPLIFIED_CHINESE);
                    boolean a = (result1 == TextToSpeech.LANG_MISSING_DATA || result1 == TextToSpeech.LANG_NOT_SUPPORTED);
                    boolean b = (result2 == TextToSpeech.LANG_MISSING_DATA || result2 == TextToSpeech.LANG_NOT_SUPPORTED);

                    Log.i("zhh_tts", "US支持否？--》" + !a +
                            "\nzh-CN支持否》--》" + !b);

                } else {
//                    Toast.makeText(AndroidTTSActivity.this, "数据丢失或不支持", Toast.LENGTH_SHORT).show();
                    Log.i("audioWarning: ","语音播报初始化失败");
                }

            }
        });
    }

    public void speak(String data){
        // 设置音调，值越大声音越尖（女生），值越小则变成男声,1.0是常规
        textToSpeech.setPitch(1.0f);
        // 设置语速
        textToSpeech.setSpeechRate(0.3f);
        textToSpeech.speak(data,//输入中文，若不支持的设备则不会读出来
                TextToSpeech.QUEUE_FLUSH, null);
    }

    public String speaktime(){
        Calendar calendar = Calendar.getInstance();//取得当前时间的年月日 时分秒
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        String word="现在时间"+year+"年"+month+"月"+day+"日"+hour+"时"+minute+"分";
        return word;
    }
    public String speaklocation(){
        return "您现在位于北京市朝阳区北京工业大学";
    }
    public String speakorientation(){
        return "现在朝向东北方向";
    }
    public void speakreport(){
        String time=speaktime();
        String loc=speaklocation();
        String ori=speakorientation();
        speak(time+loc+ori);
    }

}
