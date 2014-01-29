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
public class PerfilBean implements Serializable {

    public PerfilBean() {
        this.items = new ArrayList<>();
        this.items.addAll(Arrays.asList(PerfilEnum.values()));
    }

    private static final long serialVersionUID = 1L;
    private final List<PerfilEnum> items;

    /**
     * @return the perfis
     */
    public List<PerfilEnum> getItems() {
        return items;
    }
}
