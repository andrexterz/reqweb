/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.reqweb.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 *
 * @author andre
 */
public class Settings {


    private static final Logger log = Logger.getLogger(Settings.class);
    private static Settings instance;
    private static Properties conf;

    public Settings() {
        conf = new Properties();
        try {
            InputStream in = this.getClass().getResourceAsStream("/reqweb.properties");
            conf.load(in);
        } catch (IOException | NullPointerException e) {
            log.error("No config found: loading default configuration...");
            conf.setProperty("minPeriodo", "5");
            conf.setProperty("maxArquivos", "3");
            conf.setProperty("maxCopias", "2");
            conf.setProperty("maxArquivo", "1048576");
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
}
