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
import java.util.ResourceBundle;
import javax.faces.event.ActionEvent;
import org.apache.log4j.Logger;
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

    public CursoBean() {
        curso = null;
        itemSelecionado = null;
        operation = null;        
        termoBusca = "";
    }

    private static final long serialVersionUID = 1L;
    public static final String ADICIONA = "a";
    public static final String EDITA = "e";

    @Autowired
    private CursoDao cursoDao;
    private Curso curso;
    private Curso itemSelecionado;
    private String operation;//a: adiciona | e: edita
    private String termoBusca;
    private final ResourceBundle messages = ResourceBundle.getBundle(
            "locale.messages",
            FacesContext.getCurrentInstance().getViewRoot().getLocale());

    public void novoCurso(ActionEvent event) {
        setOperation(ADICIONA);
        curso = new Curso();
        Logger log = Logger.getLogger(CursoBean.class);
        log.info("objeto curso criado: " + curso.toString());
    }

    public void editaCurso(ActionEvent event) {
        if (getItemSelecionado() == null) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "info", messages.getString("itemSelecionar"));
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return;
        } else {
            setOperation(EDITA);
            curso = getItemSelecionado();
        }
    }

    public String excluiCurso() {
        FacesMessage msg;
        if (getItemSelecionado() == null) {
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "info", messages.getString("itemSelecionar"));
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return null;
        } else {
            cursoDao.excluir(itemSelecionado);
            itemSelecionado = null;
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "info", messages.getString("dadosExcluidos"));
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return listaCursos();
        }
    }

    public String listaCursos() {
        return "cursos";
    }

    public void selecionaItem(SelectEvent event) {
        itemSelecionado = (Curso) event.getObject();
    }

    public String salvaCurso() {
        FacesMessage msg;
        RequestContext context = RequestContext.getCurrentInstance();
        if (curso.getMatriz().equals("") || curso.getNome().equals("") || curso.getSigla().equals("")) {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "info", messages.getString("dadosInvalidos"));
            FacesContext.getCurrentInstance().addMessage(null, msg);
            context.addCallbackParam("resultado", false);
            return null;
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "info", messages.getString("dadosSalvos"));
            context.addCallbackParam("resultado", true);
            if (operation.equals(ADICIONA)) {
                cursoDao.adicionar(curso);
                itemSelecionado = curso;
            } else {
                cursoDao.atualizar(curso);
            }
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
        return listaCursos();
    }

    
    public List getCursos() {
        System.out.println("termo da busca: " + termoBusca);
        if (termoBusca.equals("")) {
            return cursoDao.listar();
        }
        else {
             
            return cursoDao.procurar(termoBusca);
        }
        
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
    
    
    public boolean isSelecionado(){
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
