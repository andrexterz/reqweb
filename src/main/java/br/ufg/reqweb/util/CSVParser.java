/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.reqweb.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 *
 * @author André
 */
public class CSVParser {

    /**
     * @param inputData
     * @return List<String[]>
     */
    public static List<String[]> parse(InputStream inputData) {
        List<String[]> data = new ArrayList<>();
        BufferedReader bReader = null;
        try {
            String line;
            bReader = new BufferedReader(new InputStreamReader(inputData, "UTF-8"));
            while ((line = bReader.readLine()) != null) {
                Pattern patt = Pattern.compile(",|;|\t");
                String[] row;
                row = patt.split(line);
                for (int i = 0; i < row.length; i++) {
                    row[i] = row[i].trim();
                }
                data.add(row);
            }
        } catch (IOException e) {
            System.out.println(e);
        } finally {
            try {
                if (bReader != null) {
                    bReader.close();
                }
            } catch (IOException e) {
                System.out.println(e);
            }
        }
        return data;
    }
}
