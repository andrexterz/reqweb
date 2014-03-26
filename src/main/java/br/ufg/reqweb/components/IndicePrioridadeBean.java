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
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.validation.Validator;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
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

    String termoBusca;
    private Thread tImportJob;
    private int progress;
    private volatile boolean stopImportaIndicesPrioridade;
    private IndicePrioridade[] itemSelecionadoList;
    private IndicePrioridade[] itemSelecionadoPreviewList;
    private Map<Long, IndicePrioridade> indicePrioridadeListPreview;
    private final LazyDataModel<IndicePrioridade> indicesPrioridadeDataModel;

    public IndicePrioridadeBean() {
        termoBusca = "";
        indicePrioridadeListPreview = new HashMap();
        indicesPrioridadeDataModel = new LazyDataModel<IndicePrioridade>() {

            @Override
            public List<IndicePrioridade> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
                setPageSize(pageSize);
                List<IndicePrioridade> indicePrioridadeList;
                if (termoBusca.equals("")) {
                    indicePrioridadeList = indicePrioridadeDao.find(first, pageSize);
                    setRowCount(indicePrioridadeDao.count());
                } else {
                    indicePrioridadeList = indicePrioridadeDao.find(termoBusca);
                    setRowCount(indicePrioridadeList.size());
                }
                return indicePrioridadeList;
            }
        };
    }

    public void uploadIndicesPrioridade(FileUploadEvent event) {
        UploadedFile file = event.getFile();
        List<String[]> data;
        indicePrioridadeListPreview = new HashMap();
        try {
            Map<Long,Usuario> discenteMap = new HashMap();
            for (Usuario u: usuarioDao.find(PerfilEnum.DISCENTE)) {
                discenteMap.put(u.getId(), u);
            }
            data = CSVParser.parse(file.getInputstream());
            for (int i = 1; i < data.size(); i++) {
                String[] row = data.get(i);
                Long id = Long.parseLong(row[0].trim());
                Float indice = Float.parseFloat(row[1].trim());
                Long discenteId = Long.parseLong(row[2].trim());
                IndicePrioridade ip = new IndicePrioridade();
                ip.setId(id);
                ip.setIndicePrioridade(indice);
                ip.setDiscente(discenteMap.get(discenteId));
                indicePrioridadeListPreview.put(ip.getId(), ip);
            }
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, String.format("%1$s %2$s.", event.getFile().getFileName(), LocaleBean.getMessageBundle().getString("arquivoEnviado")), "");
            FacesContext.getCurrentInstance().addMessage(null, msg);            
        } catch (IOException | NumberFormatException e) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, String.format("%1$s %2$s.", event.getFile().getFileName(), LocaleBean.getMessageBundle().getString("dadosInvalidos")), "");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    public void excluiArquivoUploaded(ActionEvent event) {
        indicePrioridadeListPreview.clear();
    }

    public void excluiIndicePrioridadePreview() {
        for (IndicePrioridade ip : itemSelecionadoPreviewList) {
            indicePrioridadeListPreview.remove(ip.getId());
        }
    }

    public String getTermoBusca() {
        return termoBusca;
    }

    public void setTermoBusca(String termoBusca) {
        this.termoBusca = termoBusca;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public boolean getStopImportaIndicesPrioridade() {
        return stopImportaIndicesPrioridade;
    }

    public IndicePrioridade[] getItemSelecionadoList() {
        return itemSelecionadoList;
    }

    public void setItemSelecionadoList(IndicePrioridade[] itemSelecionadoList) {
        this.itemSelecionadoList = itemSelecionadoList;
    }

    public IndicePrioridade[] getItemSelecionadoPreviewList() {
        return itemSelecionadoPreviewList;
    }

    public void setItemSelecionadoPreviewList(IndicePrioridade[] itemSelecionadoPreviewList) {
        this.itemSelecionadoPreviewList = itemSelecionadoPreviewList;
    }

    public List<IndicePrioridade> getIndicePrioridadeListPreview() {
        return new ArrayList<>(indicePrioridadeListPreview.values());
    }

    public boolean isArquivoUploaded() {
        return indicePrioridadeListPreview.size() > 0;
    }

    public LazyDataModel<IndicePrioridade> getIndicesPrioridadeDataModel() {
        return indicesPrioridadeDataModel;
    }

}
