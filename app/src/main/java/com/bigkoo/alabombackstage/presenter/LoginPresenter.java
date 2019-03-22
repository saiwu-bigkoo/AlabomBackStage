package com.bigkoo.alabombackstage.presenter;

import android.util.Base64;

import com.bigkoo.alabombackstage.TokenBean;
import com.bigkoo.alabombackstage.constants.HttpKeyConstant;
import com.bigkoo.alabombackstage.http.HttpServiceGenerator;
import com.bigkoo.alabombackstage.http.service.HttpService;
import com.bigkoo.kataframework.bean.HttpResult;
import com.bigkoo.kataframework.mvppresenter.BaseDetailPresenter;

import java.util.HashMap;

import io.reactivex.Observable;

public class LoginPresenter extends BaseDetailPresenter {
    private String account;
    private String passWord;
    @Override
    public Observable<HttpResult<TokenBean>> onLoadDataHttpRequest() {
        String ap = account+":"+passWord;
        String authorization = "Basic "+Base64.encodeToString(ap.getBytes(), Base64.DEFAULT);

        HashMap<String, Object> params = new HashMap<>();
        params.put(HttpKeyConstant.USERNAME,account);
        params.put(HttpKeyConstant.PASSWORD,passWord);
        params.put(HttpKeyConstant.GRANT_TYPE,HttpKeyConstant.PASSWORD);
        return HttpServiceGenerator.create(HttpService.class).login(authorization,params);
    }

    public void onLogin(String account,String passWord){
        this.account = account;
        this.passWord = passWord;

        onLoadData();
    }
}
