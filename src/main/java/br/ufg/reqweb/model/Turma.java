/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.reqweb.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 *
 * @author André
 */
@Entity
public class Turma implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @Column
    private String nome;

    @ManyToOne
    private Periodo periodo;

    @ManyToOne
    private Disciplina disciplina;

    @ManyToOne
    private Usuario usuario;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Periodo getPeriodo() {
        return periodo;
    }

    public void setPeriodo(Periodo periodo) {
        this.periodo = periodo;
    }

    public Disciplina getDisciplina() {
        return disciplina;
    }

    public void setDisciplina(Disciplina disciplina) throws NullPointerException {
        this.disciplina = disciplina;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        PerfilEnum perfilValido = PerfilEnum.DOCENTE;
        boolean valido = false;
        for (Perfil p : usuario.getPerfilList()) {
            if (p.getPerfil().getPapel().equals(perfilValido.getPapel())) {
                valido = true;
                perfilValido = p.getPerfil();
                break;
            }
        }
        if (valido) {
            this.usuario = usuario;
        } else {
            throw new NullPointerException(String.format("Invalid User Group:%s ", perfilValido.getGrupo()));
        }
    }
}
