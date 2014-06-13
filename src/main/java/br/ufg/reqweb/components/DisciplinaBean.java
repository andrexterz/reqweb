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
import br.ufg.reqweb.model.Perfil;
import br.ufg.reqweb.model.PerfilEnum;
import br.ufg.reqweb.model.Usuario;
import br.ufg.reqweb.util.CSVParser;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import org.apache.log4j.Logger;
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
    private static final String ADICIONA = "a";
    private static final String EDITA = "e";
    private Thread tImportJob;
    private String operation;
    private int progress;
    private boolean saveStatus;
    private volatile boolean stopImportaDisciplinas;
    private Disciplina disciplina;
    private Curso curso;
    private Disciplina itemSelecionado;
    private Disciplina itemPreviewSelecionado;
    private Map<Long, Disciplina> disciplinaListPreview;
    private List<Disciplina> disciplinas;
    private final LazyDataModel<Disciplina> disciplinasDataModel;

    public DisciplinaBean() {
        disciplinaListPreview = new HashMap();
        termoBusca = "";
        operation = null;
        saveStatus = false;
        tImportJob = null;
        disciplina = new Disciplina();
        curso = null;
        itemSelecionado = null;
        itemPreviewSelecionado = null;
        disciplinas = new ArrayList<>();
        disciplinasDataModel = new LazyDataModel<Disciplina>() {

            private List<Disciplina> data;

            @Override
            public Object getRowKey(Disciplina disciplina) {
                return disciplina.getId().toString();
            }

            @Override
            public Disciplina getRowData(String key) {
                for (Disciplina d : data) {
                    if (d.getId().toString().equals(key)) {
                        return d;
                    }
                }
                return null;
            }

            @Override
            public List<Disciplina> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
                setPageSize(pageSize);
                if (curso != null) {
                    data = disciplinaDao.findByCurso(termoBusca, curso);
                    setRowCount(data.size());
                } else {
                    if (termoBusca.equals("")) {
                        data = disciplinaDao.find(first, pageSize);
                        setRowCount(disciplinaDao.count());
                    } else {
                        data = disciplinaDao.find(termoBusca);
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

    public void novaDisciplina() {
        setOperation(ADICIONA);
        disciplina = new Disciplina();
    }

    public void editaDisciplina() {
        if (!isSelecionado()) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "info", LocaleBean.getMessageBundle().getString("itemSelecionar"));
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } else {
            setOperation(EDITA);
            disciplina = getItemSelecionado();
            FacesContext context = FacesContext.getCurrentInstance();
        }
    }

    public void excluiDisciplina() {
        FacesMessage msg;
        if (getItemSelecionado() == null) {
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "info", LocaleBean.getMessageBundle().getString("itemSelecionar"));
        } else {
            try {
                disciplinaDao.excluir(itemSelecionado);
                itemSelecionado = null;
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "info", LocaleBean.getMessageBundle().getString("dadosExcluidos"));
            } catch (Exception e) {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "info", LocaleBean.getMessageBundle().getString("violacaoRelacionamento"));
            }
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void salvaDisciplinas() {
        stopImportaDisciplinas = false;
        tImportJob = new Thread() {
            @Override
            public void run() {
                List<Disciplina> items = new ArrayList<>();
                int length = disciplinaListPreview.size();
                int counter = 0;
                for (Disciplina d : disciplinaListPreview.values()) {
                    if (!stopImportaDisciplinas) {
                        counter++;
                        progress = (int) ((counter / (float) length) * 99);
                        Set<ConstraintViolation<Disciplina>> errors = validator.validate(d);
                        if (errors.isEmpty()) {
                            items.add(d);
                        }
                    } else {
                        items.clear();
                        break;
                    }
                }
                try {
                    disciplinaDao.adicionar(items);
                    setSaveStatus(true);
                } catch (Exception e) {
                    setSaveStatus(false);
                }
                progress++;
            }
        };
        tImportJob.start();
    }

    public void salvaDisciplina() {
        RequestContext context = RequestContext.getCurrentInstance();
        try {
            Set<ConstraintViolation<Disciplina>> errors = validator.validate(disciplina);
            saveStatus = errors.isEmpty();
            if (saveStatus) {
                if (operation.equals(ADICIONA)) {
                    disciplinaDao.adicionar(disciplina);
                    itemSelecionado = disciplina;
                } else {
                    disciplinaDao.atualizar(disciplina);
                }
            }
        } catch (Exception e) {
            setSaveStatus(false);
        }
        context.addCallbackParam("resultado", saveStatus);
        handleCompleteSaveDisciplinas();
    }

    public void selecionaItem(SelectEvent event) {
        itemSelecionado = (Disciplina) event.getObject();
    }

    public void selecionaItemPreview(SelectEvent event) {
        itemPreviewSelecionado = (Disciplina) event.getObject();
    }

    /**
     *
     * @param query
     * @return the <code>List<Disciplina></code>
     * method for autocomplete widget
     */
    public List<Disciplina> findDisciplina(String query) {
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        Usuario usuario = ((UsuarioBean) sessionMap.get("usuarioBean")).getSessionUsuario();
        PerfilEnum tipoPerfil = ((UsuarioBean) sessionMap.get("usuarioBean")).getPerfil();
        Curso c = null;
        if (tipoPerfil.equals(PerfilEnum.COORDENADOR_DE_CURSO)) {
            for (Perfil p: usuario.getPerfilList()) {
                if (tipoPerfil.equals(p.getTipoPerfil())) {
                    c = p.getCurso();
                    break;
                }
            }
            return disciplinaDao.findByCurso(query, c);            
        }
        return disciplinaDao.find(query);
    }

    public void autoCompleteSelecionaCurso(SelectEvent event) {
        try {
            curso = (Curso) event.getObject();
        } catch (NullPointerException e) {
            curso = null;
        }
    }

    public StreamedContent getDisciplinasAsCSV() {
        StringBuilder csvData = new StringBuilder("id,codigo,disciplina,curso_sigla");
        for (Disciplina d : disciplinaDao.findAll()) {
            csvData.append("\n");
            csvData.append(d.getId());
            csvData.append(",");
            csvData.append(d.getCodigo());
            csvData.append(",");
            csvData.append(d.getNome());
            csvData.append(",");
            csvData.append(d.getCurso().getSigla());
        }

        InputStream stream;
        try {
            stream = new ByteArrayInputStream(csvData.toString().getBytes("UTF8"));
        } catch (UnsupportedEncodingException e) {
            stream = new ByteArrayInputStream(csvData.toString().getBytes());
        }
        StreamedContent file = new DefaultStreamedContent(stream, "text/csv", "reqweb_disciplinas.csv");
        return file;
    }

    public void uploadDisciplinas(FileUploadEvent event) {
        UploadedFile file = event.getFile();
        List<String[]> data;
        disciplinaListPreview = new HashMap();
        try {
            data = CSVParser.parse(file.getInputstream());
            Map<String, Curso> cursoMap = new HashMap();
            for (Curso c : cursoDao.findAll()) {
                cursoMap.put(c.getSigla(), c);
            }
            for (int i = 1; i < data.size(); i++) {
                String[] row = data.get(i);
                Long id = Long.parseLong(row[0].trim());
                Long codigo = Long.parseLong(row[1].trim());
                String nome = row[2].trim();
                String sigla = row[3].trim().toUpperCase();
                Disciplina d = new Disciplina();
                d.setId(id);
                d.setCodigo(codigo);
                d.setNome(nome);
                d.setCurso(cursoMap.get(sigla));
                disciplinaListPreview.put(d.getId(), d);
            }
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, String.format("%1$s %2$s.", event.getFile().getFileName(), LocaleBean.getMessageBundle().getString("arquivoEnviado")), "");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (IOException | NumberFormatException e) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, String.format("%1$s %2$s.", event.getFile().getFileName(), LocaleBean.getMessageBundle().getString("dadosInvalidos")), "");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    public void excluiArquivoUploaded() {
        disciplinaListPreview.clear();
        itemPreviewSelecionado = null;
    }

    public void excluiDisciplinaPreview() {
        disciplinaListPreview.remove(itemPreviewSelecionado.getId());
        itemPreviewSelecionado = null;
    }

    public void handleCompleteSaveDisciplinas() {
        stopImportaDisciplinas = true;
        FacesContext context = FacesContext.getCurrentInstance();
        FacesMessage msg;
        if (saveStatus) {
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, LocaleBean.getMessageBundle().getString("dadosSalvos"), "");
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "info", LocaleBean.getMessageBundle().getString("dadosInvalidos"));
        }
        context.addMessage(null, msg);
    }

    public void setupImportDisciplinas() {
        progress = 0;
        stopImportaDisciplinas = true;
    }

    public void cancelImpDisciplinas() {
        setupImportDisciplinas();
        try {
            Thread.sleep(2000);
            FacesContext context = FacesContext.getCurrentInstance();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, LocaleBean.getMessageBundle().getString("operacaoCancelada"), "");
            context.addMessage(null, msg);

        } catch (NullPointerException | InterruptedException e) {
            log.error("no thread to cancel");
        }
    }

    public LazyDataModel<Disciplina> getDisciplinasDataModel() {
        return disciplinasDataModel;
    }

    public List<Disciplina> getDisciplinas() {
        if (disciplinas.isEmpty() | disciplinaDao.count() != disciplinas.size()) {
            disciplinas = disciplinaDao.findAll();
        }
        return disciplinas;
    }

    public List<Disciplina> getDisciplinaListPreview() {
        return new ArrayList<>(disciplinaListPreview.values());
    }

    public boolean isArquivoUploaded() {
        return disciplinaListPreview.size() > 0;
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

    public boolean getStopImportaDisciplinas() {
        return stopImportaDisciplinas;
    }

    public Disciplina getDisciplina() {
        return disciplina;
    }

    public void setDisciplina(Disciplina disciplina) {
        this.disciplina = disciplina;
    }

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
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

    public boolean isPreviewSelecionado() {
        return itemPreviewSelecionado != null;
    }

    public Disciplina getItemPreviewSelecionado() {
        return itemPreviewSelecionado;
    }

    public void setItemPreviewSelecionado(Disciplina itemPreviewSelecionado) {
        this.itemPreviewSelecionado = itemPreviewSelecionado;
    }

}
