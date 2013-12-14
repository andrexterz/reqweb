/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.reqweb.util;

import br.ufg.reqweb.components.ArquivoBean;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;

/**
 *
 * @author André
 */
public class CSVParser {

    /**
     * 
     *teste de manipulação de strings
     * depois construir um método para cada tipo de arquivo:
     * parseTurma, parseDisciplina, parseIndicePrioridade
     * 
     * @param inputData
     * @param hasHeader
     * @return
     */
    public static List parse(InputStream inputData, boolean hasHeader) {
        List header;
        List data = new ArrayList<>();
        BufferedReader bReader = null;
        try {
            String line;
            bReader = new BufferedReader(new InputStreamReader(inputData));
            
            if (hasHeader == true) {
                bReader.readLine();
            }
            while ((line = bReader.readLine()) != null) {
                Pattern patt = Pattern.compile(",|;|\t");
                String[] row;
                row = patt.split(line);
                data.add(row);
                for (int i = 0; i < row.length;i++) {
                    if (i != row.length-1) {
                        System.out.print(row[i] + ", ");
                    } else {
                        System.out.print(row[i] + "\n");
                    }
                    
                }
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
    
    public static List parseTurma(InputStream inputData) {
        List header = new ArrayList<>();
        Pattern patt = Pattern.compile(",|;|\t");        
        List data = new ArrayList<>();
        BufferedReader bReader = null;
        try {
            String line;
            bReader = new BufferedReader(new InputStreamReader(inputData));
            header.addAll(Arrays.asList(patt.split(bReader.readLine())));
            
            for (Object c: header) {
                System.out.println("column: " + c);
                
            }
            while ((line = bReader.readLine()) != null) {
                String[] row;
                row = patt.split(line);
                data.add(row);
                for (int i = 0; i < row.length;i++) {
                    if (i != row.length-1) {
                        System.out.print(row[i] + ", ");
                    } else {
                        System.out.print(row[i] + "\n");
                    }
                }
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
