package com.example.lindley.secondautoapp;

import org.json.JSONObject;

import okhttp3.OkHttpClient;

/**
 * Created by lindley on 06/12/17.
 */

public class Server {
    private OkHttpClient client;
    private EchoWebSocketListener listener;

    Server(OkHttpClient client, EchoWebSocketListener listener){
        this.client = client;
        this.listener = listener;
    }

    public void sendMessage(JSONObject msg){
        listener.sendMessage(msg);
    }

    public void closeConnection(){
        client.dispatcher().executorService().shutdown();
    }
}
