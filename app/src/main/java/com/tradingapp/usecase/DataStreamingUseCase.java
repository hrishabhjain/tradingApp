package com.tradingapp.usecase;

import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Ack;
import com.github.nkzawa.socketio.client.Socket;
import com.tradingapp.OHLC;
import com.tradingapp.scope.PerActivity;


import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

/**
 * Created by rishabhjain on 3/5/18.
 */

public class DataStreamingUseCase {

    private Socket socket;
    private JSONObject jsonObject;

    @Inject
    public DataStreamingUseCase(Socket socket, JSONObject jsonObject) {
        this.socket = socket;
        this.jsonObject = jsonObject;
    }

    public Observable<OHLC> execute() {
        return Observable.create(new ObservableOnSubscribe<OHLC>() {
            @Override
            public void subscribe(final ObservableEmitter<OHLC> e) throws Exception {
                socket.on("data", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        e.onNext(OHLC.convertStringToOHLC(String.valueOf(args[0])));
                        Ack ack = (Ack) args[1];
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }

                        try {
                            ack.call(1);
                        } catch (WebsocketNotConnectedException we) {
                            we.printStackTrace();
                            if (!e.isDisposed()) {
                                e.onError(new RuntimeException("Connection lost."));
                            }
                        }

                    }
                });
                socket.on("error", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        if (!e.isDisposed()) {
                            e.onError(new RuntimeException("Some error occurred"));
                        }
                    }
                });
                socket.on("disconnect", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        if (!e.isDisposed()) {
                            e.onError(new RuntimeException("Disconnected"));
                        }
                    }
                });

                socket.connect();
                socket.emit("sub", jsonObject);
            }
        });
    }
}
