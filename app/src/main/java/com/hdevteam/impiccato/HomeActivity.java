/*
 *
 * Created by HDev (Antonio Siciliani, Cosimo Mollica, Daniele Monaca, Domenico Ferraro, Marco De Caria)
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 10/12/16 15.39
 */

package com.hdevteam.impiccato;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.hdevteam.impiccato.Gioco.Salvataggi.Loader;
import com.hdevteam.impiccato.Gioco.Salvataggi.Save;
import com.hdevteam.impiccato.Gioco.Salvataggi.Settings;
import com.hdevteam.impiccato.UI.HDTMButton;
import com.hdevteam.impiccato.UI.HDTMDialog;
import com.hdevteam.impiccato.UI.HDTMTextView;

import java.io.File;

public class HomeActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private HDTMButton singleplayerbtn;
    private HDTMButton multiplayerbtn;
    private HDTMButton playbtn;
    private HDTMButton creditsbtn;

    private HDTMTextView txtViewPGConnected;

    private Animation buttonIn;
    private Animation buttonOut;
    private Animation dialogAnim;
    private Animation achievementsEnter;
    private Animation leaderboardsEnter;
    private Animation achievementsExit;
    private Animation leaderboardsExit;
    private Animation googlePlayButtonExit;
    private Animation googlePlayButtonEnter;

    private GoogleApiClient googleApiClient;

    private boolean home;       //Viene impostato a true quando è mostrata la home, false altrimenti

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i("HomeActivity", "Connesso a Google Play Games");
        if (!Settings.isGPGConnected()) {
            txtViewPGConnected.setText(getString(R.string.pg_connected));
            txtViewPGConnected.setVisibility(View.VISIBLE);
            txtViewPGConnected.startAnimation(dialogAnim);
        }
        Settings.setGPGConnection(true);
        Settings.setAutoGPGConnection(true);
        Settings.saveSettings(new File(getApplicationContext().getExternalFilesDir(null), "settings"));
        syncScores();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("HomeActivity", "Connessione sospesa... Provo a riconnettere");
        googleApiClient.connect();
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("HomeActivity", "Connection failed: " + connectionResult.getErrorMessage());
        if (!Settings.isGPGConnected()) {
            txtViewPGConnected.setText(getString(R.string.pg_disconnected));
            txtViewPGConnected.setVisibility(View.VISIBLE);
            txtViewPGConnected.startAnimation(dialogAnim);
        }
        Settings.setGPGConnection(false);
        Settings.setAutoGPGConnection(false);
        Settings.saveSettings(new File(getApplicationContext().getExternalFilesDir(null), "settings"));
        if(connectionResult.hasResolution()){
            try {
                connectionResult.startResolutionForResult(this, 1001);
                return;
            } catch (IntentSender.SendIntentException ex){
                googleApiClient.connect();
                if(googleApiClient.isConnected())
                    onConnected(null);
                return;
            }
        } else {
            Log.e("HomeActivity", "Errore PlayGames: " + connectionResult.getErrorMessage() + "(" + connectionResult.getErrorCode() + ")");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        home = true;   //True perchè all'apertura l'applicazione mostra la home di default

        playbtn = (HDTMButton) findViewById(R.id.btnPlay);
        creditsbtn = (HDTMButton) findViewById(R.id.btnCredits);
        singleplayerbtn = (HDTMButton) findViewById(R.id.btnSingleplayer);
        multiplayerbtn = (HDTMButton) findViewById(R.id.btnMultiplayer);

        txtViewPGConnected = (HDTMTextView) findViewById(R.id.txtViewPGConnected);

        buttonIn = AnimationUtils.loadAnimation(this, R.anim.button_in);
        buttonOut = AnimationUtils.loadAnimation(this, R.anim.button_out);
        achievementsEnter = AnimationUtils.loadAnimation(this, R.anim.achievements_enter);
        leaderboardsEnter = AnimationUtils.loadAnimation(this, R.anim.leaderboards_enter);
        achievementsExit = AnimationUtils.loadAnimation(this, R.anim.achievements_exit);
        leaderboardsExit = AnimationUtils.loadAnimation(this, R.anim.leaderboards_exit);
        googlePlayButtonExit = AnimationUtils.loadAnimation(this, R.anim.googleplay_button_exit);
        googlePlayButtonEnter = AnimationUtils.loadAnimation(this, R.anim.googleplay_button_enter);
        dialogAnim = AnimationUtils.loadAnimation(this, R.anim.home_dialog_anim);
        dialogAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                txtViewPGConnected.setVisibility(View.GONE);
                if (googleApiClient.isConnected())
                    hideGPGButton();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .build();

        if (Settings.isFirstRun()) { //Se è il primo avvio
            Log.i("Home", "E' la prima volta che mostro la home");
            Settings.LoadSettings(new File(getApplicationContext().getExternalFilesDir(null), "settings"));
            showGPGButton();
            Settings.setFirstRun(false);
            if (Settings.isAutoGPGConnected()) { //Se è il primo avvio ed è impostata la connessione automatica
                Log.i("Home", "Connessione automatica attiva. Mi connetto a Play Games");
                googleApiClient.connect();
            } else {
                txtViewPGConnected.setText(getString(R.string.pg_connection_request));
                txtViewPGConnected.setVisibility(View.VISIBLE);
                txtViewPGConnected.startAnimation(dialogAnim);
            }
        } else if (Settings.isGPGConnected()) { //Se si è già connesso in precedenza e non è la prima volta che va alla home
            findViewById(R.id.playgamesBg).setVisibility(View.GONE);
            findViewById(R.id.btnPlayGamesAccess).setVisibility(View.GONE);
            findViewById(R.id.achievementsBg).setVisibility(View.VISIBLE);
            findViewById(R.id.btnAchievements).setVisibility(View.VISIBLE);
            findViewById(R.id.leaderboardsbg).setVisibility(View.VISIBLE);
            findViewById(R.id.btnShowLeaderboards).setVisibility(View.VISIBLE);
            /*showLeaderboardsButton();
            showAchievementsButton();*/
            googleApiClient.connect();
        } else {
            Log.i("Home", "Non è la prima volta che mostro la home");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    /**
     * Se è sulla home alla pressione del tasto indietro chiude l'app, altrimenti mostra la home
     */
    @Override
    public void onBackPressed() {
        if (home) {
            final HDTMDialog dialog = new HDTMDialog(this);
            dialog.show();
            dialog.setTitle(getResources().getString(R.string.dialog_want_exit));
            dialog.setPositiveBtn(getResources().getString(R.string.dialog_positive_button), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.close();
                    System.exit(0);
                }
            });
            dialog.setNegativeBtn(getResources().getString(R.string.dialog_negative_button), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.close();
                }
            });
        } else {
            home = true;
            showHomeUI();
        }
    }

    /**
     * Apre la schermata del gioco
     */
    public void btnPlayOnClick(View view) {
        ((android.os.Vibrator) this.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(25);
        showSingleMultiButtons();
        home = false;
    }

    /**
     * Gestisce la pressione del tasto singleplayer
     */
    public void btnSinglePlayerOnClick(View view) {
        ((android.os.Vibrator) this.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(25);
        startActivity(new Intent(this, DifficultyActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        this.finish();
    }

    /**
     * Gestisce la pressione del tasto multiplayer
     */
    public void btnMultiPlayerOnClick(View view) {
        ((android.os.Vibrator) this.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(25);
        startActivity(new Intent(this, BluetoothModeActivity.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        this.finish();
    }

    /**
     * Mostra i bottoni singleplayer e multiplayer e nasconde i bottoni gioca e credits
     */
    private void showSingleMultiButtons() {
        singleplayerbtn.setVisibility(HDTMButton.VISIBLE);
        multiplayerbtn.setVisibility(HDTMButton.VISIBLE);
        animatePlayCreditsOut();
        animateSingleMultiIn();
    }

    /**
     * Mostra i bottoni gioca e credits e nasconde i bottoni singleplayer e multiplayer
     */
    private void showHomeUI() {
        playbtn.setVisibility(HDTMButton.VISIBLE);
        creditsbtn.setVisibility(HDTMButton.VISIBLE);
        animateSingleMultiOut();
        animatePlayCreditsIn();
    }

    private void animateSingleMultiOut() {
        singleplayerbtn.startAnimation(buttonOut);
        multiplayerbtn.startAnimation(buttonOut);
        buttonOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                singleplayerbtn.setVisibility(HDTMButton.INVISIBLE);
                multiplayerbtn.setVisibility(HDTMButton.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void animateSingleMultiIn() {
        singleplayerbtn.startAnimation(buttonIn);
        multiplayerbtn.startAnimation(buttonIn);
        buttonIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void animatePlayCreditsOut() {
        playbtn.startAnimation(buttonOut);
        creditsbtn.startAnimation(buttonOut);
        buttonOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                playbtn.setVisibility(HDTMButton.INVISIBLE);
                creditsbtn.setVisibility(HDTMButton.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void animatePlayCreditsIn() {
        playbtn.startAnimation(buttonIn);
        creditsbtn.startAnimation(buttonIn);
        buttonIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    /**
     * Apre la schermata dei crediti
     */
    public void btnCreditsOnClick(View view) {
        ((android.os.Vibrator) this.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(25);
        startActivity(new Intent(this, CreditsActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        this.finish();
    }

    /**
     * Metodo che gestisce la pressione dell'icona di facebook
     */
    public void facebookOnClick(View view) {
        ((android.os.Vibrator) this.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(25);
        Uri uri = Uri.parse(getString(R.string.facebook_url));
        PackageManager pm = getApplicationContext().getPackageManager();
        try {
            ApplicationInfo applicationInfo = pm.getApplicationInfo("com.facebook.katana", 0);
            if (applicationInfo.enabled)
                uri = Uri.parse("fb://facewebmodal/f?href=" + getString(R.string.facebook_url));

        } catch (PackageManager.NameNotFoundException ignored) {
        }
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }

    /**
     * Metodo che gestisce la pressione dell'icona di instagram
     */
    public void instagramOnClick(View view) {
        ((android.os.Vibrator) this.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(25);
        Intent intent;
        try {
            getPackageManager().getPackageInfo("com.instagram.android", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.instagram_app_url)));
        } catch (Exception e) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.instagram_browser_url)));
        }
        startActivity(intent);
    }

    public void btnPlayGamesOnClick(View view) {
        ((android.os.Vibrator) this.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(25);
        //startActivity(new Intent(this, AchievementsActivity.class));
        if(!googleApiClient.isConnected()) {
            googleApiClient.connect();
        }
    }

    public void btnShowLeaderboardsOnClick(View view) {
        ((android.os.Vibrator) this.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(25);
        startActivityForResult(Games.Leaderboards.getAllLeaderboardsIntent(googleApiClient), 1);
    }

    public void btnAchievementsOnClick(View view) {
        ((android.os.Vibrator) this.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(25);
        if(googleApiClient.isConnected()) {
            //startActivity(new Intent(this, AchievementsActivity.class));
            startActivityForResult(Games.Achievements.getAchievementsIntent(googleApiClient), 1);
            /*overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            this.finish();*/
        }
    }

    public void showGPGButton() {
        findViewById(R.id.playgamesBg).setVisibility(View.VISIBLE);
        findViewById(R.id.btnPlayGamesAccess).setVisibility(View.VISIBLE);

        Animation temp = googlePlayButtonEnter;
        findViewById(R.id.playgamesBg).startAnimation(temp);
        findViewById(R.id.btnPlayGamesAccess).startAnimation(googlePlayButtonEnter);
    }

    public void hideGPGButton() {
        googlePlayButtonExit.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                findViewById(R.id.playgamesBg).setVisibility(View.GONE);
                findViewById(R.id.btnPlayGamesAccess).setVisibility(View.GONE);
                showAchievementsButton();
                showLeaderboardsButton();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        Animation temp = googlePlayButtonExit;
        findViewById(R.id.playgamesBg).startAnimation(temp);
        findViewById(R.id.btnPlayGamesAccess).startAnimation(googlePlayButtonExit);
    }

    public void showAchievementsButton() {
        findViewById(R.id.achievementsBg).setVisibility(View.VISIBLE);
        findViewById(R.id.btnAchievements).setVisibility(View.VISIBLE);

        Animation temp = achievementsEnter;
        findViewById(R.id.achievementsBg).startAnimation(temp);
        findViewById(R.id.btnAchievements).startAnimation(achievementsEnter);
    }

    public void showLeaderboardsButton() {
        findViewById(R.id.leaderboardsbg).setVisibility(View.VISIBLE);
        findViewById(R.id.btnShowLeaderboards).setVisibility(View.VISIBLE);

        Animation temp = leaderboardsEnter;
        findViewById(R.id.leaderboardsbg).startAnimation(temp);
        findViewById(R.id.btnShowLeaderboards).startAnimation(leaderboardsEnter);
    }

    public void hideAchievementsButton() {
        Animation temp = achievementsExit;
        findViewById(R.id.achievementsBg).startAnimation(temp);
        findViewById(R.id.btnAchievements).startAnimation(achievementsExit);
        achievementsExit.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                findViewById(R.id.achievementsBg).setVisibility(View.INVISIBLE);
                findViewById(R.id.btnAchievements).setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void hideLeaderboardsButton() {
        Animation temp = leaderboardsExit;
        findViewById(R.id.leaderboardsbg).startAnimation(temp);
        findViewById(R.id.btnShowLeaderboards).startAnimation(leaderboardsExit);
        achievementsExit.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                findViewById(R.id.leaderboardsbg).setVisibility(View.INVISIBLE);
                findViewById(R.id.btnShowLeaderboards).setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    /**Sincronizza gli score salvati con la classifica online*/
    private void syncScores(){
        Save save = Loader.loadSave(new File(getApplicationContext().getExternalFilesDir(null), "save"));
        if(googleApiClient.isConnected()){
            Games.Leaderboards.submitScore(googleApiClient, getResources().getString(R.string.leaderboard_classifica_impiccato__facile), save.getBestScoreEasy());
            Games.Leaderboards.submitScore(googleApiClient, getResources().getString(R.string.leaderboard_classifica_impiccato__medio), save.getBestScoreMedium());
            Games.Leaderboards.submitScore(googleApiClient, getResources().getString(R.string.leaderboard_classifica_impiccato__difficile), save.getBestScoreHard());
            Log.i("HomeActivity", "Scores caricati!");
        }
    }
}
