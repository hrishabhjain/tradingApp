package com.tradingapp;

import android.app.Activity;
import android.app.Application;

import com.tradingapp.component.ApplicationComponent;
import com.tradingapp.component.DaggerApplicationComponent;
import com.tradingapp.modules.ContextModule;

/**
 * Created by rishabhjain on 3/5/18.
 */

public class TradingApplication extends Application {

    private ApplicationComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        component = DaggerApplicationComponent.builder()
                .contextModule(new ContextModule(this)).build();
    }

    public static TradingApplication get(Activity activity) {
        return (TradingApplication) activity.getApplication();
    }

    public ApplicationComponent getComponent() {
        return component;
    }
}
