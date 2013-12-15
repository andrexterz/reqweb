/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufg.reqweb.model;

/**
 *
 * @author andre
 */
public enum Semestre {
    PRIMEIRO_SEMESTRE(1),
    SEGUNDO_SEMESTRE(2);
    
    private final int value;
    
    private Semestre(int value) {
        this.value = value;
    }
    
    public int getValue() {
        return value;
    }
    
    public static Semestre getSemestre(int value) {
        if (value == 1) {
            return Semestre.PRIMEIRO_SEMESTRE;
        } else if (value == 2) {
            return  Semestre.SEGUNDO_SEMESTRE;
        } else {
            return null;
        }
    }
    
}
