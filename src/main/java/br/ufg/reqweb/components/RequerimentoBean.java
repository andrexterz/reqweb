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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
    
    private static final String ADICIONA = "a";
    private static final String EDITA = "e";

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
    
    public enum TipoBusca {
        DATA_INTERVALO("dataCriacao"),
        TIPO_REQUERIMENTO("requerimento"),
        TERMO_BUSCA("discente");
        
        TipoBusca(String tipo) {
            this.tipo = tipo;
        }
        
        private final String tipo;
        
        public String getTipo() {
            return tipo;
        }
        
        public String getTipoLocale() {
            return LocaleBean.getMessageBundle().getString(tipo);
        }
    };
    
    private TipoBusca tipoBusca;
    
    public RequerimentoBean() {
        requerimento = new Requerimento();
        itemSelecionado = null;
        tipoRequerimento = null;
        operation = null;
        termoBusca = "";
        tipoBusca = TipoBusca.TERMO_BUSCA;

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

                if (tipoRequerimento != null && termoBusca.isEmpty()) {
                    dataSource = requerimentoDao.find(tipoRequerimento);
                    setRowCount(dataSource.size());
                }
                if (tipoRequerimento == null && termoBusca.isEmpty()) {
                    dataSource = requerimentoDao.find(first, pageSize);
                    setRowCount(requerimentoDao.count());
                }
                if (tipoRequerimento !=null && !termoBusca.isEmpty()) {
                    dataSource = requerimentoDao.find(termoBusca);
                    setRowCount(dataSource.size());
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
            sessionMap.put("itemRequerimento", itemRequerimento);
            System.out.println("objeto itemRequerimento criado: " + DeclaracaoDeMatricula.class);
        }
    }

    public void editaRequerimento() {
        if (!isSelecionado()) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "info", LocaleBean.getMessageBundle().getString("itemSelecionar"));
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } else {
            Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
            setOperation(EDITA);
            requerimento = requerimentoDao.findById(getItemSelecionado().getId());
            System.out.println("size: " + requerimento.getItemRequerimentoList().size());
            if (requerimento.getTipoRequerimento().equals(TipoRequerimentoEnum.DECLARACAO_DE_MATRICULA)) {
                sessionMap.put("itemRequerimento", requerimento.getItemRequerimentoList().iterator().next());
            }
        }
    }
    
    /**
     * cancels actions from <code>novoRequerimento</code>
     */
    public void cancelRequerimento() {
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        sessionMap.remove("itemRequerimento");
        setTipoRequerimento(null);
        System.out.println("cancelando add/update requerimento... ");
        System.out.println(getTipoRequerimento() + ":" + sessionMap.get("itemRequerimento"));
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
        System.out.println("req: " + requerimento);
        requerimento.setDiscente(usuarioDAo.findByLogin(loginUser));

        if (requerimento.getTipoRequerimento().equals(TipoRequerimentoEnum.DECLARACAO_DE_MATRICULA)) {
            DeclaracaoDeMatricula item = (DeclaracaoDeMatricula) sessionMap.get("itemRequerimento");
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
            sessionMap.remove("itemRequerimento");
            setTipoRequerimento(null);
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "info", LocaleBean.getMessageBundle().getString("dadosInvalidos"));
            FacesContext.getCurrentInstance().addMessage(null, msg);
            context.addCallbackParam("resultado", false);
        }
    }

    public void selecionaItem(SelectEvent event) {
        itemSelecionado = (Requerimento) event.getObject();
    }
    
    public List<TipoRequerimentoEnum> findTipoRequerimento(String query) {
        List<TipoRequerimentoEnum> items = new ArrayList<>();
        for (TipoRequerimentoEnum t: TipoRequerimentoEnum.values()) {
           if (t.getTipoLocale().toLowerCase().contains(query.toLowerCase())) {
               items.add(t);
           }
        }
        return items;
    }
    
    public void autoCompleteSelecionaTipoBuscaRequerimento(SelectEvent event) {
        try {
            tipoRequerimento = (TipoRequerimentoEnum) event.getObject();
        } catch (NullPointerException e) {
            tipoRequerimento = null;
        }
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
    
    public Map<String,TipoBusca> getTipoBuscaEnum () {
        Map<String,TipoBusca> items = new HashMap();
        for (TipoBusca t: TipoBusca.values()) {
            items.put(t.getTipoLocale(), t);
        }
        return items;
    }

    /**
     * @return the tipoBusca
     */
    public TipoBusca getTipoBusca() {
        return tipoBusca;
    }

    /**
     * @param tipoBusca the tipoBusca to set
     */
    public void setTipoBusca(TipoBusca tipoBusca) {
        if (tipoBusca != TipoBusca.TIPO_REQUERIMENTO) {
            tipoRequerimento = null;
        }
        this.tipoBusca = tipoBusca;
    }
}
