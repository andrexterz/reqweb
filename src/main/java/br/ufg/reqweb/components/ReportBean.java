/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.reqweb.components;

import br.ufg.reqweb.dao.DisciplinaDao;
import br.ufg.reqweb.dao.IndicePrioridadeDao;
import br.ufg.reqweb.dao.ReportDao;
import br.ufg.reqweb.dao.TurmaDao;
import br.ufg.reqweb.dao.UsuarioDao;
import br.ufg.reqweb.model.Curso;
import br.ufg.reqweb.model.Disciplina;
import br.ufg.reqweb.model.IndicePrioridade;
import br.ufg.reqweb.model.Perfil;
import br.ufg.reqweb.model.PerfilEnum;
import br.ufg.reqweb.model.Periodo;
import br.ufg.reqweb.model.RequerimentoStatusEnum;
import br.ufg.reqweb.model.TipoRequerimentoEnum;
import br.ufg.reqweb.model.Turma;
import br.ufg.reqweb.model.Usuario;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author andre
 */
@Component
@Scope(value = "session")
public class ReportBean {

    @Autowired
    private ReportDao reportDao;

    @Autowired
    private UsuarioDao usuarioDao;

    @Autowired
    private DisciplinaDao disciplinaDao;

    @Autowired
    private IndicePrioridadeDao indicePrioridadeDao;

    @Autowired
    private TurmaDao turmaDao;

    private Curso curso;

    private PerfilEnum perfil;

    private Usuario usuario;

    public ReportBean() {
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        curso = null;
        perfil = ((UsuarioBean) sessionMap.get("usuarioBean")).getPerfil();
        usuario = ((UsuarioBean) sessionMap.get("usuarioBean")).getSessionUsuario();
        for (Perfil p : ((UsuarioBean) sessionMap.get("usuarioBean")).getSessionUsuario().getPerfilList()) {
            if (p.getTipoPerfil().equals(perfil)) {
                curso = p.getCurso();
                break;
            }
        }
    }

    public Curso getCurso() {
        return curso;
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
            csvData.append(t.getDocente() == null ? "" : t.getDocente().getId());
            csvData.append(",");
            csvData.append(t.getDisciplina().getCurso().getSigla());
        }
        InputStream stream;
        try {
            stream = new ByteArrayInputStream(csvData.toString().getBytes("UTF8"));
        } catch (UnsupportedEncodingException e) {
            stream = new ByteArrayInputStream(csvData.toString().getBytes());
        }
        StreamedContent file = new DefaultStreamedContent(stream, "text/csv", "reqweb_turmas.csv", "UTF8");
        return file;
    }

    public StreamedContent getUsuariosAsPDF(PerfilEnum perfilTipo) {
        DefaultStreamedContent content = new DefaultStreamedContent();
        try {
            String reportPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/reports/usuarios.jasper");
            //JRBeanCollectionDataSource beanDataSource;
            JRMapCollectionDataSource dataSource;
            if (curso != null) {
                dataSource = new JRMapCollectionDataSource(reportDao.listUsuarioMap(perfilTipo, curso));
            } else {
                dataSource = new JRMapCollectionDataSource(reportDao.listUsuarioMap(perfilTipo));
            }
            Map reportParameters = new HashMap();
            reportParameters.put("TITULO", LocaleBean.getMessageBundle().getString("usuarios"));
            reportParameters.put("MATRICULA", LocaleBean.getMessageBundle().getString("usuarioMatricula"));
            reportParameters.put("NOME", LocaleBean.getMessageBundle().getString("discente"));
            reportParameters.put("CURSO", LocaleBean.getMessageBundle().getString("curso"));
            reportParameters.put("EMAIL", LocaleBean.getMessageBundle().getString("usuarioEmail"));

            JasperPrint jrp = JasperFillManager.fillReport(reportPath, reportParameters, dataSource);
            InputStream inputStream = new ByteArrayInputStream(JasperExportManager.exportReportToPdf(jrp));
            content.setName(String.format("reqweb_usuarios_%s.pdf", perfilTipo.name().toLowerCase()));
            content.setContentType("application/pdf");
            content.setStream(inputStream);

        } catch (JRException e) {
            System.out.println("error: " + e.getMessage());
        }
        return content;
    }

    public StreamedContent getDocentesAsCSV() {
        return getUsuariosAsCSV(PerfilEnum.DOCENTE);
    }

    public StreamedContent getDiscentesAsCSV() {
        return getUsuariosAsCSV(PerfilEnum.DISCENTE);
    }

    public StreamedContent getUsuariosAsCSV(PerfilEnum perfilTipo) {
        StringBuilder csvData = new StringBuilder("id,nome,login,email,tipo_perfil,matricula");
        List<Usuario> usuarios;
        if (curso == null) {
            usuarios = usuarioDao.find(perfilTipo);
        } else {
            usuarios = usuarioDao.find(perfilTipo, curso);
        }
        for (Usuario u : usuarios) {
            csvData.append("\n");
            csvData.append(u.getId());
            csvData.append(",");
            csvData.append(u.getNome());
            csvData.append(",");
            csvData.append(u.getLogin());
            csvData.append(",");
            csvData.append(u.getEmail());
            csvData.append(",");
            csvData.append(perfilTipo.getPapel());
            csvData.append(",");
            csvData.append(u.getMatricula());
        }
        InputStream stream;
        try {
            stream = new ByteArrayInputStream(csvData.toString().getBytes("UTF8"));
        } catch (UnsupportedEncodingException e) {
            stream = new ByteArrayInputStream(csvData.toString().getBytes());
        }
        StreamedContent file = new DefaultStreamedContent(stream, "text/csv", String.format("reqweb_usuarios_%s.csv", perfilTipo.name().toLowerCase()), "UTF8");
        return file;
    }

    public StreamedContent getDocentesAsPDF() {
        return getUsuariosAsPDF(PerfilEnum.DOCENTE);
    }

    public StreamedContent getDiscentesAsPDF() {
        return getUsuariosAsPDF(PerfilEnum.DISCENTE);
    }

    public StreamedContent getRelatorioTotalAsPDF() {
        DefaultStreamedContent content = new DefaultStreamedContent();
        try {
            String reportPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/reports/resumo.jasper");
            JRMapCollectionDataSource dataSource;
            List<TipoRequerimentoEnum> tipoRequerimentoList;
            List<RequerimentoStatusEnum> status;
            if (curso == null) {
                tipoRequerimentoList = Arrays.asList(new TipoRequerimentoEnum[]{
                    TipoRequerimentoEnum.DECLARACAO_DE_MATRICULA,
                    TipoRequerimentoEnum.EXTRATO_ACADEMICO,
                    TipoRequerimentoEnum.DOCUMENTO_DE_ESTAGIO,
                    TipoRequerimentoEnum.EMENTA_DE_DISCIPLINA
                });
                status = Arrays.asList(new RequerimentoStatusEnum[]{
                    RequerimentoStatusEnum.ABERTO
                });
            } else {
                tipoRequerimentoList = Arrays.asList(new TipoRequerimentoEnum[]{
                    TipoRequerimentoEnum.DECLARACAO_DE_MATRICULA,
                    TipoRequerimentoEnum.EXTRATO_ACADEMICO,
                    TipoRequerimentoEnum.DOCUMENTO_DE_ESTAGIO,
                    TipoRequerimentoEnum.EMENTA_DE_DISCIPLINA
                });
                status = Arrays.asList(new RequerimentoStatusEnum[]{
                    RequerimentoStatusEnum.ABERTO,
                    RequerimentoStatusEnum.EM_ANDAMENTO
                });
            }
            dataSource = new JRMapCollectionDataSource(reportDao.listTotalRequerimento(curso, status, tipoRequerimentoList));
            Map reportParameters = new HashMap();
            reportParameters.put("TITULO", LocaleBean.getMessageBundle().getString("requerimentos"));
            reportParameters.put("CURSO", LocaleBean.getMessageBundle().getString("curso"));
            reportParameters.put("REQUERIMENTO", LocaleBean.getMessageBundle().getString("requerimento"));
            reportParameters.put("STATUS", LocaleBean.getMessageBundle().getString("status"));
            reportParameters.put("ABERTO", LocaleBean.getMessageBundle().getString("aberto"));
            reportParameters.put("EM_ANDAMENTO", LocaleBean.getMessageBundle().getString("emAndamento"));
            reportParameters.put("TOTAL", LocaleBean.getMessageBundle().getString("total"));

            reportParameters.put("DECLARACAO_DE_MATRICULA", TipoRequerimentoEnum.DECLARACAO_DE_MATRICULA.getTipoLocale());
            reportParameters.put("EXTRATO_ACADEMICO", TipoRequerimentoEnum.EXTRATO_ACADEMICO.getTipoLocale());
            reportParameters.put("DOCUMENTO_DE_ESTAGIO", TipoRequerimentoEnum.DOCUMENTO_DE_ESTAGIO.getTipoLocale());
            reportParameters.put("EMENTA_DE_DISCIPLINA", TipoRequerimentoEnum.EMENTA_DE_DISCIPLINA.getTipoLocale());
            JasperPrint jrp = JasperFillManager.fillReport(reportPath, reportParameters, dataSource);
            InputStream inputStream = new ByteArrayInputStream(JasperExportManager.exportReportToPdf(jrp));
            content.setName("reqweb_resumo.pdf");
            content.setContentType("application/pdf");
            content.setStream(inputStream);
        } catch (JRException e) {
            System.out.println("error: " + e.getMessage());
        }
        return content;
    }

    public StreamedContent getIndicePrioridadeAsPDF() {
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        DefaultStreamedContent content = new DefaultStreamedContent();
        try {
            String reportPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/reports/usuarios_ip.jasper");
            JRMapCollectionDataSource dataSource;
            if (curso == null) {
                dataSource = new JRMapCollectionDataSource(reportDao.listIndicePrioridadeMap());
            } else {
                dataSource = new JRMapCollectionDataSource(reportDao.listIndicePrioridadeMap(curso));
            }
            Map reportParameters = new HashMap();
            reportParameters.put("TITULO", LocaleBean.getMessageBundle().getString("indicePrioridade"));
            reportParameters.put("MATRICULA", LocaleBean.getMessageBundle().getString("usuarioMatricula"));
            reportParameters.put("NOME", LocaleBean.getMessageBundle().getString("discente"));
            reportParameters.put("CURSO", LocaleBean.getMessageBundle().getString("curso"));
            JasperPrint jrp = JasperFillManager.fillReport(reportPath, reportParameters, dataSource);
            InputStream inputStream = new ByteArrayInputStream(JasperExportManager.exportReportToPdf(jrp));
            content.setName("reqweb_usuarios_ip.pdf");
            content.setContentType("application/pdf");
            content.setStream(inputStream);

        } catch (JRException e) {
            System.out.println("error: " + e.getMessage());
        }
        return content;
    }

    public StreamedContent getAjusteDeMatriculaAsPDF() {
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        DefaultStreamedContent content = new DefaultStreamedContent();
        try {
            String reportPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/reports/ajuste_de_matricula.jasper");
            Periodo periodo = ((PeriodoBean) sessionMap.get("periodoBean")).getItemSelecionado();
            JRMapCollectionDataSource dataSource = new JRMapCollectionDataSource(reportDao.listAjusteDeMatriculaMap(curso, periodo));
            Map reportParameters = new HashMap();
            reportParameters.put("TITULO", LocaleBean.getMessageBundle().getString("ajusteDeMatricula"));
            reportParameters.put("INCLUIR", LocaleBean.getMessageBundle().getString("inclusao"));
            reportParameters.put("EXCLUIR", LocaleBean.getMessageBundle().getString("exclusao"));
            reportParameters.put("MATRICULA", LocaleBean.getMessageBundle().getString("usuarioMatricula"));
            reportParameters.put("NOME", LocaleBean.getMessageBundle().getString("discente"));
            reportParameters.put("ACAO", LocaleBean.getMessageBundle().getString("acao"));

            JasperPrint jrp = JasperFillManager.fillReport(reportPath, reportParameters, dataSource);
            InputStream inputStream = new ByteArrayInputStream(JasperExportManager.exportReportToPdf(jrp));
            content.setName("reqweb_ajuste_de_matricula.pdf");
            content.setContentType("application/pdf");
            content.setStream(inputStream);

        } catch (NullPointerException | JRException e) {
            System.out.println("error: " + e.getMessage());
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    LocaleBean.getMessageBundle().getString("operacaoCancelada"), null);
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
        return content;
    }

    public StreamedContent getDocumentoDeEstagioAsPDF() {
        DefaultStreamedContent content = new DefaultStreamedContent();
        try {
            String reportPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/reports/documento_de_estagio.jasper");
            JRMapCollectionDataSource dataSource;
            if (curso == null) {
                dataSource = new JRMapCollectionDataSource(reportDao.listDocumentoDeEstagioMap(RequerimentoStatusEnum.ABERTO));
            } else {
                dataSource = new JRMapCollectionDataSource(reportDao.listDocumentoDeEstagioMap(curso));
            }
            Map reportParameters = new HashMap();
            reportParameters.put("TITULO", LocaleBean.getMessageBundle().getString("documentoDeEstagio"));
            reportParameters.put("MATRICULA", LocaleBean.getMessageBundle().getString("usuarioMatricula"));
            reportParameters.put("NOME", LocaleBean.getMessageBundle().getString("discente"));
            reportParameters.put("DOCENTE", LocaleBean.getMessageBundle().getString("docente"));
            reportParameters.put("TIPO_DE_DOCUMENTO", LocaleBean.getMessageBundle().getString("tipoDeDocumento"));
            reportParameters.put("CONTRATO_DE_ESTAGIO", LocaleBean.getMessageBundle().getString("contratoDeEstagio"));
            reportParameters.put("RELATORIO_DE_ESTAGIO", LocaleBean.getMessageBundle().getString("relatorioDeEstagio"));
            reportParameters.put("CURSO", LocaleBean.getMessageBundle().getString("curso"));

            JasperPrint jrp = JasperFillManager.fillReport(reportPath, reportParameters, dataSource);
            InputStream inputStream = new ByteArrayInputStream(JasperExportManager.exportReportToPdf(jrp));
            content.setName("reqweb_documento_de_estagio.pdf");
            content.setContentType("application/pdf");
            content.setStream(inputStream);

        } catch (NullPointerException | JRException e) {
            System.out.println("error: " + e.getMessage());
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    LocaleBean.getMessageBundle().getString("operacaoCancelada"), null);
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
        return content;
    }

    public StreamedContent getSegundaChamadaDeProvaAsPDF() {
        DefaultStreamedContent content = new DefaultStreamedContent();
        try {
            String reportPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/reports/segunda_chamada_de_prova.jasper");
            JRMapCollectionDataSource dataSource;
            RequerimentoStatusEnum status;
            if (perfil.equals(PerfilEnum.COORDENADOR_DE_CURSO)) {
                status = RequerimentoStatusEnum.ABERTO;
                dataSource = new JRMapCollectionDataSource(reportDao.listSegundaChamadaDeProvaMap(status, curso));
            } else {
                status = RequerimentoStatusEnum.EM_ANDAMENTO;
                dataSource = new JRMapCollectionDataSource(reportDao.listSegundaChamadaDeProvaMap(status, usuario));
            }
            Map reportParameters = new HashMap();
            reportParameters.put("TITULO", LocaleBean.getMessageBundle().getString("segundaChamadaDeProva"));
            reportParameters.put("MATRICULA", LocaleBean.getMessageBundle().getString("usuarioMatricula"));
            reportParameters.put("NOME", LocaleBean.getMessageBundle().getString("discente"));
            reportParameters.put("DOCENTE", LocaleBean.getMessageBundle().getString("docente"));
            reportParameters.put("DISCIPLINA", LocaleBean.getMessageBundle().getString("disciplina"));
            reportParameters.put("TURMA", LocaleBean.getMessageBundle().getString("turma"));
            reportParameters.put("CURSO", LocaleBean.getMessageBundle().getString("curso"));
            reportParameters.put("DATA_PROVA", LocaleBean.getMessageBundle().getString("dataProvaA"));
            reportParameters.put("DATA_REQUERIMENTO", LocaleBean.getMessageBundle().getString("dataCriacao"));

            JasperPrint jrp = JasperFillManager.fillReport(reportPath, reportParameters, dataSource);
            InputStream inputStream = new ByteArrayInputStream(JasperExportManager.exportReportToPdf(jrp));
            content.setName("reqweb_segunda_chamada_de_prova.pdf");
            content.setContentType("application/pdf");
            content.setStream(inputStream);

        } catch (NullPointerException | JRException e) {
            System.out.println("error: " + e.getMessage());
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    LocaleBean.getMessageBundle().getString("operacaoCancelada"), null);
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
        return content;
    }
}
