package com.example.lindley.secondautoapp;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import static java.lang.Thread.sleep;

public class NewSensor extends AppCompatActivity {
    private EditText name, port;
    private Handler handler;
    private Server server;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_sensor);

        name = findViewById(R.id.ns_name);
        port = findViewById(R.id.ns_port);
        Button create = findViewById(R.id.ns_create_btn);

        //Getting handler
        handler = SocketHandler.getHandler();
        //Getting server
        server = SocketHandler.getServer();

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                create();
            }
        });
    }

    private void create(){
        String sensorName = name.getText().toString();
        String sensorPort = port.getText().toString();

        if(sensorName.length() > 0){
            if(sensorPort.length() > 0){
                JSONObject obj = new JSONObject();
                try {
                    obj.put("target", "on_off");
                    obj.put("action", "create");
                    obj.put("name", sensorName);
                    obj.put("port", sensorPort);

                    server.sendMessage(obj);

                    //Back to previous activity
                    //onBackPressed();
                    // TODO think a better way to wait server response
                    sleep(500);
                    getActiveSensors();
                    this.finish();
                } catch (JSONException e) {
                    handler.obtainMessage(Constants.MESSAGE, e.toString()).sendToTarget();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }else{
                Toast.makeText(getApplicationContext(), R.string.empty_port, Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(getApplicationContext(), R.string.empty_name, Toast.LENGTH_SHORT).show();
        }
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
