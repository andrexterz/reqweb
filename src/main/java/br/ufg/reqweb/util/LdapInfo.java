/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.reqweb.util;

import br.ufg.reqweb.components.ConfigBean;
import br.ufg.reqweb.util.servicelocator.LDAPServiceLocator;
import br.ufg.reqweb.model.PerfilEnum;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
public class LdapInfo implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String usuario;
    private String nome;
    private String grupo;
    private String uid;
    private String matricula;
    private String email;
    static private DirContext ctx;

    public  static List<LdapInfo> scanLdap() {
        List<LdapInfo> usuarios = new ArrayList<>();
        Attributes matchAttrs = new BasicAttributes(false);
        String[] atributosRetorno = new String[]{"mail", "cn", "uid", "uidNumber", "gidNumber"};

        NamingEnumeration<?> resultado;
        try {
            ctx = LDAPServiceLocator.getInstance().getContext("", "");
            resultado = ctx.search(
                    ConfigBean.getInstance().getConf().getProperty("ldap.searchBase"),
                    matchAttrs,
                    atributosRetorno);

            while (resultado.hasMore()) {
                LdapInfo itemInfo = new LdapInfo();
                SearchResult sr = (SearchResult) resultado.next();
                Attributes atributos = sr.getAttributes();

                for (NamingEnumeration<?> todosAtributos = atributos.getAll(); todosAtributos.hasMore();) {
                    Attribute attrib = (Attribute) todosAtributos.next();
                    String nomeAtributo = attrib.getID();

                    if (nomeAtributo.equals("gidNumber")) {
                        itemInfo.setGrupo((String) attrib.get());
                    }
                    if (nomeAtributo.equals("uid")) {
                        itemInfo.setUsuario((String) attrib.get());
                        if (itemInfo.grupo.equals("500")) {
                            Pattern patt = Pattern.compile("\\d+");
                            Matcher mat = patt.matcher(itemInfo.getUsuario());
                            if (mat.find()) {
                                itemInfo.setMatricula(mat.group());
                            }
                        } else {
                            itemInfo.setMatricula(null);
                        }                        
                    }
                    if (nomeAtributo.equals("mail")) {
                        itemInfo.setEmail((String) attrib.get());
                    }
                    if (nomeAtributo.equals("cn")) {
                        itemInfo.setNome((String) attrib.get());
                    }
                    if (nomeAtributo.equals("uidNumber")) {
                        itemInfo.setUid((String) attrib.get());
                    }

                }
                if (!itemInfo.getGrupo().equals(PerfilEnum.ADMINISTRADOR.getGrupo())) {
                    usuarios.add(itemInfo);
                }

            }
        } catch (NamingException ex) {
            Logger.getLogger(LdapInfo.class.getName()).log(Level.SEVERE, null, ex);
        }
        return usuarios;
    }

    private void buscaGrupo(String grupo) {
        Attributes matchAttrs = new BasicAttributes(false);
        String[] atributosRetorno = new String[]{"mail", "cn", "uid", "uidNumber", "gidNumber"};

        matchAttrs.put(new BasicAttribute("uid", usuario));
        matchAttrs.put(new BasicAttribute("gidNumber", grupo));

        NamingEnumeration<?> resultado;
        try {
            resultado = ctx.search(
                    ConfigBean.getInstance().getConf().getProperty("ldap.searchBase"), matchAttrs,
                    atributosRetorno);

            if (resultado.hasMore()) {
                SearchResult sr = (SearchResult) resultado.next();
                Attributes atributos = sr.getAttributes();

                for (NamingEnumeration<?> todosAtributos = atributos.getAll(); todosAtributos.hasMore();) {
                    Attribute attrib = (Attribute) todosAtributos.next();
                    String nomeAtributo = attrib.getID();

                    if (nomeAtributo.equals("gidNumber")) {
                        this.grupo = (String) attrib.get();
                    }
                    if (nomeAtributo.equals("uid")) {
                        this.usuario = (String) attrib.get();
                        if (this.grupo.equals("500")) {
                            Pattern patt = Pattern.compile("\\d+");
                            Matcher mat = patt.matcher(this.usuario);
                            if (mat.find()) {
                                this.matricula = mat.group();
                            }
                        } else {
                            this.matricula = null;
                        }
                    }
                    if (nomeAtributo.equals("mail")) {
                        this.email = (String) attrib.get();
                    }
                    if (nomeAtributo.equals("cn")) {
                        this.nome = (String) attrib.get();
                    }
                    if (nomeAtributo.equals("uidNumber")) {
                        this.uid = (String) attrib.get();
                    }

                }
            }
        } catch (NamingException ex) {
            Logger.getLogger(LdapInfo.class.getName()).log(Level.SEVERE, null, ex);
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
     * 
     * @return 
     */
    public String getUid() {
        return uid;
    }
    
    /**
     * 
     * @param uid 
     */
    public void setUid(String uid) {
        this.uid = uid;
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
