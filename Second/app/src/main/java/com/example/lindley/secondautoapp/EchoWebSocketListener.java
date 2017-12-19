package com.example.lindley.secondautoapp;

/**
 * Created by lindley on 06/12/17.
 */

import android.os.Handler;

import org.json.JSONObject;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class EchoWebSocketListener extends WebSocketListener {
    private Handler handler;
    private WebSocket webSocket;
    private static final int NORMAL_CLOSURE_STATUS = 1000;

    EchoWebSocketListener(Handler handler){
        this.handler = handler;
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        //webSocket.send("{Auth-Token:secret-api-token-here}"); //wss
        //handler.obtainMessage(Constants.MESSAGE, "Opening websocket connection").sendToTarget();
        this.webSocket = webSocket;
        // TODO What this response do?
    }
    @Override
    public void onMessage(WebSocket webSocket, String text) {
        handler.obtainMessage(Constants.MESSAGE, text).sendToTarget();
    }
    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        handler.obtainMessage(Constants.MESSAGE, bytes.hex()).sendToTarget();
    }
    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        webSocket.close(NORMAL_CLOSURE_STATUS, null);
        handler.obtainMessage(Constants.CLOSE_WEBSOCKET, "Closing : " + code + " / " + reason).sendToTarget();
    }
    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        handler.obtainMessage(Constants.WEBSOCKET_FAILURE, "Error : " + t.getMessage()).sendToTarget();
    }

    public void sendMessage(JSONObject obj){
        webSocket.send(obj.toString());
    }
}
