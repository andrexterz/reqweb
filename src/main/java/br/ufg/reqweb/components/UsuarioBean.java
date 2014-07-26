package br.ufg.reqweb.components;

import br.ufg.reqweb.dao.CursoDao;
import br.ufg.reqweb.dao.UsuarioDao;
import br.ufg.reqweb.model.Curso;
import br.ufg.reqweb.model.Perfil;
import br.ufg.reqweb.model.PerfilEnum;
import br.ufg.reqweb.model.Usuario;
import br.ufg.reqweb.util.LdapInfo;
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
    private PerfilEnum perfil;
    private Usuario sessionUsuario;
    private Usuario usuario;
    private boolean autenticado;
    private int progress;
    private boolean saveStatus;
    private volatile boolean stopImportaUsuarios;
    private Thread tImportJob;
    private Usuario itemSelecionado;
    private String termoBusca;
    private PerfilEnum tipoPerfilBusca;

    public UsuarioBean() {
        usuarios = new ArrayList<>();
        usuario = new Usuario();
        sessionUsuario = null;
        autenticado = false;
        tImportJob = null;
        termoBusca = "";
        tipoPerfilBusca = null;
        saveStatus = false;
        usuariosDataModel = new LazyDataModel<Usuario>() {

            private List<Usuario> data;

            @Override
            public Object getRowKey(Usuario usuario) {
                return usuario.getId().toString();
            }

            @Override
            public Usuario getRowData(String key) {
                for (Usuario u : data) {
                    if (u.getId().toString().equals(key)) {
                        return u;
                    }
                }
                return null;
            }

            @Override
            public List<Usuario> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
                String order;
                switch (sortOrder.name()) {
                    case "ASCENDING":
                        order = "asc";
                        break;
                    case "DESCENDING":
                        order = "desc";
                        break;
                    default:
                        order = null;
                        break;
                }
                setPageSize(pageSize);
                Map<String, Object> filtros = new HashMap();
                if (termoBusca != null && !termoBusca.equals("")) {
                    System.out.format("entering termo: '%s'\n", termoBusca);
                    filtros.put("termo", termoBusca);
                }
                if (tipoPerfilBusca != null) {
                    System.out.format("entering perfil: '%s'\n", tipoPerfilBusca);
                    filtros.put("tipoPerfil", tipoPerfilBusca);
                }
                if (getPerfil().equals(PerfilEnum.COORDENADOR_DE_CURSO) || getPerfil().equals(PerfilEnum.COORDENADOR_DE_ESTAGIO)) {
                    Curso curso = null;
                    for (Perfil p : getSessionUsuario().getPerfilList()) {
                        if (p.getTipoPerfil().equals(getPerfil())) {
                            curso = p.getCurso();
                            break;
                        }
                    }
                    if (curso != null) {
                        System.out.format("entering curso: '%s'\n", curso.getNome());
                        filtros.put("curso", curso);
                    }
                }
                data = usuarioDao.find(first, pageSize, sortField, order, filtros);
                setRowCount(usuarioDao.count(filtros));

                System.out.format("result size: %d / count size: %d\n", data.size(), getRowCount());
                if (data.size() > pageSize) {
                    try {
                        return data.subList(first, first + pageSize);
                    } catch (IndexOutOfBoundsException e) {
                        return data.subList(first, first + (data.size() % pageSize));
                    }

                }
                return data;
            }
        };
    }

    public String authLogin() {
        FacesContext context = FacesContext.getCurrentInstance();
        Map<String, String> loginParameters = context.getExternalContext().getRequestParameterMap();
        String login = loginParameters.get("loginForm:login");
        String senha = loginParameters.get("loginForm:senha");
        GrantedAuthority perfilAuthority = new SimpleGrantedAuthority(perfil.name());
        try {
            Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();
            sessionUsuario = usuarioDao.findByLogin(login);
            for (Perfil p : sessionUsuario.getPerfilList()) {
                grantedAuthorities.add(new SimpleGrantedAuthority(p.getTipoPerfil().name()));
            }
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(login, senha, grantedAuthorities);
            Authentication auth = authManager.authenticate(authToken);
            SecurityContextHolder.getContext().setAuthentication(auth);
            autenticado = auth.isAuthenticated() && grantedAuthorities.contains(perfilAuthority);
        } catch (AuthenticationException | NullPointerException e) {
            log.error("erro de autenticação -> " + e.getLocalizedMessage());
        }
        if (autenticado) {
            log.info(String.format("usuario <%s: %s> efetuou login", login, perfil.getPapel()));
            return String.format("%s?faces-redirect=true", home());
        } else {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, LocaleBean.getMessageBundle().getString("erroAutenticacao"), "");
            context.addMessage("loginError", msg);
            return null;
        }
    }

    public String homeDir() {
        String homeDir = String.format("/views/secure/%s", perfil.toString()).toLowerCase();
        return homeDir;
    }

    public String home() {
        String home = String.format("/views/secure/%s/%s", perfil.toString(), perfil.toString()).toLowerCase();
        return home;
    }

    public String authLogout() {
        log.info(String.format("usuario <%s: %s> efetuou logout", sessionUsuario.getLogin(), perfil.getPapel()));
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        session.invalidate();
        return "/views/login?faces-redirect=true";
    }

    public void setupImportUsuarios() {
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
                        //0 to 99% completes only when transaction finishes
                        progress = (int) ((counter / (float) length) * 99);
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
                        }
                        Set<ConstraintViolation<Usuario>> errors = validator.validate(usr);
                        if (errors.isEmpty()) {
                            usrList.add(usr);
                        }
                    } else {
                        usrList.clear();
                        break;
                    }
                }
                try {
                    usuarioDao.adicionar(usrList);
                    setSaveStatus(true);
                } catch (Exception e) {
                    setSaveStatus(false);
                }
                //complete 100%
                progress++;
            }
        };
        tImportJob.start();
    }

    public void cancelImpUsuarios() {
        setupImportUsuarios();
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
    
    public void editaUsuario() {
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
            try {
                usuarioDao.excluir(itemSelecionado);
                itemSelecionado = null;
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "info", LocaleBean.getMessageBundle().getString("dadosExcluidos"));
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } catch (Exception e) {
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "info", LocaleBean.getMessageBundle().getString("violacaoRelacionamento"));
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        }
    }

    public void salvaUsuario() {
        RequestContext context = RequestContext.getCurrentInstance();
        try {
            Set<ConstraintViolation<Usuario>> errors = validator.validate(usuario);
            saveStatus = errors.isEmpty();
            if (saveStatus) {
                if (usuario != null && perfil.equals(PerfilEnum.ADMINISTRADOR)) {
                    usuarioDao.atualizar(usuario);
                    itemSelecionado = usuario;
                }
            }
        } catch (Exception e) {
            System.out.println("error: " + e.getLocalizedMessage());
            setSaveStatus(false);
        }

        context.addCallbackParam("resultado", saveStatus);
        handleCompleteImpUsuarios();
    }

    public void cancelSalvaUsuario() {
        System.out.println("fechando <EditaUsuarioForm>");
    }

    public void addPerfil() {
        if (isSelecionado()) {
            Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
            PerfilBean pb = (PerfilBean) sessionMap.get("perfilBean");
            CursoBean cb = (CursoBean) sessionMap.get("cursoBean");
            PerfilEnum p = pb.getItemSelecionado();
            Curso c = cb.getItemSelecionado();
            Perfil perfilItem = new Perfil();
            perfilItem.setTipoPerfil(p);
            perfilItem.setCurso(c);
            perfilItem.setUsuario(usuario);
            if (!usuario.getPerfilList().contains(perfilItem)) {
                usuario.adicionaPerfil(perfilItem);
                salvaUsuario();
            } else {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, LocaleBean.getMessageBundle().getString("dadosInvalidos"), null);
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        }
    }

    public void removePerfil(ActionEvent event) {
        Perfil perfilItem = (Perfil) event.getComponent().getAttributes().get("perfil");
        usuario.removePerfil(perfilItem);
        salvaUsuario();
    }

    public void onCursoDisable() {
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        CursoBean cb = (CursoBean) sessionMap.get("cursoBean");
        cb.setItemSelecionado(null);
    }
   
    public boolean isCursoDisabled() {
        Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        PerfilBean pb = (PerfilBean) sessionMap.get("perfilBean");
        CursoBean cb = (CursoBean) sessionMap.get("cursoBean");
        return (cb != null && Arrays.asList(Perfil.perfilCursoMustBeNull).contains(pb.getItemSelecionado()));
    }

    public void salvaSessionUsuario() {
        setUsuario(sessionUsuario);
        salvaUsuario();
        setUsuario(null);
    }

    public void selecionaItem(SelectEvent event) {
        itemSelecionado = (Usuario) event.getObject();
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

    public boolean isSaveStatus() {
        return saveStatus;
    }

    public void setSaveStatus(boolean saveStatus) {
        this.saveStatus = saveStatus;
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

    public Usuario getSessionUsuario() {
        return sessionUsuario;
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

    public PerfilEnum getTipoPerfilBusca() {
        return tipoPerfilBusca;
    }

    public void setTipoPerfilBusca(PerfilEnum tipoPerfilBusca) {
        this.tipoPerfilBusca = tipoPerfilBusca;
    }

}
