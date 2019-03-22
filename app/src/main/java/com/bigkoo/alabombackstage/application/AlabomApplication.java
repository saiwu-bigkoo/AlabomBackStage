package com.bigkoo.alabombackstage.application;

import android.app.Application;

import com.bigkoo.alabombackstage.manager.AppInfoManager;

/**
 * 2019/3/18 create by sai
 **/
public class AlabomApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AppInfoManager.getInstance().init(this);
    }
}
