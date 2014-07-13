/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.reqweb.components;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.apache.log4j.Logger;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.jasypt.properties.EncryptableProperties;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author andre
 */
@Component
@Scope(value = "singleton")
@Lazy(false)
public class ConfigBean implements Serializable {
    
    private static final Logger log = Logger.getLogger(ConfigBean.class);
    private static ConfigBean instance;
    private final String propertyFile = "/reqweb.properties";
    private final StandardPBEStringEncryptor encryptor;
    private final Properties conf;    
    
    public ConfigBean() {
        encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword("gIWxV7lW3rt3TJb1NweZKg");
        conf = new EncryptableProperties(encryptor);
        try {
            InputStream inConf = this.getClass().getResourceAsStream(propertyFile);
            conf.load(inConf);
        } catch (IOException | NullPointerException e) {
            log.error("No config found: classpath:reqweb.properties");
        }
    }
    
    public static ConfigBean getInstance() {
        if (instance == null) {
            instance = new ConfigBean();
        }
        return instance;
    }
    
    public void salvaSettings() {
        OutputStream outConf = null;
        FacesMessage msg = null;
        try {
            String confPath = this.getClass().getClassLoader().getResource(propertyFile).getPath();
            outConf = new FileOutputStream(confPath);
            conf.store(outConf, propertyFile);
            System.out.println("saved: " + confPath);
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "info", LocaleBean.getMessageBundle().getString("dadosSalvos"));
        } catch (IOException e) {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "info", LocaleBean.getMessageBundle().getString("erroGravacao"));
            log.error("error on saving: classpath:reqweb.properties: " + e.getLocalizedMessage());
        } finally {
            FacesContext.getCurrentInstance().addMessage(null, msg);
            if (outConf != null) {
                try {
                    outConf.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        }
    }

    /**
     *
     * @param key
     * @return the config value options:
     * <ul>
     * <li><b>maxArquivos</b></li>
     * <li><b>maxCopias</b></li>
     * <li><b>minPeriodo</b></li>
     * <li><b>maxPeriodo</b></li>
     * <li><b>maxArquivo</b></li>
     * </ul>
     */
    public Object getValue(String key) {
        if (key.equals("minPeriodo")) {
            Calendar minPeriodo = Calendar.getInstance();
            minPeriodo.add(Calendar.DAY_OF_MONTH, 1 - Integer.parseInt(conf.getProperty(key)));
            return minPeriodo.getTime();
        }
        if (key.equals("maxPeriodo")) {
            return Calendar.getInstance().getTime();
        }
        return conf.getProperty(key);
    }

    public boolean isExpiredPeriodo(Date date) {
        if (date != null) {
            Calendar minPeriodo = Calendar.getInstance();
            minPeriodo.add(Calendar.DAY_OF_MONTH, -Integer.parseInt(conf.getProperty("minPeriodo")));
            return date.before(minPeriodo.getTime());
        } else {
            return false;
        }
    }
    
    public Properties getConf() {
        return conf;
    }

    /**
     * @return the minPeriodo
     */
    public int getMinPeriodo() {
        return Integer.parseInt(conf.getProperty("reqweb.minPeriodo"));
    }

    /**
     * @param minPeriodo the minPeriodo to set
     */
    public void setMinPeriodo(int minPeriodo) {
        conf.setProperty("reqweb.minPeriodo", Integer.toString(minPeriodo));
    }

    /**
     * @return the maxArquivos
     */
    public int getMaxArquivos() {
        return Integer.parseInt(conf.getProperty("reqweb.maxArquivos"));
    }

    /**
     * @param maxArquivos the maxArquivos to set
     */
    public void setMaxArquivos(int maxArquivos) {
        conf.setProperty("reqweb.maxArquivos", Integer.toString(maxArquivos));
    }

    /**
     * @return the maxCopias
     */
    public int getMaxCopias() {
        return Integer.parseInt(conf.getProperty("reqweb.maxCopias"));
    }

    /**
     * @param maxCopias the maxCopias to set
     */
    public void setMaxCopias(int maxCopias) {
        conf.setProperty("reqweb.maxCopias", Integer.toString(maxCopias));
    }

    /**
     * @return the maxBytesArquivo
     */
    public int getMaxBytesArquivo() {
        return Integer.parseInt(conf.getProperty("reqweb.maxBytesArquivo"));
    }

    /**
     * @param maxBytesArquivo the maxBytesArquivo to set
     */
    public void setMaxBytesArquivo(int maxBytesArquivo) {
        conf.setProperty("reqweb.maxBytesArquivo", Integer.toString(maxBytesArquivo));
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return conf.getProperty("ldap.url");
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        conf.setProperty("ldap.url", url);
    }

    /**
     * @return the initialCtx
     */
    public String getInitialCtx() {
        return conf.getProperty("ldap.initialCtx");
    }

    /**
     * @param initialCtx the initialCtx to set
     */
    public void setInitialCtx(String initialCtx) {
        conf.setProperty("ldap.initialCtx", initialCtx);
    }

    /**
     * @return the server
     */
    public String getServer() {
        return conf.getProperty("ldap.server");
    }

    /**
     * @param server the server to set
     */
    public void setServer(String server) {
        conf.setProperty("ldap.server", server);
    }

    /**
     * @return the searchBase
     */
    public String getSearchBase() {
        return conf.getProperty("ldap.searchBase");
    }

    /**
     * @param searchBase the searchBase to set
     */
    public void setSearchBase(String searchBase) {
        conf.setProperty("ldap.searchBase", searchBase);
    }

    /**
     * @return the baseDN
     */
    public String getBaseDN() {
        return conf.getProperty("ldap.baseDN");
    }

    /**
     * @param baseDN the baseDN to set
     */
    public void setBaseDN(String baseDN) {
        conf.setProperty("ldap.baseDN", baseDN);
    }

    /**
     * @return the databaseString
     */
    public String getDatabaseString() {
        return conf.getProperty("jdbc.databaseString");
    }

    /**
     * @param databaseString the databaseString to set
     */
    public void setDatabaseString(String databaseString) {
        conf.setProperty("jdbc.databaseString", databaseString);
    }

    /**
     * @return the databaseUser
     */
    public String getDatabaseUser() {
        return conf.getProperty("jdbc.databaseUser");
    }

    /**
     * @param databaseUser the databaseUser to set
     */
    public void setDatabaseUser(String databaseUser) {
        conf.setProperty("jdbc.databaseUser", databaseUser);
    }

    /**
     * @return the databasePassword
     */
    public String getDatabasePassword() {
        try {
            return encryptor.decrypt(conf.getProperty("jdbc.databasePassword"));
        } catch (EncryptionOperationNotPossibleException e) {
            return conf.getProperty("jdbc.databasePassword");
        }
    }

    /**
     * @param databasePassword the databasePassword to set
     */
    public void setDatabasePassword(String databasePassword) {
        conf.setProperty("jdbc.databasePassword", encryptor.encrypt(databasePassword));
    }

    /**
     * @return the mailHost
     */
    public String getMailHost() {
        return conf.getProperty("mail.mailHost");
    }

    /**
     * @param mailHost the mailHost to set
     */
    public void setMailHost(String mailHost) {
        conf.setProperty("mail.mailHost", mailHost);
    }

    /**
     * @return the smtpPort
     */
    public int getSmtpPort() {
        return Integer.parseInt(conf.getProperty("mail.smtpPort"));
    }

    /**
     * @param smtpPort the smtpPort to set
     */
    public void setSmtpPort(int smtpPort) {
        conf.setProperty("mail.smtpPort", Integer.toString(smtpPort));
    }

    /**
     * @return the useSSL
     */
    public boolean isUseSSL() {
        return Boolean.parseBoolean(conf.getProperty("mail.useSSL"));
    }

    /**
     * @param useSSL the useSSL to set
     */
    public void setUseSSL(boolean useSSL) {
        conf.setProperty("mail.useSSL", Boolean.toString(useSSL));
    }

    /**
     * @return the mailSender
     */
    public String getMailSender() {
        return conf.getProperty("mail.mailSender");
    }

    /**
     * @param mailSender the mailSender to set
     */
    public void setMailSender(String mailSender) {
        conf.setProperty("mail.mailSender", mailSender);
    }

    /**
     * @return the mailUser
     */
    public String getMailUser() {
        return conf.getProperty("mail.mailUser");
    }

    /**
     * @param mailUser the mailUser to set
     */
    public void setMailUser(String mailUser) {
        conf.setProperty("mail.mailUser", mailUser);
    }

    /**
     * @return the mailPassword
     */
    public String getMailPassword() {
        try {
            return encryptor.decrypt(conf.getProperty("mail.mailPassword"));
        } catch (EncryptionOperationNotPossibleException e) {
            return conf.getProperty("mail.mailPassword");
        }
        
    }

    /**
     * @param mailPassword the mailPassword to set
     */
    public void setMailPassword(String mailPassword) {
        conf.setProperty("mail.mailPassword", encryptor.encrypt(mailPassword));

    }

    /**
     * @return the mailScheduler
     *Spring Scheduler cron like style 
     */
    public String getMailScheduler() {
        return conf.getProperty("mail.mailScheduler");
    }

    /**
     * @param mailScheduler the mailScheduler to set
     */
    public void setMailScheduler(String mailScheduler) {
        conf.setProperty("mail.mailScheduler", mailScheduler);
    }
    

}
