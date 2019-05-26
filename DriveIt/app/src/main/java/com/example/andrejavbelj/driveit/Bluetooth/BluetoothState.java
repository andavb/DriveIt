package com.example.andrejavbelj.driveit.Bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

public class BluetoothState implements Serializable {

    public static final int STATE_NOT_CONNECTED = 0;
    public static final int STATE_CONNECTED = 1;

    public static final String filename = "btState.pref";

    public static int connectionState = STATE_NOT_CONNECTED;
    public static String deviceAddress = "98:D3:37:00:BC:5D";
    public static String deviceName = "";
    public static InputStream iputstream;

    private static BluetoothSocket socket;
    private static OutputStream outputStream;


    public static void setConnectionState(boolean connected,BluetoothDevice device) {
        if(connected)
            connectionState = STATE_CONNECTED;
        else
            connectionState = STATE_NOT_CONNECTED;

        if(device != null) {
            deviceAddress = device.getAddress();
            deviceName = device.getName();
        }

    }

    public static void saveConnectionState(Context cxt, InputStream i, OutputStream o, BluetoothSocket b) throws IOException {
        iputstream = i;
        socket = b;
        outputStream = o;
        FileOutputStream fos = cxt.openFileOutput(filename, Context.MODE_PRIVATE);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeInt(connectionState);
        oos.writeUTF(deviceAddress);
        oos.writeUTF(deviceName);
    }

    public static void loadConnectionState(Context cxt) throws IOException {
        FileInputStream fis = cxt.openFileInput(filename);
        ObjectInputStream ois = new ObjectInputStream(fis);
        connectionState = ois.readInt();
        deviceAddress = ois.readUTF();
        deviceName = ois.readUTF();
    }

    public static BluetoothDevice getDevice() {
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if(!btAdapter.isEnabled())
            btAdapter.enable();

        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();

        for(BluetoothDevice d : pairedDevices)
            if(d.getAddress().equalsIgnoreCase(deviceAddress))
                return d;

        return null;
    }
}
