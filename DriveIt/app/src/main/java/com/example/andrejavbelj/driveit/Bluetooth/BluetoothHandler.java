package com.example.andrejavbelj.driveit.Bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import com.example.andrejavbelj.driveit.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import de.nitri.gauge.Gauge;

public class BluetoothHandler {

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
    TextView text;

    public Gauge gauge;
    private Activity activity;
    private Context context;

    public BluetoothHandler(Activity a) {
        activity = a;
        context = a.getBaseContext();

        gauge = (Gauge) this.activity.findViewById(R.id.IDgauge);
    }

    public boolean BTinit()
    {
        boolean found = false;

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(bluetoothAdapter == null) //Ali naprava podpira bluetooth
        {
            Toast.makeText(context, context.getResources().getText(R.string.naprava_nepodpira_b), Toast.LENGTH_SHORT).show();
        }

        if(!bluetoothAdapter.isEnabled()) //Ali je bluetooth prizgan
        {
            Toast.makeText(context, context.getResources().getText(R.string.prizgi_bluetooth), Toast.LENGTH_SHORT).show();
        }

        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();

        if(bondedDevices.isEmpty()) //Pogleda za povezane naprave
        {
            Toast.makeText(context, context.getResources().getText(R.string.povezi_bluetooth), Toast.LENGTH_SHORT).show();

            Intent pairIntent = new Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
            activity.startActivityForResult(pairIntent, 0);
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
            socket = device.createRfcommSocketToServiceRecord(PORT); //socket za output povezavo
            socket.connect();

            Toast.makeText(context,
                    context.getResources().getText(R.string.uspesno_povezano), Toast.LENGTH_LONG).show();
        }
        catch(IOException e)
        {
            e.printStackTrace();
            connected = false;

            Toast.makeText(context,
                    context.getResources().getText(R.string.ni_uspelo), Toast.LENGTH_LONG).show();
        }

        if(connected)
        {
            try
            {
                outputStream = socket.getOutputStream();
                inputStream = socket.getInputStream();
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }

        return connected;
    }

    public void beginListenForData()
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

}
