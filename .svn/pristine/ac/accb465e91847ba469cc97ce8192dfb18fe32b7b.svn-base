/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.reqweb.components;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author andre
 */
@Component
public class PerfilBean implements Serializable{
    
    private Map<String, String> perfis;

    public PerfilBean() {
        this.perfis = new HashMap<String,String>();
        this.perfis.put("001", "Administrador");
        this.perfis.put("002", "Coordenador de Curso");
        this.perfis.put("003", "Coordenador de Estágio");
        this.perfis.put("101", "docente");
        this.perfis.put("201", "técnico-administrativo");
        this.perfis.put("500", "discente");

    }

    /**
     * @return the perfis
     */
    public Map<String, String> getPerfis() {
        return perfis;
    }
     
}
