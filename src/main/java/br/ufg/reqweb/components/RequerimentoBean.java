/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.reqweb.components;

import br.ufg.reqweb.dao.RequerimentoDao;
import br.ufg.reqweb.dao.TurmaDao;
import br.ufg.reqweb.dao.UsuarioDao;
import br.ufg.reqweb.model.AjusteDeMatricula;
import br.ufg.reqweb.model.Arquivo;
import br.ufg.reqweb.model.Atendimento;
import br.ufg.reqweb.model.DeclaracaoDeMatricula;
import br.ufg.reqweb.model.Disciplina;
import br.ufg.reqweb.model.DocumentoDeEstagio;
import br.ufg.reqweb.model.EmentaDeDisciplina;
import br.ufg.reqweb.model.ExtratoAcademico;
import br.ufg.reqweb.model.ItemRequerimento;
import br.ufg.reqweb.model.ItemRequerimentoStatusEnum;
import br.ufg.reqweb.model.Perfil;
import br.ufg.reqweb.model.PerfilEnum;
import br.ufg.reqweb.model.Requerimento;
import br.ufg.reqweb.model.RequerimentoStatusEnum;
import br.ufg.reqweb.model.SegundaChamadaDeProva;
import br.ufg.reqweb.model.TipoRequerimentoEnum;
import br.ufg.reqweb.model.Turma;
import br.ufg.reqweb.model.Usuario;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
import org.springframework.orm.hibernate4.HibernateOptimisticLockingFailureException;
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
    private MailBean mailBean;

    @Autowired
    private UsuarioDao usuarioDao;

    @Autowired
    private RequerimentoDao requerimentoDao;

    @Autowired
    private TurmaDao turmaDao;
    
    private Requerimento requerimento;
    private Requerimento itemSelecionado;
    private TipoRequerimentoEnum tipoRequerimento;
    private String operation;//a: adiciona | e: edita
    private TipoRequerimentoEnum tipoRequerimentoBusca;
    private String termoBuscaDiscente;
    private String termoBuscaPeriodo;
    private final List<ItemRequerimento> itemRemovidoList;
    private Arquivo arquivo;
    private boolean confirmaRequerimento;
    private boolean saveStatus;
    private FormControl step;
    private final LazyDataModel<Requerimento> requerimentosDataModel;

    public enum TipoBusca {

        PERIODO("dataCriacao"),
        TIPO_REQUERIMENTO("requerimento"),
        DISCENTE("discente");

        private TipoBusca(String tipo) {
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

    public enum FormControl {

        STEP0(0),//generic form
        STEP1(1),//warning screen
        STEP2(2);//wizard form

        private FormControl(int value) {
            this.value = value;
        }

        private final int value;

        public int getValue() {
            return value;
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
        tipoBusca = TipoBusca.PERIODO;
        itemRemovidoList = new ArrayList<>();
        arquivo = null;
        confirmaRequerimento = false;
        saveStatus = false;
        step = FormControl.STEP0;

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
                    filtros.put("login", getSessionUsuario().getLogin());
                }
                if (getPerfilUsuario().equals(PerfilEnum.COORDENADOR_DE_CURSO)) {
                    for (Perfil p : getSessionUsuario().getPerfilList()) {
                        if (p.getTipoPerfil().equals(PerfilEnum.COORDENADOR_DE_CURSO)) {
                            filtros.put("curso", p.getCurso());
                            break;
                        }
                    }
                }
                if (getPerfilUsuario().equals(PerfilEnum.COORDENADOR_DE_ESTAGIO)) {
                    for (Perfil p : getSessionUsuario().getPerfilList()) {
                        if (p.getTipoPerfil().equals(PerfilEnum.COORDENADOR_DE_ESTAGIO)) {
                            filtros.put("curso", p.getCurso());
                            break;
                        }
                    }
                }
                if (getPerfilUsuario().equals(PerfilEnum.DOCENTE)) {
                    filtros.put("turmas", turmaDao.find(getSessionUsuario()));
                }

                if (getTipoRequerimento() != null) {
                    setTipoRequerimentoBusca(null);
                    filtros.put("tipoRequerimento", getTipoRequerimento());
                }
                if (getTipoBusca().equals(TipoBusca.TIPO_REQUERIMENTO) && getTipoRequerimentoBusca() != null) {
                    filtros.put("tipoRequerimento", getTipoRequerimentoBusca());
                }
                if (getTipoBusca().equals(TipoBusca.DISCENTE) && (getTermoBuscaDiscente() != null && !getTermoBuscaDiscente().isEmpty())) {
                    filtros.put("termo", getTermoBuscaDiscente());
                }
                if (getTipoBusca().equals(TipoBusca.PERIODO) && (getTermoBuscaPeriodo() != null && !getTermoBuscaPeriodo().isEmpty())) {
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
        setSaveStatus(false);
        requerimento = new Requerimento();
        requerimento.setTipoRequerimento(tipoRequerimento);
        System.out.println("objeto requerimento criado: " + requerimento.getClass());
        System.out.println("tipo de Requerimento: " + tipoRequerimento.getTipo());
        if (tipoRequerimento.equals(TipoRequerimentoEnum.DECLARACAO_DE_MATRICULA)) {
            DeclaracaoDeMatricula itemRequerimento = new DeclaracaoDeMatricula();
            sessionMap.put("itemRequerimento", itemRequerimento);
            requerimento.addItemRequerimento(itemRequerimento);
            System.out.println("objeto itemRequerimento criado: " + itemRequerimento.getClass());

        }
        if (tipoRequerimento.equals(TipoRequerimentoEnum.EXTRATO_ACADEMICO)) {
            ExtratoAcademico itemRequerimento = new ExtratoAcademico();
            sessionMap.put("itemRequerimento", itemRequerimento);
            requerimento.addItemRequerimento(itemRequerimento);
            System.out.println("objeto itemRequerimento criado: " + itemRequerimento.getClass());

        }
        if (tipoRequerimento.equals(TipoRequerimentoEnum.SEGUNDA_CHAMADA_DE_PROVA)) {
            setStep(FormControl.STEP1);
            SegundaChamadaDeProva itemRequerimento = new SegundaChamadaDeProva();
            sessionMap.put("itemRequerimento", itemRequerimento);
            requerimento.addItemRequerimento(itemRequerimento);
            System.out.println("objeto itemRequerimento criado: " + itemRequerimento.getClass());
        }
        if (tipoRequerimento.equals(TipoRequerimentoEnum.EMENTA_DE_DISCIPLINA)) {
            EmentaDeDisciplina itemRequerimento = new EmentaDeDisciplina();
            sessionMap.put("itemRequerimento", itemRequerimento);
            System.out.println("objeto itemRequerimento criado: " + itemRequerimento.getClass());
        }

        if (tipoRequerimento.equals(TipoRequerimentoEnum.DOCUMENTO_DE_ESTAGIO)) {
            DocumentoDeEstagio itemRequerimento = new DocumentoDeEstagio();
            sessionMap.put("itemRequerimento", itemRequerimento);
            requerimento.addItemRequerimento(itemRequerimento);
            System.out.println("objeto itemRequerimento criado: " + itemRequerimento.getClass());
        }

        if (tipoRequerimento.equals(TipoRequerimentoEnum.AJUSTE_DE_MATRICULA)) {
            if (isReqAjusteDeMatriculaExists()) {
                setStep(FormControl.STEP1);
            }
            AjusteDeMatricula itemRequerimento = new AjusteDeMatricula();
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
            setStep(FormControl.STEP0);
            setSaveStatus(false);
            requerimento = requerimentoDao.findById(getItemSelecionado().getId());
            if (!getPerfilUsuario().equals(PerfilEnum.DISCENTE) && requerimento.getAtendimento() == null) {
                Atendimento atendimento = new Atendimento();
                requerimento.setAtendimento(atendimento);
                atendimento.setAtendente(((UsuarioBean) sessionMap.get("usuarioBean")).getSessionUsuario());
            }
            System.out.println("items in requerimento: " + requerimento.getItemRequerimentoList().size());
            if (requerimento.getTipoRequerimento().equals(TipoRequerimentoEnum.DECLARACAO_DE_MATRICULA)) {
                sessionMap.put("itemRequerimento", requerimento.getItemRequerimentoList().iterator().next());
            }
            if (requerimento.getTipoRequerimento().equals(TipoRequerimentoEnum.EXTRATO_ACADEMICO)) {
                sessionMap.put("itemRequerimento", requerimento.getItemRequerimentoList().iterator().next());
            }
            if (requerimento.getTipoRequerimento().equals(TipoRequerimentoEnum.SEGUNDA_CHAMADA_DE_PROVA)) {
                setStep(FormControl.STEP2);
                ItemRequerimento itemRequerimento = requerimento.getItemRequerimentoList().iterator().next();
                sessionMap.put("itemRequerimento", itemRequerimento);
            }
            if (requerimento.getTipoRequerimento().equals(TipoRequerimentoEnum.EMENTA_DE_DISCIPLINA)) {
                sessionMap.put("itemRequerimento", new EmentaDeDisciplina());
            }
            if (requerimento.getTipoRequerimento().equals(TipoRequerimentoEnum.DOCUMENTO_DE_ESTAGIO)) {
                sessionMap.put("itemRequerimento", requerimento.getItemRequerimentoList().iterator().next());
            }
            if (requerimento.getTipoRequerimento().equals(TipoRequerimentoEnum.AJUSTE_DE_MATRICULA)) {
                sessionMap.put("itemRequerimento", new AjusteDeMatricula());
            }
        }
    }

    public Requerimento getLoadedItemRequerimento() {
        if (isSelecionado()) {
            return requerimentoDao.findById(getItemSelecionado().getId());
        } else {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, LocaleBean.getMessageBundle().getString("itemSelecionar"), null);
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return null;
        }
    }

    /**
     * cancels actions from <code>novoRequerimento</code>
     */
    public void cleanupRequerimento() {
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        sessionMap.remove("itemRequerimento");
        itemRemovidoList.clear();
        requerimento.getItemRequerimentoList().clear();
        setStep(FormControl.STEP0);
        setConfirmaRequerimento(false);
        System.out.println("cleaning up requerimento... ");
    }

    public void salvaRequerimento() {
        RequestContext context = RequestContext.getCurrentInstance();
        String msgError = validaRequerimento();
        try {
            if (saveStatus) {
                if (operation.equals(ADICIONA)) {
                    requerimentoDao.adicionar(requerimento);
                    itemSelecionado = requerimento;
                } else {
                    requerimentoDao.atualizar(requerimento);
                }
            }
            if (requerimento.getStatus().equals(RequerimentoStatusEnum.FINALIZADO)) {
                mailBean.sendMailToDiscente(requerimento);
            }
        } catch (Exception e) {
            if (e.getClass().equals(HibernateOptimisticLockingFailureException.class)) {
                msgError = String.format("%s: %s", msgError, LocaleBean.getMessageBundle().getString("erroVersao"));
            }
            System.out.println(e);
            setSaveStatus(false);
        }
        context.addCallbackParam("resultado", saveStatus);
        handleCompleteSaveRequerimento(msgError);
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

    public void handleCompleteSaveRequerimento(String msgError) {
        FacesContext context = FacesContext.getCurrentInstance();
        FacesMessage msg;
        if (saveStatus) {
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, LocaleBean.getMessageBundle().getString("dadosSalvos"), null);
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "info", msgError);
        }
        context.addMessage(null, msg);
    }

    public void selecionaItem(SelectEvent event) {
        itemSelecionado = (Requerimento) event.getObject();
    }

    public String validaRequerimento() {
        String loginUser = getSessionUsuario().getLogin();
        if (requerimento.getId() == null) {
            requerimento.setDiscente(usuarioDao.findByLogin(loginUser));
        }
        if (!getPerfilUsuario().equals(PerfilEnum.DISCENTE)) {
            handleStatusRequerimento();
        }
        Set<ConstraintViolation<Requerimento>> errors = validator.validate(requerimento);
        setSaveStatus(errors.isEmpty());
        StringBuilder formattedMsg = new StringBuilder(LocaleBean.getMessageBundle().getString("dadosInvalidos"));
        for (ConstraintViolation<Requerimento> error : errors) {
            String invalidValue = error.getPropertyPath().toString();
            Pattern pattErrorValue = Pattern.compile("(?<=\\.)\\w+");
            Matcher matcherValue = pattErrorValue.matcher(invalidValue);
            String errorMsg = error.getMessage();
            if (matcherValue.find()) {
                formattedMsg.append(String.format("- %s: %s. ", LocaleBean.getMessageBundle().getString(matcherValue.group()), errorMsg));
            }
        }
        return formattedMsg.toString();
    }

    public void handleStatusRequerimento() {
        if (requerimento.getTipoRequerimento().equals(TipoRequerimentoEnum.DECLARACAO_DE_MATRICULA) || requerimento.getTipoRequerimento().equals(TipoRequerimentoEnum.EXTRATO_ACADEMICO)) {
            if (!requerimento.getItemRequerimentoList().iterator().next().getStatus().equals(ItemRequerimentoStatusEnum.ABERTO)) {
                requerimento.setStatus(RequerimentoStatusEnum.FINALIZADO);
            } else {
                requerimento.setStatus(RequerimentoStatusEnum.ABERTO);
            }
        }
        if (requerimento.getTipoRequerimento().equals(TipoRequerimentoEnum.EMENTA_DE_DISCIPLINA) || requerimento.getTipoRequerimento().equals(TipoRequerimentoEnum.AJUSTE_DE_MATRICULA)) {
            int statusAbertoCounter = 0;
            for (ItemRequerimento item : requerimento.getItemRequerimentoList()) {
                if (item.getStatus().equals(ItemRequerimentoStatusEnum.ABERTO)) {
                    statusAbertoCounter++;
                }
            }
            System.out.format("Abertos: %d de %d\n", statusAbertoCounter, requerimento.getItemRequerimentoList().size());
            if (statusAbertoCounter == requerimento.getItemRequerimentoList().size()) {
                System.out.println("status aberto");
                requerimento.setStatus(RequerimentoStatusEnum.ABERTO);
            }
            if (statusAbertoCounter > 0 && statusAbertoCounter < requerimento.getItemRequerimentoList().size()) {
                System.out.println("status em andamento");
                requerimento.setStatus(RequerimentoStatusEnum.EM_ANDAMENTO);
            }
            if (statusAbertoCounter == 0) {
                System.out.println("status finalizado");
                requerimento.setStatus(RequerimentoStatusEnum.FINALIZADO);
            }
        }
        if (requerimento.getTipoRequerimento().equals(TipoRequerimentoEnum.SEGUNDA_CHAMADA_DE_PROVA)) {
            ItemRequerimentoStatusEnum status = requerimento.getItemRequerimentoList().iterator().next().getStatus();
            if (getPerfilUsuario().equals(PerfilEnum.COORDENADOR_DE_CURSO)) {
                switch (status) {
                    case DEFERIDO:
                        requerimento.setStatus(RequerimentoStatusEnum.EM_ANDAMENTO);
                        break;
                    case INDEFERIDO:
                        requerimento.setStatus(RequerimentoStatusEnum.FINALIZADO);
                        break;
                    default:
                        requerimento.setStatus(RequerimentoStatusEnum.ABERTO);
                        break;
                }
            } else {
                requerimento.setStatus(RequerimentoStatusEnum.FINALIZADO);
            }
            System.out.format("entering segunda_chamada_de_prova -> status is %s. current user: %s\n", status.toString(), getPerfilUsuario().toString());
        }
        if (requerimento.getTipoRequerimento().equals(TipoRequerimentoEnum.DOCUMENTO_DE_ESTAGIO)) {
            ItemRequerimentoStatusEnum status = requerimento.getItemRequerimentoList().iterator().next().getStatus();
            if (getPerfilUsuario().equals(PerfilEnum.SECRETARIA)) {
                switch (status) {
                    case RECEBIDO:
                        requerimento.setStatus(RequerimentoStatusEnum.EM_ANDAMENTO);
                        break;
                    case INDEFERIDO:
                        requerimento.setStatus(RequerimentoStatusEnum.FINALIZADO);
                        break;
                    default:
                        requerimento.setStatus(RequerimentoStatusEnum.ABERTO);
                        break;
                }
            } else {
                switch (requerimento.getItemRequerimentoList().iterator().next().getStatus()) {
                    case DEFERIDO:
                        requerimento.setStatus(RequerimentoStatusEnum.FINALIZADO);
                        break;
                    case INDEFERIDO:
                        requerimento.setStatus(RequerimentoStatusEnum.FINALIZADO);
                        break;
                    default:
                        requerimento.setStatus(RequerimentoStatusEnum.EM_ANDAMENTO);
                        break;
                }
            }

        }
        System.out.println("changing status to: " + requerimento.getStatus());
    }

    public void salvaAtendimento() {
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        requerimento.getAtendimento().setAtendente(((UsuarioBean) sessionMap.get("usuarioBean")).getSessionUsuario());
    }

    public boolean isAtendimentoRequired() {
        int statusIndeferidoCounter = 0;
        boolean observacaoIsEmpty;
        try {
            observacaoIsEmpty = requerimento.getAtendimento().getObservacao().isEmpty();
        } catch (NullPointerException e) {
            observacaoIsEmpty = true;
        }
        for (ItemRequerimento item : requerimento.getItemRequerimentoList()) {
            if (item.getStatus().equals(ItemRequerimentoStatusEnum.INDEFERIDO)) {
                statusIndeferidoCounter++;
            }
        }
        return (statusIndeferidoCounter > 0);
    }

    public void adicionaItemRequerimento() {
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        ItemRequerimento item = (ItemRequerimento) sessionMap.remove("itemRequerimento");
        Set<ConstraintViolation<ItemRequerimento>> errors = validator.validate(item);
        if (errors.isEmpty() && !requerimento.getItemRequerimentoList().contains(item)) {
            if (itemRemovidoList.contains(item)) {
                item = itemRemovidoList.get(itemRemovidoList.indexOf(item));
                itemRemovidoList.remove(item);
            }
            //adiciona item da sessao na lista de items
            requerimento.addItemRequerimento(item);

            if (requerimento.getTipoRequerimento().equals(TipoRequerimentoEnum.EMENTA_DE_DISCIPLINA)) {
                sessionMap.put("itemRequerimento", new EmentaDeDisciplina());
            }

            if (requerimento.getTipoRequerimento().equals(TipoRequerimentoEnum.AJUSTE_DE_MATRICULA)) {
                sessionMap.put("itemRequerimento", new AjusteDeMatricula());
            }

        } else {
            //devolve item para a sessao, caso haja erro
            sessionMap.put("itemRequerimento", item);
            StringBuilder formattedMsg = new StringBuilder(LocaleBean.getMessageBundle().getString("dadosInvalidos"));
            for (ConstraintViolation<ItemRequerimento> error : errors) {
                String invalidValue = error.getPropertyPath().toString();
                Pattern pattErrorValue = Pattern.compile("(?<=\\.)\\w+");
                Matcher matcherValue = pattErrorValue.matcher(invalidValue);
                String errorMsg = error.getMessage();
                if (matcherValue.find()) {
                    formattedMsg.append(String.format("- %s: %s. ", LocaleBean.getMessageBundle().getString(matcherValue.group()), errorMsg));
                }
            }
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, formattedMsg.toString(), null);
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    public void removeItemRequerimento(ItemRequerimento itemRequerimento) {
        requerimento.removeItemRequerimento(itemRequerimento);
        if (itemRequerimento.getId() != null) {
            itemRemovidoList.add(itemRequerimento);
        }
    }

    public void autoCompleteSelecionaObjectItem(SelectEvent event) {
        try {
            Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
            if (tipoRequerimento.equals(TipoRequerimentoEnum.SEGUNDA_CHAMADA_DE_PROVA)) {
                SegundaChamadaDeProva itemRequerimento = (SegundaChamadaDeProva) sessionMap.get("itemRequerimento");
                Turma turma = (Turma) event.getObject();
                itemRequerimento.setTurma(turma);
            }
            if (tipoRequerimento.equals(TipoRequerimentoEnum.EMENTA_DE_DISCIPLINA)) {
                EmentaDeDisciplina itemRequerimento = (EmentaDeDisciplina) sessionMap.get("itemRequerimento");
                Disciplina disciplina = (Disciplina) event.getObject();
                itemRequerimento.setDisciplina(disciplina);
            }
            if (tipoRequerimento.equals(TipoRequerimentoEnum.AJUSTE_DE_MATRICULA)) {
                AjusteDeMatricula itemRequerimento = (AjusteDeMatricula) sessionMap.get("itemRequerimento");
                Turma turma = (Turma) event.getObject();
                itemRequerimento.setTurma(turma);
            }

        } catch (NullPointerException e) {
            itemSelecionado = null;
        }
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

    public void uploadArquivo(FileUploadEvent event) {
        try {
            Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
            UploadedFile file = event.getFile();
            Arquivo arq = new Arquivo();
            System.out.println("novo arquivo --> " + arq);
            arq.setMimetype(file.getContentType());
            Pattern pattExt = Pattern.compile("(?<=\\.)\\w+$");
            Matcher matcherExt = pattExt.matcher(file.getFileName());
            String fileExt = "";
            if (matcherExt.find()) {
                fileExt = matcherExt.group();
            }

            Calendar calendar = Calendar.getInstance(Locale.getDefault());
            arq.setNomeArquivo(String.format("%1$s%2$tF-%2$tH%2$tM%2$tS.%3$s", getSessionUsuario().getLogin(), calendar.getTime(), fileExt));
            arq.setConteudo(file.getContents());
            SegundaChamadaDeProva itemRequerimento = (SegundaChamadaDeProva) sessionMap.get("itemRequerimento");
            itemRequerimento.addArquivo(arq);
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "info", LocaleBean.getMessageBundle().getString("arquivoEnviado"));
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } catch (Exception e) {
            System.out.println("upload error: " + e.getLocalizedMessage());
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, LocaleBean.getMessageBundle().getString("erroGravacao"), null);
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    public void removeArquivo(Arquivo arquivo) {
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        SegundaChamadaDeProva itemRequerimento = (SegundaChamadaDeProva) sessionMap.get("itemRequerimento");
        itemRequerimento.removeArquivo(arquivo);
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, LocaleBean.getMessageBundle().getString("arquivoExcluido"), null);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public Usuario getSessionUsuario() {
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        return ((UsuarioBean) sessionMap.get("usuarioBean")).getSessionUsuario();
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

    public Arquivo getArquivo() {
        return arquivo;
    }

    public void setArquivo(Arquivo arquivo) {
        this.arquivo = arquivo;
    }

    public StreamedContent getArquivoContent() {
        DefaultStreamedContent content = new DefaultStreamedContent();
        try {
            InputStream inputStream = new ByteArrayInputStream(arquivo.getConteudo());
            content.setName(arquivo.getNomeArquivo());
            content.setStream(inputStream);
            content.setContentType(arquivo.getMimetype());
        } catch (Exception e) {
            System.out.println("getContent error: " + e.getMessage());
        }
        return content;
    }

    public boolean isEditavel() {
        boolean editavel = false;
        if (getPerfilUsuario().equals(PerfilEnum.COORDENADOR_DE_CURSO)) {
            editavel = itemSelecionado != null && !itemSelecionado.getStatus().equals(RequerimentoStatusEnum.FINALIZADO);
        }
        if (getPerfilUsuario().equals(PerfilEnum.COORDENADOR_DE_ESTAGIO)) {
            if (itemSelecionado != null) {
                editavel = (itemSelecionado.getTipoRequerimento().equals(TipoRequerimentoEnum.DOCUMENTO_DE_ESTAGIO)
                        && itemSelecionado.getStatus().equals(RequerimentoStatusEnum.EM_ANDAMENTO));
            }
        }
        if (getPerfilUsuario().equals(PerfilEnum.DOCENTE)) {
            if (itemSelecionado != null) {
                editavel = (itemSelecionado.getTipoRequerimento().equals(TipoRequerimentoEnum.SEGUNDA_CHAMADA_DE_PROVA)
                        && itemSelecionado.getStatus().equals(RequerimentoStatusEnum.EM_ANDAMENTO));
            }
        }
        if (getPerfilUsuario().equals(PerfilEnum.SECRETARIA)) {
            if (itemSelecionado != null) {
                editavel = (itemSelecionado.getTipoRequerimento().equals(TipoRequerimentoEnum.DOCUMENTO_DE_ESTAGIO)
                        && itemSelecionado.getStatus().equals(RequerimentoStatusEnum.ABERTO))
                        || (!itemSelecionado.getTipoRequerimento().equals(TipoRequerimentoEnum.DOCUMENTO_DE_ESTAGIO)
                        && !itemSelecionado.getStatus().equals(RequerimentoStatusEnum.FINALIZADO)
                        && !itemSelecionado.getTipoRequerimento().equals(TipoRequerimentoEnum.SEGUNDA_CHAMADA_DE_PROVA)
                        && !itemSelecionado.getTipoRequerimento().equals(TipoRequerimentoEnum.AJUSTE_DE_MATRICULA)
                        );
            }
        }
        if (getPerfilUsuario().equals(PerfilEnum.DISCENTE)) {
            Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
            boolean requerimentoValido = true;
            if (itemSelecionado != null) {
                System.out.println("item == tiporeq? " + itemSelecionado.getTipoRequerimento().equals(getTipoRequerimento()));
                if (itemSelecionado.getTipoRequerimento().equals(TipoRequerimentoEnum.AJUSTE_DE_MATRICULA)) {
                    requerimentoValido = ((PeriodoBean) sessionMap.get("periodoBean")).isPeriodoValido();
                }
            }
            editavel = itemSelecionado != null
                    && requerimentoValido
                    && itemSelecionado.getStatus().equals(RequerimentoStatusEnum.ABERTO);
        }
        return editavel;
    }
    
    public boolean isSelecionavel() {
        return (itemSelecionado != null && (itemSelecionado.getTipoRequerimento().equals(getTipoRequerimento()) || getTipoRequerimento() == null));
    }
    
    public boolean isShowControls() {
        return step == FormControl.STEP0;
    }

    public boolean isConfirmaRequerimento() {
        return confirmaRequerimento;
    }

    public void setConfirmaRequerimento(boolean confirmaRequerimento) {
        this.confirmaRequerimento = confirmaRequerimento;
    }

    public boolean isReqAjusteDeMatriculaExists() {
        return requerimentoDao.countReqAjusteDeMatricula(getSessionUsuario().getLogin()) > 0;
    }

    public boolean isSaveStatus() {
        return saveStatus;
    }

    public void setSaveStatus(boolean saveStatus) {
        this.saveStatus = saveStatus;
    }

    public FormControl getStep() {
        return step;
    }

    public void setStep(FormControl step) {
        this.step = step;
    }

    public void nextStep() {
        switch (step.getValue()) {
            case 0:
                setStep(FormControl.STEP1);
                break;
            case 1:
                setStep(FormControl.STEP2);
                break;
            default:
                setStep(FormControl.STEP2);
                break;
        }
    }

    public void previousStep() {
        switch (step.getValue()) {
            case 2:
                setStep(FormControl.STEP1);
                break;
            case 1:
                setStep(FormControl.STEP0);
                break;
            default:
                setStep(FormControl.STEP0);
                break;
        }
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
