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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hdevteam.impiccato.HomeActivity;
import com.hdevteam.impiccato.R;

import java.util.ArrayList;

public class BTDeviceListActivity extends Activity {

    private ListView listView;
    private ArrayAdapter<String> arrayAdpt;
    private ArrayList<String> deviceList = new ArrayList<String>();
    private BluetoothAdapter bluetoothAdapter;
    private ThreadDiscovery threadDiscovery;
    private ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_btdevice_list);

        threadDiscovery = new ThreadDiscovery(this);
        listView = (ListView) findViewById(R.id.deviceListView);
        arrayAdpt = new ArrayAdapter<String>(this, R.layout.device_name, deviceList);
        listView.setAdapter(arrayAdpt);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        pb = (ProgressBar) findViewById(R.id.progressBar);
        pb.setVisibility(View.GONE);

        startDiscovery();

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String info = ((TextView) view).getText().toString();
                Log.i("BT", info);
                threadDiscovery.interrupt();
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
    /**Fa partire il thread che attiva la ricerca di dispositivi bluetooth ogni tot secondi*/
    private void startDiscovery(){
        threadDiscovery.start();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        if(bluetoothAdapter != null) {
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
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.i("BroadCastReciver", device.getName() + "\n" + device.getAddress());
                if (!checkPresence(device.getAddress())) {
                    deviceList.add(device.getName().toUpperCase() + "\n" + device.getAddress().toUpperCase());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pb.setVisibility(View.GONE);
                            updateListView();
                        }
                    });
                }
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
        arrayAdpt.notifyDataSetChanged();
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pb.setVisibility(View.VISIBLE);
                            deviceList.clear();
                            updateListView();
                        }
                    });
                    bluetoothAdapter.startDiscovery();
                    Log.i("threadDiscovery", "Sto cercando dispositivi vicini...");
                    Thread.sleep(5000);
                    bluetoothAdapter.cancelDiscovery();
                }
            }catch(InterruptedException ex){
                if (bluetoothAdapter.isDiscovering())
                    bluetoothAdapter.cancelDiscovery();
                return;
            }
        }
    }
}
