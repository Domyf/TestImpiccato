/*
 *
 * Created by HDev (Antonio Siciliani, Cosimo Mollica, Daniele Monaca, Domenico Ferraro, Marco De Caria)
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 10/12/16 19.19
 */

package com.hdevteam.impiccato.Gioco.Multiplayer.Bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.hdevteam.impiccato.HomeActivity;
import com.hdevteam.impiccato.R;

import java.util.ArrayList;

public class BTDeviceListActivity extends Activity {

    private ListView listView;
    private ArrayList<String> deviceList = new ArrayList<String>();
    private BluetoothAdapter bluetoothAdapter;
    private ThreadDiscovery threadDiscovery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_btdevice_list);

        threadDiscovery = new ThreadDiscovery(this);

        listView = (ListView) findViewById(R.id.deviceListView);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        startDiscovery();


        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String info = ((TextView) view).getText().toString();
                Log.i("BT", info);
                String address = info.substring(info.length() - 17);
                Intent intent = new Intent(BTDeviceListActivity.this, BTGameActivity.class);
                intent.putExtra("device_address", address);
                intent.putExtra("device_mode", "client");
                setResult(Activity.RESULT_OK, intent);
                startActivity(intent);
                finish();
            }
        });

    }
    /**Fa partire il thread che attiva la ricerca di dispositivi bluetooth ogni 12 secondi*/
    private void startDiscovery(){
        /*threadDiscovery = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    while(!Thread.currentThread().isInterrupted()){
                        bluetoothAdapter.startDiscovery();
                        Log.i("threadDiscovery", "discovery started");
                        Thread.sleep(3000);
                        deviceList.clear();
                        listView.setAdapter(new ArrayAdapter<String>(getApplicationContext(), R.layout.device_name, deviceList));
                    }
                }catch(InterruptedException ex){
                    return;
                }
            }
        });*/
        threadDiscovery.start();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        if(bluetoothAdapter != null) {
            bluetoothAdapter.cancelDiscovery();
            bluetoothAdapter.cancelDiscovery();
        }
        if(threadDiscovery != null){
            threadDiscovery.interrupt();
        }
        super.onDestroy();
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (!checkPresence(device.getAddress()))
                    deviceList.add(device.getName().toUpperCase() + "\n" + device.getAddress().toUpperCase());
                Log.i("BroadCastReciver", device.getName() + "\n" + device.getAddress());
                listView.setAdapter(new ArrayAdapter<String>(context, R.layout.device_name, deviceList));
            }
        }
    };

    @Override
    public void onBackPressed() {
        if(threadDiscovery != null){
            threadDiscovery.interrupt();
        }
        startActivity(new Intent(this, HomeActivity.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        this.finish();
    }

    public void updateListView() {
        listView.setAdapter(new ArrayAdapter<String>(this, R.layout.device_name, deviceList));
    }

    public boolean checkPresence(String address) {
        for (int i=0; i<deviceList.size(); i++) {
            String riga = deviceList.get(i);
            String addr = riga.substring(riga.length() - 17);
            if (address.equalsIgnoreCase(addr))
                return true;
        }
        return false;
    }

    private class ThreadDiscovery extends Thread {

        private Context context;

        public ThreadDiscovery(Context context) {
            this.context = context;
        }

        @Override
        public void run() {
            try{
                while(!Thread.currentThread().isInterrupted()){
                    int before = deviceList.size();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            deviceList.clear();
                        }
                    });
                    bluetoothAdapter.startDiscovery();
                    Log.i("threadDiscovery", "Sto cercando dispositivi vicini...");
                    Thread.sleep(5000);
                    if(before != deviceList.size()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateListView();
                            }
                        });
                    }
                    bluetoothAdapter.cancelDiscovery();
                }
            }catch(InterruptedException ex){
                return;
            }
        }
    }
    /*//Modulo BT del telefono.
    private BluetoothAdapter btAdapter;
    //Lista dei dispositivi bt trovati.
    private ArrayAdapter<String> deviceList;
    //ListView che visualizza i dispositivi bt.
    ListView deviceListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_btdevice_list);
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        deviceListView = (ListView) findViewById(R.id.deviceListView);
        ArrayAdapter<String> pairedDeviceList = new ArrayAdapter<String>(this, R.layout.device_name);
        deviceListView.setAdapter(pairedDeviceList);
        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
        if(pairedDevices.size() > 0){
            for(BluetoothDevice bluetoothDevice : pairedDevices)
                pairedDeviceList.add((bluetoothDevice.getName() + "\n" + bluetoothDevice.getAddress()).toUpperCase());
        }
        else
            pairedDeviceList.add("NESSUN DISPOSITIVO ACCOPPIATO\nSI PREGA DI ACCOPPIARE IL DISPOSITIVO DALLE IMPOSTAZIONI BLUETOOTH DI SISTEMA");
        deviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String info = ((TextView) view).getText().toString();
                String address = info.substring(info.length() - 17);
                Intent intent = new Intent(BTDeviceListActivity.this, BTGameActivity.class);
                intent.putExtra("device_address", address);
                intent.putExtra("device_mode", "client");
                setResult(Activity.RESULT_OK, intent);
                startActivity(intent);
                finish();
            }
        });
        //deviceListView.setAdapter(deviceList);

            Thread che esegue la ricerca ogni 12 secondi, android permette di tenere attiva la ricerca per 12 secondi dopo i quali viene interrotta.

        Thread threadDiscovery = new Thread(new Runnable() {
            @Override
            public void run() {
                dicovery();
                try {
                    Thread.sleep(12000);
                } catch (InterruptedException e) {

                }
            }
        });
        threadDiscovery.start();
        //this.registerReceiver(receiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(btAdapter != null)
            btAdapter.cancelDiscovery();
        if(threadDiscovery != null)
        * threadDiscovery.stop();
    }
    private void dicovery(){
        if(btAdapter.isDiscovering())
            btAdapter.cancelDiscovery();
        btAdapter.startDiscovery();
    }
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                deviceList.add(device.getName() + "\n" + device.getAddress());
            }
        }
    };*/
}
