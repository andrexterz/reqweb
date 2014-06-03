/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.reqweb.components;

import br.ufg.reqweb.dao.IndicePrioridadeDao;
import br.ufg.reqweb.dao.UsuarioDao;
import br.ufg.reqweb.model.IndicePrioridade;
import br.ufg.reqweb.model.PerfilEnum;
import br.ufg.reqweb.model.Usuario;
import br.ufg.reqweb.util.CSVParser;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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
import org.primefaces.event.FileUploadEvent;
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
public class IndicePrioridadeBean {

    private static final long serialVersionUID = 1L;

    @Autowired
    IndicePrioridadeDao indicePrioridadeDao;

    @Autowired
    UsuarioDao usuarioDao;

    @Autowired
    private Validator validator;

    private static final Logger log = Logger.getLogger(IndicePrioridadeBean.class);
    String termoBusca;
    private Thread tImportJob;
    private int progress;
    private volatile boolean stopImportaIndicePrioridade;
    IndicePrioridade indicePrioridade;
    private List<IndicePrioridade> itemSelecionadoPreviewList;
    private Map<Long, IndicePrioridade> indicePrioridadeListPreview;
    private final LazyDataModel<IndicePrioridade> indicePrioridadeDataModel;

    public IndicePrioridadeBean() {
        termoBusca = "";
        indicePrioridade = null;
        itemSelecionadoPreviewList = new ArrayList<>();
        indicePrioridadeListPreview = new HashMap();
        indicePrioridadeDataModel = new LazyDataModel<IndicePrioridade>() {
            
            private List<IndicePrioridade> data;            
            
            @Override
            public List<IndicePrioridade> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
                setPageSize(pageSize);
                if (termoBusca.equals("")) {
                    data = indicePrioridadeDao.find(first, pageSize);
                    setRowCount(indicePrioridadeDao.count());
                } else {
                    data = indicePrioridadeDao.find(termoBusca);
                    setRowCount(data.size());
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

    public void salvaIndicePrioridade() {
        stopImportaIndicePrioridade = false;
        tImportJob = new Thread() {
            @Override
            public void run() {
                List<IndicePrioridade> items = new ArrayList<>();
                int length = indicePrioridadeListPreview.size();
                int counter = 0;
                for (IndicePrioridade ip : indicePrioridadeListPreview.values()) {
                    if (!stopImportaIndicePrioridade) {
                        counter++;
                        progress = (int) ((counter / (float) length) * 100);
                        Set<ConstraintViolation<IndicePrioridade>> errors = validator.validate(ip);
                        if (errors.isEmpty()) {
                            items.add(ip);
                        }
                    } else {
                        items.clear();
                        break;
                    }
                }
                indicePrioridadeDao.adicionar(items);
            }
        };
        tImportJob.start();
    }

    public void excluiIndicePrioridade() {
        indicePrioridadeDao.excluir();
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "info", LocaleBean.getMessageBundle().getString("dadosExcluidos"));
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public void uploadIndicePrioridade(FileUploadEvent event) {
        UploadedFile file = event.getFile();
        List<String[]> data;
        indicePrioridadeListPreview = new HashMap();
        try {
            Map<String, Usuario> discenteMap = new HashMap();
            for (Usuario u : usuarioDao.find(PerfilEnum.DISCENTE)) {
                discenteMap.put(u.getMatricula(), u);
            }
            data = CSVParser.parse(file.getInputstream());
            for (int i = 1; i < data.size(); i++) {
                String[] row = data.get(i);
                Long id = Long.parseLong(row[0].trim());
                Float indice = Float.parseFloat(row[1].trim());
                String discenteMatricula = row[2].trim();
                IndicePrioridade ip = new IndicePrioridade();
                ip.setId(id);
                ip.setIndicePrioridade(indice);
                ip.setDiscente(discenteMap.get(discenteMatricula));
                indicePrioridadeListPreview.put(ip.getId(), ip);
            }
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, String.format("%1$s %2$s.", event.getFile().getFileName(), LocaleBean.getMessageBundle().getString("arquivoEnviado")), "");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (IOException | NumberFormatException e) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, String.format("%1$s %2$s.", event.getFile().getFileName(), LocaleBean.getMessageBundle().getString("dadosInvalidos")), "");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    public void excluiArquivoUploaded() {
        indicePrioridadeListPreview.clear();
        itemSelecionadoPreviewList.clear();
    }

    public void excluiIndicePrioridadePreview() {
        for (IndicePrioridade ip : itemSelecionadoPreviewList) {
            indicePrioridadeListPreview.remove(ip.getId());
        }
        itemSelecionadoPreviewList.clear();
    }

    public void handleCompleteImpIndicePrioridade() {
        stopImportaIndicePrioridade = true;
        FacesContext context = FacesContext.getCurrentInstance();
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, LocaleBean.getMessageBundle().getString("dadosSalvos"), "");
        context.addMessage(null, msg);
    }

    public void setupImportIndicePrioridade() {
        progress = 0;
        stopImportaIndicePrioridade = true;
    }

    public void cancelImpIndicePrioridade() {
        setupImportIndicePrioridade();
        try {
            Thread.sleep(2000);
            FacesContext context = FacesContext.getCurrentInstance();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, LocaleBean.getMessageBundle().getString("operacaoCancelada"), "");
            context.addMessage(null, msg);

        } catch (NullPointerException | InterruptedException e) {
            log.error("no thread to cancel");
        }
    }

    public StreamedContent getIndicePrioridadeAsCSV() {
        StringBuilder csvData = new StringBuilder("id,indice_prioridade,discente_matricula, discente_id");
        for (IndicePrioridade ip : indicePrioridadeDao.findAll()) {
            csvData.append("\n");
            csvData.append(ip.getId());
            csvData.append(",");
            csvData.append(ip.getIndicePrioridade());
            csvData.append(",");
            csvData.append(ip.getDiscente().getMatricula());
            csvData.append(",");
            csvData.append(ip.getDiscente().getId());
        }
        InputStream stream;
        try {
            stream = new ByteArrayInputStream(csvData.toString().getBytes("UTF8"));
        } catch (UnsupportedEncodingException e) {
            stream = new ByteArrayInputStream(csvData.toString().getBytes());
        }
        StreamedContent file = new DefaultStreamedContent(stream, "text/csv", "reqweb_indice_prioridade.csv");
        return file;
    }

    public String getTermoBusca() {
        return termoBusca;
    }

    public void setTermoBusca(String termoBusca) {
        this.termoBusca = termoBusca;
    }

    public IndicePrioridade getIndicePrioridade() {
        return indicePrioridade;
    }

    public void setIndicePrioridade(IndicePrioridade indicePrioridade) {
        this.indicePrioridade = indicePrioridade;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public boolean getStopImportaIndicePrioridade() {
        return stopImportaIndicePrioridade;
    }

    public List<IndicePrioridade> getItemSelecionadoPreviewList() {
        return itemSelecionadoPreviewList;
    }

    public void setItemSelecionadoPreviewList(List<IndicePrioridade> itemSelecionadoPreviewList) {
        this.itemSelecionadoPreviewList = itemSelecionadoPreviewList;
    }

    public boolean isPreviewSelecionado() {
        return itemSelecionadoPreviewList.size() > 0;
    }

    public List<IndicePrioridade> getIndicePrioridadeListPreview() {
        return new ArrayList<>(indicePrioridadeListPreview.values());
    }

    public boolean isArquivoUploaded() {
        return indicePrioridadeListPreview.size() > 0;
    }

    public LazyDataModel<IndicePrioridade> getIndicePrioridadeDataModel() {
        return indicePrioridadeDataModel;
    }

    public boolean isIndicePrioridadeDataModelEmpty() {
        return indicePrioridadeDao.count() == 0;
    }
}
