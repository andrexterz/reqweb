/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.reqweb.components;

import br.ufg.reqweb.dao.PermissaoDao;
import br.ufg.reqweb.model.PerfilEnum;
import br.ufg.reqweb.model.Permissao;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author andre
 */
@Component
public class PermissaoBean implements Serializable {

    public PermissaoBean() {
        permissao = new Permissao();
        permissoes = null;
        itemSelecionado = null;
        operation = null;
        termoBusca = "";
    }

    @Autowired
    PermissaoDao permissaoDao;

    @Autowired
    private Validator validator;

    private static final String ADICIONA = "a";
    private static final String EDITA = "e";
    private Permissao permissao;
    private List<Permissao> permissoes;
    private PerfilEnum tipoPerfil;
    private List<PerfilEnum> tipoPerfis;
    private Permissao itemSelecionado;
    private String operation;
    private String termoBusca;

    public void novaPermissao() {
        setOperation(ADICIONA);
        permissao = new Permissao();
    }

    public void editaPermissao() {
        if (!isSelecionado()) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "info", LocaleBean.getMessageBundle().getString("itemSelecionar"));
            FacesContext.getCurrentInstance().addMessage(null, msg);

        } else {
            setOperation(EDITA);
            permissao = getItemSelecionado();
        }
    }

    public void excluiPermissao() {
        FacesMessage msg;
        if (getItemSelecionado() == null) {
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "info", LocaleBean.getMessageBundle().getString("itemSelecionar"));
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } else {
            permissaoDao.excluir(itemSelecionado);
            itemSelecionado = null;
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "info", LocaleBean.getMessageBundle().getString("dadosExcluidos"));
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    public void salvaPermissao() {
        FacesMessage msg;
        RequestContext context = RequestContext.getCurrentInstance();
        permissao.setTipoPerfil(tipoPerfis);
        Set<ConstraintViolation<Permissao>> errors = validator.validate(permissao);
        if (errors.isEmpty()) {
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "info", LocaleBean.getMessageBundle().getString("dadosSalvos"));
            context.addCallbackParam("resultado", true);
            if (operation.equals(ADICIONA)) {
                permissaoDao.adicionar(permissao);
                itemSelecionado = permissao;
            } else {
                permissaoDao.atualizar(permissao);
            }
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "info", LocaleBean.getMessageBundle().getString("dadosInvalidos"));
            FacesContext.getCurrentInstance().addMessage(null, msg);
            context.addCallbackParam("resultado", false);
        }
    }

    public void selecionaItem(SelectEvent event) {
        itemSelecionado = (Permissao) event.getObject();
    }

    public void filtraPermissoesPorTipoPerfil() {
        if (tipoPerfil != null) {
            permissoes = permissaoDao.findByPerfil(tipoPerfil);
        }
    }

    public List<String> getUrls() {
        FacesContext context = FacesContext.getCurrentInstance();
        Map<String, Object> viewMap = context.getViewRoot().getViewMap();
        for (Map.Entry<String, Object> entry:viewMap.entrySet()) {
            System.out.println("s: o -> " + entry.getKey() + ": " + entry.getValue());
        }
        List<String> urls = new ArrayList<>();
        return urls;
    }

    /**
     * *
     *
     * @return the permissoes
     */
    public List<Permissao> getPermissoes() {
        return permissoes;
    }

    /**
     * @return the permissao
     */
    public Permissao getPermissao() {
        return permissao;
    }

    /**
     * @param permissao the permissao to set
     */
    public void setPermissao(Permissao permissao) {
        this.permissao = permissao;
    }

    /**
     *
     * @return tipoPerfil
     */
    public PerfilEnum getTipoPerfil() {
        return tipoPerfil;
    }

    /**
     *
     * @param tipoPerfil
     */
    public void setTipoPerfil(PerfilEnum tipoPerfil) {
        this.tipoPerfil = tipoPerfil;
    }

    /**
     *
     * @return tipoPerfis
     */
    public List<PerfilEnum> getTipoPerfis() {
        return tipoPerfis;
    }

    /**
     *
     * @param tipoPerfis
     */
    public void setTipoPerfis(List<PerfilEnum> tipoPerfis) {
        this.tipoPerfis = tipoPerfis;
    }

    /**
     *
     * @return true if item is selected or false otherwise.
     */
    public boolean isSelecionado() {
        return itemSelecionado != null;
    }

    /**
     * @return the itemSelecionado
     */
    public Permissao getItemSelecionado() {
        return itemSelecionado;
    }

    /**
     * @param itemSelecionado the itemSelecionado to set
     */
    public void setItemSelecionado(Permissao itemSelecionado) {
        this.itemSelecionado = itemSelecionado;
    }

    /**
     * @return the operation
     */
    public String getOperation() {
        return operation;
    }

    /**
     * @param operation the operation to set
     */
    public void setOperation(String operation) {
        this.operation = operation;
    }

    /**
     * @return the termoBusca
     */
    public String getTermoBusca() {
        return termoBusca;
    }

    /**
     * @param termoBusca the termoBusca to set
     */
    public void setTermoBusca(String termoBusca) {
        this.termoBusca = termoBusca;
    }
}
