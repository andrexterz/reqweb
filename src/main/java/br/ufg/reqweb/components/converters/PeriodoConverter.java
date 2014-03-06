/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufg.reqweb.components.converters;

import br.ufg.reqweb.dao.PeriodoDao;
import br.ufg.reqweb.model.Periodo;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author andre
 */

@Component
public class PeriodoConverter implements Converter {
    
    @Autowired
    PeriodoDao periodoDao;

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Periodo periodo = (Periodo) periodoDao.findById(Long.parseLong(value));
        return periodo;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        try {
            return Long.toString(((Periodo) value).getId());
        } catch (NullPointerException e) {
            return null;
        }        
    }
    
}
