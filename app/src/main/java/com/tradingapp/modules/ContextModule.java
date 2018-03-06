package com.tradingapp.modules;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tradingapp.ApplicationContext;
import com.tradingapp.scope.ApplicationScope;

import dagger.Module;
import dagger.Provides;

/**
 * Created by rishabhjain on 3/5/18.
 */
@Module
public class ContextModule {

    private final Context context;

    public ContextModule(Context context) {
        this.context = context.getApplicationContext();
    }

    @Provides
    @ApplicationScope
    @ApplicationContext
    public Context context() {
        return context;
    }

    @Provides
    @ApplicationScope
    public Gson gson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        return gsonBuilder.create();
    }

}


