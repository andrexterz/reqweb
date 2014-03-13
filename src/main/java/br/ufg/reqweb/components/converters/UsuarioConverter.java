/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufg.reqweb.components.converters;

import br.ufg.reqweb.dao.UsuarioDao;
import br.ufg.reqweb.model.Usuario;
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
public class UsuarioConverter implements Converter {
    @Autowired
    UsuarioDao usuarioDao;

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        try {
            return usuarioDao.findById(Long.parseLong(value));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        Usuario obj = (Usuario) value;
        if (obj != null && obj.getId() != null) {
            return Long.toString(obj.getId());
         } else {
            return null;
        }
    } 
}
