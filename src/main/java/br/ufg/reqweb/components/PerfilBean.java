/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.reqweb.components;

import br.ufg.reqweb.model.Perfil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 *
 * @author andre
 */
@Component
public class PerfilBean implements Serializable{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<Perfil> items;
  
    public PerfilBean() {
        this.items = new ArrayList<Perfil>();
        this.items.addAll(Arrays.asList(Perfil.values()));
    }

    /**
     * @return the perfis
     */
    public List<Perfil> getItems() {
        return items;
    }
    
      
     
}
