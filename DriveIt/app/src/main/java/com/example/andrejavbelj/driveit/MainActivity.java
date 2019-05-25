package com.example.andrejavbelj.driveit;

import android.app.ActionBar;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;
import android.widget.Toolbar;

import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import de.nitri.gauge.Gauge;

public class MainActivity extends AppCompatActivity{

    private final String DEVICE_ADDRESS = "98:D3:37:00:BC:5D";
    private final UUID PORT = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    private OutputStream ouputStream;
    byte buffer[];
    private BluetoothDevice device;
    private BluetoothSocket socket;
    private OutputStream outputStream;
    private InputStream inputStream;
    Thread thread;
    boolean stopThread;
    Button naprej, nazaj,prestava, connect;
    TextView text;
    String command;

    Gauge gauge;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gauge = (Gauge) findViewById(R.id.IDgauge);
        naprej = (Button) findViewById(R.id.IDnaprej);
        nazaj =  (Button) findViewById(R.id.IDnazaj);
        prestava =  (Button) findViewById(R.id.IDprestava);
        connect =  (Button) findViewById(R.id.IDconnect);
        text =  (TextView) findViewById(R.id.IDTextview);

        setOnButtonClickListeners();
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

    public boolean BTinit()
    {
        boolean found = false;

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(bluetoothAdapter == null) //Checks if the device supports bluetooth
        {
            Toast.makeText(getApplicationContext(), "Device doesn't support bluetooth", Toast.LENGTH_SHORT).show();
        }

        if(!bluetoothAdapter.isEnabled()) //Checks if bluetooth is enabled. If not, the program will ask permission from the user to enable it
        {
            Intent enableAdapter = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableAdapter,0);

            try
            {
                Thread.sleep(1000);
            }
            catch(InterruptedException e)
            {
                e.printStackTrace();
            }
        }

        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();

        if(bondedDevices.isEmpty()) //Checks for paired bluetooth devices
        {
            Toast.makeText(getApplicationContext(), "Please pair the device first", Toast.LENGTH_SHORT).show();
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
            socket = device.createRfcommSocketToServiceRecord(PORT); //Creates a socket to handle the outgoing connection
            socket.connect();

            Toast.makeText(getApplicationContext(),
                    "Connection to bluetooth device successful", Toast.LENGTH_LONG).show();
        }
        catch(IOException e)
        {
            e.printStackTrace();
            connected = false;
        }

        if(connected)
        {
            try
            {
                outputStream = socket.getOutputStream(); //gets the output stream of the socket
                inputStream = socket.getInputStream(); //gets the output stream of the socket
                System.out.println("duububububububububbububub");
                System.out.println("duububububububububbububub");
                System.out.println("duububububububububbububub");
                System.out.println("duububububububububbububub");
                System.out.println("duububububububububbububub");
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }

        return connected;
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


    private void setOnButtonClickListeners(){
        naprej.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                command = "1";
                System.out.println(command);
                try {
                    socket.getOutputStream().write(command.getBytes());

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

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (BTinit()){
                    if(BTconnect()){
                        beginListenForData();
                    }
                }
            }
        });
    }



}
