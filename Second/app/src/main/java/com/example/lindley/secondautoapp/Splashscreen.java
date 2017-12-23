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
    boolean is_splash_screen_time = true;

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }

    /**
     * Called when the activity is first created.
     */
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
        LinearLayout l = findViewById(R.id.lin_lay);
        l.clearAnimation();
        l.startAnimation(anim);

        anim = AnimationUtils.loadAnimation(this, R.anim.translate);
        anim.reset();
        ImageView iv = findViewById(R.id.splash);
        iv.clearAnimation();
        iv.startAnimation(anim);

        splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    SocketHandler.setHandler(handler);
                    is_splash_screen_time = true;
                    for(int i=0; i<Constants.TIMES_TO_TRY_CONNECT_TO_SERVER; i++) {
                        startServerConnection();

                        if(i==0) {
                            // Splash screen pause time
                            sleep(Constants.TIME_SPLASH_SCREEN);
                        }
                        getActiveSensors();

                        if(SocketHandler.getIsConnected()) {
                            break;
                        }else{
                            //if reach this part is because couldn't connect to server
                            //wait some time before try connect another time
                            sleep(Constants.TIME_WAIT_ON_TRY_ANOTHER_SERVER_CONNECTION);
                        }
                    }
                    is_splash_screen_time = false;
                    if(!SocketHandler.getIsConnected()) {
                        //Call Main activity and clear the activity stack
                        //if reach this part is because couldn't connect to server
                        call_main_activity();
                    }
                } catch (InterruptedException e) {
                    // do nothing
                }
            }
        };
        splashTread.start();
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE://show messages
                    //Transforming string to json
                    JSONObject obj;
                    try {
                        obj = new JSONObject(msg.obj.toString());
                        //Taking the first key (define from where the message came)
                        String key = obj.keys().next();
                        try {
                            switch (key) {
                                case "sensors":
                                    JSONArray array = obj.getJSONArray("sensors");
                                    Intent intent = new Intent(Splashscreen.this,
                                            MainActivity.class);

                                    //if the server send any sensor, take their information
                                    if (array.length() != 0) {
                                        //Iterating through all sensors
                                        for (int i = 0; i < array.length(); i++) {
                                            //Taking information
                                            obj = new JSONObject(array.get(i).toString());
                                            String port = obj.getString("pk");
                                            obj = obj.getJSONObject("fields");
                                            String name = obj.getString("name");
                                            Boolean is_on = obj.getBoolean("is_on");

                                            intent.putExtra("name" + Integer.toString(i), name);
                                            intent.putExtra("port" + Integer.toString(i), port);
                                            intent.putExtra("is_on" + Integer.toString(i), is_on);
                                        }
                                    }
                                    //Call another activity and clear the activity stack
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    Splashscreen.this.finish();

                                    break;
                                case "error":
                                    String error_obj = obj.getString("error");
                                    int error_id = getResources().getIdentifier(error_obj,"string",getPackageName());
                                    String error_message =  getResources().getString(error_id);
                                    Toast.makeText(getApplicationContext(), error_message, Toast.LENGTH_SHORT).show();
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

                    SocketHandler.setIsConnected(false);
                    break;
                case Constants.WEBSOCKET_FAILURE://Failure in websocket connection -> close it
                    //Toast.makeText(getApplicationContext(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    server.closeConnection();

                    SocketHandler.setIsConnected(false);
                    //startServerConnection();
                    if(!is_splash_screen_time) {
                        call_main_activity();
                    }
                    break;
                case Constants.STRING:
                    Toast.makeText(getApplicationContext(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
            }
            return true;
        }
    });

    private void startServerConnection() {
        Connection conn = new Connection(handler, this);
        server = conn.openConnection();
        SocketHandler.setSocket(server);
        SocketHandler.setIsConnected(true);
    }

    private void getActiveSensors() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("target", "on_off");
            obj.put("action", "read");


            server.sendMessage(obj);

        } catch (JSONException e) {
            handler.obtainMessage(Constants.MESSAGE, e.toString()).sendToTarget();
        }
    }

    private void call_main_activity(){
        Intent intent = new Intent(Splashscreen.this,
                MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        Splashscreen.this.finish();
    }
}