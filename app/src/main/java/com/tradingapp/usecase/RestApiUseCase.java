package com.tradingapp.usecase;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.tradingapp.OHLC;
import com.tradingapp.scope.PerActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by rishabhjain on 3/5/18.
 */

public class RestApiUseCase {

    private OkHttpClient okHttpClient;
    private Request request;

    @Inject
    public RestApiUseCase(OkHttpClient okHttpClient, Request request) {
        this.okHttpClient = okHttpClient;
        this.request = request;
    }

    public Observable<List<OHLC>> execute() {
        return Observable.create(new ObservableOnSubscribe<List<OHLC>>() {
            @Override
            public void subscribe(ObservableEmitter<List<OHLC>> e) throws IOException {
                Response response = okHttpClient.newCall(request).execute();
                String responseString = response.body().string();
                Log.e("response", responseString);

                if (response.code() == 200) {
                    JsonParser parser = new JsonParser();
                    JsonElement tradeElement = parser.parse(responseString);
                    JsonArray trade = tradeElement.getAsJsonArray();

                    List<OHLC> ohlcList = new ArrayList<>();
                    for (int i = 0; i < trade.size(); i++) {
                        ohlcList.add(OHLC.convertStringToOHLC(String.valueOf(trade.get(i))));
                    }

                    e.onNext(ohlcList);
                    e.onComplete();
                } else {
                    e.onError(new RuntimeException("Could not connect to the server."));
                }
            }
        });
    }
}
