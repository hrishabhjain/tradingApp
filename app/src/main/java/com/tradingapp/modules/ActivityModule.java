package com.tradingapp.modules;

import android.app.Activity;

import com.tradingapp.scope.PerActivity;
import dagger.Module;
import dagger.Provides;

@Module
@PerActivity
public class ActivityModule {
    private final Activity activity;

    public ActivityModule(Activity activity) {
        this.activity = activity;
    }

    @Provides
    @PerActivity
    public Activity getActivity() { return this.activity; }
}
