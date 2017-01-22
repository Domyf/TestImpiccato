/*
 *
 * Created by HDev (Antonio Siciliani, Cosimo Mollica, Daniele Monaca, Domenico Ferraro, Marco De Caria)
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 11/12/16 19.53
 */

package com.hdevteam.impiccato.Gioco.Multiplayer.Bluetooth;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.hdevteam.impiccato.BluetoothModeActivity;
import com.hdevteam.impiccato.Gioco.Impiccato;
import com.hdevteam.impiccato.Gioco.Salvataggi.WordList;
import com.hdevteam.impiccato.HomeActivity;
import com.hdevteam.impiccato.R;
import com.hdevteam.impiccato.UI.HDTMDialog;

import java.io.IOException;

import static com.hdevteam.impiccato.R.id.txtScore;

public class BTGameActivity extends AppCompatActivity {

    private BluetoothService BTService;
    private Impiccato game;
    private TextView textViewGuessedLetters;
    private TextView textViewMyScore;
    private TextView textViewEnemyScore;
    private TextView textViewTime;
    private ImageView life;
    private ImageView hider;
    private ProgressDialog progressDialog;
    private Animation gameLost;
    private Animation victory;
    private Animation hideKeyboard;
    private HDTMDialog dialog;
    private boolean end;
    private int rowToGuess;
    private int dictionaryLength;
    private int enemyWords;
    private int myWords;
    private Thread timer;
    private int time; //Espresso in secondi

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_btgame);

        textViewGuessedLetters = (TextView) findViewById(R.id.textViewGuessedLetters);
        textViewMyScore = (TextView) findViewById(R.id.txtBestScore);
        textViewTime = (TextView) findViewById(R.id.txtScore);
        textViewEnemyScore = (TextView) findViewById(R.id.txtMultiplier);
        hider = (ImageView) findViewById(R.id.hider);
        life = (ImageView) findViewById(R.id.life);

        dialog = new HDTMDialog(this);
        dictionaryLength = getResources().getInteger(R.integer.filelength_dizionario_medium);
        end = false;
        enemyWords = 0;
        myWords = 0;
        time = 60;

        victory = AnimationUtils.loadAnimation(this, R.anim.victory);
        gameLost = AnimationUtils.loadAnimation(this, R.anim.game_lost);
        hideKeyboard = AnimationUtils.loadAnimation(this, R.anim.hide_keyboard);

        hideUnnecessaryLayout();
        initBar();
        initTimer();
        setTimeUI();
        setProgressDialog();
        progressDialog.show();
        progressDialog.setContentView(R.layout.hdtmprogress_dialog);

        Handler handler  = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                BTGameActivity.this.handleMessage(new String((byte[]) msg.obj, 0, msg.arg1));
            }
        };

        BTService = new BluetoothService(handler);

        String type = getIntent().getStringExtra("device_mode");

        game = new Impiccato();
        //Se il dispositivo è client
        if(type.equals("client")) {
            Log.i("BTGame", "Creo BTGame - Sono un client");
            startClient();
        }
        //Se il dispositivo è server
        else {
            Log.i("BTGame", "Creo BTGame - Sono un server");
            startServer();
        }
    }

    private void setProgressDialog(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                returnHome();
            }
        });
        progressDialog.setMessage(getString(R.string.bt_connecting_message));
        progressDialog.setCanceledOnTouchOutside(false);
    }

    private void handleMessage(String msg) {
        switch (msg) { //Messaggio ricevuto
            case "won"://L'avversario ha indovinato una parola
                enemyWords++;
                textViewEnemyScore.setText("AVV: "+enemyWords);
                break;
            case "lost"://L'avversario ha perso
                end = true;
                BTService.stop();
                textViewMyScore.setVisibility(View.GONE);
                textViewEnemyScore.setVisibility(View.GONE);
                gameEnded();
                break;
            //Connessione fallita
            case "connection_failed":
                if (!end) {
                    dialog.show();
                    dialog.setTitle(getResources().getString(R.string.dialog_connection_failed_title));
                    dialog.setMessage(getResources().getString(R.string.dialog_connection_failed_message));
                    dialog.setNeutralBtn(getResources().getString(R.string.dialog_neutral_button), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.close();
                            returnHome();
                        }
                    });
                    dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            returnHome();
                        }
                    });
                }
                break;
            //Connessione interrotta
            case "connection_lost":
                if (!end) {
                    dialog.show();
                    dialog.setTitle(getResources().getString(R.string.dialog_connection_lost_title));
                    dialog.setMessage(getResources().getString(R.string.dialog_connection_lost_message));
                    dialog.setNeutralBtn(getResources().getString(R.string.dialog_neutral_button), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.close();
                            returnHome();
                        }
                    });
                    dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            returnHome();
                        }
                    });
                }
                break;
            default://Prima riga da indovinare
                rowToGuess = Integer.parseInt(msg);
                setWordToGuess();
                generateGuessedLetters();
                Log.i("Handler", "La parola da indovinare che ho ricevuto è "+game.getWordToGuess());
                setUI();
                timer.start();
                break;
        }
    }

    /** Prende la parola da indovinare nel dizionario */
    public void setWordToGuess() {
        try {
            game.setWordToGuess(WordList.getWordAt(getApplication().getAssets().open(getResources().getString(R.string.filename_dizionario_medium)), rowToGuess));
        } catch (IOException e) {
            BTService.stop();
            timer.interrupt();
            returnHome();
            e.printStackTrace();
        }
        Log.i("BTGame", "La parola da indovinare è " + game.getWordToGuess() + " alla riga "+rowToGuess);
    }

    /** Genera la stringa formata dagli "_" */
    public void generateGuessedLetters() {
        game.generateGuessedLetters();
        Log.i("BTGame", "Genero la stringa formata dagli '_'");
    }

    /**Fa partire il client*/
    private void startClient(){
        Log.i("BTGameActivity", "Client starting...");
        //Salvo l'indirizzo del server
        String serverAddress = getIntent().getStringExtra("device_address");
        //Creo l'oggetto BluetoothDevice per connettere al server.
        BluetoothDevice server = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(serverAddress);
        //Connetto il BTService con connessione secure
        BTService.connect(server, true);
        //Se il dispositivo non è connesso...
        if(BTService.getState() == 0){
            //Tento la connessione insecure TODO: Testare se serve la connessione insecure
            BTService.connect(server, false);
            if(BTService.getState() == 0) {
                Log.i("BTGameActivity", "Non connesso, esco");
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                startActivity(new Intent(this, HomeActivity.class));
                this.finish();
            }
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(BTService.getState() != 3){ }
                if(progressDialog.isShowing() && progressDialog != null)
                    progressDialog.dismiss();
            }
        }).start();
    }

    /** Fa partire il server */
    private void startServer(){
        Log.i("BTGameActivity", "Server starting...");
        BTService.start();
        /*
        Creo un thread per gestire l'avvio del server.
        Attende fino a quando non viene connesso un dispositivo.
        Dopo la connessione pesca la parola e la invia al dispositivo connesso.
        Necessario poichè contiene un while con durata potenzialmente infinita.
         */
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(BTService.getState() != 3){ }
                rowToGuess = (int)(Math.random() * dictionaryLength) + 1;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setWordToGuess();
                        generateGuessedLetters();
                    }
                });
                String temp = rowToGuess+"";
                BTService.write(temp.getBytes());
                progressDialog.dismiss();
                Log.i("Handler", "La parola da indovinare è "+game.getWordToGuess());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setUI();
                        timer.start();
                    }
                });
            }
        }).start();
    }

    public void initTimer() {
        timer = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    while(!Thread.currentThread().isInterrupted()){
                        Thread.sleep(1000);
                        time--;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (time <= 0)
                                    gameEnded();
                                else
                                    setTimeUI();
                            }
                        });
                    }
                } catch (InterruptedException ex){
                    Log.i("ThreadTimer", "Chiudo il Thread");
                    return;
                }
            }
        });
    }

    public void gameEnded() {
        timer.interrupt();
        BTService.stop();
        end = true;
        textViewMyScore.setVisibility(View.GONE);
        textViewEnemyScore.setVisibility(View.GONE);
        if (myWords == enemyWords)
            draw();
        else if (myWords > enemyWords)
            victory();
        else
            gameOver();
    }

    public void setTimeUI() {
        int min = 0;
        int seconds = 0;

        for (int i=0; i<time; i++) {
            if (seconds+1 == 60) {
                min++;
                seconds = 0;
            } else {
                seconds++;
            }
        }

        if (seconds >= 10)
            textViewTime.setText(min+":"+seconds);
        else
            textViewTime.setText(min+":0"+seconds);
    }

    public void initBar() {
        textViewMyScore.setVisibility(View.VISIBLE);
        textViewTime.setVisibility(View.VISIBLE);
        textViewEnemyScore.setVisibility(View.VISIBLE);
        textViewMyScore.setText("TU: 0");
        textViewEnemyScore.setText("AVV: 0");
    }

    /** Imposta l'interfaccia utente */
    public void setUI() {
        setLifeImage();
        textViewGuessedLetters.setText(game.getGuessedLetters());
        Log.i("BTGame", "Imposto l'interfaccia utente");
    }

    public void hideUnnecessaryLayout() {
        Log.i("BTGame", "Nascondo le parti del layout che non sono necessarie");
        //Nascondo la lampadina
        findViewById(R.id.bulb).setVisibility(View.GONE);
        findViewById(R.id.bulbBg).setVisibility(View.GONE);
    }

    /**Torna alla home*/
    private void returnHome(){
        end = true;
        if (timer != null)
            timer.interrupt();
        if(BTService != null)
            BTService.stop();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        startActivity(new Intent(this, HomeActivity.class));
        this.finish();
    }

    /**Prova la lettera passata per argomento. Gestisce la fine del gioco*/
    public void tryLetter(char letter) {
        ((android.os.Vibrator) this.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(25);
        if(game.tryLetter(letter)) {
            textViewGuessedLetters.setText(game.getGuessedLetters());
            if(game.endGame()) {
                BTService.write("won".getBytes());
                myWords++;
                textViewMyScore.setText("TU: "+myWords);
                nextWord();
            }
        } else {
            setLifeImage();
            if(game.getLives() == 0) {
                end = true;
                BTService.write("lost".getBytes());
                gameEnded();
            }
        }
    }

    /** Gestisce la vittoria */
    public void victory() {
        victory.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                setKeyboardClickable(false);
                textViewTime.setVisibility(View.VISIBLE);
                textViewTime.setText(getString(R.string.bt_won_message));
                textViewGuessedLetters.setText(game.getWordToGuess());
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        textViewGuessedLetters.startAnimation(victory);
        hider.startAnimation(hideKeyboard);
        hideKeyboard.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                findViewById(R.id.gameOverButtons).setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    /** Gestisce la sconfitta */
    public void gameOver() {
        timer.interrupt();
        textViewMyScore.setVisibility(View.GONE);
        textViewEnemyScore.setVisibility(View.GONE);
        textViewTime.setText(getString(R.string.bt_lost_message));
        life.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.life0, null));
        textViewGuessedLetters.startAnimation(gameLost);
        hider.startAnimation(hideKeyboard);
        hideKeyboard.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                setKeyboardClickable(false);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                findViewById(R.id.gameOverButtons).setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        textViewGuessedLetters.setText(game.getWordToGuess());
    }

    /** Gestisce il pareggio */
    public void draw() {
        textViewTime.setText(getString(R.string.bt_draw_message));
        life.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.life1, null));
        textViewGuessedLetters.startAnimation(gameLost);
        hider.startAnimation(hideKeyboard);
        hideKeyboard.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                setKeyboardClickable(false);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                findViewById(R.id.gameOverButtons).setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        textViewGuessedLetters.setText(game.getWordToGuess());
    }

    /**Disabilita il tasto relativo alla lettera passata come argomento*/
    public void disableLetter(char letter) {
        switch (letter) {
            case 'a':
                findViewById(R.id.btnA).setEnabled(false);
                break;
            case 'b':
                findViewById(R.id.btnB).setEnabled(false);
                break;
            case 'c':
                findViewById(R.id.btnC).setEnabled(false);
                break;
            case 'd':
                findViewById(R.id.btnD).setEnabled(false);
                break;
            case 'e':
                findViewById(R.id.btnE).setEnabled(false);
                break;
            case 'f':
                findViewById(R.id.btnF).setEnabled(false);
                break;
            case 'g':
                findViewById(R.id.btnG).setEnabled(false);
                break;
            case 'h':
                findViewById(R.id.btnH).setEnabled(false);
                break;
            case 'i':
                findViewById(R.id.btnI).setEnabled(false);
                break;
            case 'j':
                findViewById(R.id.btnJ).setEnabled(false);
                break;
            case 'k':
                findViewById(R.id.btnK).setEnabled(false);
                break;
            case 'l':
                findViewById(R.id.btnL).setEnabled(false);
                break;
            case 'm':
                findViewById(R.id.btnM).setEnabled(false);
                break;
            case 'n':
                findViewById(R.id.btnN).setEnabled(false);
                break;
            case 'o':
                findViewById(R.id.btnO).setEnabled(false);
                break;
            case 'p':
                findViewById(R.id.btnP).setEnabled(false);
                break;
            case 'q':
                findViewById(R.id.btnQ).setEnabled(false);
                break;
            case 'r':
                findViewById(R.id.btnR).setEnabled(false);
                break;
            case 's':
                findViewById(R.id.btnS).setEnabled(false);
                break;
            case 't':
                findViewById(R.id.btnT).setEnabled(false);
                break;
            case 'u':
                findViewById(R.id.btnU).setEnabled(false);
                break;
            case 'v':
                findViewById(R.id.btnV).setEnabled(false);
                break;
            case 'w':
                findViewById(R.id.btnW).setEnabled(false);
                break;
            case 'x':
                findViewById(R.id.btnX).setEnabled(false);
                break;
            case 'y':
                findViewById(R.id.btnY).setEnabled(false);
                break;
            case 'z':
                findViewById(R.id.btnZ).setEnabled(false);
                break;
        }
    }

    /**Mostra l'immagine dell'impiccato in base alle vite rimaste*/
    public void setLifeImage() {
        switch (game.getLives()) {
            case 0:
                life.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.life0, null));
                break;
            case 1:
                life.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.life1, null));
                break;
            case 2:
                life.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.life2, null));
                break;
            case 3:
                life.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.life3, null));
                break;
            case 4:
                life.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.life4, null));
                break;
            case 5:
                life.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.life5, null));
                break;
            case 6:
                life.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.life6, null));
                break;
            case 7:
                life.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.life7, null));
                break;
        }
    }

    /**Gestisce la vittoria*/
    public void nextWord() {
        textViewGuessedLetters.startAnimation(victory);
        victory.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { //Approfitto dell'animazione per caricare la parola successiva
                setKeyboardClickable(false);
                // Controllo se ho raggiunto la fine del dizionario. Se si imposto la prima riga
                if (rowToGuess + 1 > dictionaryLength)
                    rowToGuess = 1;
                else
                    rowToGuess++;
                game.setLives(7);
                setWordToGuess();
                generateGuessedLetters();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Log.i("BTGame", "Ho finito l'animazione");
                setUI();
                setKeyboardClickable(true);
                enableKeyboard();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    // <editor-fold defaultstate="collapsed" desc="Tastiera">
    public void btnAOnClick(View view) {
        disableLetter('a');
        tryLetter('a');
    }

    public void btnBOnClick(View view) {
        disableLetter('b');
        tryLetter('b');
    }
    public void btnCOnClick(View view) {
        disableLetter('c');
        tryLetter('c');
    }
    public void btnDOnClick(View view) {
        disableLetter('d');
        tryLetter('d');
    }
    public void btnEOnClick(View view) {
        disableLetter('e');
        tryLetter('e');
    }
    public void btnFOnClick(View view) {
        disableLetter('f');
        tryLetter('f');
    }
    public void btnGOnClick(View view) {
        disableLetter('g');
        tryLetter('g');
    }
    public void btnHOnClick(View view) {
        disableLetter('h');
        tryLetter('h');
    }
    public void btnIOnClick(View view) {
        disableLetter('i');
        tryLetter('i');
    }
    public void btnJOnClick(View view) {
        disableLetter('j');
        tryLetter('j');
    }
    public void btnKOnClick(View view) {
        disableLetter('k');
        tryLetter('k');
    }
    public void btnLOnClick(View view) {
        disableLetter('l');
        tryLetter('l');
    }
    public void btnMOnClick(View view) {
        disableLetter('m');
        tryLetter('m');
    }
    public void btnNOnClick(View view) {
        disableLetter('n');
        tryLetter('n');
    }
    public void btnOOnClick(View view) {
        disableLetter('o');
        tryLetter('o');
    }
    public void btnPOnClick(View view) {
        disableLetter('p');
        tryLetter('p');
    }
    public void btnQOnClick(View view) {
        disableLetter('q');
        tryLetter('q');
    }
    public void btnROnClick(View view) {
        disableLetter('r');
        tryLetter('r');
    }
    public void btnSOnClick(View view) {
        disableLetter('s');
        tryLetter('s');
    }
    public void btnTOnClick(View view) {
        disableLetter('t');
        tryLetter('t');
    }
    public void btnUOnClick(View view) {
        disableLetter('u');
        tryLetter('u');
    }
    public void btnVOnClick(View view) {
        disableLetter('v');
        tryLetter('v');
    }
    public void btnWOnClick(View view) {
        disableLetter('w');
        tryLetter('w');
    }
    public void btnXOnClick(View view) {
        disableLetter('x');
        tryLetter('x');
    }
    public void btnYOnClick(View view) {
        disableLetter('y');
        tryLetter('y');
    }
    public void btnZOnClick(View view) {
        disableLetter('z');
        tryLetter('z');
    }
    //</editor-fold>>

    /** Abilita la tastiera */
    public void enableKeyboard() {
        findViewById(R.id.btnA).setEnabled(true);
        findViewById(R.id.btnB).setEnabled(true);
        findViewById(R.id.btnC).setEnabled(true);
        findViewById(R.id.btnD).setEnabled(true);
        findViewById(R.id.btnE).setEnabled(true);
        findViewById(R.id.btnF).setEnabled(true);
        findViewById(R.id.btnG).setEnabled(true);
        findViewById(R.id.btnH).setEnabled(true);
        findViewById(R.id.btnI).setEnabled(true);
        findViewById(R.id.btnJ).setEnabled(true);
        findViewById(R.id.btnK).setEnabled(true);
        findViewById(R.id.btnL).setEnabled(true);
        findViewById(R.id.btnM).setEnabled(true);
        findViewById(R.id.btnN).setEnabled(true);
        findViewById(R.id.btnO).setEnabled(true);
        findViewById(R.id.btnP).setEnabled(true);
        findViewById(R.id.btnQ).setEnabled(true);
        findViewById(R.id.btnR).setEnabled(true);
        findViewById(R.id.btnS).setEnabled(true);
        findViewById(R.id.btnT).setEnabled(true);
        findViewById(R.id.btnU).setEnabled(true);
        findViewById(R.id.btnV).setEnabled(true);
        findViewById(R.id.btnW).setEnabled(true);
        findViewById(R.id.btnX).setEnabled(true);
        findViewById(R.id.btnY).setEnabled(true);
        findViewById(R.id.btnZ).setEnabled(true);
    }

    /** Rende la tastiera cliccabile oppure no in base al boolean passato per argomento */
    public void setKeyboardClickable(boolean bool) {
        findViewById(R.id.btnA).setClickable(bool);
        findViewById(R.id.btnB).setClickable(bool);
        findViewById(R.id.btnC).setClickable(bool);
        findViewById(R.id.btnD).setClickable(bool);
        findViewById(R.id.btnE).setClickable(bool);
        findViewById(R.id.btnF).setClickable(bool);
        findViewById(R.id.btnG).setClickable(bool);
        findViewById(R.id.btnH).setClickable(bool);
        findViewById(R.id.btnI).setClickable(bool);
        findViewById(R.id.btnJ).setClickable(bool);
        findViewById(R.id.btnK).setClickable(bool);
        findViewById(R.id.btnL).setClickable(bool);
        findViewById(R.id.btnM).setClickable(bool);
        findViewById(R.id.btnN).setClickable(bool);
        findViewById(R.id.btnO).setClickable(bool);
        findViewById(R.id.btnP).setClickable(bool);
        findViewById(R.id.btnQ).setClickable(bool);
        findViewById(R.id.btnR).setClickable(bool);
        findViewById(R.id.btnS).setClickable(bool);
        findViewById(R.id.btnT).setClickable(bool);
        findViewById(R.id.btnU).setClickable(bool);
        findViewById(R.id.btnV).setClickable(bool);
        findViewById(R.id.btnW).setClickable(bool);
        findViewById(R.id.btnX).setClickable(bool);
        findViewById(R.id.btnY).setClickable(bool);
        findViewById(R.id.btnZ).setClickable(bool);
    }

    public void btnPlayAgainOnClick(View view) {
        ((android.os.Vibrator) this.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(25);
        startActivity(new Intent(this, BluetoothModeActivity.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        this.finish();
    }

    public void btnHomeOnClick(View view) {
        ((android.os.Vibrator) this.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(25);
        returnHome();
    }

    @Override
    public void onBackPressed() {
        if (!game.endGame()) {
            dialog.show();
            dialog.setTitle(getResources().getString(R.string.dialog_go_home));
            dialog.setMessage(getResources().getString(R.string.dialog_data_will_be_lost));
            dialog.setPositiveBtn(getResources().getString(R.string.dialog_positive_button), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.close();
                    returnHome();
                }
            });
            dialog.setNegativeBtn(getResources().getString(R.string.dialog_negative_button), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.close();
                }
            });
        } else {
            returnHome();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BTService.stop();
    }
}
