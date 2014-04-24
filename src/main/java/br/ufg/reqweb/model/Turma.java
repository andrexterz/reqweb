/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.reqweb.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author André
 */
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"nome","disciplina_id"})})
public class Turma extends BaseModel {

    private static final long serialVersionUID = 1L;

    public Turma() {
    }

    public Turma(Long id, String nome, Periodo periodo, Usuario docente) {
        this.id = id;
        this.nome = nome;
        this.periodo = periodo;
        this.docente = docente;
    }
    
    

    @Size(min = 1)
    @Column
    private String nome;

    @NotNull            
    @ManyToOne
    private Periodo periodo;

    @NotNull
    @ManyToOne
    private Disciplina disciplina;

    @ManyToOne
    private Usuario docente;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome.toUpperCase();
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

    public Usuario getDocente() {
        return docente;
    }

    public void setDocente(Usuario docente) {
        if (docente != null) {
            PerfilEnum perfilValido = PerfilEnum.DOCENTE;
            boolean valido = false;
            for (Perfil p : docente.getPerfilList()) {
                if (p.getTipoPerfil().getPapel().equals(perfilValido.getPapel())) {
                    valido = true;
                    perfilValido = p.getTipoPerfil();
                    break;
                }
            }
            if (valido) {
                this.docente = docente;
            } else {
                throw new NullPointerException(String.format("Invalid User Group:%s ", perfilValido.getGrupo()));
            }
        } else {
            this.docente = null;
        }
    }
}
