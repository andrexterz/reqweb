/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufg.reqweb.teste;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
public class RegexpTest {
    
    public RegexpTest() {
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

    @Test
    public void testExpressions() {
        assertTrue(true == true);
//        Pattern pat = Pattern.compile("\\D+(?=(\\d+))");
//        String [] names = {"si10178", "cc10023", "amanda", "msc10426","afonso", "es10311"};
//        for (String s:names) {
//            Matcher m = pat.matcher(s);
//            if (m.find()) {
//                String sigla = m.group();
//                System.out.println("match: " + sigla.toUpperCase());
//            }
//        }
    }
}
