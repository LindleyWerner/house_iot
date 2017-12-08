package com.example.lindley.firstautoapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
//import android.widget.Toast;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class MainActivity extends AppCompatActivity {
    private TextView output;
    private OkHttpClient client;

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
        EchoWebSocketListener listener = new EchoWebSocketListener(handler);
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
        handler.removeCallbacksAndMessages(null);
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch(msg.what) {
                case 1://show messages
                    output(msg.obj.toString());
                    break;
            }
            return true;
        }
    });
}
