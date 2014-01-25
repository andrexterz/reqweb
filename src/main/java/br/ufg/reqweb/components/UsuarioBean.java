package br.ufg.reqweb.components;

import br.ufg.reqweb.auth.Login;
import br.ufg.reqweb.dao.CursoDao;
import br.ufg.reqweb.dao.UsuarioDao;
import br.ufg.reqweb.model.Curso;
import br.ufg.reqweb.model.Perfil;
import br.ufg.reqweb.model.PerfilEnum;
import br.ufg.reqweb.model.Usuario;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
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
    private UsuarioDao usuarioDao;

    @Autowired
    private CursoDao cursoDao;

    private final LazyDataModel<Usuario> usuarios;
    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(UsuarioBean.class);
    private String login;
    private String senha;
    private String grupo;
    private int progress;
    private volatile boolean stopImportaUsuarios;
    private Thread tImportJob;
    private PerfilEnum perfil;
    private Login objLogin;
    private Usuario usuario;
    private Usuario itemSelecionado;
    private String termoBusca;

    public UsuarioBean() {
        usuario = new Usuario();
        objLogin = null;
        tImportJob = null;
        termoBusca = "";
        usuarios = new LazyDataModel<Usuario>() {
            private static final long serialVersionUID = 1L;

            @Override
            public List<Usuario> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
                setPageSize(pageSize);
                List<Usuario> usuarioList;
                if (termoBusca.equals("")) {
                    usuarioList = usuarioDao.find(first, pageSize);
                    setRowCount(usuarioDao.count());
                } else {
                    usuarioList = usuarioDao.find(termoBusca);
                    setRowCount(usuarioList.size());
                }
                return usuarioList;
            }
        };
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

    public void setupImportUsuarios(ActionEvent event) {
        progress = 0;
        stopImportaUsuarios = true;
    }

    public void importaUsuarios() {
        final List<Usuario> usrList = new ArrayList<>();
        stopImportaUsuarios = false;
        tImportJob = new Thread() {
            @Override
            public void run() {
                List<Login> infoUsuarios = objLogin.scanLdap();
                int length = infoUsuarios.size();
                int counter = 0;
                for (Login infoUsuario : infoUsuarios) {
                    if (!stopImportaUsuarios) {
                        counter++;
                        progress = (int) ((counter / (float) length) * 100);
                        Usuario usr = new Usuario();
                        usr.setLogin(infoUsuario.getUsuario());
                        usr.setNome(infoUsuario.getNome());
                        usr.setEmail(infoUsuario.getEmail());
                        for (PerfilEnum pEnum : PerfilEnum.values()) {
                            if (pEnum.getGrupo().equals(infoUsuario.getGrupo())) {
                                Perfil p = new Perfil();
                                //corrigir: atribuir curso correto aos perfis do tipo "discente" pela sigla
                                Curso curso = cursoDao.findById(11L);
                                p.setCurso(curso);
                                p.setPerfil(pEnum);
                                usr.adicionaPerfil(p);
                                break;
                            }
                            usrList.add(usr);
                        }
                    } else {
                        usrList.removeAll(usrList);
                        break;
                    }
                }
                usuarioDao.adicionar(usrList);
            }
        };
        tImportJob.start();
    }

    public void cancelImpUsuarios(ActionEvent event) {
        setupImportUsuarios(event);
        try {
            Thread.sleep(2000);
            FacesContext context = FacesContext.getCurrentInstance();
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, LocaleBean.getMessageBundle().getString("operacaoCancelada"), "");
            context.addMessage(null, msg);

        } catch (NullPointerException | InterruptedException e) {
            log.error("no thread to cancel");
        }
    }

    public void handleCompletImpUsuarios() {
        stopImportaUsuarios = true;
        FacesContext context = FacesContext.getCurrentInstance();
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, LocaleBean.getMessageBundle().getString("dadosSalvos"), "");
        context.addMessage(null, msg);
    }

    public void editaUsuario(ActionEvent event) {
        if (getItemSelecionado() == null) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "info", LocaleBean.getMessageBundle().getString("itemSelecionar"));
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } else {
            usuario = getItemSelecionado();
        }
    }

    public String excluiUsuario() {
        FacesMessage msg;
        if (getItemSelecionado() == null) {
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "info", LocaleBean.getMessageBundle().getString("itemSelecionar"));
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return null;
        } else {
            usuarioDao.excluir(itemSelecionado);
            itemSelecionado = null;
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "info", LocaleBean.getMessageBundle().getString("dadosExcluidos"));
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

    public List<Perfil> getPerfis() {
        return usuarioDao.findById(itemSelecionado.getId()).getPerfilList();
    }

    public PerfilEnum getPerfil() {
        return perfil;
    }

    public void setPerfil(PerfilEnum perfil) {
        this.perfil = perfil;
    }

    public String getGrupo() {
        return objLogin.getGrupo();
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public boolean getStopImportaUsuarios() {
        return stopImportaUsuarios;
    }

    public String getMatricula() {
        return objLogin.getMatricula();
    }

    public String getNome() {
        return objLogin.getNome();
    }

    public String getEmail() {
        return objLogin.getEmail();
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
            return usuarioDao.findAll();
        } else {
            return usuarioDao.find(termoBusca);
        }
    }

    public LazyDataModel<Usuario> getUsuarios() {
        return usuarios;
    }

    /**
     * tests if itemSelecionado
     *
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
