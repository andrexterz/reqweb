/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.reqweb.components;

import br.ufg.reqweb.dao.ConfigDao;
import br.ufg.reqweb.dao.ReportDao;
import br.ufg.reqweb.dao.UsuarioDao;
import br.ufg.reqweb.model.Curso;
import br.ufg.reqweb.model.PerfilEnum;
import br.ufg.reqweb.model.RequerimentoStatusEnum;
import br.ufg.reqweb.model.TipoRequerimentoEnum;
import br.ufg.reqweb.model.Usuario;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ScheduledFuture;
import javax.activation.DataSource;
import javax.annotation.PostConstruct;
import javax.mail.util.ByteArrayDataSource;
import javax.servlet.ServletContext;
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
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

/**
 *
 * @author André
 */
@Component
@Scope(value = "singleton")
public class MailBean implements Serializable {

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    ServletContext context;

    @Autowired
    ReportDao reportDao;

    @Autowired
    UsuarioDao usuarioDao;

    @Autowired
    ConfigDao configDao;
    
    ThreadPoolTaskScheduler scheduler;
    private ScheduledFuture<Object> scheduleTask;

    Logger log;
    

    public MailBean() {
        log = Logger.getLogger(this.getClass());
    }
    
    @PostConstruct
    public void scheduleJob() {
        scheduler = (ThreadPoolTaskScheduler) applicationContext.getBean("reqwebTimer");
        Runnable job = new Runnable() {
            @Override
            public void run() {
                sendEmailtoStaff();
            }
        };
        if (scheduleTask != null) {
            scheduleTask.cancel(false);
        }
        System.out.println("cron expression: " + configDao.getValue("mail.mailScheduler"));
        CronTrigger cron = new CronTrigger(configDao.getValue("mail.mailScheduler"));
        scheduleTask = scheduler.schedule(job, cron);
    }

    private Map<String, String> sendMailToDiscente() {
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

    private void processEmailToStaff(Map<String, List<Map<String, ?>>> messageMap, String reportPath) {
        for (Entry message : messageMap.entrySet()) {
            JRMapCollectionDataSource dataSource;
            List<TipoRequerimentoEnum> tipoRequerimentoList;
            List<RequerimentoStatusEnum> status;
            Map reportParameters = new HashMap();
            tipoRequerimentoList = Arrays.asList(TipoRequerimentoEnum.values());
            status = Arrays.asList(new RequerimentoStatusEnum[]{RequerimentoStatusEnum.ABERTO, RequerimentoStatusEnum.EM_ANDAMENTO});
            for (RequerimentoStatusEnum statusEnum : RequerimentoStatusEnum.values()) {
                reportParameters.put(statusEnum.name(), LocaleBean.getDefaultMessageBundle().getString(statusEnum.getStatus()));
            }
            System.out.println("path: " + reportPath);
            dataSource = new JRMapCollectionDataSource((List<Map<String, ?>>) message.getValue());
            reportParameters.put("TITULO", LocaleBean.getDefaultMessageBundle().getString("requerimentos"));
            reportParameters.put("MATRICULA", LocaleBean.getDefaultMessageBundle().getString("usuarioMatricula"));
            reportParameters.put("NOME", LocaleBean.getDefaultMessageBundle().getString("discente"));
            reportParameters.put("DOCENTE", LocaleBean.getDefaultMessageBundle().getString("docente"));
            reportParameters.put("DISCIPLINA", LocaleBean.getDefaultMessageBundle().getString("disciplina"));
            reportParameters.put("TURMA", LocaleBean.getDefaultMessageBundle().getString("turma"));
            reportParameters.put("DATA_PROVA", LocaleBean.getDefaultMessageBundle().getString("dataProvaA"));
            reportParameters.put("DATA_REQUERIMENTO", LocaleBean.getDefaultMessageBundle().getString("dataCriacao"));
            reportParameters.put("CURSO", LocaleBean.getDefaultMessageBundle().getString("curso"));
            reportParameters.put("REQUERIMENTO", LocaleBean.getDefaultMessageBundle().getString("requerimento"));
            reportParameters.put("STATUS", LocaleBean.getDefaultMessageBundle().getString("status"));
            reportParameters.put("TOTAL", LocaleBean.getDefaultMessageBundle().getString("total"));
            for (TipoRequerimentoEnum tipoRequerimento : tipoRequerimentoList) {
                reportParameters.put(tipoRequerimento.name(), tipoRequerimento.getTipoLocale());
            }
            JasperPrint jrp;
            try {
                jrp = JasperFillManager.fillReport(context.getRealPath(reportPath), reportParameters, dataSource);
                InputStream inputStream = new ByteArrayInputStream(JasperExportManager.exportReportToPdf(jrp));
                MultiPartEmail email = new MultiPartEmail();
                DataSource ds = new ByteArrayDataSource(inputStream, "application/pdf");
                email.attach(ds, "reqweb.pdf", LocaleBean.getDefaultMessageBundle().getString("relatorioDiario"));
                email.setHostName(configDao.getValue("mail.mailHost"));
                email.setSmtpPort(Integer.parseInt(configDao.getValue("mail.smtpPort")));

                String mailSender = configDao.getValue("mail.mailSender");
                String user = configDao.getValue("mail.mailUser");
                String password = configDao.getValue("mail.mailPassword");
                boolean useSSL = Boolean.parseBoolean(configDao.getValue("mail.useSSL"));

                email.setAuthenticator(new DefaultAuthenticator(user, password));
                email.setSSLOnConnect(useSSL);
                email.setFrom(mailSender);
                email.addTo(message.getKey().toString());
                email.setSubject(LocaleBean.getDefaultMessageBundle().getString("subjectMail"));
                String messageBody = String.format("%s\n\n%s",
                        LocaleBean.getDefaultMessageBundle().getString("subjectMail"),
                        LocaleBean.getDefaultMessageBundle().getString("messageMail")
                );
                email.setMsg(messageBody);
                email.send();
            } catch (IOException | NullPointerException | EmailException | JRException e) {
                System.out.println("erro ao enviar email");
                log.error(e);
            }
        }
    }

    private Map<String, List<Map<String, ?>>> messageCoordenadorDeCurso() {
        System.out.println("messsage to: coordenador_de_curso");
        Map<String, List<Map<String, ?>>> groups = new HashMap();
        List<TipoRequerimentoEnum> tipoRequerimentoList = Arrays.asList(TipoRequerimentoEnum.values());
        List<RequerimentoStatusEnum> status = Arrays.asList(new RequerimentoStatusEnum[]{
            RequerimentoStatusEnum.ABERTO,
            RequerimentoStatusEnum.EM_ANDAMENTO
        });
        List<Map<String, ?>> rMap = reportDao.listTotalRequerimento(PerfilEnum.COORDENADOR_DE_CURSO, null, status, tipoRequerimentoList);
        for (Map<String, ?> item : rMap) {
            String k = String.valueOf(item.get("email"));
            if (!groups.containsKey(k)) {
                List<Map<String, ?>> itemList = new ArrayList<>();
                itemList.add(item);
                groups.put(k, itemList);
            } else {
                groups.get(k).add(item);
            }
        }
        return groups;
    }

    private Map<String, List<Map<String, ?>>> messageCoordenadorDeEstagio() {
        System.out.println("messsage to: coordenador_de_estagio");
        Map<String, List<Map<String, ?>>> groups = new HashMap();
        List<TipoRequerimentoEnum> tipoRequerimentoList = Arrays.asList(new TipoRequerimentoEnum[]{
            TipoRequerimentoEnum.DOCUMENTO_DE_ESTAGIO
        });
        List<RequerimentoStatusEnum> status = Arrays.asList(new RequerimentoStatusEnum[]{
            RequerimentoStatusEnum.EM_ANDAMENTO
        });
        List<Map<String, ?>> rMap = reportDao.listTotalRequerimento(PerfilEnum.COORDENADOR_DE_ESTAGIO, null, status, tipoRequerimentoList);
        for (Map<String, ?> item : rMap) {
            String k = String.valueOf(item.get("email"));
            if (!groups.containsKey(k)) {
                List<Map<String, ?>> itemList = new ArrayList<>();
                itemList.add(item);
                groups.put(k, itemList);
            } else {
                groups.get(k).add(item);
            }
        }
        return groups;
    }

    private Map<String, List<Map<String, ?>>> messageSecretaria() {
        System.out.println("messsage to: secretaria");
        Map<String, List<Map<String, ?>>> groups = new HashMap();
        List<TipoRequerimentoEnum> tipoRequerimentoList = Arrays.asList(
                new TipoRequerimentoEnum[]{
                    TipoRequerimentoEnum.DECLARACAO_DE_MATRICULA,
                    TipoRequerimentoEnum.EXTRATO_ACADEMICO,
                    TipoRequerimentoEnum.DOCUMENTO_DE_ESTAGIO,
                    TipoRequerimentoEnum.EMENTA_DE_DISCIPLINA
                });
        List<RequerimentoStatusEnum> status = Arrays.asList(
                new RequerimentoStatusEnum[]{
                    RequerimentoStatusEnum.ABERTO
                });
        List<Map<String, ?>> rMap = reportDao.listTotalRequerimento(null, null, status, tipoRequerimentoList);
        for (Usuario usuario : usuarioDao.find(PerfilEnum.SECRETARIA)) {
            groups.put(usuario.getEmail(), rMap);
        }
        return groups;
    }

    private Map<String, List<Map<String, ?>>> messageDocente() {
        System.out.println("messsage to: docente");
        Map<String, List<Map<String, ?>>> groups = new HashMap();
        List<TipoRequerimentoEnum> tipoRequerimentoList = Arrays.asList(
                new TipoRequerimentoEnum[]{
                    TipoRequerimentoEnum.SEGUNDA_CHAMADA_DE_PROVA
                });
        List<RequerimentoStatusEnum> status = Arrays.asList(
                new RequerimentoStatusEnum[]{
                    RequerimentoStatusEnum.EM_ANDAMENTO
                });
        //(CAST) NULL -> para contornar ambiguidade de chamada de metodo overloaded
        List<Map<String, ?>> rMap = reportDao.listSegundaChamadaDeProvaMap(RequerimentoStatusEnum.ABERTO, (Curso) null);
        for (Map<String, ?> item : rMap) {
            String k = String.valueOf(item.get("email"));
            if (!groups.containsKey(k)) {
                List<Map<String, ?>> itemList = new ArrayList<>();
                itemList.add(item);
                groups.put(k, itemList);
            } else {
                groups.get(k).add(item);
            }
        }

        return groups;
    }

    private void sendEmailtoStaff() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss - dd/MM/Y");
        System.out.format("scheduder executed at: %s\t%s\n", dateFormat.format(Calendar.getInstance().getTime()), configDao.getValue("mail.mailScheduler"));
        /*
         processEmailToStaff(messageCoordenadorDeCurso(), "/reports/resumo.jasper");
         processEmailToStaff(messageCoordenadorDeEstagio(), "/reports/resumo.jasper");
         processEmailToStaff(messageSecretaria(), "/reports/resumo.jasper");
         processEmailToStaff(messageDocente(), "/reports/segunda_chamada_de_prova.jasper");
         */
    }
}
