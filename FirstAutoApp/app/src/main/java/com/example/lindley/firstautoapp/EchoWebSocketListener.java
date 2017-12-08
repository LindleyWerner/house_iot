package com.example.lindley.firstautoapp;

import android.os.Handler;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class EchoWebSocketListener extends WebSocketListener {
    private Handler handler;
    private static final int NORMAL_CLOSURE_STATUS = 1000;

    EchoWebSocketListener(Handler handler){
        this.handler = handler;
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        //webSocket.send("{Auth-Token:secret-api-token-here}"); //wss
        JSONObject obj = new JSONObject();
        try {
            obj.put("target", "on_off");
            obj.put("action", "create");
            obj.put("name", "Luz quarto");
            obj.put("port", "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        webSocket.send(obj.toString());
        obj.remove("name");
        obj.remove("port");
        try {
            obj.put("name", "Luz cozinha");
            obj.put("port", "2");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        webSocket.send(obj.toString());
        JSONObject obj2 = new JSONObject();
        try {
            obj2.put("target", "on_off");
            obj2.put("action", "read");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        webSocket.send(obj2.toString());
        JSONObject obj3 = new JSONObject();
        try {
            obj3.put("target", "logout");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        webSocket.send(obj3.toString());
    }
    @Override
    public void onMessage(WebSocket webSocket, String text) {
        handler.obtainMessage(1, "Receiving : " + text).sendToTarget();
    }
    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        handler.obtainMessage(1, "Receiving bytes : " + bytes.hex()).sendToTarget();
    }
    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        webSocket.close(NORMAL_CLOSURE_STATUS, null);
        handler.obtainMessage(1, "Closing : " + code + " / " + reason).sendToTarget();
    }
    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        handler.obtainMessage(1, "Error : " + t.getMessage()).sendToTarget();
    }
}
