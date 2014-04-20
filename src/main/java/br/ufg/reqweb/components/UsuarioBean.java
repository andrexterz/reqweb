package br.ufg.reqweb.components;

import br.ufg.reqweb.util.LdapInfo;
import br.ufg.reqweb.dao.CursoDao;
import br.ufg.reqweb.dao.UsuarioDao;
import br.ufg.reqweb.model.Curso;
import br.ufg.reqweb.model.Perfil;
import br.ufg.reqweb.model.PerfilEnum;
import br.ufg.reqweb.model.Usuario;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpSession;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import org.apache.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @Autowired
    private Validator validator;
    
    @Autowired
    private ProviderManager authManager;

    private final LazyDataModel<Usuario> usuariosDataModel;
    private List<Usuario> usuarios;
    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(UsuarioBean.class);
    private String login;
    private String senha;
    private PerfilEnum perfil;
    private Usuario usuario;
    private boolean autenticado;
    private List<Perfil> perfilRemovido;
    private int progress;
    private volatile boolean stopImportaUsuarios;
    private boolean saveStatus;
    private Thread tImportJob;
    private Usuario itemSelecionado;
    private String termoBusca;

    public UsuarioBean() {
        usuarios = new ArrayList<>();
        usuario = new Usuario();
        autenticado = false;
        perfilRemovido = new ArrayList<>();
        tImportJob = null;
        termoBusca = "";
        saveStatus = false;
        usuariosDataModel = new LazyDataModel<Usuario>() {
  
            private List<Usuario> dataSource;
            
            @Override
            public List<Usuario> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
                setPageSize(pageSize);
                if (termoBusca.equals("")) {
                    dataSource = usuarioDao.find(first, pageSize);
                    setRowCount(usuarioDao.count());
                    
                } else {
                    dataSource = usuarioDao.find(termoBusca);
                    setRowCount(dataSource.size());                
                }
                if (dataSource.size() > pageSize) {
                    try {
                        return dataSource.subList(first, first + pageSize);
                    } catch (IndexOutOfBoundsException e) {
                         return dataSource.subList(first, first + (dataSource.size() % pageSize));
                    }
                    
                }
                return dataSource;
            }
        };
    }

    @RequestMapping(value = "login", method = RequestMethod.POST)
    public String authLogin() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();
            for (Perfil p : usuarioDao.findByLogin(login).getPerfilList()) {
                grantedAuthorities.add(new SimpleGrantedAuthority(p.getTipoPerfil().name()));
            }
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(login, senha, grantedAuthorities);
            Authentication auth = authManager.authenticate(authToken);
            SecurityContextHolder.getContext().setAuthentication(auth);
            autenticado = auth.isAuthenticated();
        } catch (AuthenticationException|NullPointerException e) {
            log.error("erro de autenticação - " + e.getCause());
        }
        if (autenticado) {
            log.info(String.format("usuario %s: %s efetuou login", login, perfil.getPapel()));
            return home();
        } else {
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
                List<LdapInfo> infoUsuarios = LdapInfo.scanLdap();
                int length = infoUsuarios.size();
                int counter = 0;
                Map<String, Curso> cursoMap = new HashMap();
                for (Curso c : cursoDao.findAll()) {
                    cursoMap.put(c.getSigla(), c);
                }
                for (LdapInfo infoUsuario : infoUsuarios) {
                    if (!stopImportaUsuarios) {
                        counter++;
                        progress = (int) ((counter / (float) length) * 100);
                        Usuario usr = new Usuario();
                        usr.setLogin(infoUsuario.getUsuario());
                        usr.setNome(infoUsuario.getNome());
                        usr.setEmail(infoUsuario.getEmail());
                        usr.setMatricula(infoUsuario.getMatricula());
                        for (PerfilEnum pEnum : PerfilEnum.values()) {
                            if (pEnum.getGrupo().equals(infoUsuario.getGrupo())) {
                                Perfil p = new Perfil();
                                Pattern patt = Pattern.compile("\\D+(?=(\\d+))");
                                Matcher mat = patt.matcher(usr.getLogin());
                                Curso curso;
                                if (mat.find()) {
                                    curso = cursoMap.get(mat.group().toUpperCase());//cursoDao.findBySigla(mat.group().toUpperCase());
                                } else {
                                    curso = null;
                                }
                                p.setCurso(curso);
                                p.setTipoPerfil(pEnum);
                                usr.adicionaPerfil(p);
                                break;
                            }
                            Set<ConstraintViolation<Usuario>> errors = validator.validate(usr);
                            if (errors.isEmpty()) {
                                usrList.add(usr);
                            }
                        }
                    } else {
                        usrList.clear();
                        break;
                    }
                }
                try {
                    usuarioDao.adicionar(usrList);
                    saveStatus = true;
                } catch (ConstraintViolationException e) {
                    saveStatus = false;
                }

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

    public void handleCompleteImpUsuarios() {
        stopImportaUsuarios = true;
        FacesContext context = FacesContext.getCurrentInstance();
        FacesMessage msg;
        if (saveStatus) {
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, LocaleBean.getMessageBundle().getString("dadosSalvos"), "");
        } else {
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "info", LocaleBean.getMessageBundle().getString("dadosInvalidos"));
        }
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

    public void excluiUsuario() {
        FacesMessage msg;
        if (getItemSelecionado() == null) {
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "info", LocaleBean.getMessageBundle().getString("itemSelecionar"));
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } else {
            usuarioDao.excluir(itemSelecionado);
            itemSelecionado = null;
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "info", LocaleBean.getMessageBundle().getString("dadosExcluidos"));
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    public void salvaUsuario() {
        RequestContext context = RequestContext.getCurrentInstance();
        try {
            Set<ConstraintViolation<Usuario>> errors = validator.validate(usuario);
            saveStatus = errors.isEmpty();
            if (saveStatus) {
                if (usuario != null && perfil.equals(PerfilEnum.ADMINISTRADOR)) {
                    if (!perfilRemovido.isEmpty()) {
                        usuarioDao.removePerfil(perfilRemovido);
                        perfilRemovido.clear();
                    }
                    usuarioDao.atualizar(usuario);
                    itemSelecionado = usuario;
                }
            }
        } catch (ConstraintViolationException e) {
            saveStatus = false;
        }

        context.addCallbackParam("resultado", saveStatus);
        handleCompleteImpUsuarios();
    }

    public void cancelSalvaUsuario(ActionEvent event) {
        List<Perfil> tempPerfilList = new ArrayList<Perfil>() {
            {
                addAll(perfilRemovido);
                for (Perfil p : usuario.getPerfilList()) {
                    if (p.getId() == null) {
                        add(p);
                    }
                }
            }
        };
        for (Perfil p : tempPerfilList) {
            if (p.getId() != null) {
                usuario.adicionaPerfil(p);
            } else {
                usuario.removePerfil(p);
            }
            System.out.println("p: " + p);
        }
        perfilRemovido.clear();
        System.out.println("cancelando alteração de usuário");
    }

    public void addPerfil() {
        if (isSelecionado()) {
            HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
            PerfilBean pb = (PerfilBean) session.getAttribute("perfilBean");
            CursoBean cb = (CursoBean) session.getAttribute("cursoBean");
            PerfilEnum p = pb.getItemSelecionado();
            Curso c = cb.getItemSelecionado();
            Perfil perfilItem = new Perfil();
            perfilItem.setTipoPerfil(p);
            perfilItem.setCurso(c);
            usuario.adicionaPerfil(perfilItem);
            System.out.println("perfil added: "
                    + perfilItem.getId() + ": "
                    + perfilItem.getTipoPerfil());
        }
    }

    public void removePerfil(ActionEvent event) {
        Perfil perfilItem = (Perfil) event.getComponent().getAttributes().get("perfil");
        usuario.removePerfil(perfilItem);
        if (perfilItem.getId() != null) {
            perfilRemovido.add(perfilItem);
        }
        System.out.println("perfil removed: "
                + perfilItem.getId()
                + ": " + perfilItem.getTipoPerfil());
    }

    public void onCursoDisable() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        CursoBean cb = (CursoBean) session.getAttribute("cursoBean");
        cb.setItemSelecionado(null);
    }

    public boolean isCursoDisabled() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        PerfilBean pb = (PerfilBean) session.getAttribute("perfilBean");
        CursoBean cb = (CursoBean) session.getAttribute("cursoBean");
        return (cb != null && Arrays.asList(Perfil.perfilCursoMustBeNull).contains(pb.getItemSelecionado()));
    }

    public void selecionaItem(SelectEvent event) {
        itemSelecionado = (Usuario) event.getObject();
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

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public boolean getStopImportaUsuarios() {
        return stopImportaUsuarios;
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
    
    public boolean isAutenticado() {
        return autenticado;
    }

    public List<Usuario> findUsuario(String query) {
        return usuarioDao.find(query);
    }

    public List<Usuario> findDocente(String query) {
        return usuarioDao.find(query, PerfilEnum.DOCENTE);
    }

    public List<Usuario> findDiscente(String query) {
        return usuarioDao.find(query, PerfilEnum.DISCENTE);
    }

    public List<Usuario> findCoordenadorCurso(String query) {
        return usuarioDao.find(query, PerfilEnum.COORDENADOR_DE_CURSO);
    }

    public List<Usuario> findCoordenadorEstagio(String query) {
        return usuarioDao.find(query, PerfilEnum.COORDENADOR_DE_ESTAGIO);
    }

    public List<Usuario> findSecretaria(String query) {
        return usuarioDao.find(query, PerfilEnum.SECRETARIA);
    }

    public List<Usuario> getFiltroUsuarios() {
        if (termoBusca.equals("")) {
            return usuarioDao.findAll();
        } else {
            return usuarioDao.find(termoBusca);
        }
    }

    public LazyDataModel<Usuario> getUsuariosDataModel() {
        return usuariosDataModel;
    }

    public List<Usuario> getUsuarios() {
        if (usuarios.isEmpty() | usuarioDao.count() != usuarios.size()) {
            usuarios = usuarioDao.findAll();
        }
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
