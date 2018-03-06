package com.tradingapp.modules;

import android.content.Context;

import com.github.nkzawa.socketio.client.Manager;
import com.github.nkzawa.socketio.client.Socket;
import com.tradingapp.ApplicationContext;
import com.tradingapp.scope.ApplicationScope;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by rishabhjain on 3/5/18.
 */

@Module(includes = ContextModule.class)
public class NetworkModule {

    @Provides
    @ApplicationScope
    public Cache cache(File cacheFile) {
        return new Cache(cacheFile, 10 * 1000 * 1000);
    }

    @Provides
    @ApplicationScope
    public File cacheFile(@ApplicationContext Context context) {
        return new File(context.getCacheDir(), "okhttp_cache");
    }

    @Provides
    @ApplicationScope
    public OkHttpClient okHttpClient(Cache cache) {
        return new OkHttpClient.Builder()
                .cache(cache)
                .build();
    }

    @Provides
    @ApplicationScope
    public Request request() {
        return new Request.Builder()
//                .header("Accept-Encoding", "gzip")
                .url("http://kaboom.rksv.net/api/historical?interval=1")
                .build();

    }

    @Provides
    @ApplicationScope
    public URI uri() {
        try {
            return new URI("http://kaboom.rksv.net");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Provides
    @ApplicationScope
    public Manager manager(URI uri) {
        return new Manager(uri);
    }

    @Provides
    @ApplicationScope
    public Socket socket(Manager manager) {
        return manager.socket("/watch");
    }
}

