package com.bigkoo.alabombackstage.http.factory;

import com.bigkoo.kataframework.bean.HttpResult;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * 因Alabom返回的数据不是框架定义的格式，所以在这里插值封成定义的格式
 * 2019/3/22 create by sai
 **/
final class GsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private final Gson gson;
    private final TypeAdapter<T> adapter;

    GsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }

    @Override public T convert(ResponseBody value) throws IOException {
        String resultString = "{\"code\":0,\"msg\":\"提示消息\",\"data\":"+value.string()+"}";

        try {
            T result = adapter.fromJson(resultString);
            return result;
        } finally {
            value.close();
        }
    }
}
