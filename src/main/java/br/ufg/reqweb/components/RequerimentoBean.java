/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.reqweb.components;

import br.ufg.reqweb.dao.RequerimentoDao;
import br.ufg.reqweb.dao.UsuarioDao;
import br.ufg.reqweb.model.DeclaracaoDeMatricula;
import br.ufg.reqweb.model.Requerimento;
import br.ufg.reqweb.model.TipoRequerimentoEnum;
import br.ufg.reqweb.model.Usuario;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 *
 * @author andre
 */
@Component
@Scope(value = "session")
public class RequerimentoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String ADICIONA = "a";
    public static final String EDITA = "e";

    @Autowired
    private Validator validator;
    
    @Autowired
    private UsuarioDao usuarioDAo;

    @Autowired
    private RequerimentoDao requerimentoDao;

    private Requerimento requerimento;
    private Requerimento itemSelecionado;
    private TipoRequerimentoEnum tipoRequerimento;
    private String operation;//a: adiciona | e: edita
    private String termoBusca;
    private final LazyDataModel<Requerimento> requerimentosDataModel;

    public RequerimentoBean() {
        requerimento = new Requerimento();
        itemSelecionado = null;
        tipoRequerimento = null;
        operation = null;
        termoBusca = "";

        requerimentosDataModel = new LazyDataModel<Requerimento>() {
            private List<Requerimento> dataSource;

            @Override
            public Object getRowKey(Requerimento requerimento) {
                return requerimento.getId().toString();
            }

            @Override
            public Requerimento getRowData(String key) {
                for (Requerimento r : dataSource) {
                    if (r.getId().toString().equals(key)) {
                        return r;
                    }
                }
                return null;
            }

            @Override
            public List<Requerimento> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
                setPageSize(pageSize);

                if (tipoRequerimento != null) {
                    dataSource = requerimentoDao.find(termoBusca, tipoRequerimento);
                    setRowCount(dataSource.size());
                } else {
                    if (termoBusca.equals("")) {
                        dataSource = requerimentoDao.find(first, pageSize);
                        setRowCount(requerimentoDao.count());
                    } else {
                        dataSource = requerimentoDao.find(termoBusca);
                        setRowCount(dataSource.size());
                    }

                }
                if (dataSource.size() > pageSize) {
                    try {
                        return dataSource.subList(first, first + pageSize);
                    } catch (IndexOutOfBoundsException e) {
                        return dataSource.subList(first, first + (dataSource.size() % pageSize));
                    }

                }
                return dataSource;
            }

        };
    }

    public void novoRequerimento() {
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        setOperation(ADICIONA);
        requerimento = new Requerimento();
        requerimento.setTipoRequerimento(tipoRequerimento);
        System.out.println("objeto requerimento criado: " + requerimento.getClass());
        System.out.println("tipo de Requerimento: " + tipoRequerimento.getTipo());
        if (tipoRequerimento.equals(TipoRequerimentoEnum.DECLARACAO_DE_MATRICULA)) {
            DeclaracaoDeMatricula itemRequerimento = new DeclaracaoDeMatricula();
            sessionMap.put("declaracaoDeMatricula", itemRequerimento);
            System.out.println("objeto itemRequerimento criado: " + DeclaracaoDeMatricula.class);
        }
    }

    public void editaRequerimento() {
        if (!isSelecionado()) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "info", LocaleBean.getMessageBundle().getString("itemSelecionar"));
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } else {
            setOperation(EDITA);
            requerimento = getItemSelecionado();
        }
    }

    public void excluiRequerimento() {
        FacesMessage msg;
        if (getItemSelecionado() == null) {
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "info", LocaleBean.getMessageBundle().getString("itemSelecionar"));
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } else {
            requerimentoDao.excluir(itemSelecionado);
            itemSelecionado = null;
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "info", LocaleBean.getMessageBundle().getString("dadosExcluidos"));
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    public void salvaRequerimento() {
        FacesMessage msg;
        RequestContext context = RequestContext.getCurrentInstance();
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        
        String loginUser = SecurityContextHolder.getContext().getAuthentication().getName();
        requerimento.setDiscente(usuarioDAo.findByLogin(loginUser));
        
        if (tipoRequerimento.equals(TipoRequerimentoEnum.DECLARACAO_DE_MATRICULA)) {
            DeclaracaoDeMatricula item = (DeclaracaoDeMatricula) sessionMap.get("declaracaoDeMatricula");
            requerimento.addItemRequerimento(item);
        }
        Set<ConstraintViolation<Requerimento>> errors = validator.validate(requerimento);
        if (errors.isEmpty()) {
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "info", LocaleBean.getMessageBundle().getString("dadosSalvos"));
            context.addCallbackParam("resultado", true);
            if (operation.equals(ADICIONA)) {
                requerimentoDao.adicionar(requerimento);
                itemSelecionado = requerimento;
            } else {
                requerimentoDao.atualizar(requerimento);
            }
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "info", LocaleBean.getMessageBundle().getString("dadosInvalidos"));
            FacesContext.getCurrentInstance().addMessage(null, msg);
            context.addCallbackParam("resultado", false);
        }
    }

    public void selecionaItem(SelectEvent event) {
        itemSelecionado = (Requerimento) event.getObject();
    }

    /**
     * @return the requerimento
     */
    public Requerimento getRequerimento() {
        return requerimento;
    }

    /**
     * @param requerimento the requerimento to set
     */
    public void setRequerimento(Requerimento requerimento) {
        this.requerimento = requerimento;
    }

    /**
     * @return the itemSelecionado
     */
    public Requerimento getItemSelecionado() {
        return itemSelecionado;
    }

    /**
     * @param itemSelecionado the itemSelecionado to set
     */
    public void setItemSelecionado(Requerimento itemSelecionado) {
        this.itemSelecionado = itemSelecionado;
    }

    public boolean isSelecionado() {
        return itemSelecionado != null;
    }

    /**
     *
     * @return the tipoRequerimento
     */
    public TipoRequerimentoEnum getTipoRequerimento() {
        return tipoRequerimento;
    }

    /**
     *
     * @param tipoRequerimento the tipoRequerimento to set
     */
    public void setTipoRequerimento(TipoRequerimentoEnum tipoRequerimento) {
        System.out.println("metodo setTipoRequerimento invocado");
        this.tipoRequerimento = tipoRequerimento;
    }

    /**
     *
     * @return the tipoRequerimento
     */
    public List<TipoRequerimentoEnum> getTipoRequerimentoList() {
        return Arrays.asList(TipoRequerimentoEnum.values());
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

    public LazyDataModel<Requerimento> getRequerimentosDataModel() {
        return requerimentosDataModel;
    }
}
