/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.reqweb.components;

import br.ufg.reqweb.dao.TurmaDao;
import br.ufg.reqweb.model.Disciplina;
import br.ufg.reqweb.model.Perfil;
import br.ufg.reqweb.model.PerfilEnum;
import br.ufg.reqweb.model.Turma;
import br.ufg.reqweb.model.Usuario;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author andre
 */
@Component
@Scope(value = "session")
public class TurmaBean {

    private static final long serialVersionUID = 1L;
    @Autowired
    Validator validator;
    @Autowired
    TurmaDao turmaDao;
    private Turma turma;
    private Usuario usuario;
    private Turma itemSelecionado;
    private Turma itemPreviewSelecionado;
    private String termoBusca;
    public static final String ADICIONA = "a";
    public static final String EDITA = "e";
    private Thread tImportJob;
    private String operation;
    private int progress;
    private volatile boolean stopImportaTurmas;
    private Map<Long, Turma> turmaListPreview;
    private final LazyDataModel turmas;

    public TurmaBean() {
        turma = new Turma();
        operation = null;
        itemSelecionado = null;
        itemPreviewSelecionado = null;
        termoBusca = null;
        turmas = new LazyDataModel<Turma>() {
            @Override
            public List<Turma> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
                setPageSize(pageSize);
                List<Turma> turmaList;
                turmaList = turmaDao.find(first, pageSize);
                setRowCount(turmaDao.count());
                return turmaList;
            }
        };
    }

    public void novaTurma(ActionEvent event) {
        setOperation(ADICIONA);
        turma = new Turma();
    }

    public void editaTurma(ActionEvent event) {
        if (getItemSelecionado() == null) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "info", LocaleBean.getMessageBundle().getString("itemSelecionar"));
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } else {
            setOperation(EDITA);
            turma = getItemSelecionado();
            FacesContext context = FacesContext.getCurrentInstance();
        }
    }

    public void excluiTurma() {
        FacesMessage msg;
        if (getItemSelecionado() == null) {
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "info", LocaleBean.getMessageBundle().getString("itemSelecionar"));
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } else {
            turmaDao.excluir(itemSelecionado);
            itemSelecionado = null;
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "info", LocaleBean.getMessageBundle().getString("dadosExcluidos"));
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    public void salvaTurmas() {

    }

    public void salvaTurma() {
        FacesMessage msg;
        RequestContext context = RequestContext.getCurrentInstance();
        turma.setUsuario(usuario);
        Set<ConstraintViolation<Turma>> errors = validator.validate(turma);
        if (errors.isEmpty()) {
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "info", LocaleBean.getMessageBundle().getString("dadosSalvos"));
            context.addCallbackParam("resultado", true);
            if (operation.equals(ADICIONA)) {
                turmaDao.adicionar(turma);
                itemSelecionado = turma;
            } else {
                turmaDao.atualizar(turma);
            }
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "info", LocaleBean.getMessageBundle().getString("dadosInvalidos"));
            FacesContext.getCurrentInstance().addMessage(null, msg);
            context.addCallbackParam("resultado", false);
        }
    }

    public void selecionaItem(SelectEvent event) {

    }

    public void selecionaItemPreview(SelectEvent event) {

    }

    public void autoCompleteSelecionaUsuario(SelectEvent event) {
        try {
            usuario = (Usuario) event.getObject();
        } catch (NullPointerException e) {
            usuario = null;
        }
    }

    public Turma getTurma() {
        return turma;
    }

    public void setTurma(Turma turma) {
        this.turma = turma;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public boolean isSelecionado() {
        return itemSelecionado != null;
    }

    public Turma getItemSelecionado() {
        return itemSelecionado;
    }

    public void setItemSelecionado(Turma itemSelecionado) {
        this.itemSelecionado = itemSelecionado;
    }

    public boolean isPreviewSelecionado() {
        return itemPreviewSelecionado != null;
    }

    public Turma getItemPreviewSelecionado() {
        return itemPreviewSelecionado;
    }

    public void setItemPreviewSelecionado(Turma itemPreviewSelecionado) {
        this.itemPreviewSelecionado = itemPreviewSelecionado;
    }

    public String getTermoBusca() {
        return termoBusca;
    }

    public void setTermoBusca(String termoBusca) {
        this.termoBusca = termoBusca;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public boolean getStopImportaTurmas() {
        return stopImportaTurmas;
    }

    public Map<Long, Turma> getTurmaListPreview() {
        return turmaListPreview;
    }

    public LazyDataModel getTurmas() {
        return turmas;
    }
}
