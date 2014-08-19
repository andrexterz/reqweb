/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.reqweb.components;

import br.ufg.reqweb.dao.CursoDao;
import java.io.Serializable;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.springframework.stereotype.Component;

import br.ufg.reqweb.model.Curso;
import java.util.ArrayList;
import java.util.Set;
import javax.faces.event.ActionEvent;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

/**
 *
 * @author andre
 */
@Component
@Scope(value = "session")
public class CursoBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String ADICIONA = "a";
    private static final String EDITA = "e";
    @Autowired
    private Validator validator;
    @Autowired
    private CursoDao cursoDao;

    private Curso curso;
    private Curso itemSelecionado;
    private String operation;//a: adiciona | e: edita
    private String termoBusca;
    private List<Curso> cursos;

    public CursoBean() {
        curso = new Curso();
        itemSelecionado = null;
        operation = null;
        termoBusca = "";
        cursos = new ArrayList<>();
    }

    public void novoCurso() {
        setOperation(ADICIONA);
        curso = new Curso();
    }

    public void editaCurso() {
        if (!isSelecionado()) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "info", LocaleBean.getMessageBundle().getString("itemSelecionar"));
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } else {
            setOperation(EDITA);
            curso = getItemSelecionado();
        }
    }

    public void excluiCurso() {
        FacesMessage msg;
        if (getItemSelecionado() == null) {
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "info", LocaleBean.getMessageBundle().getString("itemSelecionar"));
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } else {
            cursoDao.excluir(itemSelecionado);
            itemSelecionado = null;
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "info", LocaleBean.getMessageBundle().getString("dadosExcluidos"));
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    public void salvaCurso() {
        boolean saveStatus = false;
        FacesMessage msg;
        RequestContext context = RequestContext.getCurrentInstance();
        Set<ConstraintViolation<Curso>> errors = validator.validate(curso);
        try {
            if (errors.isEmpty()) {
                context.addCallbackParam("resultado", true);
                if (operation.equals(ADICIONA)) {
                    cursoDao.adicionar(curso);
                    itemSelecionado = curso;
                } else {
                    cursoDao.atualizar(curso);
                }
                saveStatus = true;
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        if (saveStatus) {
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "info", LocaleBean.getMessageBundle().getString("dadosSalvos"));
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "info", LocaleBean.getMessageBundle().getString("dadosInvalidos"));
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
        context.addCallbackParam("resultado", saveStatus);
    }

    public void selecionaItem(SelectEvent event) {
        itemSelecionado = (Curso) event.getObject();
    }

    public List<Curso> getFiltroCursos() {
        System.out.println("termo da busca: " + termoBusca);
        if (termoBusca.equals("")) {
            return cursoDao.findAll();
        } else {
            return cursoDao.find(termoBusca);
        }
    }

    public List<Curso> findCurso(String query) {
        return cursoDao.find(query);
    }

    public List<Curso> getCursos() {
        if (cursos.isEmpty() | cursoDao.count() != cursos.size()) {
            cursos = cursoDao.findAll();
        }
        return cursos;
    }

    /**
     * @return the curso
     */
    public Curso getCurso() {
        return curso;
    }

    /**
     * @param curso the curso to set
     */
    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    /**
     * tests if itemSelecionado
     *
     * @return
     */
    public boolean isSelecionado() {
        return itemSelecionado != null;
    }

    /**
     * @return the itemSelecionado
     */
    public Curso getItemSelecionado() {
        return itemSelecionado;
    }

    /**
     * @param itemSelecionado the itemSelecionado to set
     */
    public void setItemSelecionado(Curso itemSelecionado) {
        this.itemSelecionado = itemSelecionado;
    }

    public void selecionaItemListener(ActionEvent event) {
        itemSelecionado = (Curso) event.getComponent().getAttributes().get("cursoItem");
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
