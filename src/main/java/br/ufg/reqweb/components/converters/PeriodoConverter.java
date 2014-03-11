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
        try {
           return (Periodo) periodoDao.findById(Long.parseLong(value));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        Periodo obj = (Periodo) value;
        if (obj != null && obj.getId() != null) {
            return Long.toString(obj.getId());
         } else {
            return null;
        }
    }
}
