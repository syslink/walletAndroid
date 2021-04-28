package com.alphawallet.app.util;


import android.annotation.SuppressLint;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CoinExchangeRateUtil extends java.util.Observable implements Callback {
    private static final String TAG = "CoinExchangeRateUtil";
    private static CoinExchangeRateUtil sInstance;
    private OkHttpClient mOkHttpClient;
    private HashMap<String, String> mExchangeMap;
    private Map<String, String> mCoinAddressAndIdMap;

    public static CoinExchangeRateUtil getInstance() {
        if (sInstance == null) {
            return sInstance = new CoinExchangeRateUtil();
        } else {
            return sInstance;
        }
    }

    public CoinExchangeRateUtil setCoinIds(Map<String, String> ids) {
        this.mCoinAddressAndIdMap = ids;
        Log.d(TAG, "setCoinIds: mCoinAddressAndIdMap = " + mCoinAddressAndIdMap.size());
        return sInstance;
    }

    public CoinExchangeRateUtil setOkhttpClient(OkHttpClient client) {
        this.mOkHttpClient = client;
        return sInstance;
    }

    public String getCnyByAddress(String address) {
        if (mExchangeMap != null) {
            return mExchangeMap.get(address) == null ? " ~ " : mExchangeMap.get(address).split("_")[0];
        }
        return " ~ ";
    }

    public String getUsdByAddress(String address) {
        if (mExchangeMap != null) {
            return mExchangeMap.get(address) == null ? " ~ " : mExchangeMap.get(address).split("_")[1];
        }
        return " ~ ";
    }

    @SuppressLint("CheckResult")
    public void CheckExchangeRate() {
        if (null != mCoinAddressAndIdMap && mCoinAddressAndIdMap.size() > 0) {
            String url = getUrl();
            //key:coin address  values:cny_usd
            mExchangeMap = new HashMap<>();
            Request request = new Request.Builder().url(url)
                    .build();
            mOkHttpClient.newCall(request).enqueue(this);
        }
    }


    private String getUrl() {
        StringBuilder coinIds = new StringBuilder();
        coinIds.append("?ids=");
        for (String value : mCoinAddressAndIdMap.values()) {
            if (!value.isEmpty()) {
                coinIds.append(value).append(",");
            }
        }
        coinIds.deleteCharAt(coinIds.length() - 1);
        coinIds.append("&");
        coinIds.append("vs_currencies=");
        coinIds.append("cny,");
        coinIds.append("usd");
        return "https://api.coingecko.com/api/v3/simple/price" + coinIds.toString();
    }

    @Override
    public void onFailure(@NotNull Call call, @NotNull IOException e) {

    }

    @Override
    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
        String string = response.body().string();
        JSONObject jsonObject = JSON.parseObject(string);
        //mCoinAddressAndIdMap  key-->address   value-->coinId
        for (Map.Entry<String, String> entry : mCoinAddressAndIdMap.entrySet()) {
            String value = entry.getValue();
            Object obj = jsonObject.get(value);
            if (obj != null) {
                JSONObject object = JSON.parseObject(obj.toString());
                if (object != null) {
                    mExchangeMap.put(entry.getKey(), object.getString("cny") + "_" + object.getString("usd"));
                }
            }


        }
        setChanged();
        notifyObservers();
    }
}
