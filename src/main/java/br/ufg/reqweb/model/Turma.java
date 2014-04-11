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
public class Turma implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
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

      @Override
    public boolean equals(Object obj) {
        if ((obj != null) && (obj.getClass() == this.getClass())) {
            Turma other = (Turma) obj;
            if (other.getId() != null && this.getId() != null) {
                return other.getId().longValue() == this.getId().longValue();
            } else {
                return other.getId() == this.getId();
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (id != null) ? 11 * 7 + (int) (id ^ (id >>> 32)) : super.hashCode();
    }

    @Override
    public String toString() {
        return this.getClass().getName() + "@" + id;
    }
}
