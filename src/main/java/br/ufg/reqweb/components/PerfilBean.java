/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.reqweb.components;

import br.ufg.reqweb.model.Perfil;
import br.ufg.reqweb.model.PerfilEnum;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.MenuModel;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author andre
 */

@Component
@Scope(value = "session")
public final class PerfilBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private final List<PerfilEnum> items;
    private Perfil perfil;
    private final MenuModel menuItems;    
    

    public PerfilBean() {
        this.items = new ArrayList<>();
        this.items.addAll(Arrays.asList(PerfilEnum.values()));
        perfil = new Perfil();
        perfil.setTipoPerfil(PerfilEnum.ADMINISTRADOR);
        menuItems = new DefaultMenuModel();
        for (PerfilEnum p: getItems()) {
             DefaultMenuItem item = new DefaultMenuItem(p.getPapel());
             //item.setCommand(null);
             item.setUpdate("perfilUsuarioButton");
            menuItems.addElement(item);
            
        }
        
    }

    /**
     * @return the perfis
     */
    public List<PerfilEnum> getItems() {
        return items;
    }
    
    public MenuModel getMenuItems() {
        return menuItems;
    }
    
    public Perfil getPerfil() {
        return perfil;
    }

    public void setPerfil(Perfil perfil) {
        this.perfil = perfil;
    }
}
