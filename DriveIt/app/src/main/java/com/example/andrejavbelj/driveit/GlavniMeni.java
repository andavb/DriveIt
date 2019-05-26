package com.example.andrejavbelj.driveit;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import de.nitri.gauge.Gauge;

public class GlavniMeni extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final String DEVICE_ADDRESS = "98:D3:37:00:BC:5D";
    private final UUID PORT = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    private byte buffer[];
    private BluetoothDevice device;
    private BluetoothSocket socket;
    private OutputStream outputStream;
    private InputStream inputStream;
    private boolean stopThread, con;
    private ImageView naprej, nazaj;
    private TextView text, prestava, automatic;
    private String command;
    private Gauge gauge;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glavni_meni);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        gauge = (Gauge) findViewById(R.id.IDgauge);
        naprej = (ImageView) findViewById(R.id.IDnaprej);
        nazaj =  (ImageView) findViewById(R.id.IDnazaj);
        prestava =  (TextView) findViewById(R.id.IDprestava);
        text =  (TextView) findViewById(R.id.IDTextview);
        automatic =  (TextView) findViewById(R.id.ID_automatic);

        if (savedInstanceState != null){

           String sock = savedInstanceState.getString("device", "");
           Gson json = new Gson();

           device =(BluetoothDevice) json.fromJson(sock, BluetoothDevice.class);


            socket = null;
            inputStream = null;
            outputStream = null;
            stopThread = false;
            con = false;

           if(BTconnect()) {
               con = true;
               fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
               beginListenForData();
           }

         }

        setOnButtonClickListeners();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopThread = true;
        try{
            socket.close();
        }
        catch (Exception e){
            System.out.println(e);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.glavni_meni, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {
            Intent bluetoothPicker = new Intent("android.bluetooth.devicepicker.action.LAUNCH");
            startActivity(bluetoothPicker);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public boolean BTinit()
    {
        boolean found = false;

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(bluetoothAdapter == null) //Ali naprava podpira bluetooth
        {
            Toast.makeText(this, getResources().getText(R.string.naprava_nepodpira_b), Toast.LENGTH_SHORT).show();
        }

        if(!bluetoothAdapter.isEnabled()) //Ali je bluetooth prizgan
        {
            Toast.makeText(this, getResources().getText(R.string.prizgi_bluetooth), Toast.LENGTH_SHORT).show();
        }

        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();

        if(bondedDevices.isEmpty()) //Pogleda za povezane naprave
        {
            Toast.makeText(this, getResources().getText(R.string.povezi_bluetooth), Toast.LENGTH_SHORT).show();

            Intent pairIntent = new Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
            startActivityForResult(pairIntent, 0);
        }
        else
        {
            for(BluetoothDevice iterator : bondedDevices)
            {
                if(iterator.getAddress().equals(DEVICE_ADDRESS))
                {
                    device = iterator;
                    found = true;
                    break;
                }
            }
        }

        return found;
    }

    public boolean BTconnect()
    {
        boolean connected = true;

        try
        {
            System.out.println(socket);
            System.out.println(device);

            socket = device.createRfcommSocketToServiceRecord(PORT); //socket za output povezavo

            socket.connect();

            Toast.makeText(this,
                    getResources().getText(R.string.uspesno_povezano), Toast.LENGTH_LONG).show();
        }
        catch(IOException e)
        {
            e.printStackTrace();
            connected = false;

            Toast.makeText(this,
                    getResources().getText(R.string.ni_uspelo), Toast.LENGTH_LONG).show();
        }

        if(connected)
        {
            try
            {
                outputStream = socket.getOutputStream();
                inputStream = socket.getInputStream();
                con = true;
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }

        return connected;
    }

    void beginListenForData()
    {
        final Handler handler = new Handler();
        stopThread = false;
        buffer = new byte[1024];
        final byte delimiter = 10;
        Thread thread  = new Thread(new Runnable()
        {
            public void run()
            {
                int readBufferPosition = 0;
                while(!Thread.currentThread().isInterrupted() && !stopThread)
                {
                    try {
                        int bytesAvailable = inputStream.available();

                        if (bytesAvailable > 0) {
                            byte[] packetBytes = new byte[bytesAvailable];
                            inputStream.read(packetBytes);
                            for (int i = 0; i < bytesAvailable; i++) {
                                byte b = packetBytes[i];
                                if (b == delimiter) {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(buffer, 0,
                                            encodedBytes, 0,
                                            encodedBytes.length);
                                    final String data = new String(encodedBytes, "UTF-8");
                                    readBufferPosition = 0;

                                    final int result = strToInt(data)/10;
                                    /*Integer result = Integer.valueOf(data);
                                    if( result > 500){
                                        naprej.performClick();
                                    };*/


                                    handler.post(new Runnable() {
                                        public void run() {
                                            text.setText(data);
                                            float rez = result/(float)3.3;
                                            gauge.moveToValue(Math.round(rez/5)*5);
                                            if (result > 700){
                                                //naprej.performClick(); gre naprej
                                            }
                                        }
                                    });
                                } else {
                                    buffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    } catch (IOException ex) {
                        stopThread = true;
                    }
                }
            }
        });

        thread.start();
    }


    public static int strToInt(String str ){
        int i = 0;
        int num = 0;
        boolean isNeg = false;

        //Check for negative sign; if it's there, set the isNeg flag
        if (str.charAt(0) == '-') {
            isNeg = true;
            i = 1;
        }

        //Process each character of the string;
        while( i < str.length()) {
            num *= 10;
            num += str.charAt(i++) - '0'; //Minus the ASCII code of '0' to get the value of the charAt(i++).
        }

        if (isNeg)
            num = -num;
        return num;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();

        if(bondedDevices.isEmpty()) //Checks for paired bluetooth devices
        {
            Toast.makeText(getApplicationContext(), "Please pair the device first", Toast.LENGTH_SHORT).show();

            Intent i = new Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
            startActivityForResult(i, 0);
        }
        else
        {
            for(BluetoothDevice iterator : bondedDevices)
            {
                if(iterator.getAddress().equals(DEVICE_ADDRESS))
                {
                    device = iterator;
                    if(BTconnect()){
                        fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                        beginListenForData();
                    }
                    break;
                }
            }
        }

    }


    private void setOnButtonClickListeners(){
        naprej.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                command = "1";
                System.out.println(command);
                try {
                    socket.getOutputStream().write(command.getBytes());
                    automatic.setText("R");
                }
                catch (IOException e){
                    e.printStackTrace();
                }
            }
        });

        nazaj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                command = "2";

                try {
                    socket.getOutputStream().write(command.getBytes());
                    automatic.setText("D");

                }
                catch (IOException e){
                    e.printStackTrace();
                }
            }
        });

        prestava.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                command = "3";

                try {
                    socket.getOutputStream().write(command.getBytes());
                }
                catch (IOException e){
                    e.printStackTrace();
                }
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (con == false){
                    Toast.makeText(getApplicationContext(), getResources().getText(R.string.povezujem), Toast.LENGTH_SHORT).show();
                    if (BTinit()){
                        if(BTconnect()){
                            fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
                            beginListenForData();
                        }
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(), getResources().getText(R.string.ze_povezavno), Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Gson json = new Gson();
        //String j = json.toJson(socket, BluetoothSocket.class);

        String deviceB = json.toJson(device, BluetoothDevice.class);

        outState.putString("device", deviceB);


    }

}
