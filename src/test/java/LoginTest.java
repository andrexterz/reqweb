
import br.ufg.reqweb.auth.Login;
import java.util.List;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author André
 */
public class LoginTest {
    
    public static void main (String[] args) {
        Login login = Login.autenticar("amanda", "Aed5ahm4", "101");
        
        List<Login> users = login.scanLdap();
        
        for (Login u:users) {
            System.out.println(
                      "nome.....:" + u.getNome() + "\n"
                    + "username.:" + u.getUsuario() +      "\n"
                    + "matricula:" + u.getMatricula() + "\n"
            );
        }
    }
    
}
