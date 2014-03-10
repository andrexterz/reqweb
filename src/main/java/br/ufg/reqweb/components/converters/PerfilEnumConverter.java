/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufg.reqweb.components.converters;

import br.ufg.reqweb.model.PerfilEnum;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import org.springframework.stereotype.Component;

/**
 *
 * @author André
 */

@Component
public class PerfilEnumConverter implements Converter{

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        return PerfilEnum.valueOf(value);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        PerfilEnum perfilType = (PerfilEnum) value;
        System.out.println(perfilType);
        return perfilType.toString();
    }
    
}
