/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.reqweb.components.converters;

import br.ufg.reqweb.dao.DisciplinaDao;
import br.ufg.reqweb.model.Disciplina;
import java.util.HashMap;
import java.util.Map;
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
public class DisciplinaConverter implements Converter {

    @Autowired
    DisciplinaDao disciplinaDao;

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value != null && !value.isEmpty()) {
            return disciplinaDao.findById(Long.parseLong(value));
        } else {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        Disciplina obj = (Disciplina) value;
        if (obj != null && obj.getId() != null) {
            return Long.toString(obj.getId());
         } else {
            return null;
        }
    }

}
