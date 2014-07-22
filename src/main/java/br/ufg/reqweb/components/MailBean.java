/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.reqweb.components;

import br.ufg.reqweb.dao.ReportDao;
import br.ufg.reqweb.dao.UsuarioDao;
import br.ufg.reqweb.model.DocumentoDeEstagio;
import br.ufg.reqweb.model.PerfilEnum;
import br.ufg.reqweb.model.RequerimentoStatusEnum;
import br.ufg.reqweb.model.TipoRequerimentoEnum;
import br.ufg.reqweb.model.Usuario;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
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
    ConfigBean configBean;

    @Autowired
    ReportDao reportDao;

    @Autowired
    UsuarioDao usuarioDao;

    Logger log = Logger.getLogger(MailBean.class);

    private void sendEmail(Map<String, String> content) throws EmailException {
        for (String k : content.keySet()) {
            Email email = new SimpleEmail();
            email.setHostName(configBean.getMailHost());
            email.setSmtpPort(configBean.getSmtpPort());
            email.setAuthenticator(new DefaultAuthenticator(configBean.getMailUser(), configBean.getMailPassword()));
            email.setSSLOnConnect(configBean.isUseSSL());
            email.setFrom(configBean.getMailSender());
            email.addTo(k);
            email.setSubject(LocaleBean.getDefaultMessageBundle().getString("subjectMail"));
            String message = String.format("%s\n\n%s", LocaleBean.getDefaultMessageBundle().getString("subjectMail"), content.get(k));
            email.setMsg(message);
            email.send();
        }
    }
    
    private Map<String, String> messageDiscente() {
        List<Map<String, ?>> rMap = reportDao.listRequerimentoByStatusMap(RequerimentoStatusEnum.FINALIZADO);
        Map<String, String> groups = new HashMap();
        for (Map<String, ?> item : rMap) {
            String k = item.get("email").toString();
            String v = String.format("%s\t%s\n",
                    LocaleBean.getDefaultMessageBundle().getString(TipoRequerimentoEnum.valueOf(item.get("requerimento").toString()).getTipo()),
                    LocaleBean.getDefaultMessageBundle().getString(RequerimentoStatusEnum.valueOf(item.get("status").toString()).getStatus())
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
        Map<String, String> cursoGroups = new HashMap();
        Map<String, String> groups = new HashMap();
        List<TipoRequerimentoEnum> tipoRequerimentoList = new ArrayList();
        tipoRequerimentoList.add(TipoRequerimentoEnum.DECLARACAO_DE_MATRICULA);
        tipoRequerimentoList.add(TipoRequerimentoEnum.EXTRATO_ACADEMICO);
        tipoRequerimentoList.add(TipoRequerimentoEnum.DOCUMENTO_DE_ESTAGIO);
        tipoRequerimentoList.add(TipoRequerimentoEnum.EMENTA_DE_DISCIPLINA);
        List<RequerimentoStatusEnum> status = Arrays
                .asList(new RequerimentoStatusEnum[] {RequerimentoStatusEnum.ABERTO});
        List<Map<String, ?>> rMap = reportDao.listTotalRequerimento(null, status, tipoRequerimentoList);
        for (Map<String, ?> item: rMap) {
            String k = item.get("curso").toString();
            String v = String.format("\t%s\t%s\n",
                    LocaleBean.getDefaultMessageBundle().getString(TipoRequerimentoEnum.valueOf(item.get("requerimento").toString()).getTipo()),
                    item.get("total").toString()
            );
            if (!cursoGroups.containsKey(k)) {
                cursoGroups.put(k, v);
            } else {
                cursoGroups.put(k, String.format("%s%s", cursoGroups.get(k), v));
            }
        }
        StringBuilder message = new StringBuilder();
        for (Entry<String,String> msgGroup: cursoGroups.entrySet()) {
            message.append(msgGroup.getKey());
            message.append("\n");
            message.append(msgGroup.getValue());
        }
        for (Usuario usuario: usuarioDao.find(PerfilEnum.SECRETARIA)) {
            groups.put(usuario.getEmail(), message.toString());
        }
        return groups;
    }

    private Map<String, String> messageDocente() {
        Map<String, String> groups = new HashMap();
        return groups;
    }
    
    private Map<String, String> messageCoordenadorDeCurso() {
        Map<String, String> cursoGroups = new HashMap();
        Map<String, String> groups = new HashMap();
        List<TipoRequerimentoEnum> tipoRequerimentoList = new ArrayList();
        tipoRequerimentoList.add(TipoRequerimentoEnum.AJUSTE_DE_MATRICULA);
        tipoRequerimentoList.add(TipoRequerimentoEnum.DECLARACAO_DE_MATRICULA);
        tipoRequerimentoList.add(TipoRequerimentoEnum.DOCUMENTO_DE_ESTAGIO);
        tipoRequerimentoList.add(TipoRequerimentoEnum.EMENTA_DE_DISCIPLINA);
        tipoRequerimentoList.add(TipoRequerimentoEnum.EXTRATO_ACADEMICO);
        tipoRequerimentoList.add(TipoRequerimentoEnum.SEGUNDA_CHAMADA_DE_PROVA);
        List<RequerimentoStatusEnum> status = Arrays.asList(new RequerimentoStatusEnum[] {
            RequerimentoStatusEnum.ABERTO,
            RequerimentoStatusEnum.EM_ANDAMENTO
        });
        List<Map<String, ?>> rMap = reportDao.listTotalRequerimento(null, status, tipoRequerimentoList);
        for (Map<String, ?> item: rMap) {
            String k = item.get("curso").toString();
            String v = String.format("\t%s\t%s\n",
                    LocaleBean.getDefaultMessageBundle().getString(TipoRequerimentoEnum.valueOf(item.get("requerimento").toString()).getTipo()),
                    item.get("total").toString()
            );
            if (!cursoGroups.containsKey(k)) {
                cursoGroups.put(k, v);
            } else {
                cursoGroups.put(k, String.format("%s%s", cursoGroups.get(k), v));
            }
        }
        StringBuilder message = new StringBuilder();
        for (Entry<String,String> msgGroup: cursoGroups.entrySet()) {
            message.append(msgGroup.getKey());
            message.append("\n");
            message.append(msgGroup.getValue());
        }
        for (Usuario usuario: usuarioDao.find(PerfilEnum.SECRETARIA)) {
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
                    .toString()
                    .equals(DocumentoDeEstagio.TipoDeDocumento.CONTRATO_DE_ESTAGIO.name()) ?
                    LocaleBean.getDefaultMessageBundle().getString("contratoDeEstagio") :
                    LocaleBean.getDefaultMessageBundle().getString("relatorioDeEstagio");
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
        try {
            sendEmail(messageCoordenadorDeEstagio());
            sendEmail(messageSecretaria());
            sendEmail(messageDiscente());
        } catch (EmailException e) {
            log.error(e);
        }
    }
}
