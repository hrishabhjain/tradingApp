package com.tradingapp.component;

import com.tradingapp.HomeActivity;
import com.tradingapp.modules.ActivityModule;
import com.tradingapp.scope.PerActivity;

import dagger.Subcomponent;

/**
 * Created by rishabhjain on 3/5/18.
 */
@PerActivity
@Subcomponent(modules = ActivityModule.class)
public interface ActivitySubComponent {

    void inject(HomeActivity homeActivity);

    @Subcomponent.Builder
    interface Builder {
        Builder activityModule(ActivityModule module);
        ActivitySubComponent build();
    }

}

