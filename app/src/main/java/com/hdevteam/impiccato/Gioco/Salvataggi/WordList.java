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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/**
 * @author HDevTeam
 * Classe che consente di estrarre parole dal file contenente la lista delle parole.
 */
public class WordList {
    private File file;
    private int dim;

    public WordList(File file, int dim) {
        this.file = file;
        this.dim = dim;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public int getDim() {
        return dim;
    }

    public void setDim(int dim) {
        this.dim = dim;
    }

    public String getWordAt(int row) {
        if(row > dim)
            row = 1;
        String word = null;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            for(int i = 0; i < row; i++)
                word = reader.readLine();
            return word;
        } catch (Exception ex) {
            Log.e("WordList.getWordAt(" + row + ")", "Eccezione: " + ex.getMessage());
        }
        return word;
    }
    /**Restituisce la parola contenuta nella riga del file*/
    public static String getWordAt(InputStream is, int row) {
        String word = null;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            for(int i = 0; i < row; i++)
                word = reader.readLine();
            return word;
        } catch (Exception ex) {
            Log.e("WordList.getWordAt(" + row + ")", "Eccezione(static): " + ex.getMessage());
        }
        return word;
    }
}
