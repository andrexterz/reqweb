/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.reqweb.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

/**
 *
 * @author André
 */
@Entity
public class Turma implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "TURMA_ID", sequenceName = "turma_turma_id_seq", allocationSize = 1)
    @GeneratedValue(generator = "TURMA_ID", strategy = GenerationType.SEQUENCE)
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
            if (p.getTipoPerfil().getPapel().equals(perfilValido.getPapel())) {
                valido = true;
                perfilValido = p.getTipoPerfil();
                break;
            }
        }
        if (valido) {
            this.usuario = usuario;
        } else {
            throw new NullPointerException(String.format("Invalid User Group:%s ", perfilValido.getGrupo()));
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null) {
            return ((obj instanceof Turma) && ((long) ((id == null) ? Long.MIN_VALUE: id)) == (long) ((Turma) obj).getId());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return (id != null) ? 11 * 7 + (int) (id ^ (id >>> 32)): super.hashCode();
    }

    @Override
    public String toString() {
        return this.getClass().getName() + "@" + id;
    }    
}
