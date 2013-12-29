/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufg.reqweb.components;

import org.springframework.stereotype.Component;

/**
 *
 * @author André
 */
@Component
public class TesteComponentBean {
    public TesteComponentBean() {
        
    }
    
    public String retornaPagina(String page) {
        return page;
    }
}
