/*
 *
 * Created by HDev (Antonio Siciliani, Cosimo Mollica, Daniele Monaca, Domenico Ferraro, Marco De Caria)
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 11/12/16 19.53
 */

package com.hdevteam.impiccato.Gioco.Salvataggi;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * @author HDevTeam
 * Classe che permette di caricare in memoria i progressi contenuti nel file di salvataggio.
 *
 */
public class Loader {

    /**Ritorna un oggetto Save contenente i progressi salvati all'interno del file di salvataggio*/
    public static Save loadSave(File file) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));

            int rowToGuessEasy, rowToGuessMedium, rowToGuessHard, bestScoreEasy, bestScoreMedium, bestScoreHard;

            /*Crypter crypter = new Crypter();
            rowToGuess = Integer.parseInt(crypter.AutoDesCrypt(reader.readLine()));
            score = Integer.parseInt(crypter.AutoDesCrypt(reader.readLine()));
            attempts = Integer.parseInt(crypter.AutoDesCrypt(reader.readLine()));
            lives = Integer.parseInt(crypter.AutoDesCrypt(reader.readLine()));
            guessedLetters = crypter.AutoDesCrypt(reader.readLine());*/
            rowToGuessEasy = Integer.parseInt(reader.readLine());
            rowToGuessMedium = Integer.parseInt(reader.readLine());
            rowToGuessHard = Integer.parseInt(reader.readLine());
            bestScoreEasy = Integer.parseInt(reader.readLine());
            bestScoreMedium = Integer.parseInt(reader.readLine());
            bestScoreHard = Integer.parseInt(reader.readLine());
            //return new Save(rowToGuess, score, attempts, lives, won, lost, guessedLetters, null);
            return new Save(rowToGuessEasy, rowToGuessMedium, rowToGuessHard, bestScoreEasy, bestScoreMedium, bestScoreHard);
        } catch (Exception ex) {
            Log.i("Loader", "Exception");
            int randomRow = (int) (Math.random() * 10) + 1;
            Saver.saveData(new Save(randomRow, randomRow, randomRow, 0, 0, 0), file);
            return new Save(randomRow, randomRow, randomRow, 0, 0, 0);
        }
    }
}
