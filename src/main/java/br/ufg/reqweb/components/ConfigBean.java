/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.reqweb.components;

import br.ufg.reqweb.dao.ConfigDao;
import java.io.IOException;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author andre
 */

@Component
@Scope(value = "session")
public class ConfigBean implements Serializable {

    
    @Autowired
    ConfigDao configDao;

    @Autowired
    MailBean mailBean;
    
    private boolean showPassword;
    
    public ConfigBean() {
        showPassword = false;
    }
    
    public void reagendaCron() {
        mailBean.scheduleJob();
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "info", LocaleBean.getMessageBundle().getString("cronRescheduled"));
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void salvaSettings() {
        FacesMessage msg;
        try {
            configDao.salva();
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "info", LocaleBean.getMessageBundle().getString("dadosSalvos"));
            System.out.println("reqweb.properties changed. restart scheduler to take effect");
        } catch (IOException e) {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, LocaleBean.getMessageBundle().getString("erroGravacao"), null);
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
    
    public boolean isExpiredPeriodo(Date date) {
        if (date != null) {
            Calendar minPeriodo = Calendar.getInstance();
            minPeriodo.add(Calendar.DAY_OF_MONTH, - Integer.parseInt(configDao.getConf().getProperty("reqweb.minPeriodo")));
            return date.before(minPeriodo.getTime());
        } else {
            return false;
        }
    }

    public boolean isShowPassword() {
        return showPassword;
    }

    public void setShowPassword(boolean showPassword) {
        this.showPassword = showPassword;
    }
    
    public Date getMinDate() {
        Calendar minPeriodo = Calendar.getInstance();
        minPeriodo.add(Calendar.DAY_OF_MONTH, 1 - Integer.parseInt(configDao.getConf().getProperty("reqweb.minPeriodo")));
        return minPeriodo.getTime();
        
    }
    
    public Date getMaxDate() {
        return Calendar.getInstance().getTime();
    }
    

    /**
     * @return the minPeriodo
     */
    public int getMinPeriodo() {
        return Integer.parseInt(configDao.getConf().getProperty("reqweb.minPeriodo"));
    }

    /**
     * @param minPeriodo the minPeriodo to set
     */
    public void setMinPeriodo(int minPeriodo) {
        configDao.getConf().setProperty("reqweb.minPeriodo", String.valueOf(minPeriodo));
    }

    /**
     * @return the maxArquivos
     */
    public int getMaxArquivos() {
        return Integer.parseInt(configDao.getConf().getProperty("reqweb.maxArquivos"));
    }

    /**
     * @param maxArquivos the maxArquivos to set
     */
    public void setMaxArquivos(int maxArquivos) {
        configDao.getConf().setProperty("reqweb.maxArquivos", String.valueOf(maxArquivos));
    }

    /**
     * @return the maxCopias
     */
    public int getMaxCopias() {
        return Integer.parseInt(configDao.getConf().getProperty("reqweb.maxCopias"));
    }

    /**
     * @param maxCopias the maxCopias to set
     */
    public void setMaxCopias(int maxCopias) {
        configDao.getConf().setProperty("reqweb.maxCopias", String.valueOf(maxCopias));
    }

    /**
     * @return the maxBytesArquivo
     */
    public int getMaxBytesArquivo() {
        return Integer.parseInt(configDao.getConf().getProperty("reqweb.maxBytesArquivo"));
    }

    /**
     * @param maxBytesArquivo the maxBytesArquivo to set
     */
    public void setMaxBytesArquivo(int maxBytesArquivo) {
        configDao.getConf().setProperty("reqweb.maxBytesArquivo", String.valueOf(maxBytesArquivo));
    }

    /**
     * @return the ldapUrl
     */
    public String getLdapUrl() {
        return configDao.getConf().getProperty("ldap.url");
    }

    /**
     * @param url the url to set
     */
    public void setLdapUrl(String url) {
        configDao.getConf().setProperty("ldap.url", url);
    }

    /**
     * @return the initialCtx
     */
    public String getInitialCtx() {
        return configDao.getConf().getProperty("ldap.initialCtx");
    }

    /**
     * @param initialCtx the initialCtx to set
     */
    public void setInitialCtx(String initialCtx) {
        configDao.getConf().setProperty("ldap.initialCtx", initialCtx);
    }

    /**
     * @return the ldapServer
     */
    public String getLdapServer() {
        return configDao.getConf().getProperty("ldap.server");
    }

    /**
     * @param server the server to set
     */
    public void setLdapServer(String server) {
        configDao.getConf().setProperty("ldap.server", server);
    }

    /**
     * @return the searchBase
     */
    public String getSearchBase() {
        return configDao.getConf().getProperty("ldap.searchBase");
    }

    /**
     * @param searchBase the searchBase to set
     */
    public void setSearchBase(String searchBase) {
        configDao.getConf().setProperty("ldap.searchBase", searchBase);
    }

    /**
     * @return the baseDN
     */
    public String getBaseDN() {
        return configDao.getConf().getProperty("ldap.baseDN");
    }

    /**
     * @param baseDN the baseDN to set
     */
    public void setBaseDN(String baseDN) {
        configDao.getConf().setProperty("ldap.baseDN", baseDN);
    }

    /**
     * @return the databaseString
     */
    public String getDatabaseString() {
        return configDao.getConf().getProperty("jdbc.databaseString");
    }

    /**
     * @param databaseString the databaseString to set
     */
    public void setDatabaseString(String databaseString) {
        configDao.getConf().setProperty("jdbc.databaseString", databaseString);
    }

    /**
     * @return the databaseUser
     */
    public String getDatabaseUser() {
        return configDao.getConf().getProperty("jdbc.databaseUser");
    }

    /**
     * @param databaseUser the databaseUser to set
     */
    public void setDatabaseUser(String databaseUser) {
        configDao.getConf().setProperty("jdbc.databaseUser",databaseUser);
    }

    /**
     * @return the databasePassword
     */
    public String getDatabasePassword() {
        return configDao.getValue("jdbc.databasePassword");
    }

    /**
     * @param databasePassword the databasePassword to set
     */
    public void setDatabasePassword(String databasePassword) {
        configDao.getConf().setProperty("jdbc.databasePassword", configDao.getEncryptor().encrypt(databasePassword));
    }

    /**
     * @return the mailHost
     */
    public String getMailHost() {
        return configDao.getConf().getProperty("mail.mailHost");
    }

    /**
     * @param mailHost the mailHost to set
     */
    public void setMailHost(String mailHost) {
        configDao.getConf().setProperty("mail.mailHost",mailHost);
    }

    /**
     * @return the smtpPort
     */
    public int getSmtpPort() {
        return Integer.parseInt(configDao.getConf().getProperty("mail.smtpPort"));
    }

    /**
     * @param smtpPort the smtpPort to set
     */
    public void setSmtpPort(int smtpPort) {
        configDao.getConf().setProperty("mail.smtpPort", String.valueOf(smtpPort));
    }

    /**
     * @return the useSSL
     */
    public boolean isUseSSL() {
        return Boolean.parseBoolean(configDao.getConf().getProperty("mail.useSSL"));
    }

    /**
     * @param useSSL the useSSL to set
     */
    public void setUseSSL(boolean useSSL) {
        configDao.getConf().setProperty("mail.useSSL", String.valueOf(useSSL));
    }

    /**
     * @return the mailSender
     */
    public String getMailSender() {
        return configDao.getConf().getProperty("mail.mailSender");
    }

    /**
     * @param mailSender the mailSender to set
     */
    public void setMailSender(String mailSender) {
        configDao.getConf().setProperty("mail.mailSender", mailSender);
    }

    /**
     * @return the mailUser
     */
    public String getMailUser() {
        return configDao.getConf().getProperty("mail.mailUser");
    }

    /**
     * @param mailUser the mailUser to set
     */
    public void setMailUser(String mailUser) {
        configDao.getConf().setProperty("mail.mailUser", mailUser);
    }

    /**
     * @return the mailPassword
     */
    public String getMailPassword() {
        return configDao.getValue("mail.mailPassword");
    }

    /**
     * @param mailPassword the mailPassword to set
     */
    public void setMailPassword(String mailPassword) {
        configDao.getConf().setProperty("mail.mailPassword", configDao.getEncryptor().encrypt(mailPassword));
    }

    /**
     * @return the mailScheduler Spring Scheduler cron like style
     */
    public String getMailScheduler() {
        return configDao.getConf().getProperty("mail.mailScheduler");
    }

    /**
     * @param mailScheduler the mailScheduler to set
     */
    public void setMailScheduler(String mailScheduler) {
        configDao.getConf().setProperty("mail.mailScheduler", mailScheduler);
    }
}
