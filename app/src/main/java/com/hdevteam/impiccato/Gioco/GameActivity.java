/*
 *
 * Created by HDev (Antonio Siciliani, Cosimo Mollica, Daniele Monaca, Domenico Ferraro, Marco De Caria)
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 11/12/16 19.53
 */

package com.hdevteam.impiccato.Gioco;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//import com.adcash.mobileads.AdcashError;
//import com.adcash.mobileads.AdcashReward;
//import com.adcash.mobileads.ui.AdcashRewardedVideo;
//import com.adcash.mobileads.ui.AdcashRewardedVideo;

import com.chartboost.sdk.CBLocation;
import com.chartboost.sdk.Chartboost;
import com.chartboost.sdk.ChartboostDelegate;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.hdevteam.impiccato.DifficultyActivity;
import com.hdevteam.impiccato.Gioco.Salvataggi.Loader;
import com.hdevteam.impiccato.Gioco.Salvataggi.Save;
import com.hdevteam.impiccato.Gioco.Salvataggi.Saver;
import com.hdevteam.impiccato.Gioco.Salvataggi.Settings;
import com.hdevteam.impiccato.Gioco.Salvataggi.WordList;
import com.hdevteam.impiccato.Gioco.Wordlist.WordlistHelper;
import com.hdevteam.impiccato.HomeActivity;
import com.hdevteam.impiccato.R;
import com.hdevteam.impiccato.UI.HDTMDialog;

import java.io.File;
import java.io.IOException;

/**
 * @author HDevTeam
 * Attività che consente di giocare al gioco dell'Impiccato.
 */

public class GameActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private Impiccato game;
    private Save save;
    private int rowToGuess;

    private TextView textViewGuessedLetters;
    private TextView textViewMultiplier;
    private TextView textViewScore;
    private TextView textViewBestScore;
    private ImageView bulb;
    private ImageView life;
    private ImageView hider;

    //AdcashRewardedVideo mRewardedVid;

    private Animation victory;
    private Animation gameLost;
    private Animation fadein;
    private Animation hideKeyboard;
    private Animation bulbPulse;
    private Animation bulbExit;
    private Animation bulbEnter;

    private Thread bulbPulseTh;

    private String filename_dizionario;
    private int dictionaryLength;  //Numero di parole nel dizionario
    private boolean newRecord;
    private boolean tutorial;
    private boolean end;
    GoogleApiClient googleApiClient;
    WordlistHelper wordlistHelper;
    private ChartboostDelegate YourDelegateObject = new ChartboostDelegate() {
        public void didCompleteRewardedVideo(String location, int reward) {
            rewardLetter();
            Log.i("GameActivity", "Ho finito di vedere la pubblicità");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .build();
        wordlistHelper = new WordlistHelper(this);
        /*mRewardedVid = new AdcashRewardedVideo();
        mRewardedVid.setZoneId("1492439");
        mRewardedVid.setAdListener(new AdcashRewardedVideo.Listener() {
            @Override
            public void onAdLoaded(AdcashReward adcashReward) {
                Log.i("GameActivity", "Ad caricato");
            }

            @Override
            public void onAdFailedToLoad(AdcashError adcashError) {
                switch (adcashError) {
                    case NO_NETWORK:
                        Log.i("GameActivity", "Ad non caricato. Nessuna connessione");
                        // Handle the "No internet connection" situation here
                        break;
                    case REQUEST_FAILED:
                        Log.i("GameActivity", "Ad non caricato. Richiesta fallita");
                        // Handle the "Request failed" situation here
                        break;
                    case NETWORK_FAILURE:
                        Log.i("GameActivity", "Ad non caricato. Problema di connessione");
                        // Handle the "Network failure" situation here
                        break;
                    case NO_AD:
                        Log.i("GameActivity", "Ad non caricato. Non ci sono ad");
                        // Handle the "There is no ad" situation here
                        break;
                    case NOT_READY:
                        Log.i("GameActivity", "Ad non caricato. Non sono pronto");
                        // Handle the "Ad not loaded" situation here
                        break;
                    case ALREADY_DISPLAYED:
                        Log.i("GameActivity", "Ad non caricato. Sto già mostrando l'ad");
                        // Handle the "Already displayed" situation here
                        break;
                }
            }

            @Override
            public void onAdOpened() {
                Log.i("GameActivity", "Ad aperto");
            }

            @Override
            public void onAdReward(AdcashReward adcashReward) {
                Log.i("GameActivity", "L'Ad restituisce il reward");
            }

            @Override
            public void onAdClosed() {
                Log.i("GameActivity", "Ad chiuso dall'utente");
            }

            @Override
            public void onAdLeftApplication() {
                Log.i("GameActivity", "L'utente è uscito dall'applicazione cliccando sull'Ad");
            }
        });
        mRewardedVid.loadAd(this);*/

        if (Settings.isGPGConnected()) {
            Log.i("GameActivity", "L'app era connessa a Play Games");
            if (googleApiClient.isConnected())
                Log.i("GameActivity", "Sono connesso a Play Games!");
            else {
                googleApiClient.connect();
                Log.w("GameActivity", "Non sono connesso a Play Games!");
            }
        } else
            Log.i("GameActivity", "L'app non era connessa a Play Games");

        Chartboost.startWithAppId(this, getResources().getString(R.string.chart_boost_app_id), getResources().getString(R.string.chart_boost_app_signature));
        Chartboost.onCreate(this);
        Chartboost.cacheRewardedVideo(CBLocation.LOCATION_MAIN_MENU);
        Chartboost.setDelegate(YourDelegateObject);

        textViewGuessedLetters = (TextView) findViewById(R.id.textViewGuessedLetters);
        textViewScore = (TextView) findViewById(R.id.txtScore);
        textViewBestScore = (TextView) findViewById(R.id.txtBestScore);
        textViewMultiplier = (TextView) findViewById(R.id.txtMultiplier);
        bulb = (ImageView) findViewById(R.id.bulb);
        life = (ImageView) findViewById(R.id.life);
        hider = (ImageView) findViewById(R.id.hider);

        tutorial = false;
        end = false;

        victory = AnimationUtils.loadAnimation(this, R.anim.victory);
        gameLost = AnimationUtils.loadAnimation(this, R.anim.game_lost);
        fadein = AnimationUtils.loadAnimation(this, R.anim.fade_in_word_to_guess);
        hideKeyboard = AnimationUtils.loadAnimation(this, R.anim.hide_keyboard);
        bulbPulse = AnimationUtils.loadAnimation(this, R.anim.bulb_pulse);
        bulbExit = AnimationUtils.loadAnimation(this, R.anim.googleplay_button_exit);
        bulbEnter = AnimationUtils.loadAnimation(this, R.anim.googleplay_button_enter);

        setBulbPulseThread();
        this.game = new Impiccato();
        Log.i("GameActivity", "Creo la classe impiccato");
        Bundle b = getIntent().getExtras();
        rowToGuess = b.getInt("rowToGuess");
        tutorial = b.getBoolean("tutorial");
        game.setDifficulty(b.getInt("difficulty"));
        game.setBestScore(b.getInt("bestScore"));
        game.setScore(b.getInt("currentScore"));
        setUpDifficulty();
        setWordToGuess();
        generateGuessedLetters();
        setUI();
        if(isNetworkAvailable()) {
            showBulb();
        } else {
            bulb.setVisibility(View.GONE);
            findViewById(R.id.bulbBg).setVisibility(View.INVISIBLE);
        }
        bulbPulseTh.start();
        unlockAchievement(getResources().getString(R.string.achievement_benvenuto));
        newRecord = false;
    }

    /**Sblocca un achievement di Play Games*/
    private void unlockAchievement(String achievementID){
        if(googleApiClient.isConnected()) {
            Log.i("GameActivity", "Sblocco achievement " + achievementID);
            Games.Achievements.unlock(googleApiClient, achievementID);
        }
    }

    /** Imposta il nome del file in base alla difficoltà */
    private void setUpDifficulty() {
        switch (game.getDifficulty()) {
            case 1:
                filename_dizionario = getResources().getString(R.string.filename_dizionario_easy);
                dictionaryLength = getResources().getInteger(R.integer.filelength_dizionario_easy);
                Log.i("GameActivity", "La difficoltà è Facile");
                break;
            case 2:
                filename_dizionario = getResources().getString(R.string.filename_dizionario_medium);
                dictionaryLength = getResources().getInteger(R.integer.filelength_dizionario_medium);
                Log.i("GameActivity", "La difficoltà è Media");
                break;
            case 3:
                filename_dizionario = getResources().getString(R.string.filename_dizionario_hard);
                dictionaryLength = getResources().getInteger(R.integer.filelength_dizionario_hard);
                Log.i("GameActivity", "La difficoltà è Difficile");
                break;
        }

    }

    /** Prende la parola da indovinare nel dizionario */
    public void setWordToGuess() {

        // try {
            game.setWordToGuess(wordlistHelper.getWord(rowToGuess, filename_dizionario));
        /*} catch (IOException e) {
            e.printStackTrace();
        }
        Log.i("GameActivity", "La parola da indovinare è " + game.getWordToGuess() + " alla riga "+rowToGuess);*/
    }

    /** Genera la stringa formata dagli "_" */
    public void generateGuessedLetters() {
        game.generateGuessedLetters();
        Log.i("GameActivity", "Genero la stringa formata dagli '_'");
    }

    /** Imposta l'interfaccia utente */
    public void setUI() {
        setLifeImage();
        bulb.setEnabled(true);
        if (isNetworkAvailable() && bulb.getVisibility() == View.GONE) {
            showBulb();
        } else if (!isNetworkAvailable() && bulb.getVisibility() == View.VISIBLE) {
            hideBulb();
        }
        textViewGuessedLetters.setText(game.getGuessedLetters());
        textViewGuessedLetters.startAnimation(fadein);
        textViewMultiplier.setText("X1");
        textViewScore.setText("" + game.getScore());
        textViewBestScore.setText(""+game.getBestScore());
        game.setMultiplier(1);
        Log.i("GameActivity", "Imposto l'interfaccia utente");
    }

    /** Crea il thread che gestisce la lampadina. Ogni 8 secondi, questo thread si occupa di far
     *  pulsare la lampadina. La lampadina pulsa quando si continua a fare errori oppure si sta
     *  senza provare alcuna lettera */
    public void setBulbPulseThread() {
        bulbPulseTh = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    while(!Thread.currentThread().isInterrupted()){
                        if (!game.isHintUsed()) {
                            final int tempScore = game.getScore();
                            final int tempLives = game.getLives();
                            Thread.sleep(8000);
                            //Anima la lampadina
                            if (!bulbPulse.hasStarted() && tempLives < game.getLives() || tempScore == game.getScore()) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (bulb.getVisibility() == View.VISIBLE) {
                                            Log.i("ThreadBulbPulse", "Animo la lampadina");
                                            bulb.startAnimation(bulbPulse);
                                        }
                                        bulbPulse.setAnimationListener(new Animation.AnimationListener() {
                                            @Override
                                            public void onAnimationStart(Animation animation) {

                                            }

                                            @Override
                                            public void onAnimationEnd(Animation animation) {
                                                if (!game.isHintUsed() && game.getLives() != 0 && (bulb.isEnabled() && !bulbPulse.hasStarted() && tempLives < game.getLives() || tempScore == game.getScore()))
                                                    bulb.startAnimation(bulbPulse);
                                            }

                                            @Override
                                            public void onAnimationRepeat(Animation animation) {

                                            }
                                        });
                                    }
                                });
                            }
                        }
                    }
                } catch (InterruptedException ex){
                    Log.i("ThreadBulbPulse", "Chiudo il Thread");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            bulb.clearAnimation();
                        }
                    });
                    return;
                }
            }
        });
    }

    /** Mostra l'immagine dell'impiccato in base alle vite rimaste*/
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

    /** Ottiene una lettera come ricompensa per aver aver chiesto un suggerimento e guardato la pubblicità*/
    public void rewardLetter() {
        int n;
        do {
            n = (int) (Math.random() * (game.getGuessedLetters().length() - 2)) + 1;
        } while(game.getGuessedLetters().charAt(n) != '_');
        showRewardLetter(game.getWordToGuess().charAt(n));
        unlockAchievement(getResources().getString(R.string.achievement_aiuto));
    }

    /** Disabilita il tasto relativo alla lettera passata come argomento */
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

    /** Disabilita la tastiera */
    public void disableKeyboard() {
        findViewById(R.id.btnA).setEnabled(false);
        findViewById(R.id.btnB).setEnabled(false);
        findViewById(R.id.btnC).setEnabled(false);
        findViewById(R.id.btnD).setEnabled(false);
        findViewById(R.id.btnE).setEnabled(false);
        findViewById(R.id.btnF).setEnabled(false);
        findViewById(R.id.btnG).setEnabled(false);
        findViewById(R.id.btnH).setEnabled(false);
        findViewById(R.id.btnI).setEnabled(false);
        findViewById(R.id.btnJ).setEnabled(false);
        findViewById(R.id.btnK).setEnabled(false);
        findViewById(R.id.btnL).setEnabled(false);
        findViewById(R.id.btnM).setEnabled(false);
        findViewById(R.id.btnN).setEnabled(false);
        findViewById(R.id.btnO).setEnabled(false);
        findViewById(R.id.btnP).setEnabled(false);
        findViewById(R.id.btnQ).setEnabled(false);
        findViewById(R.id.btnR).setEnabled(false);
        findViewById(R.id.btnS).setEnabled(false);
        findViewById(R.id.btnT).setEnabled(false);
        findViewById(R.id.btnU).setEnabled(false);
        findViewById(R.id.btnV).setEnabled(false);
        findViewById(R.id.btnW).setEnabled(false);
        findViewById(R.id.btnX).setEnabled(false);
        findViewById(R.id.btnY).setEnabled(false);
        findViewById(R.id.btnZ).setEnabled(false);
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

    /** Mostra la lettera d'aiuto */
    public void showRewardLetter(char letter) {
        game.giveRewardLetter(letter);
        textViewGuessedLetters.setText(game.getGuessedLetters());
        disableLetter(letter);
        if(game.endGame())
            nextWord();
    }

    /**Prova la lettera passata per argomento. Gestisce la fine del gioco*/
    public void tryLetter(char letter) {
        ((android.os.Vibrator) this.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(25);
        int score = game.getScore();
        if(game.getDifficulty() == 3 && game.getScore() >= 10000)
            unlockAchievement(getResources().getString(R.string.achievement_vocabolario_umano));
        if(game.tryLetter(letter)) {
            textViewGuessedLetters.setText(game.getGuessedLetters());
            if (game.checkBestScore()) {
                newRecord = true;
                setBestScoreUI(score);
                game.setBestScore(game.getScore());
            }
            setScoreUI(score);
            Log.i("GameActivity", "Score: "+game.getScore());
            if(game.endGame()) {
                nextWord();
            }
        } else {
            setLifeImage();
            if(game.getLives() == 0) {
                gameOver();
            }
        }
        textViewMultiplier.setText("X"+game.getMultiplier());
    }

    /** Imposta lo score sullo schermo con effetto incremento */
    private void setScoreUI(int score) {
        for (int i=0; i<game.getScore()-score; i++) {
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    int s = Integer.parseInt(String.valueOf(textViewScore.getText()))+1;
                    textViewScore.setText(""+s);
                }
            }, i*8+(40/game.getMultiplier()));
        }
    }

    /** Imposta il miglior score sullo schermo con effetto incremento */
    private void setBestScoreUI(int score) {
        for (int i = 0; i < game.getScore() - score; i++) {
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    int s = Integer.parseInt(String.valueOf(textViewScore.getText())) + 1;
                    textViewBestScore.setText("" + s);
                }
            }, i * 8 + (40 / game.getMultiplier()));
        }
    }

    /**Gestisce la sconfitta*/
    public void gameOver() {
        Settings.setGamesPlayed(Settings.getGamesPlayed()+1);
        unlockAchievement(getResources().getString(R.string.achievement_impiccato));
        //Se è stato raggiunto un nuovo record e se si è connessi a GPG, carica il punteggio nella classifica relativa alla difficoltà giocata.
        if (googleApiClient.isConnected()) {
            switch (game.getDifficulty()){
                case 1:
                    Log.i("GameActivity", "Nuovo record facile");
                    Games.Leaderboards.submitScore(googleApiClient, getResources().getString(R.string.leaderboard_classifica_impiccato__facile), game.getScore());
                    break;
                case 2:
                    Log.i("GameActivity", "Nuovo record medio");
                    Games.Leaderboards.submitScore(googleApiClient, getResources().getString(R.string.leaderboard_classifica_impiccato__medio), game.getScore());
                    break;
                case 3:
                    Log.i("GameActivity", "Nuovo record difficile");
                    Games.Leaderboards.submitScore(googleApiClient, getResources().getString(R.string.leaderboard_classifica_impiccato__difficile), game.getScore());
                    break;
            }
        }

        bulbPulseTh.interrupt();
        end = true;
        tutorial = false;
        Chartboost.cacheRewardedVideo(CBLocation.LOCATION_MAIN_MENU);
        Log.i("GameActivity", "Game over!");
        hideBulb();
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
        textViewGuessedLetters.startAnimation(gameLost);
        textViewGuessedLetters.setText(game.getWordToGuess());

        loadSave();
        checkBestScore();

        // Controllo se ho raggiunto la fine del dizionario. Se si imposto la prima riga
        switch (game.getDifficulty()) {
            case 1: //Easy
                if (save.getRowToGuessEasy()+1 > dictionaryLength)
                    save.setRowToGuessEasy(1);
                else
                    save.setRowToGuessEasy(save.getRowToGuessEasy()+1);
                break;
            case 2: //Medium
                if (save.getRowToGuessMedium()+1 > dictionaryLength)
                    save.setRowToGuessMedium(1);
                else
                    save.setRowToGuessMedium(save.getRowToGuessMedium()+1);
                break;
            case 3: //Hard
                if (save.getRowToGuessHard()+1 > dictionaryLength)
                    save.setRowToGuessHard(1);
                else
                    save.setRowToGuessHard(save.getRowToGuessHard()+1);
                break;
        }
        //Salvo i dati
        Saver.saveData(save, new File(getApplicationContext().getExternalFilesDir(null), "save"));
        Log.i("GameActivity", "Ho salvato");
    }

    /**Gestisce la vittoria*/
    public void nextWord() {
        unlockAchievement(getResources().getString(R.string.achievement_sopravvissuto));
        if(game.getLives() == 7)
            unlockAchievement(getResources().getString(R.string.achievement_impeccabile));
        else if(game.getLives() == 1)
            unlockAchievement(getResources().getString(R.string.achievement_per_un_pelo));

        bulbPulseTh.interrupt();
        Chartboost.cacheRewardedVideo(CBLocation.LOCATION_MAIN_MENU);
        tutorial = false;
        textViewGuessedLetters.startAnimation(victory);
        victory.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { //Approfitto dell'animazione per lavorare sul salvataggio e caricare la parola successiva
                setKeyboardClickable(false);
                loadSave();
                checkBestScore();
                // Controllo se ho raggiunto la fine del dizionario. Se si imposto la prima riga
                switch (game.getDifficulty()) {
                    case 1: //Easy
                        if (save.getRowToGuessEasy()+1 > dictionaryLength) {
                            save.setRowToGuessEasy(1);
                            rowToGuess = 1;
                        } else {
                            save.setRowToGuessEasy(save.getRowToGuessEasy() + 1);
                            rowToGuess = save.getRowToGuessEasy();
                        }
                        break;
                    case 2: //Medium
                        if (save.getRowToGuessMedium()+1 > dictionaryLength) {
                            save.setRowToGuessMedium(1);
                            rowToGuess = 1;
                        } else {
                            save.setRowToGuessMedium(save.getRowToGuessMedium() + 1);
                            rowToGuess = save.getRowToGuessMedium();
                        }
                        break;
                    case 3: //Hard
                        if (save.getRowToGuessHard()+1 > dictionaryLength) {
                            save.setRowToGuessHard(1);
                            rowToGuess = 1;
                        } else {
                            save.setRowToGuessHard(save.getRowToGuessHard() + 1);
                            rowToGuess = save.getRowToGuessHard();
                        }
                        break;
                }
                //Salvo i dati
                Saver.saveData(save, new File(getApplicationContext().getExternalFilesDir(null), "save"));
                Log.i("GameActivity", "Ho salvato");

                game.setHintUsed(false);
                game.setLives(7);
                setWordToGuess();
                generateGuessedLetters();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Log.i("GameActivity", "Ho finito l'animazione");
                setUI();
                setBulbPulseThread();
                setKeyboardClickable(true);
                enableKeyboard();
                if(bulbPulseTh != null)
                    bulbPulseTh.start();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    /* Nasconde la lampadina animandone l'uscita */
    public void hideBulb() {
        Animation temp = bulbExit;
        bulb.startAnimation(bulbExit);
        findViewById(R.id.bulbBg).startAnimation(temp);
        bulbExit.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                findViewById(R.id.bulbBg).setVisibility(View.INVISIBLE);
                bulb.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    /* Mostra la lampadina con un'animazione d'entrata */
    public void showBulb() {
        bulb.setVisibility(View.VISIBLE);
        findViewById(R.id.bulbBg).setVisibility(View.VISIBLE);
        Animation temp = bulbEnter;
        findViewById(R.id.bulbBg).startAnimation(temp);
        bulb.startAnimation(bulbEnter);
    }

    /* Mostra il dialog per chiedere la recensione */
    public void showRateUsDialog() {
        final HDTMDialog dialog = new HDTMDialog(this);
        dialog.show();
        dialog.isRateUs(true);
        dialog.setTitle(getResources().getString(R.string.dialog_rate_us_title));
        dialog.setMessage(getResources().getString(R.string.dialog_rate_us_message));
        dialog.setPositiveBtn(getResources().getString(R.string.dialog_rate_us_positive_button), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("RateUsDialog", "Vado su Google Play");
                Settings.setRateUs(false);
                Settings.saveSettings(new File(getApplicationContext().getExternalFilesDir(null), "settings"));
                goToPlayStore();
                dialog.dismiss();
            }
        });
        dialog.setNeutralBtn(getResources().getString(R.string.dialog_rate_us_neutral_button), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                startDifficultyActivity();
            }
        });
        dialog.setNegativeBtn(getResources().getString(R.string.dialog_rate_us_negative_button), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Settings.setRateUs(false);
                Settings.saveSettings(new File(getApplicationContext().getExternalFilesDir(null), "settings"));
                dialog.dismiss();
                startDifficultyActivity();
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

    /* Va al Play Store */
    public void goToPlayStore() {
        Uri uri = Uri.parse("market://details?id=" + this.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + this.getPackageName())));
        }
    }

    /** Gestisco la pressione della lampadina */
    public void bulbOnClick(View view) {
        if (Chartboost.hasRewardedVideo(CBLocation.LOCATION_MAIN_MENU)) {
            Chartboost.showRewardedVideo(CBLocation.LOCATION_MAIN_MENU);
            Chartboost.cacheRewardedVideo(CBLocation.LOCATION_MAIN_MENU);
            bulb.setEnabled(false);
            game.setHintUsed(true);
            bulbPulseTh.interrupt();
        } else if (isNetworkAvailable()) {
            Chartboost.cacheRewardedVideo(CBLocation.LOCATION_MAIN_MENU);
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.unable_to_show_reward_video), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
        }
        /*if (mRewardedVid.isReady())
            mRewardedVid.showAd(this);
        *//*if (isNetworkAvailable())
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.unable_to_show_reward_video), Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();*/
    }

    /** Gestisce la pressione del tasto Gioca ancora */
    public void btnPlayAgainOnClick(View view) {
        ((android.os.Vibrator) this.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(25);
        if (Settings.hasToShowRateUs() || game.getScore() >= 1000)
            showRateUsDialog();
        else
            startDifficultyActivity();
    }

    public void startDifficultyActivity() {
        Intent intent = new Intent(this, DifficultyActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        this.finish();
    }

    /** Gestisce la pressione del tasto home */
    public void btnHomeOnClick(View view) {
        ((android.os.Vibrator) this.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(25);
        startActivity(new Intent(this, HomeActivity.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        this.finish();
    }

    /** Gestisce la pressione del tasto indietro */
    @Override
    public void onBackPressed() {
        // Se è aperto l'interstital lo chiude, altrimenti mostra il dialog per chiedere se tornare alla home oppure no
        if (Chartboost.onBackPressed()) {
            Log.i("Pubblicità", "Ho premuto indietro");
            return;
        } else if (!end){
            final HDTMDialog dialog = new HDTMDialog(this);
            dialog.show();
            dialog.setTitle(getResources().getString(R.string.dialog_go_home));
            dialog.setMessage(getResources().getString(R.string.dialog_data_will_be_lost));
            dialog.setPositiveBtn(getResources().getString(R.string.dialog_positive_button), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.close();
                    loadSave();
                    checkBestScore();

                    // Controllo se ho raggiunto la fine del dizionario. Se si imposto la prima riga
                    switch (game.getDifficulty()) {
                        case 1: //Easy
                            if (save.getRowToGuessEasy() + 1 > dictionaryLength)
                                save.setRowToGuessEasy(1);
                            else
                                save.setRowToGuessEasy(save.getRowToGuessEasy() + 1);
                            break;
                        case 2: //Medium
                            if (save.getRowToGuessMedium() + 1 > dictionaryLength)
                                save.setRowToGuessMedium(1);
                            else
                                save.setRowToGuessMedium(save.getRowToGuessMedium() + 1);
                            break;
                        case 3: //Hard
                            if (save.getRowToGuessHard() + 1 > dictionaryLength)
                                save.setRowToGuessHard(1);
                            else
                                save.setRowToGuessHard(save.getRowToGuessHard() + 1);
                            break;
                    }

                    //Salvo i dati
                    Saver.saveData(save, new File(getApplicationContext().getExternalFilesDir(null), "save"));
                    Log.i("GameActivity", "Ho salvato il salvataggio");
                    exitGame();

                }
            });
            dialog.setNegativeBtn(getResources().getString(R.string.dialog_negative_button), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.close();
                }
            });
        } else {
            exitGame();
        }
    }

    /** Metodo che controlla se lo score corrente è più alto del miglior score */
    public void checkBestScore() {
        switch (game.getDifficulty()) {
            case 1: //Easy
                if (game.getScore() > save.getBestScoreEasy())
                    save.setBestScoreEasy(game.getScore());
                break;
            case 2: //Medium
                if (game.getScore() > save.getBestScoreMedium())
                    save.setBestScoreMedium(game.getScore());
                break;
            case 3: //Hard
                if (game.getScore() > save.getBestScoreHard())
                    save.setBestScoreHard(game.getScore());
                break;
        }
        Log.i("GameActivity", "Ho controllato lo score migliore");
    }

    /** Carica il salvataggio */
    public void loadSave() {
        this.save = Loader.loadSave(new File(getApplicationContext().getExternalFilesDir(null), "save"));
        Log.i("GameActivity", "Ho caricato il salvataggio");
    }

    /** Esco dalla partita e vado sulla home */
    public void exitGame(){
        bulbPulseTh.interrupt();
        startActivity(new Intent(this, HomeActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        this.finish();
    }

    /** Metodo che controlla se è disponibile la connessione ad internet. Restituisce true se è disponibile, false altrimenti */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override

    public void onStart() {
        super.onStart();
        Chartboost.onStart(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Chartboost.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        Chartboost.onPause(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        Chartboost.onStop(this);
    }

 @Override
    protected void onDestroy() {
        super.onDestroy();
        Chartboost.onDestroy(this);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i("GameActivity", "Connesso a Google Play Games");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("GameActivity", "Connessione sospesa... Provo a riconnettere");
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("GameActivity", "Connection failed: " + connectionResult.getErrorMessage());
        if(connectionResult.hasResolution()){
            try{
                connectionResult.startResolutionForResult(this, 1);
                return;
            }catch (IntentSender.SendIntentException ex){
                googleApiClient.connect();
                return;
            }
        }
        else
            Log.e("GameActivity", "Errore PlayGames: " + connectionResult.getErrorMessage() + "(" + connectionResult.getErrorCode() + ")");
    }


}