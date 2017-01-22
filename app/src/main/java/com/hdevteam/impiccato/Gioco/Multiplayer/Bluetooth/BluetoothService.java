/*
 *
 * Created by HDev (Antonio Siciliani, Cosimo Mollica, Daniele Monaca, Domenico Ferraro, Marco De Caria)
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 11/12/16 19.53
 */

package com.hdevteam.impiccato.Gioco.Multiplayer.Bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.util.Log;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class BluetoothService {
    private static final String TAG = "BluetoothService";

    private static final String NAME_SECURE = "BluetoothChatSecure";
    private static final String NAME_INSECURE = "BluetoothChatInsecure";

    private static final UUID UUID_SECURE = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
    private static final UUID UUID_INSECURE = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    private final BluetoothAdapter bluetoothAdapter;
    private final Handler handler;
    private AcceptThread secureAcceptThread;
    private AcceptThread insecureAcceptThread;
    private ConnectThread connectThread;
    private ConnectedThread connectedThread;
    private int state;
    private static String CONNECTION_FAILED = "connection_failed";
    private static String CONNECTION_LOST = "connection_lost";

    public static final int STATE_NONE = 0;
    public static final int STATE_LISTENING = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;

    public BluetoothService(Handler handler) {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        state = STATE_NONE;
        this.handler = handler;
    }

    /**Imposta lo stato del servizio*/
    private synchronized void setState(int state) {
        Log.d(TAG, "setState() " + state + " -> " + state);
        this.state = state;
    }

    public synchronized int getState() {
        return state;
    }

    /**Fa partire un server bluetooth*/
    public synchronized void start() {
        Log.d(TAG, "start");

        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }

        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }

        setState(STATE_LISTENING);

        if (secureAcceptThread == null) {
            secureAcceptThread = new AcceptThread(true);
            secureAcceptThread.start();
        }
        if (insecureAcceptThread == null) {
            insecureAcceptThread = new AcceptThread(false);
            insecureAcceptThread.start();
        }
    }

    /**Si connette ad un altro dispositivo bluetooth*/
    public synchronized void connect(BluetoothDevice device, boolean secure) {
        Log.d(TAG, "Connesso a: " + device);

        if (state == STATE_CONNECTING) {
            if (connectThread != null) {
                connectThread.cancel();
                connectThread = null;
            }
        }

        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }

        connectThread = new ConnectThread(device, secure);
        connectThread.start();
        setState(STATE_CONNECTING);
    }

    /**Eseguito quando avviene la connessione, fa partire il thread per la comunicazione*/
    public synchronized void connected(BluetoothSocket socket, final String socketType) {
        Log.d(TAG, "connesso!");

        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }

        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }

        if (secureAcceptThread != null) {
            secureAcceptThread.cancel();
            secureAcceptThread = null;
        }
        if (insecureAcceptThread != null) {
            insecureAcceptThread.cancel();
            insecureAcceptThread = null;
        }
        connectedThread = new ConnectedThread(socket, socketType);
        connectedThread.start();

        setState(STATE_CONNECTED);
    }

    /**Termina il BluetoothService*/
    public synchronized void stop() {
        Log.d(TAG, "stop");

        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }

        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }

        if (secureAcceptThread != null) {
            secureAcceptThread.cancel();
            secureAcceptThread = null;
        }

        if (insecureAcceptThread != null) {
            insecureAcceptThread.cancel();
            insecureAcceptThread = null;
        }
        setState(STATE_NONE);
    }

    /**Invia un messaggio via bluetooth*/
    public void write(byte[] out) {
        ConnectedThread r;
        synchronized (this) {
            if (state != STATE_CONNECTED) return;
                r = connectedThread;
        }
        r.write(out);
    }

    /**Metodo che viene eseguito in caso di connessione fallita*/
    private void connectionFailed() {
        byte[] msg = CONNECTION_FAILED.getBytes();
        byte bytes = (byte)msg.length;
        handler.obtainMessage(2, bytes, -1, msg).sendToTarget();
        BluetoothService.this.start();
    }

    /**Metodo che viene eseguito in caso di connessione persa*/
    private void connectionLost() {
        byte[] msg = CONNECTION_LOST.getBytes();
        byte bytes = (byte)msg.length;
        handler.obtainMessage(2, bytes, -1, msg).sendToTarget();
        BluetoothService.this.start();
    }

    /**Thread per il server che resta in ascolto per una connessione*/
    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;
        private String mSocketType;
        public AcceptThread(boolean secure) {
            BluetoothServerSocket tmp = null;
            mSocketType = secure ? "Secure" : "Insecure";
            try {
                if (secure) 
                    tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME_SECURE, UUID_SECURE);
                else 
                    tmp = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(NAME_INSECURE, UUID_INSECURE);
            } catch (IOException e) {
                Log.e(TAG, "Socket Type: " + mSocketType + "listen() failed", e);
            }
            mmServerSocket = tmp;
        }

        public void run() {
            Log.d(TAG, "run() AcceptThread");
            setName("AcceptThread" + mSocketType);
            BluetoothSocket socket = null;
            while (state != STATE_CONNECTED) {
                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    Log.e(TAG, "Socket Type: " + mSocketType + "accept() failed", e);
                    break;
                }
                if (socket != null) {
                    synchronized (BluetoothService.this) {
                        switch (state) {
                            case STATE_LISTENING:
                            case STATE_CONNECTING:
                                connected(socket, mSocketType);
                                break;
                            case STATE_NONE:
                            case STATE_CONNECTED:
                                try {
                                    socket.close();
                                } catch (IOException e) {
                                    Log.e(TAG, "Chiusura socket fallita: " + e.getLocalizedMessage());
                                }
                                break;
                        }
                    }
                }
            }
            Log.i(TAG, "fine AccetpThread");

        }

        public void cancel() {
            Log.d(TAG, "Chiusura Socket AcceptThread");
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Chiusura Socket AcceptThread fallita: " + e.getLocalizedMessage());
            }
        }
    }


    /**Thread che gestisce la connessione ad un dispositivo bluetooth*/
    private class ConnectThread extends Thread {
        private final BluetoothSocket socket;
        private String socketType;

        public ConnectThread(BluetoothDevice device, boolean secure) {
            BluetoothSocket tmp = null;
            socketType = secure ? "Secure" : "Insecure";
            try {
                if (secure)
                    tmp = device.createRfcommSocketToServiceRecord(UUID_SECURE);
                else
                    tmp = device.createInsecureRfcommSocketToServiceRecord(UUID_INSECURE);

            } catch (IOException e) {
                Log.e(TAG, "Connessione fallita: " + e.getLocalizedMessage());
            }
            socket = tmp;
        }

        public void run() {
            Log.i(TAG, "start ConnectThread");
            setName("ConnectThread" + socketType);
            bluetoothAdapter.cancelDiscovery();
            try {
                socket.connect();
            } catch (IOException e) {
                try {
                    socket.close();
                } catch (IOException ex) {
                    Log.e(TAG, "Errore chiusura Socket ConnectThread: " + ex.getLocalizedMessage());
                }
                connectionFailed();
                return;
            }
            synchronized (BluetoothService.this) {
                connectThread = null;
            }

            connected(socket, socketType);
        }

        public void cancel() {
            try {
                socket.close();
            } catch (IOException e) {
                Log.e(TAG, "Errore chiusura Socket ConnectThread (cancel()): " + e.getLocalizedMessage());
            }
        }
    }

    /**Thread che gestisce lo scambio di messaggi bluetooth dopo la connessione*/
    private class ConnectedThread extends Thread {
        private final BluetoothSocket socket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public ConnectedThread(BluetoothSocket socket, String socketType) {
            Log.d(TAG, "creazione ConnectedThread: " + socketType);
            this.socket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "Socket temporanei non creati: " + e.getLocalizedMessage());
            }

            inputStream = tmpIn;
            outputStream = tmpOut;
        }

        public void run() {
            Log.i(TAG, "start ConnectedThread");
            byte[] buffer = new byte[1024];
            int bytes;

            Log.i(TAG, "Stato: " + state);
            while (state == STATE_CONNECTED) {
                try {
                    bytes = inputStream.read(buffer);
                    handler.obtainMessage(2, bytes, -1, buffer)
                            .sendToTarget();
                } catch (IOException e) {
                    Log.e(TAG, "Connessione persa: " + e.getLocalizedMessage());
                    connectionLost();
                    BluetoothService.this.start();
                    break;
                }
            }
        }

        /**Invia un messaggio al dispositivo bluetooth connesso*/
        public void write(byte[] buffer) {
            try {
                outputStream.write(buffer);
            } catch (IOException e) {
                Log.e(TAG, "Errore invio: " + e.getLocalizedMessage());
            }
        }

        public void cancel() {
            try {
                socket.close();
            } catch (IOException e) {
                Log.e(TAG, "Errore chiusura socket: " + e.getLocalizedMessage());
            }
        }
    }
}
