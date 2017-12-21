package com.example.lindley.secondautoapp;

import org.json.JSONObject;
import java.io.Serializable;
import okhttp3.OkHttpClient;

/**
 * Created by lindley on 06/12/17.
 */

class Server implements Serializable {
    private OkHttpClient client;
    private EchoWebSocketListener listener;

    Server(OkHttpClient client, EchoWebSocketListener listener){
        this.client = client;
        this.listener = listener;
    }

    void sendMessage(JSONObject msg){
            listener.sendMessage(msg);
    }

    void closeConnection(){
        client.dispatcher().executorService().shutdown();
    }
}
