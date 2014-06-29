/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.reqweb.components;

import br.ufg.reqweb.dao.UsuarioDao;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import javax.faces.context.FacesContext;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
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
    private UsuarioDao usuarioDao;

    public StreamedContent getReportUsuarios() {
        DefaultStreamedContent content = new DefaultStreamedContent();
        try {
            String reportPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/reports/usuarios.jasper");
            JRBeanCollectionDataSource beanDataSource = new JRBeanCollectionDataSource(usuarioDao.findAll());
            JasperPrint jrp = JasperFillManager.fillReport(reportPath, new HashMap(), beanDataSource);
            InputStream inputStream = new ByteArrayInputStream(JasperExportManager.exportReportToPdf(jrp));
            content.setName(jrp.getName());
            content.setContentType("application/pdf");
            content.setStream(inputStream);
            
        } catch (JRException e) {
            System.out.println("error: " + e.getMessage());
        }
        return content;
    }

}
