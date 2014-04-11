/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.reqweb.components;

import br.ufg.reqweb.model.PerfilEnum;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
    private PerfilEnum itemSelecionado;
    private final List<PerfilEnum> items;
    

    public PerfilBean() {
        itemSelecionado = null;
        items = new ArrayList<>();
        items.addAll(Arrays.asList(PerfilEnum.values()));
    }

    /**
     * @return the itemSelecionado
     */
    public PerfilEnum getItemSelecionado() {
        return itemSelecionado;
    }

    /**
     * @param itemSelecionado the itemSelecionado to set
     */
    public void setItemSelecionado(PerfilEnum itemSelecionado) {
        this.itemSelecionado = itemSelecionado;
    }

    /**
     * @return the perfis
     */
    public List<PerfilEnum> getItems() {
        return items;
    }
}
