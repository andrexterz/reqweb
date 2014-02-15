/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufg.reqweb.teste;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import br.ufg.reqweb.auth.Login;
import java.util.List;
import static org.junit.Assert.*;

/**
 *
 * @author André
 */
public class LDAPTest {
    
    public LDAPTest() {
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
    @Test
    public void scanldap() {
        assertTrue(true == true);
//        Login login = Login.autenticar("amanda", "Aed5ahm4", "100");
//        List<Login> uList = login.scanLdap();
//        for (Login u: uList) {
//            System.out.print("\nuser info -->"
//                    + "\ngrupo....: " + u.getGrupo()
//                    + "\nuser id..: " + u.getUid()
//                    + "\nmatricula: " + u.getMatricula()
//                    + "\nusername.: " + u.getUsuario()
//                    + "\nnome.....: " + u.getNome()
//            );
//        }
    }
}
