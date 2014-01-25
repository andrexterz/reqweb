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

/**
 *
 * @author andre
 */
@Embeddable
public class Perfil implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Enumerated(EnumType.STRING)
    private PerfilEnum perfil;

    private Curso curso;

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
    public Curso getCurso() {
        return curso;
    }

    /**
     * @param curso the curso to set
     */
    public void setCurso(Curso curso) {
        this.curso = curso;
    }
}
