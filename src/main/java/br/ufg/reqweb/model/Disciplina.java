/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufg.reqweb.model;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.NotNull;

/**
 *
 * @author André
 */

@Entity
public class Disciplina implements Serializable {

    @Id
    @SequenceGenerator(name = "DISCIPLINA_ID", sequenceName = "disciplina_disciplina_id_seq", allocationSize = 1)
    @GeneratedValue(generator = "DISCIPLINA_ID", strategy = GenerationType.SEQUENCE)
    private Long id;
    
    @NotNull
    @Column(unique = true, nullable = false)
    private Long codigo;
    
    @NotNull
    @Column(nullable = false)
    private String nome;
    
    @NotNull
    @ManyToOne
    private Curso curso;

    @OneToMany(mappedBy = "disciplina")
    private List<Turma> turmas;
    
    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }
    
    /**
     * @return the codigo
     */
    public Long getCodigo() {
        return codigo;
    }

    /**
     * @param codigo the codigo to set
     */
    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }
    
    /**
     * @return the nome
     */
    public String getNome() {
        return nome;
    }

    /**
     * @param nome the nome to set
     */
    public void setNome(String nome) {
        this.nome = nome;
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
