package com.example.lindley.firstautoapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

import org.json.*;

public class MainActivity extends AppCompatActivity {
    private TextView output;
    private OkHttpClient client;

    private final class EchoWebSocketListener extends WebSocketListener {
        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            //webSocket.send("{Auth-Token:secret-api-token-here}"); //wss
            JSONObject obj = new JSONObject();
            try {
                obj.put("target", "asd");
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
            output("Receiving : " + text);
        }
        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
            output("Receiving bytes : " + bytes.hex());
        }
        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            output("Closing : " + code + " / " + reason);
        }
        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            output("Error : " + t.getMessage());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button start = findViewById(R.id.start);
        output = findViewById(R.id.output);
        client = new OkHttpClient();
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start();
            }
        });
    }

    private void start() {
        Request request = new Request.Builder()
                .url("ws://192.168.1.5:8000") //change ws to wss
                //.header("Auth-Token","secret-api-token-here") //wss
                //https://stackoverflow.com/questions/46931663/using-secure-websockets-wss-with-okhttp
                .build();
        EchoWebSocketListener listener = new EchoWebSocketListener();
        client.newWebSocket(request, listener);
        client.dispatcher().executorService().shutdown();
    }

    private void output(final String txt) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                output.setText(String.format("%s\n\n%s", output.getText().toString(), txt));
            }
        });
    }

    protected void onDestroy() {
        super.onDestroy();

    }
}
