package com.tradingapp;

import android.app.Activity;
import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.util.Log;

import com.tradingapp.component.ApplicationComponent;
import com.tradingapp.scope.PerActivity;
import com.tradingapp.usecase.DataStreamingUseCase;
import com.tradingapp.usecase.RestApiUseCase;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by rishabhjain on 3/5/18.
 */

@PerActivity
public class HomeViewModel extends BaseObservable {

    @Inject
    public DataStreamingUseCase dataStreamingUseCase;

    @Inject
    public RestApiUseCase restApiUseCase;

    private String liveRate;
    private CallBack callBack;

    @Inject
    public HomeViewModel() {

    }

    public void onLoad(Activity context) {
        ApplicationComponent component = TradingApplication.get(context).getComponent();
        component.inject(this);

        dataStreamingUseCase.execute()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<OHLC>() {
                    @Override
                    public void onNext(OHLC ohlc) {
                        setLiveRate("$" + ohlc.getHigh());
//                        callBack.addEntry(ohlc);

                    }

                    @Override
                    public void onError(Throwable e) {
                        callBack.setError(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
        restApiUseCase.execute()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<List<OHLC>>() {
                    @Override
                    public void onNext(List<OHLC> ohlcList) {
                        callBack.setData(ohlcList);
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e instanceof IOException) {
                            callBack.setError("Please check your internet connection.");
                        } else {
                            callBack.setError(e.getMessage());
                        }

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    @Bindable
    public String getLiveRate() {
        return liveRate;
    }

    public void setLiveRate(String liveRate) {
        this.liveRate = liveRate;
        notifyPropertyChanged(BR.liveRate);
    }

    public interface CallBack {
        void setData(List<OHLC> ohlcList);
        void setError(String error);
    }
}
