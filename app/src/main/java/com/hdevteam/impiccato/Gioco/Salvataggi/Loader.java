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

            String rowToGuessEasy, rowToGuessMedium, rowToGuessHard, bestScoreEasy, bestScoreMedium, bestScoreHard;
            int rTGEasy, rTGMedium, rTGHard, bSEasy, bSMedium, bSHard;

            // Leggo i dati dal file
            rowToGuessEasy = reader.readLine();
            rowToGuessMedium = reader.readLine();
            rowToGuessHard = reader.readLine();
            bestScoreEasy = reader.readLine();
            bestScoreMedium = reader.readLine();
            bestScoreHard = reader.readLine();

            try {
                //Decripto i dati
                Crypter crypter = new Crypter();
                rTGEasy = Integer.parseInt(crypter.AutoDesCrypt(rowToGuessEasy));
                rTGMedium = Integer.parseInt(crypter.AutoDesCrypt(rowToGuessMedium));
                rTGHard = Integer.parseInt(crypter.AutoDesCrypt(rowToGuessHard));
                bSEasy = Integer.parseInt(crypter.AutoDesCrypt(bestScoreEasy));
                bSMedium = Integer.parseInt(crypter.AutoDesCrypt(bestScoreMedium));
                bSHard = Integer.parseInt(crypter.AutoDesCrypt(bestScoreHard));
                Log.i("Loader", "rowToGuessEasy: "+rTGEasy + "\trowToGuessMedium: "+rTGMedium + "\trowToGuessHard: "+rTGHard + "\nbestScoreEasy: "+bSEasy + "\tbestScoreMedium: "+bSMedium + "\tbestScoreHard: "+bSHard);
            } catch (Exception ex) {
                //I dati non sono criptati. Li riscrivo nel file ma criptati
                rTGEasy = Integer.parseInt(rowToGuessEasy);
                rTGMedium = Integer.parseInt(rowToGuessMedium);
                rTGHard = Integer.parseInt(rowToGuessHard);
                bSEasy = Integer.parseInt(bestScoreEasy);
                bSMedium = Integer.parseInt(bestScoreMedium);
                bSHard = Integer.parseInt(bestScoreHard);
                Log.i("Loader", ex.getMessage()+"\nrowToGuessEasy: "+rTGEasy + "\trowToGuessMedium: "+rTGMedium + "\trowToGuessHard: "+rTGHard + "\nbestScoreEasy: "+bSEasy + "\tbestScoreMedium: "+bSMedium + "\tbestScoreHard: "+bSHard);
                Saver.saveData(new Save(rTGEasy, rTGMedium, rTGHard, bSEasy, bSMedium, bSHard), file);
            }

            return new Save(rTGEasy, rTGMedium, rTGHard, bSEasy, bSMedium, bSHard);

            /* Vecchia lettura dei dati non criptati
            int rowToGuessEasy, rowToGuessMedium, rowToGuessHard, bestScoreEasy, bestScoreMedium, bestScoreHard;

            rowToGuessEasy = Integer.parseInt(reader.readLine());
            rowToGuessMedium = Integer.parseInt(reader.readLine());
            rowToGuessHard = Integer.parseInt(reader.readLine());
            bestScoreEasy = Integer.parseInt(reader.readLine());
            bestScoreMedium = Integer.parseInt(reader.readLine());
            bestScoreHard = Integer.parseInt(reader.readLine());

            //Lettura dei dati criptati
            *//*Crypter crypter = new Crypter();
            rowToGuessEasy = Integer.parseInt(crypter.AutoDesCrypt(reader.readLine()));
            rowToGuessMedium = Integer.parseInt(crypter.AutoDesCrypt(reader.readLine()));
            rowToGuessHard = Integer.parseInt(crypter.AutoDesCrypt(reader.readLine()));
            bestScoreEasy = Integer.parseInt(crypter.AutoDesCrypt(reader.readLine()));
            bestScoreMedium = Integer.parseInt(crypter.AutoDesCrypt(reader.readLine()));
            bestScoreHard = Integer.parseInt(crypter.AutoDesCrypt(reader.readLine()));*//*

            return new Save(rowToGuessEasy, rowToGuessMedium, rowToGuessHard, bestScoreEasy, bestScoreMedium, bestScoreHard);*/

        } catch (Exception ex) {
            Log.i("Loader", ex.getMessage());
            int randomRow = (int) (Math.random() * 30) + 1;
            Saver.saveData(new Save(randomRow, randomRow, randomRow, 0, 0, 0), file);
            return new Save(randomRow, randomRow, randomRow, 0, 0, 0);
        }
    }
}
