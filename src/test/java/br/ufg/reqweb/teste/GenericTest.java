/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufg.reqweb.teste;

import br.ufg.reqweb.model.DeclaracaoDeMatricula;
import br.ufg.reqweb.model.ItemRequerimento;
import java.util.HashSet;
import java.util.Set;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

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
        boolean T, R, D;
        T = false;
        R = true;
        D = false;
        if (T == true && R == false && D == false) {
            System.out.println("search by termoBusca");
        } else if (T == false && R == true && D == false) {
            System.out.println("search by Requerimento");
        } else if (T == false && R == false && D == true) {
            System.out.println("search by Data");
        } else {
            System.out.println("search all");
        }
    }
}
