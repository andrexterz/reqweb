/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufg.reqweb.components;

import br.ufg.reqweb.dao.TurmaDao;
import br.ufg.reqweb.model.Turma;
import java.util.List;
import java.util.Map;
import javax.faces.event.ActionEvent;
import javax.validation.Validator;
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
    private Turma itemSelecionado;
    private Turma itemPreviewSelecionado;
    private String termoBusca;
    public static final String ADICIONA = "a";
    public static final String EDITA = "e";
    private Thread tImportJob;
    private String operation;
    private int progress;
    private volatile boolean stopImportaTurmas;
    private Map<Long,Turma> turmaListPreview;
    private final LazyDataModel turmas;
    
    public TurmaBean(){
        turma = new Turma();
        operation = null;
        itemSelecionado = null;
        turmas = new LazyDataModel<Turma>() {
            @Override
            public List<Turma> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters ) {
                setPageSize(pageSize);
                List<Turma> turmaList;
                //falta implementar busca
                // modificar DAO para ficar igual ao da disciplina find(first, pageSize) {...}
                turmaList = turmaDao.findAll();
                return turmaList;
            }
        };
    }
    
    public void novaTurma(ActionEvent event) {
        
    }
    
    public void editaTurma(ActionEvent event) {
        
    }

    public void excluiTurma() {
        
    }
    
    public void salvaTurmas () {
        
    }
    
    public String salvaTurma() {
        return listaTurmas();
    }
    
    public String listaTurmas() {
        return"turmas";
    }
    
    public void selecionaItem (SelectEvent event) {
        
    }
    
    public void selecionaItemPreview(SelectEvent event) {
        
    }
    
    public Turma getTurma() {
        return turma;
    }

    public void setTurma(Turma turma) {
        this.turma = turma;
    }

    public Turma getItemSelecionado() {
        return itemSelecionado;
    }

    public void setItemSelecionado(Turma itemSelecionado) {
        this.itemSelecionado = itemSelecionado;
    }

    public Turma getItemPreviewSelecionado() {
        return itemPreviewSelecionado;
    }

    public void setItemPreviewSelecionado(Turma itemPreviewSelecionado) {
        this.itemPreviewSelecionado = itemPreviewSelecionado;
    }
    
    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }
    
}
