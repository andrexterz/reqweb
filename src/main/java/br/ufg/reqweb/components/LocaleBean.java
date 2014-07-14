package br.ufg.reqweb.components;

/**
 *
 * @author Andre Luiz Fernandes Ribeiro Barca
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "session")
public class LocaleBean implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static final String PT_BR = "pt_BR";
    private static final String EN_US = "en_US";
    private static final List<SelectItem> LOCALES;

    static {
        LOCALES = new ArrayList<>(2);
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
    
    public String getLocaleName() {
        return getMessageBundle().getString("language");
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
    
    public static ResourceBundle getMessageBundle() {
        ResourceBundle messages = ResourceBundle.getBundle(
            "locale.messages",
            FacesContext.getCurrentInstance().getViewRoot().getLocale());
        return messages;
    }
    
    public static ResourceBundle getDefaultMessageBundle() {
        return ResourceBundle.getBundle("locale.messages");
    }

}
