package br.ufg.reqweb.util.servicelocator;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

/**
 * Classe responsavel por localizar um contexto de diretÃ³rio LDAP
 */
public class LDAPServiceLocator {

    protected static LDAPServiceLocator instance;

    private LDAPServiceLocator() {
        super();
    }

    /**
     * Obtem a mesma instancia de LDAPServiceLocator para todas as chamadas
     * (Classe singleton)
     *
     * @return um objeto LDAPServiceLocator
     */
    public static LDAPServiceLocator getInstance() {

        if (instance == null) {
            instance = new LDAPServiceLocator();
        }

        return instance;
    }

    public DirContext getContext(String usuario, String senha) throws NamingException {


        Hashtable<String, String> env = new Hashtable<String, String>(11);

        // Especifica a fabrica de INITIAL CONTEXT
        env.put(Context.INITIAL_CONTEXT_FACTORY, LDAPParametrosConfig.INITIAL_CTX);
        // Especifica o IP/Nome e a porta do servidor LDAP
        env.put(Context.PROVIDER_URL, LDAPParametrosConfig.SERVIDOR);
        // As linhas abaixo são usadas quando o servidor LDAP não permite busca como anonymous             
        //env.put(Context.SECURITY_PRINCIPAL, LDAPParametrosConfig.ADMIN_DN );                
        //Especifia o tipo de autenticação
        env.put(Context.SECURITY_AUTHENTICATION, "simple");   //com none não confere senha // auf

        env.put(Context.SECURITY_PRINCIPAL, usuario);        //auf
        //env.put(Context.SECURITY_CREDENTIALS, LDAPParametrosConfig.ADMIN_PW );
        env.put(Context.SECURITY_CREDENTIALS, senha);      //auf

        DirContext ctx = null;

        // Obtem um Initial Context
        ctx = new InitialDirContext(env);


        return ctx;
    }
}