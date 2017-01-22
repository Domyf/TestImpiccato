/*
 *
 * Created by HDev (Antonio Siciliani, Cosimo Mollica, Daniele Monaca, Domenico Ferraro, Marco De Caria)
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 10/01/17 19.06
 */

package com.hdevteam.impiccato.Gioco.Salvataggi;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class Settings {

    private static boolean autoGPGConnection = false;   //True se l'app deve collegarsi automaticamente a Play Games, false altrimenti. Salvato nel file.
    private static boolean GPGConnection = false;       //True se l'app è riuscita a connettersi a Play Games, False altrimenti.
    private static boolean rateUs = true;               //True se bisogna mostrare il rateUsDialog, false altrimenti. Salvato nel file.
    private static boolean firstRun = true;             //True se è la prima volta che l'app raggiunge la home, false altrimenti.
    private static int gamesPlayed = 0;                 //Rappresenta quante partite sono state giocate nella corrente esecuzione dell'app. Viene incrementato al game over

    /* Carico le impostazioni (autoGPGConnection e rateUS) */
    public static void LoadSettings(File file) {
        Log.i("Settings", "Carico le impostazioni...");
        try{
            BufferedReader reader = new BufferedReader(new FileReader(file));
            //Lettura dei dati criptati
            Crypter crypter = new Crypter();
            autoGPGConnection = Boolean.valueOf(crypter.AutoDesCrypt(reader.readLine()));
            rateUs = Boolean.valueOf(crypter.AutoDesCrypt(reader.readLine()));
            Log.i("Settings", "Impostazioni caricate!\n"+"Connessione automatica a Google Play Games: " + autoGPGConnection+"\nMostrare dialog rate us: "+rateUs);
        } catch(FileNotFoundException ex){
            Log.w("Settings", "File impostazioni inesistente");
            ResetSettings(file);
        } catch(Exception ex){
            Log.e("Settings", "Errore: " + ex.getLocalizedMessage());
            //Se c'è qualche problema, resetto le impostazioni nel file e ritorno le impostazioni di default
            ResetSettings(file);
        }
    }

    /* Salvo le impostazioni (autoGPGConnection e rateUS)*/
    public static boolean saveSettings(File file){
        Log.i("Settings", "Salvo le impostazioni...");
        try{
            PrintWriter writer = new PrintWriter(file);
            //Scrittura dei dati criptati
            Crypter crypter = new Crypter();
            writer.println(crypter.AutoCrypt(String.valueOf(autoGPGConnection)));
            writer.println(crypter.AutoCrypt(String.valueOf(rateUs)));
            writer.close();
            return true;
        }catch(FileNotFoundException ex){
            Log.i("Settings", "Errore salvataggio: " + ex.getLocalizedMessage());
            if(CreateFileSettings(file))
                saveSettings(file);
            else
                return false;
        }
        return true;
    }

    /* Creo il file delle impostazioni. Restituisco true se ci riesco, false altrimenti*/
    private static boolean CreateFileSettings(File dir){
        try{
            if(!dir.createNewFile())
                return false;
            else
                return true;
        }catch(IOException ex){
            return false;
        }
    }

    /* Resetto le impostazioni e scrivo il file */
    public static void ResetSettings(File file){
        autoGPGConnection = false;
        GPGConnection = false;
        rateUs = true;
        firstRun = true;
        saveSettings(file);
    }

    public static boolean hasToShowRateUs() {
        if (gamesPlayed % 2 == 0)
            return true;
        return false;
    }

    public static boolean isRateUs() {
        return rateUs;
    }

    public static void setRateUs(boolean rateUs) {
        Settings.rateUs = rateUs;
    }

    public static boolean isAutoGPGConnected(){
        return autoGPGConnection;
    }

    public static void setAutoGPGConnection(boolean connection){
        autoGPGConnection = connection;
    }

    public static boolean isGPGConnected(){
        return GPGConnection;
    }

    public static void setGPGConnection(boolean connection){
        GPGConnection = connection;
    }

    public static boolean isFirstRun(){
        return firstRun;
    }

    public static void setFirstRun(boolean run){
        firstRun = run;
    }

    public static int getGamesPlayed() {
        return gamesPlayed;
    }

    public static void setGamesPlayed(int gamesPlayed) {
        Settings.gamesPlayed = gamesPlayed;
    }
}
