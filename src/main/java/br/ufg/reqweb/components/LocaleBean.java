package br.ufg.reqweb.components;

/**
 *
 * @author Andre Luiz Fernandes Ribeiro Barca
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import org.springframework.stereotype.Component;


@Component
public class LocaleBean {

    private static final String PT_BR = "pt_BR";
    private static final String EN_US = "en_US";
    private static final List<SelectItem> LOCALES;
    static {
        LOCALES = new ArrayList<SelectItem>(2);
        LOCALES.add(new SelectItem(EN_US, "English"));
        LOCALES.add(new SelectItem(PT_BR, "Português"));
    }

    private String locale;

    /**
     * @return the locale
     */
    public String getLocale() {
        if (locale == null) {
            return PT_BR;
        } else {
            return locale;
        }
    }

    /**
     * @param locale the locale to set
     */
    public void setLocale(String locale) {
        this.locale = locale;
    }

    public List<SelectItem> getLocaleList() {
        return LOCALES;
    }

    public void changeLocale(ValueChangeEvent event) {
        locale = event.getNewValue().toString();
        FacesContext context = FacesContext.getCurrentInstance();
        context.getViewRoot().setLocale(new Locale(locale));
    }

}