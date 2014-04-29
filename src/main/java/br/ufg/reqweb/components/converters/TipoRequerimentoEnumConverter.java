/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufg.reqweb.components.converters;

import br.ufg.reqweb.model.TipoRequerimentoEnum;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import org.springframework.stereotype.Component;

/**
 *
 * @author André
 */

@Component
public class TipoRequerimentoEnumConverter implements Converter{

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        return TipoRequerimentoEnum.valueOf(value);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        TipoRequerimentoEnum tipoRequerimentoType = (TipoRequerimentoEnum) value;
        return tipoRequerimentoType.toString();
    }
    
}
