/*
 *
 * Created by HDev (Antonio Siciliani, Cosimo Mollica, Daniele Monaca, Domenico Ferraro, Marco De Caria)
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 11/12/16 19.53
 */

package com.hdevteam.impiccato.Gioco.Salvataggi;

/**
 * @author HDevTeam
 * Classe che rappresenta il salvataggio contenente i progressi.
 */
public class Save {

    private int rowToGuessEasy;
    private int rowToGuessMedium;
    private int rowToGuessHard;
    private int bestScoreEasy;
    private int bestScoreMedium;
    private int bestScoreHard;

    public Save(int rowToGuessEasy, int rowToGuessMedium, int rowToGuessHard, int bestScoreEasy, int bestScoreMedium, int bestScoreHard) {
        this.rowToGuessEasy = rowToGuessEasy;
        this.rowToGuessMedium = rowToGuessMedium;
        this.rowToGuessHard = rowToGuessHard;
        this.bestScoreEasy = bestScoreEasy;
        this.bestScoreMedium = bestScoreMedium;
        this.bestScoreHard = bestScoreHard;
    }

    public int getRowToGuessEasy() {
        return rowToGuessEasy;
    }

    public void setRowToGuessEasy(int rowToGuessEasy) {
        this.rowToGuessEasy = rowToGuessEasy;
    }

    public int getRowToGuessMedium() {
        return rowToGuessMedium;
    }

    public void setRowToGuessMedium(int rowToGuessMedium) {
        this.rowToGuessMedium = rowToGuessMedium;
    }

    public int getRowToGuessHard() {
        return rowToGuessHard;
    }

    public void setRowToGuessHard(int rowToGuessHard) {
        this.rowToGuessHard = rowToGuessHard;
    }

    public int getBestScoreEasy() {
        return bestScoreEasy;
    }

    public void setBestScoreEasy(int bestScoreEasy) {
        this.bestScoreEasy = bestScoreEasy;
    }

    public int getBestScoreMedium() {
        return bestScoreMedium;
    }

    public void setBestScoreMedium(int bestScoreMedium) {
        this.bestScoreMedium = bestScoreMedium;
    }

    public int getBestScoreHard() {
        return bestScoreHard;
    }

    public void setBestScoreHard(int bestScoreHard) {
        this.bestScoreHard = bestScoreHard;
    }

}
