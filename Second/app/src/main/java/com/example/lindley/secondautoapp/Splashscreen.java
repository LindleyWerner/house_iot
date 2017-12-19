package com.example.lindley.secondautoapp;

/**
 * Created by lindley on 12/12/17.
 */

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Splashscreen extends Activity {
    Server server;

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }
    /** Called when the activity is first created. */
    Thread splashTread;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        StartAnimations();
    }
    private void StartAnimations() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
        anim.reset();
        LinearLayout l=(LinearLayout) findViewById(R.id.lin_lay);
        l.clearAnimation();
        l.startAnimation(anim);

        anim = AnimationUtils.loadAnimation(this, R.anim.translate);
        anim.reset();
        ImageView iv = (ImageView) findViewById(R.id.splash);
        iv.clearAnimation();
        iv.startAnimation(anim);

        splashTread = new Thread() {
            @Override
            public void run() {
            try {
                startServerConnection();
                SocketHandler.setHandler(handler);

                // Splash screen pause time
                sleep(3000);

                getActiveSensors();

                // Splash screen pause time
                //sleep(2000);

                /*Intent intent = new Intent(Splashscreen.this,
                        MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);*/
            } catch (InterruptedException e) {
                // do nothing
            } finally {
                //Splashscreen.this.finish();

            }

            }
        };
        splashTread.start();

    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch(msg.what) {
                case Constants.MESSAGE://show messages
                    //Transforming string to json
                    JSONObject obj = null;
                    try {
                        obj = new JSONObject(msg.obj.toString());
                        //Taking the first key (define from where the message came)
                        String key = obj.keys().next();
                        try {
                            switch (key){
                                case "sensors":
                                    JSONArray array;
                                    Intent intent = new Intent(Splashscreen.this,
                                            MainActivity.class);

                                    array = obj.getJSONArray("sensors");
                                    //Iterating through all sensors
                                    for(int i=0; i<array.length(); i++){
                                        //Taking information
                                        obj = new JSONObject(array.get(i).toString());
                                        String port = obj.getString("pk");
                                        obj = obj.getJSONObject("fields");
                                        String name = obj.getString("name");

                                        intent.putExtra("name" + Integer.toString(i), name);
                                        intent.putExtra("port" + Integer.toString(i), port);
                                    }
                                    //Call another activity and clear the activity stack
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION|Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    Splashscreen.this.finish();
                                    break;
                            }
                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    }

                    break;
                case Constants.CLOSE_WEBSOCKET://Close websocket
                    Toast.makeText(getApplicationContext(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    server.closeConnection();
                    break;
                case Constants.WEBSOCKET_FAILURE://Failure in websocket connection -> close it
                    Toast.makeText(getApplicationContext(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    // TODO close connection and open it again (is it ok?)
                    server.closeConnection();
                    startServerConnection();
                    break;
            }
            return true;
        }
    });

    private void startServerConnection(){
        Connection conn = new Connection(handler, this);
        server = conn.openConnection();
        SocketHandler.setSocket(server);
    }

    private void getActiveSensors(){
        JSONObject obj = new JSONObject();
        try {
            obj.put("target", "on_off");
            obj.put("action", "read");

            server.sendMessage(obj);
        } catch (JSONException e) {
            handler.obtainMessage(Constants.MESSAGE, e.toString()).sendToTarget();
        }
    }
}