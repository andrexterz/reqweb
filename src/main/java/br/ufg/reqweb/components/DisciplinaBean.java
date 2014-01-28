/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.reqweb.components;

import br.ufg.reqweb.dao.CursoDao;
import br.ufg.reqweb.dao.DisciplinaDao;
import br.ufg.reqweb.model.Curso;
import br.ufg.reqweb.model.Disciplina;
import br.ufg.reqweb.util.CSVParser;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import org.apache.log4j.Logger;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.primefaces.model.UploadedFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author andre
 */
@Component
public class DisciplinaBean implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    @Autowired
    private CursoDao cursoDao;

    @Autowired
    private DisciplinaDao disciplinaDao;

    @Autowired
    private Validator validator;

    private static final Logger log = Logger.getLogger(DisciplinaBean.class);
    private String termoBusca;
    public static final String ADICIONA = "a";
    public static final String EDITA = "e";
    private Thread tImportJob;
    private String operation;
    private int progress;
    private volatile boolean stopImportaDisciplinas;
    private Disciplina disciplina;
    private Disciplina itemSelecionado;
    private List<Disciplina> disciplinaListPreview;
    private final LazyDataModel<Disciplina> disciplinas;

    public DisciplinaBean() {
        disciplinaListPreview = new ArrayList<>();
        termoBusca = "";
        operation = null;
        tImportJob = null;
        disciplina = new Disciplina();
        itemSelecionado = null;
        disciplinas = new LazyDataModel<Disciplina>() {

            @Override
            public List<Disciplina> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
                setPageSize(pageSize);
                List<Disciplina> disciplinaList;
                if (termoBusca.equals("")) {
                    disciplinaList = disciplinaDao.find(first, pageSize);
                    setRowCount(disciplinaDao.count());
                } else {
                    disciplinaList = disciplinaDao.find(termoBusca);
                    setRowCount(disciplinaList.size());
                }
                return disciplinaList;
            }

        };

    }

    public void novaDisciplina(ActionEvent event) {
        setOperation(ADICIONA);
        disciplina = new Disciplina();

    }

    public void editaDisciplina(ActionEvent event) {
        if (getItemSelecionado() == null) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "info", LocaleBean.getMessageBundle().getString("itemSelecionar"));
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } else {
            setOperation(EDITA);
            disciplina = getItemSelecionado();
        }
    }

    public void excluiDisciplina() {
        FacesMessage msg;
        if (getItemSelecionado() == null) {
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "info", LocaleBean.getMessageBundle().getString("itemSelecionar"));
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } else {
            disciplinaDao.excluir(itemSelecionado);
            itemSelecionado = null;
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "info", LocaleBean.getMessageBundle().getString("dadosExcluidos"));
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    public void salvaDisciplinas() {
        stopImportaDisciplinas = false;
        tImportJob = new Thread() {
            @Override
            public void run() {
                List<Disciplina> items = new ArrayList<>();
                int length = disciplinaListPreview.size();
                int counter = 0;
                for (Disciplina d : disciplinaListPreview) {
                    if (!stopImportaDisciplinas) {
                        counter++;
                        progress = (int) ((counter / (float) length) * 100);
                        Set<ConstraintViolation<Disciplina>> errors = validator.validate(d);
                        if (errors.isEmpty()) {
                            items.add(d);
                        }
                    } else {
                        items.clear();
                        break;
                    }
                }
                disciplinaDao.adicionar(items);
            }
        };
        tImportJob.start();
    }

    public void salvaDisciplina() {
        FacesMessage msg;
        RequestContext context = RequestContext.getCurrentInstance();
        Set<ConstraintViolation<Disciplina>> errors = validator.validate(disciplina);
        if (errors.isEmpty()) {
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "info", LocaleBean.getMessageBundle().getString("dadosSalvos"));
            context.addCallbackParam("resultado", true);
            if (operation.equals(ADICIONA)) {
                disciplinaDao.adicionar(disciplina);
                itemSelecionado = disciplina;
            } else {
                disciplinaDao.atualizar(disciplina);
            }
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "info", LocaleBean.getMessageBundle().getString("dadosInvalidos"));
            FacesContext.getCurrentInstance().addMessage(null, msg);
            context.addCallbackParam("resultado", false);
        }
    }

    public String listaDisciplinas() {
        return "disciplinas";
    }

    public void selecionaItem(SelectEvent event) {
        itemSelecionado = (Disciplina) event.getObject();
    }

    public void uploadDisciplinas(FileUploadEvent event) {
        UploadedFile file = event.getFile();
        List<String[]> data;
        disciplinaListPreview = new ArrayList<>();
        try {
            data = CSVParser.parse(file.getInputstream());
            Curso c = null;
            for (int i = 1; i < data.size(); i++) {
                String[] row = data.get(i);
                Long codigo = Long.parseLong(row[0].trim());
                String nome = row[1].trim();
                String matriz = row[2].trim();
                Disciplina d = new Disciplina();
                if (c != null) {
                    if (!c.getMatriz().equals(matriz)) {
                        c = cursoDao.findByMatriz(matriz);
                        System.out.println("row[2]: " + matriz + "\tmatriz: " + c.getMatriz());
                    }
                } else {
                    System.out.println("curso é nulo");
                    c = cursoDao.findByMatriz(matriz);

                }
                d.setCodigo(codigo);
                d.setNome(nome);
                d.setCurso(c);
                disciplinaListPreview.add(d);
            }
        } catch (IOException ex) {
            Logger.getLogger(ArquivoBean.class).error(ex.getMessage());
        }
        FacesMessage msg = new FacesMessage("info", String.format("%1$s %2$s.", event.getFile().getFileName(), LocaleBean.getMessageBundle().getString("arquivoEnviado")));
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void handleCompleteImpDisciplinas() {
        stopImportaDisciplinas = true;
        FacesContext context = FacesContext.getCurrentInstance();
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, LocaleBean.getMessageBundle().getString("dadosSalvos"), "");
        context.addMessage(null, msg);
    }

    public void setupImportDisciplinas(ActionEvent event) {
        progress = 0;
        stopImportaDisciplinas = true;
    }

    public void cancelImpDiscipinas(ActionEvent event) {
        setupImportDisciplinas(event);
        try {
            Thread.sleep(2000);
            FacesContext context = FacesContext.getCurrentInstance();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, LocaleBean.getMessageBundle().getString("operacaoCancelada"), "");
            context.addMessage(null, msg);

        } catch (NullPointerException | InterruptedException e) {
            log.error("no thread to cancel");
        }
    }

    public LazyDataModel<Disciplina> getDisciplinas() {
        return disciplinas;
    }

    public List<Disciplina> getDisciplinaListPreview() {
        return disciplinaListPreview;
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

    public boolean getStopImportaDisciplinas() {
        return stopImportaDisciplinas;
    }

    public Disciplina getDisciplina() {
        return disciplina;
    }

    public void setDisciplina(Disciplina disciplina) {
        this.disciplina = disciplina;
    }

    public boolean isSelecionado() {
        return itemSelecionado != null;
    }

    public Disciplina getItemSelecionado() {
        return itemSelecionado;
    }

    public void setItemSelecionado(Disciplina itemSelecionado) {
        this.itemSelecionado = itemSelecionado;
    }

}
