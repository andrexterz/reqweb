/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.reqweb.components.converters;

import br.ufg.reqweb.components.CursoBean;
import br.ufg.reqweb.dao.CursoDao;
import br.ufg.reqweb.model.Curso;
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
public class CursoConverter implements Converter {
    
    @Autowired
    CursoDao cursoDao;

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        try {
        Curso curso = cursoDao.findById(Long.parseLong(value));
        return curso;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        Curso obj = (Curso) value;
        if (obj != null && obj.getId() != null) {
            return Long.toString(obj.getId());
         } else {
            return null;
        }

    }

}
