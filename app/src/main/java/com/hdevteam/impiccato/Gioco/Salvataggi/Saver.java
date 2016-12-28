/*
 *
 * Created by HDev (Antonio Siciliani, Cosimo Mollica, Daniele Monaca, Domenico Ferraro, Marco De Caria)
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 11/12/16 19.53
 */

package com.hdevteam.impiccato.Gioco.Salvataggi;
import java.io.File;
import java.io.PrintWriter;

/**
 * @author HDevTeam
 * Classe che si occupa del salvataggio dei progressi.
 */
public class Saver {

    /**Salva il salvataggio passato come argomento all'interno del file*/
    public static void saveData(Save save, File dir) {
        try {
            PrintWriter writer = new PrintWriter(dir);
            //Scrittura dei dati:
            writer.println(String.valueOf(save.getRowToGuessEasy()));
            writer.println(String.valueOf(save.getRowToGuessMedium()));
            writer.println(String.valueOf(save.getRowToGuessHard()));
            writer.println(String.valueOf(save.getBestScoreEasy()));
            writer.println(String.valueOf(save.getBestScoreMedium()));
            writer.println(String.valueOf(save.getBestScoreHard()));

            //Scrittura dei dati criptati
            //Crypter crypter = new Crypter();
            /*writer.println(new Crypter().AutoCrypt(""+save.getRowToGuess()));
            writer.println(new Crypter().AutoCrypt(""+save.getScore()));
            writer.println(new Crypter().AutoCrypt(""+save.getAttempts()));*/
            //Chiusura del flusso
            writer.close();
        } catch (Exception e) {
            //Da implementare
        }
    }
    /**Azzera i progressi contenuti nel file di salvataggio*/
    public static Save reset(File dir) {
        int randomRow = (int) (Math.random() * 100);
        try {
            PrintWriter writer = new PrintWriter(dir);
            writer.println("" + randomRow);
            writer.println("" + randomRow);
            writer.println("" + randomRow);
            writer.println("" + 0); //Best Score Easy
            writer.println("" + 0); //Best Score Medium
            writer.println("" + 0); //Best Score Hard
        } catch (Exception e) {}
        return new Save(randomRow, randomRow, randomRow, 0, 0, 0);
    }
}
