package com.bigkoo.alabombackstage.manager;

import android.content.Context;

/**
 * 2019/3/18 create by sai
 **/
public class AppInfoManager {
    private AppInfoManager() {
    }

    private Context context;


    private static class AppInfoManagerInstance {
        private static final AppInfoManager INSTANCE = new AppInfoManager();
    }

    public static AppInfoManager getInstance() {
        return AppInfoManagerInstance.INSTANCE;
    }

    public void init(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }
}
