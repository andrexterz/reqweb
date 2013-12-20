package br.ufg.reqweb.components;

import br.ufg.reqweb.auth.Login;
import br.ufg.reqweb.dao.UsuarioDao;
import br.ufg.reqweb.model.Perfil;
import br.ufg.reqweb.model.PerfilEnum;
import br.ufg.reqweb.model.Usuario;
import java.io.Serializable;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.primefaces.event.SelectEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 * @author andre
 */
@Component
@Scope(value = "session")
public class UsuarioBean implements Serializable {
    
    @Autowired
    UsuarioDao usuarioDao;
    
    public static final String ADICIONA = "a";
    public static final String EDITA = "e";

    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(UsuarioBean.class);
    private String login;
    private String senha;
    private String grupo;
    private String matricula;
    private String nome;
    private String email;
    private PerfilEnum perfil;
    private Login objLogin;
    private String operation;
    private Usuario usuario;
    private Usuario itemSelecionado;
    private String termoBusca;

    private final ResourceBundle messages = ResourceBundle.getBundle(
            "locale.messages",
            FacesContext.getCurrentInstance().getViewRoot().getLocale());    

    public UsuarioBean() {
        usuario = new Usuario();        
        objLogin = null;
        operation = null;
        termoBusca = "";
    }

    @RequestMapping(value = "login", method = RequestMethod.POST)
    public String authLogin() {
        FacesContext context = FacesContext.getCurrentInstance();

        setGrupo(perfil.getGrupo());

        objLogin = Login.autenticar(login, senha, grupo);

        
        if (objLogin != null) {
            log.info(String.format("usuario %s efetuou login", login));
            return home();
        } else {
            log.error("erro de autenticação");
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro de autenticação", "");
            context.addMessage("loginError", msg);
            return null;
        }
    }
    
    public String homeDir() {
        String homeDir = String.format("/views/secure/%s", perfil.toString()).toLowerCase();
        return homeDir;        
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
    
    public String importaUsuarios() {
        List<Login> infoUsuarios = objLogin.scanLdap();
        for (Login infoUsuario:infoUsuarios) {
            Usuario usr = new Usuario();
            usr.setLogin(infoUsuario.getUsuario());
            usr.setNome(infoUsuario.getNome());
            usr.setEmail(infoUsuario.getEmail());
            for (PerfilEnum pEnum:PerfilEnum.values()) {
                if (pEnum.getGrupo().equals(infoUsuario.getGrupo())) {
                    System.out.println("Perfil: " + pEnum.toString());
                    System.out.println("Grupo.: " + infoUsuario.getGrupo());
                    Perfil p = new Perfil();
                    p.setCurso(Integer.toString(new Random().nextInt()));
                    p.setPerfil(pEnum);
                    usr.adicionaPerfil(p);
                    break;
                }
            }
            usuarioDao.adicionar(usr);
        }
        return listaUsuarios();
    }
    
    public void novoUsuario(ActionEvent event) {
        setOperation(ADICIONA);
        usuario = new Usuario();
    }
    
    public void editaUsuario(ActionEvent event) {
        if (getItemSelecionado() == null) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "info", messages.getString("itemSelecionar"));
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } else {
            setOperation(EDITA);
            usuario = getItemSelecionado();
        }        
    }
    
    public String excluiUsuario() {
        FacesMessage msg;
        if (getItemSelecionado() == null) {
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "info", messages.getString("itemSelecionar"));
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return null;
        } else {
            usuarioDao.excluir(itemSelecionado);
            itemSelecionado = null;
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "info", messages.getString("dadosExcluidos"));
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return listaUsuarios();
        }        
    }
    
    public String salvaUsuario() {
        //implementar
        return listaUsuarios();
    }
    
    public String listaUsuarios() {
        return "usuarios";
    }
    
    public void selecionaItem(SelectEvent event) {
        itemSelecionado = (Usuario) event.getObject();
    }

    public boolean isAutenticado() {
        return objLogin != null;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public PerfilEnum getPerfil() {
        return perfil;
    }

    public void setPerfil(PerfilEnum perfil) {
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

    /**
     * @return the operation
     */
    public String getOperation() {
        return operation;
    }

    /**
     * @param operation the operation to set
     */
    public void setOperation(String operation) {
        this.operation = operation;
    }

    /**
     * @return the usuario
     */
    public Usuario getUsuario() {
        return usuario;
    }

    /**
     * @param usuario the usuario to set
     */
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
    
    public List<Usuario> getFiltroUsuarios() {
        if (termoBusca.equals("")) {
            return usuarioDao.listar();
        }
        else {
            return usuarioDao.procurar(termoBusca);
        }
    }
    
    /**
     * tests if itemSelecionado
     * @return 
     */
    public boolean isSelecionado() {
        return itemSelecionado != null;
    }

    /**
     * @return the itemSelecionado
     */
    public Usuario getItemSelecionado() {
        return itemSelecionado;
    }

    /**
     * @param itemSelecionado the itemSelecionado to set
     */
    public void setItemSelecionado(Usuario itemSelecionado) {
        this.itemSelecionado = itemSelecionado;
    }

    /**
     * @return the termoBusca
     */
    public String getTermoBusca() {
        return termoBusca;
    }

    /**
     * @param termoBusca the termoBusca to set
     */
    public void setTermoBusca(String termoBusca) {
        this.termoBusca = termoBusca;
    }
}
