/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.reqweb.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 *
 * @author andre
 */
@Entity
public class Curso implements Serializable {
    

	public Curso() {
        
    }
    
    public Curso (long id, String matriz, String nome, String sigla, Date dataModificacao) {
        this.id = id;
        this.matriz = matriz;
        this.nome = nome;
        this.sigla = sigla;
        this.dataModificacao = dataModificacao;
    }

    @Id
    @SequenceGenerator(name = "CURSO_ID", sequenceName = "curso_curso_id_seq", allocationSize = 1)
    @GeneratedValue(generator = "CURSO_ID", strategy = GenerationType.SEQUENCE)
    private long id;
    
    @Column
    private String matriz;
    
    @Column
    private String nome;
    
    @Column(length = 6)
    private String sigla;
    

    @Temporal(TemporalType.TIMESTAMP)
    private Date dataModificacao;
       
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    
    /**
     *
     * @return
     */
    public String getMatriz() {
        return matriz;
    }
    
    /**
     *
     * @param matriz
     */
    public void setMatriz(String matriz) {
        this.matriz = matriz;
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
     * @return the sigla
     */
    public String getSigla() {
        return sigla;
    }

    /**
     * @param sigla the sigla to set
     */
    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    /**
     * @return the dataModificacao
     */
    public Date getDataModificacao() {
        return dataModificacao;
    }

    /**
     * @param dataModificacao the dataModificacao to set
     */
    public void setDataModificacao(Date dataModificacao) {
        this.dataModificacao = dataModificacao;
    }
}
