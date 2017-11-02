package com.example.xunfei.app;

import android.app.Application;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

/**
 * Created by Administrator on 2017/11/1.
 */

public class MyApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        SpeechUtility.createUtility(this, SpeechConstant.APPID +"=59f93b5c");

    }
}
