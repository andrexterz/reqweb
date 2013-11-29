package br.ufg.reqweb.components;

import br.ufg.reqweb.auth.Login;
import br.ufg.reqweb.model.Perfil;
import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 * @author andre
 */
@Component(value = "usuarioBean")
@Scope(value = "session")
public class UsuarioBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(UsuarioBean.class);
    private String usuario;
    private String senha;
    private String grupo;
    private String matricula;
    private String nome;
    private String email;
    private Perfil perfil;
    private Login objLogin;

    public UsuarioBean() {
        objLogin = null;
    }

    @RequestMapping(value = "login", method = RequestMethod.POST)
    public String authLogin() {
        FacesContext context = FacesContext.getCurrentInstance();

        setGrupo(perfil.getGrupo());

        objLogin = Login.autenticar(usuario, senha, grupo);

        
        if (objLogin != null) {
            log.info(String.format("usuario %s efetuou login", usuario));
            return home();
        } else {
            log.error("erro de autenticação");
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro de autenticação", "");
            context.addMessage("loginError", msg);
            return null;
        }
    }

    public String home() {
        String home = String.format("/views/secure/%s/%s?faces-redirect=true", perfil.toString(), perfil.toString()).toLowerCase();
        return home;
    }

    public String authLogout() {
        objLogin = null;
        System.out.println("usuario efetuou logout");
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        session.invalidate();
        return "/views/login?faces-redirect=true";
    }

    public boolean isAutenticado() {
        if (objLogin != null) {
            return true;
        } else {
            return false;
        }
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Perfil getPerfil() {
        return perfil;
    }

    public void setPerfil(Perfil perfil) {
        this.perfil = perfil;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
