/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.reqweb.components;

import br.ufg.reqweb.dao.DisciplinaDao;
import br.ufg.reqweb.dao.PeriodoDao;
import br.ufg.reqweb.dao.TurmaDao;
import br.ufg.reqweb.dao.UsuarioDao;
import br.ufg.reqweb.model.Disciplina;
import br.ufg.reqweb.model.PerfilEnum;
import br.ufg.reqweb.model.Periodo;
import org.apache.log4j.Logger;
import br.ufg.reqweb.model.Semestre;
import br.ufg.reqweb.model.Turma;
import br.ufg.reqweb.model.Usuario;
import br.ufg.reqweb.util.CSVParser;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author andre
 */
@Component
@Scope(value = "session")
public class TurmaBean implements Serializable {

    private static final long serialVersionUID = 1L;
    @Autowired
    Validator validator;

    @Autowired
    TurmaDao turmaDao;

    @Autowired
    PeriodoDao periodoDao;

    @Autowired
    DisciplinaDao disciplinaDao;

    @Autowired
    UsuarioDao usuarioDao;

    private static final Logger log = Logger.getLogger(DisciplinaBean.class);
    private Turma turma;
    private Periodo periodo;
    private Turma itemSelecionado;
    private Turma itemPreviewSelecionado;
    private String termoBusca;
    private static final String ADICIONA = "a";
    private static final String EDITA = "e";
    private Thread tImportJob;
    private String operation;
    private int progress;
    private boolean saveStatus;
    private volatile boolean stopImportaTurmas;
    private Map<Long, Turma> turmaListPreview;
    private final LazyDataModel turmasDataModel;

    public TurmaBean() {
        turmaListPreview = new HashMap();
        turma = new Turma();
        periodo = null;
        operation = null;
        saveStatus = false;
        itemSelecionado = null;
        itemPreviewSelecionado = null;
        termoBusca = "";
        turmasDataModel = new LazyDataModel<Turma>() {
            
            private List<Turma> data;

            @Override
            public Object getRowKey(Turma turma) {
                return turma.getId().toString(); 
            }

            @Override
            public Turma getRowData(String key) {
                for (Turma t: data) {
                    if (t.getId().toString().equals(key)) {
                        return t;
                    }
                }
                return null;
            }
            
            @Override
            public List<Turma> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
                setPageSize(pageSize);

                if (periodo != null) {
                    data = turmaDao.find(termoBusca, periodo);
                    setRowCount(data.size());
                } else {
                    if (termoBusca.equals("")) {
                        data = turmaDao.find(first, pageSize);
                        setRowCount(turmaDao.count());
                    } else {
                        data = turmaDao.find(termoBusca);
                        setRowCount(data.size());
                    }

                }
                if (data.size() > pageSize) {
                    try {
                        return data.subList(first, first + pageSize);
                    } catch (IndexOutOfBoundsException e) {
                         return data.subList(first, first + (data.size() % pageSize));
                    }
                    
                }
                return data;
            }
        };
    }

    public void novaTurma() {
        setOperation(ADICIONA);
        turma = new Turma();
    }

    public void editaTurma() {
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
        stopImportaTurmas = false;
        tImportJob = new Thread() {
            @Override
            public void run() {
                List<Turma> items = new ArrayList<>();
                int length = turmaListPreview.size();
                int counter = 0;
                for (Turma t : turmaListPreview.values()) {
                    if (!stopImportaTurmas) {
                        counter++;
                        progress = (int) ((counter / (float) length) * 99);
                        Set<ConstraintViolation<Turma>> errors = validator.validate(t);
                        if (errors.isEmpty()) {
                            items.add(t);
                        }
                    } else {
                        items.clear();
                        break;
                    }
                }
                try {
                    turmaDao.adicionar(items);
                    setSaveStatus(true);
                } catch (Exception e) {
                    setSaveStatus(false);
                }
                progress++;
            }
        };
        tImportJob.start();
    }

    public void salvaTurma() {
        FacesMessage msg;
        RequestContext context = RequestContext.getCurrentInstance();
        try {
            Set<ConstraintViolation<Turma>> errors = validator.validate(turma);
            saveStatus = errors.isEmpty();
            if (saveStatus) {
                if (operation.equals(ADICIONA)) {
                    turmaDao.adicionar(turma);
                    itemSelecionado = turma;
                } else {
                    turmaDao.atualizar(turma);
                }
            }
        } catch (Exception e) {
            setSaveStatus(false);
        }
        context.addCallbackParam("resultado", saveStatus);
        handleCompleteSaveTurmas();
    }

    public void selecionaItem(SelectEvent event) {
        itemSelecionado = (Turma) event.getObject();
    }

    public void selecionaItemPreview(SelectEvent event) {
        itemPreviewSelecionado = (Turma) event.getObject();

    }

    public StreamedContent getTurmasAsCSV() {
        StringBuilder csvData = new StringBuilder("id,ano,nome,semestre,disciplina_id,docente_id,curso_sigla");
        for (Turma t : turmaDao.findAll()) {
            csvData.append("\n");
            csvData.append(t.getId());
            csvData.append(",");
            csvData.append(t.getPeriodo().getAno());
            csvData.append(",");
            csvData.append(t.getNome());
            csvData.append(",");
            csvData.append(t.getPeriodo().getSemestre().getValue());
            csvData.append(",");
            csvData.append(t.getDisciplina().getId());
            csvData.append(",");
            csvData.append(t.getDocente() == null ? "": t.getDocente().getId());
            csvData.append(",");
            csvData.append(t.getDisciplina().getCurso().getSigla());
        }

        InputStream stream = new ByteArrayInputStream(csvData.toString().getBytes());
        StreamedContent file = new DefaultStreamedContent(stream, "text/csv", "reqweb_turmas.csv");
        return file;
    }

    public void uploadTurmas(FileUploadEvent event) {
        UploadedFile file = event.getFile();
        List<String[]> data;
        turmaListPreview = new HashMap();
        try {
            Map<Long, Usuario> docenteMap = new HashMap();
            Map<Long, Periodo> periodoMap = new HashMap();
            Map<Long, Disciplina> disciplinaMap = new HashMap();

            for (Usuario u : usuarioDao.find(PerfilEnum.DOCENTE)) {
                docenteMap.put(u.getId(), u);
            }

            for (Periodo p : periodoDao.findAll()) {
                periodoMap.put(p.getId(), p);
            }

            for (Disciplina d : disciplinaDao.findAll()) {
                disciplinaMap.put(d.getId(), d);
            }

            data = CSVParser.parse(file.getInputstream());

            for (int i = 1; i < data.size(); i++) {
                String[] row = data.get(i);
                Long id = Long.parseLong(row[0].trim());
                int ano = Integer.parseInt(row[1].trim());
                String nome = row[2].trim();
                Semestre semestre = Semestre.getSemestre(Integer.parseInt(row[3].trim()));
                Long disciplinaId = Long.parseLong(row[4].trim());
                Long docenteId = row[5].trim().isEmpty() ? null: Long.parseLong(row[5].trim());
                Turma t = new Turma();
                t.setId(id);
                t.setNome(nome);
                for (Periodo p : periodoMap.values()) {
                    if (p.getAno() == ano && p.getSemestre() == semestre) {
                        t.setPeriodo(p);
                        break;
                    }
                }
                t.setDocente(docenteMap.get(docenteId));
                t.setDisciplina(disciplinaMap.get(disciplinaId));
                turmaListPreview.put(id, t);
            }
        } catch (IOException | NumberFormatException e) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, String.format("%1$s %2$s.", event.getFile().getFileName(), LocaleBean.getMessageBundle().getString("dadosInvalidos")), null);
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    public void excluiArquivoUploaded() {
        turmaListPreview.clear();
        itemPreviewSelecionado = null;
    }

    public void excluiTurmaPreview() {
        turmaListPreview.remove(itemPreviewSelecionado.getId());
        itemPreviewSelecionado = null;
    }

    public void handleCompleteSaveTurmas() {
        stopImportaTurmas = true;
        FacesContext context = FacesContext.getCurrentInstance();
        FacesMessage msg;
        if (saveStatus) {
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, LocaleBean.getMessageBundle().getString("dadosSalvos"), "");
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "info", LocaleBean.getMessageBundle().getString("dadosInvalidos"));
        }
        context.addMessage(null, msg);
    }

    public void setupImportTurmas() {
        progress = 0;
        stopImportaTurmas = true;
    }

    public void cancelImpTurmas() {
        setupImportTurmas();
        try {
            Thread.sleep(2000);
            FacesContext context = FacesContext.getCurrentInstance();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, LocaleBean.getMessageBundle().getString("operacaoCancelada"), "");
            context.addMessage(null, msg);

        } catch (NullPointerException | InterruptedException e) {
            log.error("no thread to cancel");
        }
    }

    public void autoCompleteSelecionaDisciplina(SelectEvent event) {
        try {
            turma.setDisciplina((Disciplina) event.getObject());
        } catch (NullPointerException e) {
            turma.setDisciplina(null);
        }
    }

    public void autoCompleteSelecionaDocente(SelectEvent event) {
        try {
            turma.setDocente((Usuario) event.getObject());
        } catch (NullPointerException e) {
            turma.setDocente(null);
        }
    }
    
    public void autoCompleteSelecionaPeriodo(SelectEvent event) {
        try {
            periodo = (Periodo) event.getObject();
        } catch (NullPointerException e) {
            periodo = null;
        }
    }
    
    public List<Turma> findTurma(String query) {
        return turmaDao.find(query);
    }

    public Turma getTurma() {
        return turma;
    }

    public void setTurma(Turma turma) {
        this.turma = turma;
    }

    public Periodo getPeriodo() {
        return periodo;
    }

    public void setPeriodo(Periodo periodo) {
        this.periodo = periodo;
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

    public boolean isSaveStatus() {
        return saveStatus;
    }

    public void setSaveStatus(boolean saveStatus) {
        this.saveStatus = saveStatus;
    }
    

    public boolean getStopImportaTurmas() {
        return stopImportaTurmas;
    }

    public List<Turma> getTurmaListPreview() {
        return new ArrayList<>(turmaListPreview.values());
    }

    public boolean isArquivoUploaded() {
        return turmaListPreview.size() > 0;
    }

    public LazyDataModel getTurmasDataModel() {
        return turmasDataModel;
    }
}
