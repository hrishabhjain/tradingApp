package com.tradingapp.component;

import com.tradingapp.HomeViewModel;
import com.tradingapp.modules.ApplicationModule;
import com.tradingapp.modules.NetworkModule;
import com.tradingapp.scope.ApplicationScope;

import dagger.Component;

/**
 * Created by rishabhjain on 3/5/18.
 */

@ApplicationScope
@Component(modules = {ApplicationModule.class})
public interface ApplicationComponent {

    ActivitySubComponent.Builder getActivitySubComponentBuilder();

    void inject(HomeViewModel homeViewModel);
}
