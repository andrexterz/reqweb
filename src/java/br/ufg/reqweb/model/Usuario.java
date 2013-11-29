/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.reqweb.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;

/**
 *
 * @author andre
 */
@Entity
public class Usuario implements Serializable {
    
    @Id
    @SequenceGenerator(name = "USUARIO_ID", sequenceName = "usuario_usuario_id_seq", allocationSize = 1)
    @GeneratedValue(generator = "USUARIO_ID", strategy = GenerationType.SEQUENCE)
    private Long id;
    
    @Column
    private String nome;
    
    @Column
    private String login;
    
    @Column
    private String matricula;
    
    @Column
    private String email;
    
    @OneToMany(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "usuario_id")
    private List<Perfil> perfil = new ArrayList<Perfil>();

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    
    /**
     *
     * @param id the id to set
     */
    public void setId(long id) {
        this.id = id;
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
     * @return the login
     */
    public String getLogin() {
        return login;
    }

    /**
     * @param login the login to set
     */
    public void setLogin(String login) {
        this.login = login;
    }

    /**
     * @return the matricula
     */
    public String getMatricula() {
        return matricula;
    }

    /**
     * @param matricula the matricula to set
     */
    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the perfil
     */
    public List<Perfil> getPerfil() {
        return perfil;
    }

    /**
     * @param perfil the perfil to set
     */
    public void setPerfil(List<Perfil> perfil) {
        this.perfil = perfil;
    }

}
