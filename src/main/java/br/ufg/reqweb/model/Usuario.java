/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.reqweb.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

/**
 *
 * @author andre
 */
@Entity
public class Usuario implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Usuario() {
        //inicializar lista por causa do method adicionaPerfil
        this.perfilList = new ArrayList<>();
    }
    
    @Id
    @SequenceGenerator(name = "USUARIO_ID", sequenceName = "usuario_usuario_id_seq", allocationSize = 1)
    @GeneratedValue(generator = "USUARIO_ID", strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column
    private String nome;

    @Column
    private String login;

    @Column
    private String email;

    @ElementCollection(targetClass = Perfil.class, fetch = FetchType.LAZY)
    private List<Perfil> perfilList;

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
     * @return the perfilList
     */
    public List<Perfil> getPerfilList() {
        return perfilList;
    }

    /**
     * @param perfilList the perfilList to set
     */
    public void setPerfil(List<Perfil> perfilList) {
        this.perfilList = perfilList;
    }

    public void adicionaPerfil(Perfil perfil) {
        this.perfilList.add(perfil);
    }

    public void removePerfil(Perfil perfil) {
        this.perfilList.remove(perfil);
    }
}
