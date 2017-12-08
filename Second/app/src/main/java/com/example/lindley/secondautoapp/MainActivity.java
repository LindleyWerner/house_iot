package com.example.lindley.secondautoapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Server server;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageButton onoff = findViewById(R.id.onoff);
        ImageButton favourites = findViewById(R.id.favourites);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO Open activity to create objects
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        server = openConnection();


        onoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onoff();
            }
        });

        favourites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favourites();
            }
        });
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
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(getApplicationContext(), "settings", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
            Toast.makeText(getApplicationContext(), "camera", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

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

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch(msg.what) {
                case Constants.MESSAGE://show messages
                    Toast.makeText(getApplicationContext(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
                case Constants.CLOSE_WEBSOCKET://Close websocket
                    Toast.makeText(getApplicationContext(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    server.closeConnection();
                    break;
                case Constants.WEBSOCKET_FAILURE://Failure in websocket connection -> close it
                    Toast.makeText(getApplicationContext(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    // TODO close connection and open it again
                    break;
            }
            return true;
        }
    });

    //Buttons functions/actions
    private void onoff(){
        // TODO Call another activity and show options
        handler.obtainMessage(1, "On off").sendToTarget();
        JSONObject obj = new JSONObject();
        try {
            obj.put("target", "on_off");
            obj.put("action", "create");
            obj.put("name", "Luz quarto");
            obj.put("port", "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        server.sendMessage(obj);
    }

    private void favourites(){
        handler.obtainMessage(1, "Favourites").sendToTarget();
    }

    private Server openConnection(){
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("ws://192.168.1.2:8000") //change ws to wss
                //.header("Auth-Token","secret-api-token-here") //wss
                //https://stackoverflow.com/questions/46931663/using-secure-websockets-wss-with-okhttp
                .build();
        EchoWebSocketListener listener = new EchoWebSocketListener(handler);
        //calls onOpen
        client.newWebSocket(request, listener);

        //Just to return 2 objects
        Server newServer = new Server(client, listener);

        return newServer;
    }
}
