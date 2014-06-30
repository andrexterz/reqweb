/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.reqweb.components;

import br.ufg.reqweb.dao.UsuarioDao;
import br.ufg.reqweb.model.PerfilEnum;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.faces.context.FacesContext;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
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
    private SessionFactory sessionFactory;
    
    @Autowired
    private UsuarioDao usuarioDao;

    public StreamedContent getUsuariosAsPDF(PerfilEnum perfilTipo) {
        DefaultStreamedContent content = new DefaultStreamedContent();
        try {
            String reportPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/reports/usuarios.jasper");
            JRBeanCollectionDataSource beanDataSource = new JRBeanCollectionDataSource( usuarioDao.find(perfilTipo));
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
    public StreamedContent getDocentesAsPDF() {
        return getUsuariosAsPDF(PerfilEnum.DOCENTE);
    }

    public StreamedContent getDiscentesAsPDF() {
        return getUsuariosAsPDF(PerfilEnum.DISCENTE);
    }
    
    @Transactional(readOnly= true)
    public StreamedContent getIndicePrioridade() {
        DefaultStreamedContent content = new DefaultStreamedContent();
        try {
            String reportPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/reports/usuarios_ip.jasper");
            Map reportParameters = new HashMap();
            Session session = this.sessionFactory.getCurrentSession();
            reportParameters.put("HIBERNATE_SESSION", session);
            reportParameters.put("TITULO", LocaleBean.getMessageBundle().getString("indicePrioridade"));
            JasperPrint jrp = JasperFillManager.fillReport(reportPath, reportParameters);
            InputStream inputStream = new ByteArrayInputStream(JasperExportManager.exportReportToPdf(jrp));
            content.setName("reqweb_usuarios_ip.pdf");
            content.setContentType("application/pdf");
            content.setStream(inputStream);

        } catch (JRException e) {
            System.out.println("error: " + e.getMessage());
        }
        return content;
        
    }
}
