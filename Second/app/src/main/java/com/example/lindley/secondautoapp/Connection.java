package com.example.lindley.secondautoapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by lindley on 12/12/17.
 */

public class Connection{
    Handler handler;
    Context context;
    Connection(Handler handler, Context context){
        this.handler = handler;
        this.context = context;
    }

    public Server openConnection(){
        OkHttpClient client = new OkHttpClient();

        //get values from preferences
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        String server_ip, server_port;
        if (settings.contains("ip")) {
            server_ip = settings.getString("ip", "0");
        }else{
            // TODO do not connect with server (Create a default IP?)
            server_ip = "192.168.1.8";
        }
        if (settings.contains("port")) {
            server_port = settings.getString("port", "0");
        }else{
            // TODO do not connect with server (Create a default Port?)
            server_port = "8000";
        }

        String adress = server_ip + ":" + server_port + "/";

        Request request = new Request.Builder()
                .url("ws://" + adress) //change ws to wss
                //.header("Auth-Token","secret-api-token-here") //wss
                //https://stackoverflow.com/questions/46931663/using-secure-websockets-wss-with-okhttp
                .build();
        EchoWebSocketListener listener = new EchoWebSocketListener(handler);
        //calls onOpen
        client.newWebSocket(request, listener);

        //Just to return 2 objects
        Server newServer = new Server(client, listener);

        return newServer;
    }
}
