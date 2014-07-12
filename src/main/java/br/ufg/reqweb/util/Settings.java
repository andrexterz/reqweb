/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.reqweb.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import javax.faces.context.FacesContext;
import org.apache.log4j.Logger;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.jasypt.properties.EncryptableProperties;

/**
 *
 * @author andre
 */
public final class Settings {

    private static final Logger log = Logger.getLogger(Settings.class);
    private final String propertyFile = "/reqweb.properties";

    private static Settings instance;
    private StandardPBEStringEncryptor encryptor;
    private Properties conf;

    public Settings() {
        encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword("$key$ReqwebEncryptor$key$20140711");
        conf = new EncryptableProperties(encryptor);
        try {
            InputStream inConf = this.getClass().getResourceAsStream(propertyFile);
            conf.load(inConf);
        } catch (IOException | NullPointerException e) {
            log.error("No config found: classpath:reqweb.properties");
        }
    }

    public void salvaSettings() {
        OutputStream outConf = null;
        try {
            String confPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath(propertyFile);
            outConf = new FileOutputStream(confPath);
            conf.store(outConf, propertyFile);
            System.out.println("saved: " + confPath);
        } catch (IOException e) {
            log.error("error on saving: classpath:reqweb.properties: " + e.getLocalizedMessage());
        } finally {
            if (outConf != null) {
                try {
                    outConf.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }

        }
    }

    public Properties getConf() {
        return conf;
    }

    public static Settings getInstance() {

        if (instance == null) {
            instance = new Settings();
        }
        return instance;
    }

    /**
     * @return the minPeriodo
     */
    public int getMinPeriodo() {
        return Integer.parseInt(conf.getProperty("minPeriodo"));
    }

    /**
     * @param minPeriodo the minPeriodo to set
     */
    public void setMinPeriodo(int minPeriodo) {
        conf.setProperty("minPeriodo", Integer.toString(minPeriodo));
    }

    /**
     * @return the maxArquivos
     */
    public int getMaxArquivos() {
        return Integer.parseInt(conf.getProperty("maxArquivos"));
    }

    /**
     * @param maxArquivos the maxArquivos to set
     */
    public void setMaxArquivos(int maxArquivos) {
        conf.setProperty("maxArquivos", Integer.toString(maxArquivos));
    }

    /**
     * @return the maxCopias
     */
    public int getMaxCopias() {
        return Integer.parseInt(conf.getProperty("maxCopias"));
    }

    /**
     * @param maxCopias the maxCopias to set
     */
    public void setMaxCopias(int maxCopias) {
        conf.setProperty("maxCopias", Integer.toString(maxCopias));
    }

    /**
     * @return the maxBytesArquivo
     */
    public int getMaxBytesArquivo() {
        return Integer.parseInt(conf.getProperty("maxBytesArquivo"));
    }

    /**
     * @param maxBytesArquivo the maxBytesArquivo to set
     */
    public void setMaxBytesArquivo(int maxBytesArquivo) {
        conf.setProperty("maxBytesArquivo", Integer.toString(maxBytesArquivo));
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return conf.getProperty("url");
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        conf.setProperty("url", url);
    }

    /**
     * @return the initialCtx
     */
    public String getInitialCtx() {
        return conf.getProperty("initialCtx");
    }

    /**
     * @param initialCtx the initialCtx to set
     */
    public void setInitialCtx(String initialCtx) {
        conf.setProperty("initialCtx", initialCtx);
    }

    /**
     * @return the server
     */
    public String getServer() {
        return conf.getProperty("server");
    }

    /**
     * @param server the server to set
     */
    public void setServer(String server) {
        conf.setProperty("server", server);
    }

    /**
     * @return the searchBase
     */
    public String getSearchBase() {
        return conf.getProperty("searchBase");
    }

    /**
     * @param searchBase the searchBase to set
     */
    public void setSearchBase(String searchBase) {
        conf.setProperty("searchBase", searchBase);
    }

    /**
     * @return the baseDN
     */
    public String getBaseDN() {
        return conf.getProperty("baseDN");
    }

    /**
     * @param baseDN the baseDN to set
     */
    public void setBaseDN(String baseDN) {
        conf.setProperty("baseDN", baseDN);
    }

    /**
     * @return the databaseString
     */
    public String getDatabaseString() {
        return conf.getProperty("databaseString");
    }

    /**
     * @param databaseString the databaseString to set
     */
    public void setDatabaseString(String databaseString) {
        conf.setProperty("databaseString", databaseString);
    }

    /**
     * @return the databaseUser
     */
    public String getDatabaseUser() {
        return conf.getProperty("databaseUser");
    }

    /**
     * @param databaseUser the databaseUser to set
     */
    public void setDatabaseUser(String databaseUser) {
        conf.setProperty("databaseUser", databaseUser);
    }

    /**
     * @return the databasePassword
     */
    public String getDatabasePassword() {
        try {
            return encryptor.decrypt(conf.getProperty("databasePassword"));
        } catch (EncryptionOperationNotPossibleException e) {
            return conf.getProperty("databasePassword");
        }
    }

    /**
     * @param databasePassword the databasePassword to set
     */
    public void setDatabasePassword(String databasePassword) {
        conf.setProperty("databasePassword", encryptor.encrypt(databasePassword));
    }

    /**
     * @return the hostMail
     */
    public String getHostMail() {
        return conf.getProperty("hostMail");
    }

    /**
     * @param hostMail the hostMail to set
     */
    public void setHostMail(String hostMail) {
        conf.setProperty("hostMail", hostMail);
    }

    /**
     * @return the smtpPort
     */
    public int getSmtpPort() {
        return Integer.parseInt(conf.getProperty("smtpPort"));
    }

    /**
     * @param smtpPort the smtpPort to set
     */
    public void setSmtpPort(int smtpPort) {
        conf.setProperty("smtpPort", Integer.toString(smtpPort));
    }

    /**
     * @return the useSSL
     */
    public boolean isUseSSL() {
        return Boolean.parseBoolean(conf.getProperty("useSSL"));
    }

    /**
     * @param useSSL the useSSL to set
     */
    public void setUseSSL(boolean useSSL) {
        conf.setProperty("useSSL", Boolean.toString(useSSL));
    }

    /**
     * @return the senderMail
     */
    public String getSenderMail() {
        return conf.getProperty("senderMail");
    }

    /**
     * @param senderMail the senderMail to set
     */
    public void setSenderMail(String senderMail) {
        conf.setProperty("senderMail", senderMail);
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return conf.getProperty("username");
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        conf.setProperty("username", username);
    }

    /**
     * @return the password
     */
    public String getPassword() {
        try {
            return encryptor.decrypt(conf.getProperty("password"));
        } catch (EncryptionOperationNotPossibleException e) {
            return conf.getProperty("password");
        }
        
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        conf.setProperty("password", encryptor.encrypt(password));

    }

    /**
     * @return the mailScheduler
     *Spring Scheduler cron like style 
     */
    public String getMailScheduler() {
        return conf.getProperty("mailScheduler");
    }

    /**
     * @param mailScheduler the mailScheduler to set
     */
    public void setMailScheduler(String mailScheduler) {
        conf.setProperty("mailScheduler", mailScheduler);
    }

}
