/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.reqweb.dao;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.jasypt.properties.EncryptableProperties;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

/**
 *
 * @author andre
 */
@Repository
@Lazy(false)
@Scope(value = "singleton")
public class ConfigDao {

    private final String propertyFile = "/reqweb.properties";
    private final StandardPBEStringEncryptor encryptor;
    private final Properties conf;
    private static ConfigDao instance = null;

    public ConfigDao() {
        encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword("gIWxV7lW3rt3TJb1NweZKg");
        conf = new EncryptableProperties(encryptor);
        try {
            InputStream inConf = this.getClass().getResourceAsStream(propertyFile);
            conf.load(inConf);
        } catch (IOException | NullPointerException e) {
            System.out.println("No config found: classpath:reqweb.properties");
        }
    }
    

    public static ConfigDao getInstance() {
        if (instance == null) {
            instance = new ConfigDao();
        }
        return instance;
    }

    public void salva() throws IOException {
        String confPath = this.getClass().getClassLoader().getResource(propertyFile).getPath();
        try (OutputStream outConf = new FileOutputStream(confPath)) {
            conf.store(outConf, propertyFile);
            System.out.println("saved: " + confPath);
        }
    }

    public StandardPBEStringEncryptor getEncryptor() {
        return encryptor;
    }

    public Properties getConf() {
        return conf;
    }

    /**
     *
     * @param key
     * @return property value as plain text
     */
    public String getValue(String key) {
        String value = conf.getProperty(key);
        try {
            return encryptor.decrypt(value);
        } catch (EncryptionOperationNotPossibleException e) {
            return value;
        }
    }
}
