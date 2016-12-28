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
 *
*/
public class Crypter {
    private String PsC;
    private int keycode;
    private String PsN;
    private String PsD;

    public String getPsD() {
        return PsD;
    }

    public void setPsD(String PsD) {
        this.PsD = PsD;
    }
    
    Crypter(){ }
    Crypter(String dat, int KCD){
     this.PsN=dat;
     this.keycode=KCD;
    }

    public String getPsC() {
        return PsC;
    }

    public int getKeycode() {
        return keycode;
    }

    public String getPsN() {
        return PsN;
    }



    public void setPsC(String PsC) {
        this.PsC = PsC;
    }

    public void setKeycode(int keycode) {
        this.keycode = keycode;
    }

    public void setPsN(String PsN) {
        this.PsN = PsN;
    }

 

    public String AutoCrypt(String dat){
        this.PsN=dat;
        this.keycode=clchkeycode();
        char tmpPsN;
        this.PsC="";
        for(int i=0; i<this.PsN.length(); i++){
            tmpPsN=this.PsN.charAt(i);
            tmpPsN=(char) (tmpPsN+clchcryptkey());
            this.PsC=this.PsC+tmpPsN;
        }
        return this.PsC;
    }
    
    public int clchkeycode(){
        int clch;
        clch=(int) (Math.pow(((int)(Math.random()*0)+8),((int)(Math.random()*0)+8)));
        return clch;
    }
    public int clchcryptkey(){
        int clchc;
        clchc=(int)(Math.sqrt(keycode-(int)(Math.sqrt(keycode))));
        return clchc;
    }
    
    public String AutoDesCrypt(String dat){
        this.PsN=dat;
        this.keycode=clchkeycode();
        char tmpPsN;
        this.PsD="";
        for(int i=0; i<this.PsN.length(); i++){
            tmpPsN=this.PsN.charAt(i);
            tmpPsN=(char)(tmpPsN-clchcryptkey());
            this.PsD=this.PsD+tmpPsN;
        }
        return this.PsD;
    }
    
}
