/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.reqweb.components;

import br.ufg.reqweb.dao.ConfigDao;
import br.ufg.reqweb.dao.ReportDao;
import br.ufg.reqweb.dao.UsuarioDao;
import br.ufg.reqweb.model.DocumentoDeEstagio;
import br.ufg.reqweb.model.PerfilEnum;
import br.ufg.reqweb.model.RequerimentoStatusEnum;
import br.ufg.reqweb.model.TipoRequerimentoEnum;
import br.ufg.reqweb.model.Usuario;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 *
 * @author André
 */
@Component
@Scope(value = "singleton")
public class MailBean {

    private int counter = 0;
    
    @Autowired
    ReportDao reportDao;

    @Autowired
    UsuarioDao usuarioDao;
    
    @Autowired
    ConfigDao configDao;

    Logger log;
    
    public MailBean() {
        log = Logger.getLogger(MailBean.class);
    }

    private void sendEmailToStaff(Map<String, String> content) {
        for (String k : content.keySet()) {
            MultiPartEmail email = new MultiPartEmail();
            JRMapCollectionDataSource dataSource;
            List<TipoRequerimentoEnum> tipoRequerimentoList;
            List<RequerimentoStatusEnum> status;
            tipoRequerimentoList = Arrays.asList(TipoRequerimentoEnum.values());
            status = Arrays.asList(RequerimentoStatusEnum.values());
            String reportPath = this.getClass().getResource("reports/resumo.jasper").getPath();
            dataSource = new JRMapCollectionDataSource(reportDao.listTotalRequerimento(null, status, tipoRequerimentoList));
            Map reportParameters = new HashMap();
            reportParameters.put("TITULO", LocaleBean.getDefaultMessageBundle().getString("requerimentos"));
            reportParameters.put("CURSO", LocaleBean.getDefaultMessageBundle().getString("curso"));
            reportParameters.put("REQUERIMENTO", LocaleBean.getDefaultMessageBundle().getString("requerimento"));
            reportParameters.put("STATUS", LocaleBean.getDefaultMessageBundle().getString("status"));
            for (RequerimentoStatusEnum statusEnum: RequerimentoStatusEnum.values()) {
                reportParameters.put(statusEnum.name(), LocaleBean.getDefaultMessageBundle().getString(statusEnum.getStatus()));
            }
            reportParameters.put("TOTAL", LocaleBean.getDefaultMessageBundle().getString("total"));
            reportParameters.put("DECLARACAO_DE_MATRICULA", TipoRequerimentoEnum.DECLARACAO_DE_MATRICULA.getTipoLocale());
            reportParameters.put("EXTRATO_ACADEMICO", TipoRequerimentoEnum.EXTRATO_ACADEMICO.getTipoLocale());
            reportParameters.put("DOCUMENTO_DE_ESTAGIO", TipoRequerimentoEnum.DOCUMENTO_DE_ESTAGIO.getTipoLocale());
            reportParameters.put("EMENTA_DE_DISCIPLINA", TipoRequerimentoEnum.EMENTA_DE_DISCIPLINA.getTipoLocale());
            JasperPrint jrp;
            try {
                jrp = JasperFillManager.fillReport(reportPath, reportParameters, dataSource);
                InputStream inputStream = new ByteArrayInputStream(JasperExportManager.exportReportToPdf(jrp));
                DataSource ds = new ByteArrayDataSource(inputStream, "application/pdf");
                email.attach(ds, "reqweb.pdf", LocaleBean.getDefaultMessageBundle().getString("relatorioDiario"));
                email.setHostName(configDao.getValue("mail.HostName"));
                email.setSmtpPort(Integer.parseInt(configDao.getValue("mail.smtpPort")));
                
                String mailSender = configDao.getValue("mail.mailSender");
                String user = configDao.getValue("mail.mailUser");
                String password = configDao.getValue("mail.mailPassword");
                boolean useSSL = Boolean.parseBoolean(configDao.getValue("mail.useSSL"));

                email.setAuthenticator(new DefaultAuthenticator(user, password));
                email.setSSLOnConnect(useSSL);
                email.setFrom(mailSender);
                email.addTo(k);
                email.setSubject(LocaleBean.getDefaultMessageBundle().getString("subjectMail"));
                String message = String.format("%s\n\n%s", LocaleBean.getDefaultMessageBundle().getString("subjectMail"), content.get(k));
                email.setMsg(message);
                email.send();
            } catch (IOException | JRException | EmailException e) {
                System.out.println("erro ao gerar relatório de email");
                log.error(e);
            }
        }
    }

    private Map<String, String> messageDiscente() {
        System.out.println("messsage to: discente");
        List<Map<String, ?>> rMap = reportDao.listRequerimentoByStatusMap(RequerimentoStatusEnum.FINALIZADO);
        Map<String, String> groups = new HashMap();
        for (Map<String, ?> item : rMap) {
            String k = item.get("email").toString().trim();
            String v = String.format("%s\t%s\n",
                    LocaleBean.getDefaultMessageBundle().getString(TipoRequerimentoEnum.valueOf(item.get("requerimento").toString().trim()).getTipo()),
                    LocaleBean.getDefaultMessageBundle().getString(RequerimentoStatusEnum.valueOf(item.get("status").toString().trim()).getStatus())
            );
            if (!groups.containsKey(k)) {
                groups.put(k, v);
            } else {
                groups.put(k, String.format("%s%s", groups.get(k), v));
            }
        }
        return groups;
    }

    private Map<String, String> messageSecretaria() {
        System.out.println("messsage to: secretaria");
        Map<String, String> cursoGroups = new HashMap();
        Map<String, String> groups = new HashMap();
        List<TipoRequerimentoEnum> tipoRequerimentoList = new ArrayList();
        tipoRequerimentoList.add(TipoRequerimentoEnum.DECLARACAO_DE_MATRICULA);
        tipoRequerimentoList.add(TipoRequerimentoEnum.EXTRATO_ACADEMICO);
        tipoRequerimentoList.add(TipoRequerimentoEnum.DOCUMENTO_DE_ESTAGIO);
        tipoRequerimentoList.add(TipoRequerimentoEnum.EMENTA_DE_DISCIPLINA);
        List<RequerimentoStatusEnum> status = Arrays
                .asList(new RequerimentoStatusEnum[]{RequerimentoStatusEnum.ABERTO});
        List<Map<String, ?>> rMap = reportDao.listTotalRequerimento(null, status, tipoRequerimentoList);
        for (Map<String, ?> item : rMap) {
            String k = item.get("curso").toString().trim();
            String v = String.format("\t%s - %s\n",
                    item.get("total").toString().trim(),
                    LocaleBean.getDefaultMessageBundle().getString(TipoRequerimentoEnum.valueOf(item.get("requerimento").toString().trim()).getTipo())
            );
            if (!cursoGroups.containsKey(k)) {
                cursoGroups.put(k, v);
            } else {
                cursoGroups.put(k, String.format("%s%s", cursoGroups.get(k), v));
            }
        }
        StringBuilder message = new StringBuilder();
        for (Entry<String, String> msgGroup : cursoGroups.entrySet()) {
            message.append(msgGroup.getKey());
            message.append("\n");
            message.append(msgGroup.getValue());
        }
        for (Usuario usuario : usuarioDao.find(PerfilEnum.SECRETARIA)) {
            groups.put(usuario.getEmail(), message.toString());
        }
        return groups;
    }

    private Map<String, String> messageDocente() {
        System.out.println("messsage to: docente");
        Map<String, String> groups = new HashMap();
        return groups;
    }

    private Map<String, String> messageCoordenadorDeCurso() {
        System.out.println("messsage to: coordenador_de_curso");
        Map<String, String> cursoGroups = new HashMap();
        Map<String, String> groups = new HashMap();
        List<TipoRequerimentoEnum> tipoRequerimentoList = new ArrayList();
        tipoRequerimentoList.add(TipoRequerimentoEnum.AJUSTE_DE_MATRICULA);
        tipoRequerimentoList.add(TipoRequerimentoEnum.DECLARACAO_DE_MATRICULA);
        tipoRequerimentoList.add(TipoRequerimentoEnum.DOCUMENTO_DE_ESTAGIO);
        tipoRequerimentoList.add(TipoRequerimentoEnum.EMENTA_DE_DISCIPLINA);
        tipoRequerimentoList.add(TipoRequerimentoEnum.EXTRATO_ACADEMICO);
        tipoRequerimentoList.add(TipoRequerimentoEnum.SEGUNDA_CHAMADA_DE_PROVA);
        List<RequerimentoStatusEnum> status = Arrays.asList(new RequerimentoStatusEnum[]{
            RequerimentoStatusEnum.ABERTO,
            RequerimentoStatusEnum.EM_ANDAMENTO
        });
        List<Map<String, ?>> rMap = reportDao.listTotalRequerimento(null, status, tipoRequerimentoList);
        for (Map<String, ?> item : rMap) {
            String k = item.get("curso").toString();
            String v = String.format("\t%s - %s\n",
                    item.get("total").toString().trim(),
                    LocaleBean.getDefaultMessageBundle().getString(TipoRequerimentoEnum.valueOf(item.get("requerimento").toString().trim()).getTipo())
            );
            if (!cursoGroups.containsKey(k)) {
                cursoGroups.put(k, v);
            } else {
                cursoGroups.put(k, String.format("%s%s", cursoGroups.get(k), v));
            }
        }
        StringBuilder message = new StringBuilder();
        for (Entry<String, String> msgGroup : cursoGroups.entrySet()) {
            message.append(msgGroup.getKey());
            message.append("\n");
            message.append(msgGroup.getValue());
        }
        for (Usuario usuario : usuarioDao.find(PerfilEnum.COORDENADOR_DE_CURSO)) {
            groups.put(usuario.getEmail(), message.toString());
        }
        return groups;
    }

    private Map<String, String> messageCoordenadorDeEstagio() {
        List<Map<String, ?>> rMap = reportDao.listDocumentoDeEstagioMap(RequerimentoStatusEnum.EM_ANDAMENTO);
        Map<String, String> groups = new HashMap();
        for (Map<String, ?> item : rMap) {
            String k = item.get("email").toString();
            String tipoDeDocumento = item.get("tipodedocumento")
                    .toString().trim()
                    .equals(DocumentoDeEstagio.TipoDeDocumento.CONTRATO_DE_ESTAGIO.name())
                    ? LocaleBean.getDefaultMessageBundle().getString("contratoDeEstagio")
                    : LocaleBean.getDefaultMessageBundle().getString("relatorioDeEstagio");
            String v = String.format("%s\t%s\t%s\n",
                    item.get("matricula"),
                    item.get("discente"),
                    tipoDeDocumento
            );
            if (!groups.containsKey(k)) {
                groups.put(k, v);
            } else {
                groups.put(k, String.format("%s%s", groups.get(k), v));
            }
        }
        return groups;
    }

    @Scheduled(cron = "${mail.mailScheduler}")
    public void runTimer() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss - dd/MM/Y");
        System.out.format("scheduder%d executed at: %s\n", ++counter, dateFormat.format(Calendar.getInstance().getTime()));
        sendEmailToStaff(messageCoordenadorDeCurso());
        sendEmailToStaff(messageCoordenadorDeEstagio());
        sendEmailToStaff(messageSecretaria());
        //sendEmail(messageDiscente());
    }
}
