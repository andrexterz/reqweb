/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.reqweb.components;

import br.ufg.reqweb.util.Settings;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author andre
 */
@Component
@Scope(value = "singleton")
public class ConfigBean implements Serializable {

    private final Properties config;

    public ConfigBean() {
        config = new Settings().getConf();
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
            minPeriodo.add(Calendar.DAY_OF_MONTH, 1 - Integer.parseInt(config.getProperty(key)));
            return minPeriodo.getTime();
        }
        if (key.equals("maxPeriodo")) {
            return Calendar.getInstance().getTime();
        }
        return config.getProperty(key);
    }

    public boolean isExpiredPeriodo(Date date) {
        if (date != null) {
            Calendar minPeriodo = Calendar.getInstance();
            minPeriodo.add(Calendar.DAY_OF_MONTH, -Integer.parseInt(config.getProperty("minPeriodo")));
            return date.before(minPeriodo.getTime());
        } else {
            return false;
        }
    }

}
