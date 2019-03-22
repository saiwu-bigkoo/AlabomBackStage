package com.bigkoo.alabombackstage.utils;

import android.text.TextUtils;

import com.bigkoo.alabombackstage.R;
import com.bigkoo.alabombackstage.manager.AppInfoManager;

public class ValidateUtil {
    public static String account(String content){
        if(TextUtils.isEmpty(content)){
            return AppInfoManager.getInstance().getContext().getString(R.string.accountIsNull);
        }
        return null;
    }
    public static String passWord(String content){
        if(TextUtils.isEmpty(content)){
            return AppInfoManager.getInstance().getContext().getString(R.string.passWordIsNull);
        }
        return null;
    }
}
