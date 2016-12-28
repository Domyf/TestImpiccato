/*
 *
 * Created by HDev (Antonio Siciliani, Cosimo Mollica, Daniele Monaca, Domenico Ferraro, Marco De Caria)
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 07/12/16 22.29
 */

package com.hdevteam.impiccato;


import android.*;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.hdevteam.impiccato.Gioco.Multiplayer.Bluetooth.BTDeviceListActivity;
import com.hdevteam.impiccato.Gioco.Multiplayer.Bluetooth.BTGameActivity;

public class BluetoothModeActivity extends AppCompatActivity {

    BluetoothAdapter btAdapter;
    private final int ENABLE_BLUETOOTH_REQUEST = 1;
    private final int DEVICE_LIST_REQUEST = 2;
    private final int DISCOVERABLE_REQUEST = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_mode);
    }

    @Override
    protected void onStart() {
        super.onStart();
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if(btAdapter == null){
            //Errore, bluetooth non presente nel dispositivo (Esistono dispositivi senza bluetooth nel 2016??).
            startActivity(new Intent(this, HomeActivity.class));
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            this.finish();
        }
        if(!btAdapter.isEnabled()){
            enableBluetooth();
        }
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            PermissionUtils.requestPermission(this, 1, android.Manifest.permission.ACCESS_FINE_LOCATION, true);
    }

    public void btnServerOnClick(View view) {
        ensureDiscoverable();
    }

    public void btnClientOnClick(View view) {
        if(!btAdapter.isEnabled()){
            enableBluetooth();
        }
        Intent intent = new Intent(this, BTDeviceListActivity.class);
        startActivityForResult(intent, 1);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        this.finish();
    }
    public void enableBluetooth(){
        startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), ENABLE_BLUETOOTH_REQUEST);
    }

    /**Metodo che viene seguito al termine di un activity richiamata con il metodo startActivityForResult.
     * Consente di conoscere il risultato della activity richiamata.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            //Se l'activity richiesta è l'attivazione del bt
            case ENABLE_BLUETOOTH_REQUEST:
                //Se la richiesta è stata rifiutata, ritorna alla home.
                if(resultCode == Activity.RESULT_CANCELED){
                    Toast.makeText(getApplicationContext(), "Bluetooth non attivato", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
                break;
            case DISCOVERABLE_REQUEST:
                Log.i("onActivityResult", resultCode+"");
                if(resultCode == 300){
                    Log.i("BluetoothMode", "Server");
                    playAsServer();
                } else{
                    Toast.makeText(this, "È necessario rendersi visibile per ospitare una partita!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, HomeActivity.class));
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    this.finish();
                }
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, HomeActivity.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        this.finish();
    }

    private void ensureDiscoverable() {
        if (btAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivityForResult(discoverableIntent, DISCOVERABLE_REQUEST);
        } else {
            playAsServer();
        }
    }

    public void playAsServer() {
        Intent intent = new Intent(this, BTGameActivity.class);
        intent.putExtra("device_mode", "server");
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        this.finish();
    }
}
