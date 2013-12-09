/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.reqweb.auth;

import br.ufg.reqweb.auth.servicelocator.LDAPParametrosConfig;
import br.ufg.reqweb.auth.servicelocator.LDAPServiceLocator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchResult;

/**
 *
 * @author andre
 */
public class Login implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String usuario;
    private String nome;
    private String grupo;
    private String matricula;
    private String email;
    private DirContext ctx;

    public static Login autenticar(String usuario, String senha, String grupo) {
        Login objLogin = new Login();

        if (objLogin.doLogin(usuario, senha)) {
            objLogin.buscaGrupo(grupo);
            if (objLogin.getGrupo() == null && !grupo.equals("100")) {
                objLogin = null;
            }
            return objLogin;
        } else {
            return null;
        }

    }

    private boolean doLogin(String usuario, String senha) {

        this.usuario = usuario;
        boolean res;
        String usuarioLdap = "uid=" + usuario + ", OU=Users,dc=dionisio, dc=inf, dc=ufg, dc=br";

        try {
            ctx = LDAPServiceLocator.getInstance().getContext(usuarioLdap, senha);
            res = true;
        } catch (NamingException ex) {
            res = false;
        }
        return res;
    }

    public List<Login> scanLdap() {
        List usuarios = new ArrayList<Login>();
        Attributes matchAttrs = new BasicAttributes(false);
        String[] atributosRetorno = new String[]{"mail", "cn", "uid", "uidNumber", "gidNumber"};

        NamingEnumeration resultado;
        try {
            resultado = ctx.search(
                    LDAPParametrosConfig.SEARCHBASE,
                    matchAttrs,
                    atributosRetorno);

            while (resultado.hasMore()) {
                Login itemLogin = new Login();
                usuarios.add(itemLogin);
                SearchResult sr = (SearchResult) resultado.next();
                Attributes atributos = sr.getAttributes();

                for (NamingEnumeration todosAtributos = atributos.getAll(); todosAtributos.hasMore();) {
                    Attribute attrib = (Attribute) todosAtributos.next();
                    String nomeAtributo = attrib.getID();

                    if (nomeAtributo.equals("gidNumber")) {
                        itemLogin.setGrupo((String) attrib.get());
                    }
                    if (nomeAtributo.equals("uid")) {
                        itemLogin.setUsuario((String) attrib.get());
                    }
                    if (nomeAtributo.equals("mail")) {
                        itemLogin.setEmail((String) attrib.get());
                    }
                    if (nomeAtributo.equals("cn")) {
                        itemLogin.setNome((String) attrib.get());
                    }
                    if (nomeAtributo.equals("uidNumber")) {
                        itemLogin.setMatricula((String) attrib.get());
                    }

                }
            }
        } catch (NamingException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }
        return usuarios;
    }

    private void buscaGrupo(String grupo) {
        Attributes matchAttrs = new BasicAttributes(false);
        String[] atributosRetorno = new String[]{"mail", "cn", "uid", "uidNumber", "gidNumber"};

        matchAttrs.put(new BasicAttribute("uid", usuario));
        matchAttrs.put(new BasicAttribute("gidNumber", grupo));

        NamingEnumeration resultado;
        try {
            resultado = ctx.search(
                    LDAPParametrosConfig.SEARCHBASE, matchAttrs,
                    atributosRetorno);

            if (resultado.hasMore()) {
                SearchResult sr = (SearchResult) resultado.next();
                Attributes atributos = sr.getAttributes();

                for (NamingEnumeration todosAtributos = atributos.getAll(); todosAtributos.hasMore();) {
                    Attribute attrib = (Attribute) todosAtributos.next();
                    String nomeAtributo = attrib.getID();

                    if (nomeAtributo.equals("gidNumber")) {
                        this.grupo = (String) attrib.get();
                    }
                    if (nomeAtributo.equals("uid")) {
                        this.usuario = (String) attrib.get();
                    }
                    if (nomeAtributo.equals("mail")) {
                        this.email = (String) attrib.get();
                    }
                    if (nomeAtributo.equals("cn")) {
                        this.nome = (String) attrib.get();
                    }
                    if (nomeAtributo.equals("uidNumber")) {
                        this.matricula = (String) attrib.get();
                    }

                }
            }
        } catch (NamingException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @return the usuario
     */
    public String getUsuario() {
        return usuario;
    }

    private void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    /**
     * @return the grupo
     */
    public String getGrupo() {
        return grupo;
    }

    private void setGrupo(String grupo) {
        this.grupo = grupo;
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
    private void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    /**
     * @return the nome
     */
    public String getNome() {
        return nome;
    }

    private void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    private void setEmail(String email) {
        this.email = email;
    }

}
