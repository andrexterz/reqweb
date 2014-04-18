package br.ufg.reqweb.util.servicelocator;
 
/**
 * Parametros de configuração do LDAP
 *
 */
 
public class LDAPParametrosConfig {
 
    
    public static final String INITIAL_CTX = "com.sun.jndi.ldap.LdapCtxFactory";
 
    /*
     * Servidor LDAP
     */
    public static final String SERVIDOR = "ldap://200.137.197.214:389";
 
    /*
     * Base de busca
     */
    public static final String SEARCHBASE = "ou=Users, dc=dionisio, dc=inf, dc=ufg, dc=br";
    
    
    /*
     * Nome do usuário do admin
     */       
    public static final String ADMIN_DN = "Cn=administrator,dc=dionisio, dc=inf, dc=ufg, dc=br";
    
    /*
     * Senha
     */
    public static final String ADMIN_PW = "senha123";
    
 
    /*
     *  diretório (base DN)
     */
 
    public static final String BASE_DN = "dc=dionisio, dc=inf, dc=ufg, dc=br";
}