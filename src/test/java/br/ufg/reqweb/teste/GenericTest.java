/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufg.reqweb.teste;

import br.ufg.reqweb.model.Curso;
import br.ufg.reqweb.model.Disciplina;
import br.ufg.reqweb.model.Perfil;
import br.ufg.reqweb.model.PerfilEnum;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
public class GenericTest {
    
    public GenericTest() {
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
        List<Object> objList = new ArrayList<>();
        Disciplina d1 = new Disciplina();
        d1.setId(1L);
        objList.add(d1);
        Disciplina d2 = new Disciplina();
        d2.setId(2L);
        objList.add(d2);
        System.out.println("must be true.: " + (objList.get(0) == d1));
        System.out.println("must be true.: " + (objList.get(1) == d2));
        System.out.println("must be false: " + (objList.get(0) == d2));
        System.out.println("must be false: " + (objList.get(1) == d1));
        System.out.println("must be true.: " + objList.get(0).equals(d1));
        System.out.println("must be true.: " + objList.get(1).equals(d2));
        System.out.println("must be false: " + objList.get(0).equals(d2));
        System.out.println("must be false: " + objList.get(1).equals(d1));
        System.out.println("hashCode obj1: " + objList.get(0).hashCode());
        System.out.println("hashCode obj2: " + objList.get(1).hashCode());
        
        
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
