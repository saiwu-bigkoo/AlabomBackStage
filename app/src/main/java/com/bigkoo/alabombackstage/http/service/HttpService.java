package com.bigkoo.alabombackstage.http.service;

import com.bigkoo.alabombackstage.TokenBean;
import com.bigkoo.alabombackstage.constants.HttpUrlConstant;
import com.bigkoo.kataframework.bean.HttpResult;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * @Description：描述信息
 * @Author：Sai
 * @Date：2019/3/15 11:36
 */
public interface HttpService {
    //获取token
    @FormUrlEncoded
    @POST(HttpUrlConstant.APPURL + "oauth/token/")
    Observable<HttpResult<TokenBean>> login(@Header("Authorization")String authorization, @FieldMap Map<String, Object> map);

}
