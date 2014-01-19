/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufg.reqweb.teste;

import br.ufg.reqweb.util.CSVParser;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author andre
 */
public class CSVParserTest {
    
    public CSVParserTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    
    @Test
    public void testParseFile() throws FileNotFoundException {
        InputStream inp = new FileInputStream("G:/espweb/x.tcc/turmas.csv");
        List<String[]> lines = CSVParser.parse(inp);
        for (String[] tokens:lines) {
            for (String token:tokens) {
                System.out.print("column: " + token + "\t");
            }
            System.out.println("");
        }
        assertFalse(lines.isEmpty());
    }
}
