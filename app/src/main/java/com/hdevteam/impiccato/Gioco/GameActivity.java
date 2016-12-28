/*
 *
 * Created by HDev (Antonio Siciliani, Cosimo Mollica, Daniele Monaca, Domenico Ferraro, Marco De Caria)
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 11/12/16 19.53
 */

package com.hdevteam.impiccato.Gioco;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import com.hdevteam.impiccato.DifficultyActivity;
import com.hdevteam.impiccato.Gioco.Salvataggi.Loader;
import com.hdevteam.impiccato.Gioco.Salvataggi.Save;
import com.hdevteam.impiccato.Gioco.Salvataggi.Saver;
import com.hdevteam.impiccato.Gioco.Salvataggi.WordList;
import com.hdevteam.impiccato.HomeActivity;
import com.hdevteam.impiccato.R;
import com.hdevteam.impiccato.UI.HDTMDialog;

import com.chartboost.sdk.Chartboost;
import com.chartboost.sdk.CBLocation;
import com.chartboost.sdk.ChartboostDelegate;

/**
 * @author HDevTeam
 * Attività che consente di giocare al gioco dell'Impiccato.
 */

public class GameActivity extends AppCompatActivity {

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

    private Animation victory;
    private Animation gameLost;
    private Animation fadein;
    private Animation hideKeyboard;
    private Animation bulbPulse;

    private Thread bulbPulseTh;

    private String filename_dizionario;
    private int dictionaryLength;  //Numero di parole nel dizionario

    private boolean tutorial;
    private boolean end;
    private boolean needHelp;

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
        needHelp = false;

        victory = AnimationUtils.loadAnimation(this, R.anim.victory);
        gameLost = AnimationUtils.loadAnimation(this, R.anim.game_lost);
        fadein = AnimationUtils.loadAnimation(this, R.anim.fade_in_word_to_guess);
        hideKeyboard = AnimationUtils.loadAnimation(this, R.anim.hide_keyboard);
        bulbPulse = AnimationUtils.loadAnimation(this, R.anim.bulb_pulse);

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
        bulbPulseTh.start();
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
        try {
            game.setWordToGuess(WordList.getWordAt(getApplication().getAssets().open(filename_dizionario), rowToGuess));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i("GameActivity", "La parola da indovinare è " + game.getWordToGuess() + " alla riga "+rowToGuess);
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
        textViewGuessedLetters.setText(game.getGuessedLetters());
        textViewGuessedLetters.startAnimation(fadein);
        textViewMultiplier.setText("X1");
        textViewScore.setText("" + game.getScore());
        textViewBestScore.setText(""+game.getBestScore());
        game.setMultiplier(1);
        Log.i("GameActivity", "Imposto l'interfaccia utente");
    }

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
                                Log.i("ThreadBulbPulse", "Animo la lampadina");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        bulb.startAnimation(bulbPulse);
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

    /**Ottiene una lettera come ricompensa per aver aver chiesto un suggerimento e guardato la pubblicità*/
    public void rewardLetter() {
        int n;
        do {
            n = (int) (Math.random() * (game.getGuessedLetters().length() - 2)) + 1;
        } while(game.getGuessedLetters().charAt(n) != '_');
        showRewardLetter(game.getWordToGuess().charAt(n));
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
        if(game.tryLetter(letter)) {
            textViewGuessedLetters.setText(game.getGuessedLetters());
            if (game.checkBestScore()) {
                setBestScoreUI(score);
                game.setBestScore(game.getScore());
            }
            setScoreUI(score);
            Log.i("GameActivity", "Score: "+game.getScore());
            //textViewScore.setText(game.getScore()+"");
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
        bulbPulseTh.interrupt();
        end = true;
        Chartboost.cacheRewardedVideo(CBLocation.LOCATION_MAIN_MENU);
        Log.i("GameActivity", "Game over!");
        tutorial = false;
        bulb.setVisibility(View.GONE);
        hider.startAnimation(hideKeyboard);
        hideKeyboard.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                disableKeyboard();
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
        bulbPulseTh.interrupt();
        Chartboost.cacheRewardedVideo(CBLocation.LOCATION_MAIN_MENU);
        tutorial = false;
        //final int score = Integer.parseInt((String)textViewScore.getText());
        textViewGuessedLetters.startAnimation(victory);
        victory.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { //Approfitto dell'animazione per lavorare sul salvataggio e caricare la parola successiva
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
                enableKeyboard();
                bulbPulseTh.start();
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

    public void bulbOnClick(View view) {
        if (Chartboost.hasRewardedVideo(CBLocation.LOCATION_MAIN_MENU)) {
            Chartboost.showRewardedVideo(CBLocation.LOCATION_MAIN_MENU);
            Chartboost.cacheRewardedVideo(CBLocation.LOCATION_MAIN_MENU);
            bulb.setEnabled(false);
            game.setHintUsed(true);
            bulbPulseTh.interrupt();
        } else {
            // We don't have a cached video right now, but try to get one for next time
            if (isNetworkAvailable())
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.unable_to_show_reward_video), Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
            Chartboost.cacheRewardedVideo(CBLocation.LOCATION_MAIN_MENU);
        }
    }

    public void btnPlayAgainOnClick(View view) {
        ((android.os.Vibrator) this.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(25);
        startActivity(new Intent(this, DifficultyActivity.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        this.finish();
    }

    public void btnHomeOnClick(View view) {
        ((android.os.Vibrator) this.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(25);
        startActivity(new Intent(this, HomeActivity.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        this.finish();
    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        // If an interstitial is on screen, close it.
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

    public void exitGame(){
        //Esco dalla partita e vado sulla home
        bulbPulseTh.interrupt();
        startActivity(new Intent(this, HomeActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        this.finish();
    }

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
    public void onDestroy() {
        super.onDestroy();
        Chartboost.onDestroy(this);
    }
}