/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.reqweb.auth;

import br.ufg.reqweb.auth.servicelocator.LDAPParametrosConfig;
import br.ufg.reqweb.auth.servicelocator.LDAPServiceLocator;
import javax.naming.AuthenticationException;
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
public class Login {

    private String login;
    private String grupo;
    private String matricula;
    private String nome;
    private String email;
    private boolean autentiado = false;

    public static Login doLogin(String login, String senha, String grupo) throws AuthenticationException, NamingException {
        Login loginObj = new Login();
        String usuario = "uid=" + login + ", OU=Users,dc=dionisio, dc=inf, dc=ufg, dc=br";


        //efetua login
        DirContext ctx = LDAPServiceLocator.getInstance().getContext(usuario, senha);

        Attributes matchAttrs = new BasicAttributes(false);
        String[] atributosRetorno = new String[]{"mail", "cn", "uid", "uidNumber", "gidNumber"};

        matchAttrs.put(new BasicAttribute("uid", login));
        matchAttrs.put(new BasicAttribute("gidNumber", grupo));

        NamingEnumeration resultado = ctx.search(
                LDAPParametrosConfig.SEARCHBASE, matchAttrs,
                atributosRetorno);

        if (!resultado.hasMore()) {
            loginObj = null;
        } else {
            loginObj = new Login();
            loginObj.setAutentiado(true);
            SearchResult sr = (SearchResult) resultado.next();
            Attributes atributos = sr.getAttributes();

            for (NamingEnumeration todosAtributos = atributos.getAll(); todosAtributos
                    .hasMore();) {
                Attribute attrib = (Attribute) todosAtributos.next();
                String nomeAtributo = attrib.getID();

                if (nomeAtributo.equals("gidNumber")) {
                    loginObj.setGrupo((String) attrib.get());
                }
                if (nomeAtributo.equals("uid")) {
                    loginObj.setLogin((String) attrib.get());
                }
                if (nomeAtributo.equals("mail")) {
                    loginObj.setEmail((String) attrib.get());
                }
                if (nomeAtributo.equals("cn")) {
                    loginObj.setNome((String) attrib.get());
                }
                if (nomeAtributo.equals("uidNumber")) {
                    loginObj.setMatricula((String) attrib.get());
                }

            }
        }
        return loginObj;
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
    private void setLogin(String login) {
        this.login = login;
    }

    /**
     * @return the grupo
     */
    public String getGrupo() {
        return grupo;
    }

    /**
     * @param grupo the grupo to set
     */
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

    /**
     * @param nome the nome to set
     */
    public void setNome(String nome) {
        this.nome = nome;
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
     * @return the autentiado
     */
    public boolean isAutentiado() {
        return autentiado;
    }

    private void setAutentiado(boolean autenticado) {
        this.autentiado = autenticado;
    }
    
    public void logout() {
        this.autentiado = false;
    }
}