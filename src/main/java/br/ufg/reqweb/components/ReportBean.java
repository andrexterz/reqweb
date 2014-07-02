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
import br.ufg.reqweb.model.Turma;
import br.ufg.reqweb.model.Usuario;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import javax.faces.context.FacesContext;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
            JRBeanCollectionDataSource beanDataSource = new JRBeanCollectionDataSource(usuarioDao.find(perfilTipo));
            Map reportParameters = new HashMap();
            reportParameters.put("TITULO", LocaleBean.getMessageBundle().getString("usuarios"));
            JasperPrint jrp = JasperFillManager.fillReport(reportPath, reportParameters, beanDataSource);
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
        for (Usuario u : usuarioDao.find(perfilTipo)) {
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

    @Transactional(readOnly = true)
    public StreamedContent getIndicePrioridadeAsPDF() {
        DefaultStreamedContent content = new DefaultStreamedContent();
        try {
            String reportPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/reports/usuarios_ip.jasper");
            JRMapCollectionDataSource dataSource = new JRMapCollectionDataSource(reportDao.listIndicePrioridadeMap());
            Map reportParameters = new HashMap();
            reportParameters.put("TITULO", LocaleBean.getMessageBundle().getString("indicePrioridade"));
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

    @Transactional(readOnly = true)
    public StreamedContent getAjusteDeMatriculaAsPDF() {
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        Curso curso = null;
        for (Perfil p : ((UsuarioBean) sessionMap.get("usuarioBean")).getSessionUsuario().getPerfilList()) {
            if (p.getTipoPerfil().equals(((UsuarioBean) sessionMap.get("usuarioBean")).getPerfil())) {
                curso = p.getCurso();
                break;
            }
        }
        DefaultStreamedContent content = new DefaultStreamedContent();
        try {
            String reportPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/reports/ajuste_de_matricula.jasper");
            JRMapCollectionDataSource dataSource = new JRMapCollectionDataSource(reportDao.listjusteDeMatriculaMap(curso));
            Map reportParameters = new HashMap();
            reportParameters.put("TITULO", LocaleBean.getMessageBundle().getString("ajusteDeMatricula"));
            reportParameters.put("INCLUIR", LocaleBean.getMessageBundle().getString("inclusao"));
            reportParameters.put("EXCLUIR", LocaleBean.getMessageBundle().getString("exclusao"));

            JasperPrint jrp = JasperFillManager.fillReport(reportPath, reportParameters, dataSource);
            InputStream inputStream = new ByteArrayInputStream(JasperExportManager.exportReportToPdf(jrp));
            content.setName("reqweb_ajuste_de_matricula.pdf");
            content.setContentType("application/pdf");
            content.setStream(inputStream);

        } catch (JRException e) {
            System.out.println("error: " + e.getMessage());
        }
        return content;
    }
}
