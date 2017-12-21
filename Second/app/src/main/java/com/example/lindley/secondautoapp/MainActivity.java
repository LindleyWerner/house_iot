package com.example.lindley.secondautoapp;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.view.DragEvent;
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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

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

        if(SocketHandler.getIsConnected()){
            if (getIntent().getExtras() != null) {
                add_buttons();
                add_fab_button();
            } else {
                show_add_sensor_button();

                fab.setVisibility(View.GONE);
            }
        }else {
            toolbar.setBackgroundColor(getResources().getColor(R.color.bg_toolbar_color_disconnected));
            show_connect_button();
            fab.setVisibility(View.GONE);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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

        //int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_new_sensor) {
            if(SocketHandler.getIsConnected()){
                Intent intent = new Intent(MainActivity.this,
                        NewSensor.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }else {
                Toast.makeText(getApplicationContext(), R.string.app_disconnected, Toast.LENGTH_SHORT).show();
            }
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

    private void add_fab_button(){
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
                Intent intent = new Intent(MainActivity.this,
                        NewSensor.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });
    }

    private void add_buttons(){
        RelativeLayout rl = findViewById(R.id.layout);
        LinearLayout line = null;

        for (int i = 0; ; i++) {
            String name, port;

            name = getIntent().getExtras().getString("name" + Integer.toString(i));
            port = getIntent().getExtras().getString("port" + Integer.toString(i));

            if(name == null || port == null){
                break;
            }

            //Create a linearLayout to be a line
            if(i%2==0) {
                line = new LinearLayout(this);

                line.setOrientation(LinearLayout.HORIZONTAL);
                // The START_RANGE shift is to avoid give the same IDs to buttons and layouts
                line.setId((i/2)+Constants.START_RANGE);

                RelativeLayout.LayoutParams parent_param = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);

                if(i>1) {
                    int previous_line_id = (i/2) - 1 + Constants.START_RANGE;
                    parent_param.addRule(RelativeLayout.BELOW, previous_line_id);
                }
                rl.addView(line, parent_param);
            }

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 0.5f);

            final int port_ = Integer.parseInt(port);
            Button btn = new Button(this);
            btn.setId(port_);
            btn.setText(name);
            btn.setBackgroundColor(Color.rgb(70, 80, 90));

            //Setting button parameters
            params.setMargins(20,20,20,0);

            line.addView(btn, params);

            final Button btn1 = findViewById(port_);
            btn1.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Toast.makeText(view.getContext(),
                            "Button clicked index = " + port_, Toast.LENGTH_SHORT)
                            .show();
                }
            });

            btn1.setOnLongClickListener(new View.OnLongClickListener(){
                @Override
                public boolean onLongClick(View v) {
                    //Draw the shadow
                    ClipData data = ClipData.newPlainText("","");
                    View.DragShadowBuilder myShadowBuilder = new View.DragShadowBuilder(v);
                    v.startDrag(data, myShadowBuilder, v, 0);

                    //show the trash button
                    show_trash_button();

                    return true;
                }
            });

            //Actions when button is dragged
            btn1.setOnDragListener(dragListener);
        }
    }

    private void show_trash_button(){
        final FloatingActionButton trash = findViewById(R.id.trash);

        trash.setVisibility(View.VISIBLE);
        trash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trash.setVisibility(View.GONE);
            }
        });

        trash.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                int dragEvent = event.getAction();
                final View button = (View) event.getLocalState();
                switch (dragEvent) {
                    case DragEvent.ACTION_DROP:
                        delete_sensor(button.getId());
                        break;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        //TODO how can I change the color or highlight button?
                        trash.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                        //handler.obtainMessage(Constants.STRING, "entered").sendToTarget();
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:
                        trash.setBackgroundColor(getResources().getColor(R.color.normal_trash_color));
                        //handler.obtainMessage(Constants.STRING, "leaved").sendToTarget();
                        break;
                }
                return true;
            }
        });
    }

    View.OnDragListener dragListener = new View.OnDragListener() {
        @Override
        public boolean onDrag(View v, DragEvent event) {

            int dragEvent = event.getAction();
            if(dragEvent == DragEvent.ACTION_DRAG_ENDED){
                FloatingActionButton trash = findViewById(R.id.trash);
                trash.setVisibility(View.GONE);
            }
            return false;
        }
    };

    private void show_add_sensor_button(){
        final ImageButton add = findViewById(R.id.addButton);

        add.setVisibility(View.VISIBLE);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,
                        NewSensor.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });
    }

    private void show_connect_button(){
        final ImageButton conn = findViewById(R.id.connectButton);

        conn.setVisibility(View.VISIBLE);
        conn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startServerConnection();
            }
        });
    }

    private void startServerConnection() {
        Intent intent = new Intent(MainActivity.this,
                Splashscreen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        MainActivity.this.finish();
    }

    private void delete_sensor(int sensor_id){
        JSONObject obj = new JSONObject();
        try {
            obj.put("target", "on_off");
            obj.put("action", "delete");
            obj.put("id", sensor_id);

            server.sendMessage(obj);

        } catch (JSONException e) {
            handler.obtainMessage(Constants.MESSAGE, e.toString()).sendToTarget();
        }
    }
}
