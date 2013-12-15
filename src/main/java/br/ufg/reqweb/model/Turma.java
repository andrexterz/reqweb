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
    
    @Id
    @SequenceGenerator(name = "TURMA_ID", sequenceName = "turma_turma_id", allocationSize = 1)
    @GeneratedValue(generator = "TURMA_ID", strategy = GenerationType.SEQUENCE)    
    private Long id;
    
    @Column
    private String nome;
    
    @ManyToOne
    private Periodo periodo;
    
    //disciplina (falta criar entity)
    //docente (falta entity)

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

}
