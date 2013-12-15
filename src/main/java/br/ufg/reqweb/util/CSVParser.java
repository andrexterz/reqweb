/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.reqweb.util;

import br.ufg.reqweb.components.ArquivoBean;
import br.ufg.reqweb.dao.PeriodoDao;
import br.ufg.reqweb.model.Periodo;
import br.ufg.reqweb.model.Semestre;
import br.ufg.reqweb.model.Turma;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 *
 * @author André
 */
public class CSVParser {

    /**
     * @param inputData
     * @return
     */
    public static List parse(InputStream inputData) {
        List header;
        List data = new ArrayList<>();
        BufferedReader bReader = null;
        try {
            String line;
            bReader = new BufferedReader(new InputStreamReader(inputData));
            while ((line = bReader.readLine()) != null) {
                Pattern patt = Pattern.compile(",|;|\t");
                String[] row;
                row = patt.split(line);
                for (int i = 0; i < row.length; i++) {
                    row[i] = row[i].trim();
                }
                data.add(row);
            }
        } catch (IOException ex) {
            Logger.getLogger(ArquivoBean.class).error(ex.getMessage());
        } finally {
            try {
                if (bReader != null) {
                    bReader.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(ArquivoBean.class).error(ex.getMessage());
            }
        }
        return data;
    }
}
