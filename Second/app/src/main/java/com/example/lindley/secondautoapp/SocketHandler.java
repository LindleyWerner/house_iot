package com.example.lindley.secondautoapp;

import android.os.Handler;

/**
 * Created by lindley on 19/12/17.
 */

public class SocketHandler {
    private static Server server;

    public static synchronized Server getServer(){
        return server;
    }

    public static synchronized void setSocket(Server server){
        SocketHandler.server = server;
    }


    private static Handler handler;

    public static synchronized Handler getHandler(){
        return handler;
    }

    public static synchronized void setHandler(Handler handler){
        SocketHandler.handler = handler;
    }
}
