/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.reqweb.model;

import java.io.Serializable;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
//import javax.persistence.GeneratedValue;
//import javax.persistence.GenerationType;
//import javax.persistence.Id;
//import javax.persistence.SequenceGenerator;



/**
 *
 * @author andre
 */
@Embeddable
public class Perfil implements Serializable {
    
//    @Id
//    @SequenceGenerator(name = "PERFIL_ID", sequenceName = "perfil_perfil_id_seq", allocationSize = 1)
//    @GeneratedValue(generator = "PERFIL_ID", strategy = GenerationType.SEQUENCE)
//    private Long id;
    
    @Enumerated(EnumType.STRING)
    private PerfilEnum perfil;
    
    private String curso;

     /**
     * @return the perfil
     */
    public PerfilEnum getPerfil() {
        return perfil;
    }

    /**
     * @param perfil the perfil to set
     */
    public void setPerfil(PerfilEnum perfil) {
        this.perfil = perfil;
    }
    
    /**
     * @return the curso
     */
    public String getCurso() {
        return curso;
    }

    /**
     * @param curso the curso to set
     */
    public void setCurso(String curso) {
        this.curso = curso;
    }

//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
}
