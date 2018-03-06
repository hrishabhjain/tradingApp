package com.tradingapp.modules;

import org.json.JSONException;
import org.json.JSONObject;

import dagger.Module;
import dagger.Provides;

/**
 * Created by rishabhjain on 3/5/18.
 */

@Module(includes = NetworkModule.class)
public class ApplicationModule {

    @Provides
    public JSONObject jsonObject() {
        try {
            return new JSONObject().put("state", true);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

}
