/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.reqweb.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.validation.ValidationException;
import javax.validation.constraints.NotNull;

/**
 *
 * @author andre
 */
@Entity
public class Usuario extends BaseModel {

    private static final long serialVersionUID = 1L;

    public Usuario() {
        //inicializar lista por causa do method adicionaPerfil
        perfilList = new ArrayList<>();
    }

    public Usuario(Long id, String nome, String login, String matricula, String email, List<Perfil> perfilList) {
        this.id = id;
        this.nome = nome;
        this.login = login;
        this.matricula = matricula;
        this.email = email;
        this.perfilList = perfilList;
    }

    @NotNull
    @Column
    private String nome;

    @NotNull
    @Column(unique = true)
    private String login;

    @Column(unique = true)
    private String matricula;

    @NotNull
    @Column(unique = true)
    private String email;

    //@Cascade(CascadeType.ALL)
    @OneToMany(mappedBy = "usuario", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Perfil> perfilList;

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
     *
     * @return the matricula
     */
    public String getMatricula() {
        return matricula;
    }

    /**
     *
     * @param matricula
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
        for (Perfil p : this.perfilList) {
            validatePerfil(p);
            p.setUsuario(this);
        }
    }

    public void adicionaPerfil(Perfil perfil) {
        validatePerfil(perfil);
        perfil.setUsuario(this);
        this.perfilList.add(perfil);
    }

    public void removePerfil(Perfil perfil) {
        this.perfilList.remove(perfil);
    }

    private void validatePerfil(Perfil perfil) {
        if (Arrays.asList(Perfil.perfilCursoMustBeNull).contains(perfil.getTipoPerfil()) && perfil.getCurso() != null) {
            throw new ValidationException("Perfil " + perfil.getTipoPerfil() + " requires Curso == null");
        }
    }
}
