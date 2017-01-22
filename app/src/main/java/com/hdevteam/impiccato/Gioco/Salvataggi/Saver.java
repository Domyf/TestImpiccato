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
            //Scrittura dei dati non criptati:
            /*writer.println(String.valueOf(save.getRowToGuessEasy()));
            writer.println(String.valueOf(save.getRowToGuessMedium()));
            writer.println(String.valueOf(save.getRowToGuessHard()));
            writer.println(String.valueOf(save.getBestScoreEasy()));
            writer.println(String.valueOf(save.getBestScoreMedium()));
            writer.println(String.valueOf(save.getBestScoreHard()));*/

            //Scrittura dei dati criptati
            Crypter crypter = new Crypter();
            writer.println(crypter.AutoCrypt(String.valueOf(save.getRowToGuessEasy())));
            writer.println(crypter.AutoCrypt(String.valueOf(save.getRowToGuessMedium())));
            writer.println(crypter.AutoCrypt(String.valueOf(save.getRowToGuessHard())));
            writer.println(crypter.AutoCrypt(String.valueOf(save.getBestScoreEasy())));
            writer.println(crypter.AutoCrypt(String.valueOf(save.getBestScoreMedium())));
            writer.println(crypter.AutoCrypt(String.valueOf(save.getBestScoreHard())));

            //Chiusura del flusso
            writer.close();
        } catch (Exception e) {
            //Da implementare
        }
    }
}
