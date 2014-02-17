/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufg.reqweb.teste;

import java.io.FileNotFoundException;
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
        assertTrue(true == true);
//        InputStream inp;
//        try {
//            inp = new FileInputStream("G:/espweb/x.tcc/turmas.csv");
//        } catch (FileNotFoundException e) {
//            inp = new FileInputStream("/media/ANDRELUIZDG/espweb/x.tcc/arquivos_importação_reqweb/turmas.csv");
//        }
//        
//        List<String[]> lines = CSVParser.parse(inp);
//        for (String[] tokens:lines) {
//            for (String token:tokens) {
//                System.out.print("column: " + token + "\t");
//            }
//            System.out.println("");
//        }
//        assertFalse(lines.isEmpty());
    }
}
