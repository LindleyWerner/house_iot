package com.example.lindley.secondautoapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Server server;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Doing this, I can use server and handler in another activities as a static object
        handler = SocketHandler.getHandler();
        server = SocketHandler.getServer();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO Open activity to create objects
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
                Intent intent = new Intent(MainActivity.this,
                        NewSensor.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Getting intent
        Intent intent = getIntent();


        for (int i = 0; ; i++) {
            String name, port;

            name = getIntent().getExtras().getString("name" + Integer.toString(i));
            port = getIntent().getExtras().getString("port" + Integer.toString(i));

            if(name == null || port == null){
                break;
            }

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(140, 123);

            Button btn = new Button(this);
            final int port_ = Integer.parseInt(port);
            btn.setId(port_);
            btn.setText(name);
            btn.setBackgroundColor(Color.rgb(70, 80, 90));
            LinearLayout ll = findViewById(R.id.layout);
            ll.addView(btn, params);
            Button btn1 = findViewById(port_);
            btn1.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Toast.makeText(view.getContext(),
                            "Button clicked index = " + port_, Toast.LENGTH_SHORT)
                            .show();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu); //I deleted settings
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_new_sensor) {
            //Toast.makeText(getApplicationContext(), "New sensor", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this,
                    NewSensor.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            //intent.putExtra("MESSENGER", (Serializable) handler);
            startActivity(intent);
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(MainActivity.this,
                    Settings.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        } else if (id == R.id.nav_share) {
            Toast.makeText(getApplicationContext(), "Share", Toast.LENGTH_SHORT).show();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /*
    //TODO How to properly turn the screen? (Fragments)
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        try {
            savedInstanceState.putAll(savedInstanceState);
        }catch (Exception e){
            Toast.makeText(this, "Erro ao girar tela", Toast.LENGTH_SHORT).show();
        }
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }*/

    @Override
    protected void onDestroy(){
        //Close everything here
        super.onDestroy();
        server.closeConnection();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onStop(){//called when the activity is hidden, onCreate is called when return
        super.onStop();
        server.closeConnection();
    }
}
