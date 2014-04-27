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
        Set<ItemRequerimento> items = new HashSet<>();
        for (long i=0;i<10;i++) {
            ItemRequerimento item = new DeclaracaoDeMatricula();
            item.setId(i);
            items.add(item);
            System.out.println("lenght " + items.size());
        }
    }
}
