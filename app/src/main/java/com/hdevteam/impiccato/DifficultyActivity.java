/*
 *
 * Created by HDev (Antonio Siciliani, Cosimo Mollica, Daniele Monaca, Domenico Ferraro, Marco De Caria)
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 07/12/16 22.29
 */

package com.hdevteam.impiccato;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.hdevteam.impiccato.Gioco.GameActivity;
import com.hdevteam.impiccato.Gioco.Salvataggi.Loader;
import com.hdevteam.impiccato.Gioco.Salvataggi.Save;

import java.io.File;

public class DifficultyActivity extends AppCompatActivity {

    private final int EASY = 1;
    private final int MEDIUM = 2;
    private final int HARD = 3;

    private boolean tutorial;

    private Save save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_difficulty);
        tutorial = false;
        loadSave();

        if (save.getBestScoreEasy() == 0 && save.getBestScoreMedium() == 0 && save.getBestScoreHard() == 0)
            tutorial = true;

        if (tutorial)
            Log.i("DifficultyActivity", "E' la prima partita. Bisogna mostrare il tutorial");
        else
            Log.i("DifficultyActivity", "Non è la prima partita. Non bisogna mostrare il tutorial");

        TextView bestScoreEasy = (TextView) findViewById(R.id.bestScoreEasy);
        bestScoreEasy.setText(""+save.getBestScoreEasy());

        TextView bestScoreMedium = (TextView) findViewById(R.id.bestScoreMedium);
        bestScoreMedium.setText(""+save.getBestScoreMedium());

        TextView bestScoreHard = (TextView) findViewById(R.id.bestScoreHard);
        bestScoreHard.setText(""+save.getBestScoreHard());

    }

    /** Carica il salvataggio */
    public void loadSave() {
        this.save = Loader.loadSave(new File(getApplicationContext().getExternalFilesDir(null), "save"));
        Log.i("DifficultyActivity", "Ho caricato il salvataggio");
        Log.i("DifficultyActivity", "Best Score Facile: "+save.getBestScoreEasy()+"    Best Score Media: "+save.getBestScoreMedium()+"    Best Score Difficile: "+save.getBestScoreHard());

        // Fix per chi ha già scaricato l'applicazione
        if (save.getRowToGuessEasy() == 0)
            save.setRowToGuessEasy(1);

        if (save.getRowToGuessMedium() == 0)
            save.setRowToGuessMedium(1);

        if (save.getRowToGuessHard() == 0)
            save.setRowToGuessHard(1);

    }

    /** Metodo che gestisce la pressione del tasto della difficoltà Facile */
    public void btnEasyOnClick(View view) {
        ((android.os.Vibrator) this.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(25);
        Intent intent = new Intent(this, GameActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("difficulty", EASY);
        bundle.putInt("rowToGuess", save.getRowToGuessEasy());
        bundle.putBoolean("tutorial", tutorial);
        bundle.putInt("bestScore", save.getBestScoreEasy());
        bundle.putInt("currentScore", 0);
        intent.putExtras(bundle);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        this.finish();
    }

    /** Metodo che gestisce la pressione del tasto della difficoltà Media */
    public void btnMediumOnClick(View view) {
        ((android.os.Vibrator) this.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(25);
        Intent intent = new Intent(this, GameActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("difficulty", MEDIUM);
        bundle.putInt("rowToGuess", save.getRowToGuessMedium());
        bundle.putBoolean("tutorial", tutorial);
        bundle.putInt("bestScore", save.getBestScoreMedium());
        bundle.putInt("currentScore", 0);
        intent.putExtras(bundle);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        this.finish();
    }

    /** Metodo che gestisce la pressione del tasto della difficoltà Difficile */
    public void btnHardOnClick(View view) {
        ((android.os.Vibrator) this.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(25);
        Intent intent = new Intent(this, GameActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("difficulty", HARD);
        bundle.putInt("rowToGuess", save.getRowToGuessHard());
        bundle.putBoolean("tutorial", tutorial);
        bundle.putInt("bestScore", save.getBestScoreHard());
        bundle.putInt("currentScore", 0);
        intent.putExtras(bundle);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        this.finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, HomeActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        this.finish();
    }
}
