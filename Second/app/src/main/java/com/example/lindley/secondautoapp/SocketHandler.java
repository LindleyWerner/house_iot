package com.example.lindley.secondautoapp;

import android.os.Handler;

/**
 * Created by lindley on 19/12/17.
 */

class SocketHandler {
    private static Server server;

    static synchronized Server getServer(){
        return server;
    }

    static synchronized void setSocket(Server server){
        SocketHandler.server = server;
    }


    private static Handler handler;

    static synchronized Handler getHandler(){
        return handler;
    }

    static synchronized void setHandler(Handler handler){
        SocketHandler.handler = handler;
    }
}
