package com.hdevteam.impiccato;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;


import java.lang.reflect.Field;

/*import com.hdevteam.impiccato.Gioco.GameActivity;
import com.hdevteam.impiccato.Options.CreditsActivity;*/
import com.hdevteam.impiccato.UI.HDTMButton;
import com.hdevteam.impiccato.UI.HDTMDialog;

public class HomeActivity extends AppCompatActivity {

    private HDTMButton singleplayerbtn;
    private HDTMButton multiplayerbtn;
    private HDTMButton playbtn;
    private HDTMButton creditsbtn;
    private ImageView life;
    private boolean home;       //Viene impostato a true quando è mostrata la home, false altrimenti

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        home = true;   //True perchè all'apertura l'applicazione mostra la home di default

        playbtn = (HDTMButton) findViewById(R.id.btnPlay);
        creditsbtn = (HDTMButton) findViewById(R.id.btnCredits);
        singleplayerbtn = (HDTMButton) findViewById(R.id.btnSingleplayer);
        multiplayerbtn = (HDTMButton) findViewById(R.id.btnMultiplayer);

        //new LoadDrawable().execute(new Integer(R.drawable.life1));

        Typeface font = Typeface.createFromAsset(getApplicationContext().getAssets(), "HDevTMPencil.ttf");
        try {
            Field f = Typeface.class.getDeclaredField("DEFAULT");
            f.setAccessible(true);
            f.set(null, font);
        } catch (NoSuchFieldException e) {
            Log.d("home", "font errore");
        } catch (IllegalAccessException e) {
            Log.d("home", "font errore");
        }
        //setLifeImage(1);
        //setLifeImage(Loader.loadSave(new File(getApplicationContext().getExternalFilesDir(null), "save")).getLives());
    }

    /** Se è sulla home alla pressione del tasto indietro chiude l'app, altrimenti mostra la home */
    @Override
    public void onBackPressed() {
        if (home) {
            final HDTMDialog dialog = new HDTMDialog(this);
            dialog.setTitle("Vuoi uscire");
            dialog.setPositiveButton("Si", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    System.exit(0);
                }
            });
            dialog.setNegativeButton("No", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        } else {
            home = true;
            showHomeUI();
        }
    }

    /**Apre la schermata del gioco*/
    public void btnPlayOnClick(View view) {
        ((android.os.Vibrator) this.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(25);
        home = false;
        showSingleMultiButtons();
    }

    /** Gestisce la pressione del tasto singleplayer */
    public void btnSinglePlayerOnClick(View view) {
        ((android.os.Vibrator) this.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(25);
        //playSinglePlayer();
    }

    /** Gestisce la pressione del tasto multiplayer */
    public void btnMultiPlayerOnClick(View view) {
        ((android.os.Vibrator) this.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(25);
        //playMultiPlayer();
    }

    /** Fa partire l'activity del singleplayer e chiude l'activity corrente */
    /*private void playSinglePlayer() {
        startActivity(new Intent(this, DifficultyActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        this.finish();
    }

    *//** Fa partire l'activity del multiplayer e chiude l'activity corrente *//*
    private void playMultiPlayer() {
        startActivity(new Intent(this, GameActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        this.finish();
    }*/

    /** Mostra i bottoni singleplayer e multiplayer e nasconde i bottoni gioca e credits*/
    private void showSingleMultiButtons(){
        playbtn.setVisibility(HDTMButton.INVISIBLE);
        creditsbtn.setVisibility(HDTMButton.INVISIBLE);
        singleplayerbtn.setVisibility(HDTMButton.VISIBLE);
        multiplayerbtn.setVisibility(HDTMButton.VISIBLE);
    }

    /** Mostra i bottoni gioca e credits e nasconde i bottoni singleplayer e multiplayer*/
    private void showHomeUI(){
        playbtn.setVisibility(HDTMButton.VISIBLE);
        creditsbtn.setVisibility(HDTMButton.VISIBLE);
        singleplayerbtn.setVisibility(HDTMButton.INVISIBLE);
        multiplayerbtn.setVisibility(HDTMButton.INVISIBLE);
    }

    /**Apre la schermata dei crediti*/
    public void btnCreditsOnClick(View view) {
        ((android.os.Vibrator) this.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(25);
        /*startActivity(new Intent(this, CreditsActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        this.finish();*/
    }

    /** Metodo che gestisce la pressione dell'icona di facebook */
    public void facebookOnClick(View view) {
        ((android.os.Vibrator) this.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(25);
        Uri uri = Uri.parse("https://www.facebook.com/hdevteam");
        PackageManager pm = getApplicationContext().getPackageManager();
        try {
            ApplicationInfo applicationInfo = pm.getApplicationInfo("com.facebook.katana", 0);
            if (applicationInfo.enabled)
                uri = Uri.parse("fb://facewebmodal/f?href=https://www.facebook.com/hdevteam");

        } catch (PackageManager.NameNotFoundException ignored) { }
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }

    /** Metodo che gestisce la pressione dell'icona di instagram */
    public void instagramOnClick(View view) {
        ((android.os.Vibrator) this.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(25);
        Intent intent;
        try {
            getPackageManager().getPackageInfo("com.instagram.android", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/_u/hdevteam"));
        } catch (Exception e) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://instagram.com/hdevteam"));
        }
        startActivity(intent);
    }

    private class LoadDrawable extends AsyncTask<Integer, Void, Drawable[]> {
        @Override
        protected Drawable[] doInBackground(Integer... ids) {
            //Loading the drawable in the background
            Drawable[] loadedDrawables = new Drawable[ids.length];
            for (int i=0; i<ids.length; i++)
                loadedDrawables[i] = ResourcesCompat.getDrawable(getResources(), ids[i], null);
            //After the drawable is loaded, onPostExecute is called
            Log.i("doInBackground|Home", "Ho caricato in background i drawables");

            return loadedDrawables;
        }

        @Override
        protected void onPostExecute(Drawable[] loadedDrawables) {
            //cloud1.setImageDrawable(loadedClouds);
            //cloud1.startAnimation(moveCloud1);
            //for (int i=0; i<loadedClouds.length; i++)
            //    Log.i("onPostExecute", "Fatto! "+loadedClouds[i]);
            life.setImageDrawable(loadedDrawables[0]);
        }

        @Override
        protected void onPreExecute() {
            playbtn = (HDTMButton) findViewById(R.id.btnPlay);
            creditsbtn = (HDTMButton) findViewById(R.id.btnCredits);
            singleplayerbtn = (HDTMButton) findViewById(R.id.btnSingleplayer);
            multiplayerbtn = (HDTMButton) findViewById(R.id.btnMultiplayer);
            life = (ImageView) findViewById(R.id.life);
        }

        @Override
        protected void onProgressUpdate(Void... values) {}
    }
}
