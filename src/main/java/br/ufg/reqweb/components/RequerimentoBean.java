/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.reqweb.components;

import br.ufg.reqweb.dao.RequerimentoDao;
import br.ufg.reqweb.dao.UsuarioDao;
import br.ufg.reqweb.model.Arquivo;
import br.ufg.reqweb.model.Atendimento;
import br.ufg.reqweb.model.DeclaracaoDeMatricula;
import br.ufg.reqweb.model.ExtratoAcademico;
import br.ufg.reqweb.model.ItemRequerimento;
import br.ufg.reqweb.model.PerfilEnum;
import br.ufg.reqweb.model.Requerimento;
import br.ufg.reqweb.model.SegundaChamadaDeProva;
import br.ufg.reqweb.model.TipoRequerimentoEnum;
import br.ufg.reqweb.model.Turma;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;
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
public class RequerimentoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final String ADICIONA = "a";
    private static final String EDITA = "e";

    @Autowired
    private Validator validator;

    @Autowired
    private UsuarioDao usuarioDao;

    @Autowired
    private RequerimentoDao requerimentoDao;

    private Requerimento requerimento;
    private Requerimento itemSelecionado;
    private TipoRequerimentoEnum tipoRequerimento;
    private String operation;//a: adiciona | e: edita
    private TipoRequerimentoEnum tipoRequerimentoBusca;
    private String termoBuscaDiscente;
    private String termoBuscaPeriodo;
    private List<Atendimento> atendimentos;
    private List<Arquivo> arquivos;
    private Arquivo arquivo;
    private boolean arquivoEnviado;
    private final LazyDataModel<Requerimento> requerimentosDataModel;

    public enum TipoBusca {

        DATA_PERIODO("dataCriacao"),
        TIPO_REQUERIMENTO("requerimento"),
        DISCENTE("discente");

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
        tipoRequerimentoBusca = null;
        termoBuscaDiscente = "";
        termoBuscaPeriodo = "";
        tipoBusca = TipoBusca.DATA_PERIODO;
        atendimentos = new ArrayList<>();
        arquivos = new ArrayList<>();
        arquivo = null;
        arquivoEnviado = false;

        requerimentosDataModel = new LazyDataModel<Requerimento>() {
            private List<Requerimento> data;

            @Override
            public Object getRowKey(Requerimento reqObj) {
                return reqObj.getId().toString();
            }

            @Override
            public Requerimento getRowData(String key) {
                for (Requerimento reqObj : data) {
                    if (reqObj.getId().toString().equals(key)) {
                        return reqObj;
                    }
                }
                return null;
            }

            @Override
            public List<Requerimento> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
                String order;
                switch (sortOrder.name()) {
                    case "ASCENDING":
                        order = "asc";
                        break;
                    case "DESCENDING":
                        order = "desc";
                        break;
                    default:
                        order = null;
                        break;
                }
                setPageSize(pageSize);

                Map<String, Object> filtros = new HashMap();
                if (getPerfilUsuario().equals(PerfilEnum.DISCENTE)) {
                    filtros.put("login", getLoginUsuario());
                }
                if (getTipoRequerimento() != null) {
                    setTipoRequerimentoBusca(null);
                    filtros.put("tipoRequerimento", getTipoRequerimento());
                }
                if (getTipoBusca().equals(TipoBusca.TIPO_REQUERIMENTO) && getTipoRequerimentoBusca() != null) {
                    filtros.put("tipoRequerimento", getTipoRequerimentoBusca());
                } else if (getTipoBusca().equals(TipoBusca.DISCENTE) && (getTermoBuscaDiscente() != null && !getTermoBuscaDiscente().isEmpty())) {
                    filtros.put("termo", getTermoBuscaDiscente());
                } else if (getTipoBusca().equals(TipoBusca.DATA_PERIODO) && (getTermoBuscaPeriodo() != null && !getTermoBuscaPeriodo().isEmpty())) {
                    Pattern pattDateA = Pattern.compile("^\\d{2}/\\d{2}/\\d{4}");
                    Matcher matcherA = pattDateA.matcher(termoBuscaPeriodo);

                    Pattern pattDateB = Pattern.compile("\\d{2}/\\d{2}/\\d{4}$");
                    Matcher matcherB = pattDateB.matcher(getTermoBuscaPeriodo());

                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                    if (matcherA.find() && matcherB.find()) {
                        try {
                            Date dateA = formatter.parse(matcherA.group());
                            Date dateB = formatter.parse(matcherB.group());
                            filtros.put("dataCriacao", new Date[]{dateA, dateB});
                        } catch (ParseException e) {
                            System.out.println("error formatter " + e.getLocalizedMessage());
                            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                    LocaleBean.getMessageBundle().getString("dataInvalida"), null);
                            FacesContext.getCurrentInstance().addMessage(null, msg);
                        }
                    }
                }

                if (filtros.isEmpty()) {
                    data = requerimentoDao.find(first, pageSize, sortField, order);
                    setRowCount(requerimentoDao.count());
                } else {
                    data = requerimentoDao.find(sortField, order, filtros);
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
            System.out.println("objeto itemRequerimento criado: " + itemRequerimento.getClass());
        }
        if (tipoRequerimento.equals(TipoRequerimentoEnum.EXTRATO_ACADEMICO)) {
            ExtratoAcademico itemRequerimento = new ExtratoAcademico();
            sessionMap.put("itemRequerimento", itemRequerimento);
            System.out.println("objeto itemRequerimento criado: " + itemRequerimento.getClass());
        }
        if (tipoRequerimento.equals(TipoRequerimentoEnum.SEGUNDA_CHAMADA_DE_PROVA)) {
            SegundaChamadaDeProva itemRequerimento = new SegundaChamadaDeProva();
            sessionMap.put("itemRequerimento", itemRequerimento);
            System.out.println("objeto itemRequerimento criado: " + itemRequerimento.getClass());
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
            System.out.println("items in requerimento: " + requerimento.getItemRequerimentoList().size());
            if (requerimento.getTipoRequerimento().equals(TipoRequerimentoEnum.DECLARACAO_DE_MATRICULA)) {
                sessionMap.put("itemRequerimento", requerimento.getItemRequerimentoList().iterator().next());
            }
            if (requerimento.getTipoRequerimento().equals(TipoRequerimentoEnum.EXTRATO_ACADEMICO)) {
                sessionMap.put("itemRequerimento", requerimento.getItemRequerimentoList().iterator().next());
            }
            if (requerimento.getTipoRequerimento().equals(TipoRequerimentoEnum.SEGUNDA_CHAMADA_DE_PROVA)) {
                ItemRequerimento itemRequerimento = requerimento.getItemRequerimentoList().iterator().next();
                sessionMap.put("itemRequerimento", itemRequerimento);
                findArquivos(itemRequerimento);
            }
        }
    }

    /**
     * cancels actions from <code>novoRequerimento</code>
     */
    public void cancelRequerimento() {
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        sessionMap.remove("itemRequerimento");
        System.out.println("cancelando add/update requerimento... ");
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
        String loginUser = getLoginUsuario();
        System.out.println("req: " + requerimento);
        requerimento.setDiscente(usuarioDao.findByLogin(loginUser));

        if (requerimento.getTipoRequerimento().equals(TipoRequerimentoEnum.DECLARACAO_DE_MATRICULA)) {
            DeclaracaoDeMatricula item = (DeclaracaoDeMatricula) sessionMap.get("itemRequerimento");
            requerimento.addItemRequerimento(item);
        }

        if (requerimento.getTipoRequerimento().equals(TipoRequerimentoEnum.EXTRATO_ACADEMICO)) {
            ExtratoAcademico item = (ExtratoAcademico) sessionMap.get("itemRequerimento");
            requerimento.addItemRequerimento(item);
        }

        if (requerimento.getTipoRequerimento().equals(TipoRequerimentoEnum.SEGUNDA_CHAMADA_DE_PROVA)) {
            SegundaChamadaDeProva item = (SegundaChamadaDeProva) sessionMap.get("itemRequerimento");
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
        for (TipoRequerimentoEnum t : TipoRequerimentoEnum.values()) {
            if (t.getTipoLocale().toLowerCase().contains(query.toLowerCase())) {
                items.add(t);
            }
        }
        return items;
    }

    public void autoCompleteSelecionaTipoBuscaRequerimento(SelectEvent event) {
        try {
            tipoRequerimentoBusca = (TipoRequerimentoEnum) event.getObject();
        } catch (NullPointerException e) {
            tipoRequerimentoBusca = null;
        }
    }

    public void autoCompleteSelecionaTurma(SelectEvent event) {
        try {
            RequestContext context = RequestContext.getCurrentInstance();
            Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
            SegundaChamadaDeProva itemRequerimento = (SegundaChamadaDeProva) sessionMap.get("itemRequerimento");
            Turma turma = (Turma) event.getObject();
            itemRequerimento.setTurma(turma);
        } catch (NullPointerException e) {
            tipoRequerimento = null;
        }
    }

    public List<ItemRequerimento> getItemRequerimentoSelecionadoList() {
        if (isSelecionado()) {
            return requerimentoDao.findItemRequerimentoList(itemSelecionado);
        } else {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, LocaleBean.getMessageBundle().getString("itemSelecionar"), null);
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return new ArrayList<>();
        }
    }

    public void findAtendimentos() {
        if (isSelecionado()) {
            atendimentos = requerimentoDao.findAtendimento(itemSelecionado);
        } else {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, LocaleBean.getMessageBundle().getString("itemSelecionar"), null);
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    public void novoArquivoTabEvent(TabChangeEvent event) {
        System.out.println("tab changed: " + event.getTab().getId());
        if (event.getTab().getId().equals("arquivoTab2")) {
            novoArquivo();
        }
    }

    public void novoArquivo() {
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        Arquivo itemArquivo = new Arquivo();
        sessionMap.put("itemArquivo", itemArquivo);
        System.out.println("novo arquivo --> " + itemArquivo);
        arquivoEnviado = false;
    }

    public void uploadArquivo(FileUploadEvent event) {
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        Arquivo itemArquivo = (Arquivo) sessionMap.get("itemArquivo");
        SegundaChamadaDeProva itemRequerimento = (SegundaChamadaDeProva) sessionMap.get("itemRequerimento");        

        String uploadDir = FacesContext.getCurrentInstance().getExternalContext().getInitParameter("userUploadDir");
        Path path = Paths.get(FacesContext.getCurrentInstance().getExternalContext().getRealPath(uploadDir));
        UploadedFile file = event.getFile();

        itemRequerimento.addArquivo(arquivo);
        
        Calendar calendar = Calendar.getInstance(Locale.getDefault());        
        if (itemArquivo.getNomeArquivo().isEmpty()) {
            itemArquivo.setNomeArquivo(String.format("%1$s%2$F-%2$H%2$M%2$S", file.getFileName(), calendar.getTime()));        
        } else {
            itemArquivo.setNomeArquivo(String.format("%1$s%2$F-%2$H%2$M%2$S", itemArquivo.getNomeArquivo(), calendar.getTime()));        
        }
        itemArquivo.setMimetype(file.getContentType());
        itemArquivo.setCaminhoArquivo(String.format("%s/%s/%s", uploadDir, getLoginUsuario(), itemArquivo.getNomeArquivo()));
        
        path.resolve(getLoginUsuario()).resolve(itemArquivo.getNomeArquivo());
        System.out.println("upload dir is -> " + path.toString());
        try {
            BufferedWriter newFile = Files.newBufferedWriter(path, Charset.forName("UTF-8"), StandardOpenOption.CREATE);
            BufferedReader uploadedFile = new BufferedReader(new InputStreamReader(file.getInputstream(), "UTF-8"));
            newFile.write(uploadedFile.read());
            arquivoEnviado = true;
        } catch (IOException ex) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, LocaleBean.getMessageBundle().getString("erroGravacao"), null);
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    public void findArquivos(ItemRequerimento itemRequerimento) {
        arquivos = requerimentoDao.findArquivos(itemRequerimento);
    }

    public String getLoginUsuario() {
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        return ((UsuarioBean) sessionMap.get("usuarioBean")).getLogin();
    }

    public PerfilEnum getPerfilUsuario() {
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        return ((UsuarioBean) sessionMap.get("usuarioBean")).getPerfil();
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
        System.out.println("form for type: " + tipoRequerimento);
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
     * @return the tipoRequerimentoBusca
     */
    public TipoRequerimentoEnum getTipoRequerimentoBusca() {
        return tipoRequerimentoBusca;
    }

    /**
     * @param tipoRequerimentoBusca the tipoRequerimentoBusca to set
     */
    public void setTipoRequerimentoBusca(TipoRequerimentoEnum tipoRequerimentoBusca) {
        this.tipoRequerimentoBusca = tipoRequerimentoBusca;
    }

    /**
     * @return the termoBuscaDiscente
     */
    public String getTermoBuscaDiscente() {
        return termoBuscaDiscente;
    }

    /**
     * @param termoBuscaDiscente the termoBuscaDiscente to set
     */
    public void setTermoBuscaDiscente(String termoBuscaDiscente) {
        this.termoBuscaDiscente = termoBuscaDiscente;
    }

    /**
     * @return the termoBuscaPeriodo
     */
    public String getTermoBuscaPeriodo() {
        try {
            Pattern pattDateA = Pattern.compile("^\\d{2}/\\d{2}/\\d{4}");
            Matcher matcherA = pattDateA.matcher(termoBuscaPeriodo);

            Pattern pattDateB = Pattern.compile("\\d{2}/\\d{2}/\\d{4}$");
            Matcher matcherB = pattDateB.matcher(termoBuscaPeriodo);
            if (!matcherA.find() || !matcherB.find()) {
                termoBuscaPeriodo = "";
            }
        } catch (Exception e) {
            return "";
        }
        return termoBuscaPeriodo;
    }

    /**
     * @param termoBuscaPeriodo the termoBuscaPeriodo to set
     */
    public void setTermoBuscaPeriodo(String termoBuscaPeriodo) {
        this.termoBuscaPeriodo = termoBuscaPeriodo;
    }

    public List<Atendimento> getAtendimentos() {
        return atendimentos;
    }

    public List<Arquivo> getArquivos() {
        return arquivos;
    }

    public Arquivo getArquivo() {
        return arquivo;
    }

    public void setArquivo(Arquivo arquivo) {
        this.arquivo = arquivo;
    }

    public boolean isArquivoEnviado() {
        return arquivoEnviado;
    }

    public StreamedContent getArquivoContent() {
        Path filePath = Paths.get(arquivo.getCaminhoArquivo());
        DefaultStreamedContent content = new DefaultStreamedContent();
        try {
            FileInputStream inputStream = new FileInputStream(filePath.toFile());
            content.setName(arquivo.getNomeArquivo());
            content.setStream(inputStream);
            content.setContentType(Files.probeContentType(filePath));
        } catch (IOException e) {
            System.out.println("error: " + e.getMessage());
        }
        return content;
    }

    public LazyDataModel<Requerimento> getRequerimentosDataModel() {
        return requerimentosDataModel;
    }

    public Map<String, TipoBusca> getTipoBuscaEnum() {
        Map<String, TipoBusca> items = new HashMap();
        for (TipoBusca t : TipoBusca.values()) {
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
        this.tipoBusca = tipoBusca;
    }
}
