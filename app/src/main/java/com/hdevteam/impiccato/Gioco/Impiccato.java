/*
 *
 * Created by HDev (Antonio Siciliani, Cosimo Mollica, Daniele Monaca, Domenico Ferraro, Marco De Caria)
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 11/12/16 19.53
 */

package com.hdevteam.impiccato.Gioco;

import android.util.Log;

/**
*  @author HDevTeam
 *  Classe che contiene i metodi e attributi per giocare al gioco dell'Impiccato
*/

public class Impiccato {

    private int difficulty; //1 = Easy; 2 = Medium; 3 = Hard
    private int lives;
    private int score;
    private int multiplier;
    private int bestScore;
    private String guessedLetters;
    private String wordToGuess;
    private boolean hintUsed;
    private boolean newBestScore;

    public Impiccato () {
        lives = 7;
        score = 0;
        hintUsed = false;
        multiplier = 1;
        newBestScore = false;
    }

    /**Crea una stringa di dimensione uguale alla parola da indovinare, che ha come prima e ultima lettera corrispondenti a quelle della parola, e il resto è riempito da underscore*/
    public void generateGuessedLetters() {
        String guessedLetters = "";
        guessedLetters += wordToGuess.charAt(0);
        for(int i=1; i<wordToGuess.length()-1; i++)
            guessedLetters += "_";
        guessedLetters += wordToGuess.charAt(wordToGuess.length()-1);
        this.guessedLetters = guessedLetters;
        Log.i("Impiccato", "Guessed letters: "+guessedLetters);
    }

    /**Metodo che inserisce la lettera suggerita*/
    public void giveRewardLetter(char rewardLetter) {
        for(int i=0; i<wordToGuess.length(); i++){
            if(rewardLetter == wordToGuess.charAt(i)){
                setGuessedLetters(i, rewardLetter);
            }
        }
    }

    /**Metodo che verifica se una lettera è contenuta nella parola da indovinare*/
    public boolean tryLetter(char letter){
        boolean ver = false;
        for(int i=1; i<wordToGuess.length()-1; i++){
            if(letter == wordToGuess.charAt(i)){
                if(letter != guessedLetters.charAt(i)) {
                    setGuessedLetters(i, letter);
                    score = score + (multiplier * 10);
                    multiplier++;
                }
                ver = true;
            }
        }
        if (!ver) {
            lives--;
            multiplier = 1;
        }
        return ver;
    }

    /** Imposta le lettere indovinate */
    public void setGuessedLetters(int indice, char letter) {
        String temp = "";
        for(int i=0; i<guessedLetters.length(); i++) {
            if(indice==i) {
                temp += letter;
            } else if(guessedLetters.charAt(i)!='_') {
                temp +=String.valueOf(guessedLetters.charAt(i));
            } else {
                temp +="_";
            }
        }
        this.guessedLetters = temp;
    }

    /**Ritorna true se le lettere indovinate corrispondono alla parola da indovinare, false altrimenti*/
    public boolean endGame() {
        if (guessedLetters.equalsIgnoreCase(wordToGuess))
            return true;
        return false;
    }

    /** Metodo che restituisce true se è stato totalizzato lo score migliore, false altrimenti */
    public boolean checkBestScore() {
        if (this.score > bestScore) {
            newBestScore = true;
            return true;
        }
        return false;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getBestScore() {
        return bestScore;
    }

    public void setBestScore(int bestScore) {
        this.bestScore = bestScore;
    }

    public String getGuessedLetters() {
        return guessedLetters;
    }

    public void setGuessedLetters(String guessedLetters) {
        this.guessedLetters = guessedLetters;
    }

    public String getWordToGuess() {
        return wordToGuess;
    }

    public void setWordToGuess(String wordToGuess) {
        this.wordToGuess = wordToGuess;
    }

    public boolean isHintUsed() {
        return hintUsed;
    }

    public void setHintUsed(boolean hintUsed) {
        this.hintUsed = hintUsed;
    }

    public int getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(int multiplier) {this.multiplier = multiplier;}

    public boolean isNewBestScore() {
        return newBestScore;
    }

    public void setNewBestScore(boolean newBestScore) {
        this.newBestScore = newBestScore;
    }
}
