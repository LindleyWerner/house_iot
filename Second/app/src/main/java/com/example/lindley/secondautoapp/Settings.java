package com.example.lindley.secondautoapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Settings extends AppCompatActivity {
    EditText ip, port;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ip = findViewById(R.id.editText_ip);
        port = findViewById(R.id.editText_port);
        Button confirm = findViewById(R.id.button_confirm_settings);

        //get values from preferences
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        String server_ip = settings.getString("ip", "0");
        String server_port = settings.getString("port", "0");
        //set recovered data into editText
        ip.setText(server_ip);
        port.setText(server_port);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm();
            }
        });
    }

    private void confirm(){
        String server_ip = ip.getText().toString();
        String server_port = port.getText().toString();
        //Save in preferences
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("ip", server_ip);
        editor.putString("port", server_port);
        editor.apply();
        //Back to previous activity
        onBackPressed();
    }
}
