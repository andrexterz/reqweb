/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.reqweb.dao;

import br.ufg.reqweb.model.Perfil;
import br.ufg.reqweb.model.PerfilEnum;
import br.ufg.reqweb.model.Usuario;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.validation.ValidationException;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Scope(value = "singleton")
public class UsuarioDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Transactional
    public void adicionar(List<Usuario> usuarios) {
        Session session = this.sessionFactory.getCurrentSession();
        int counter = 0;
        boolean userExists;
        List<String> loginNames = session.createQuery("SELECT login FROM Usuario u").list();
        for (Usuario usuario : usuarios) {
            userExists = loginNames.contains(usuario.getLogin());
            if (!userExists) {
                session.save(usuario);
                for (Perfil p : usuario.getPerfilList()) {
                    session.save(p);
                }
                if (++counter % 20 == 0) {
                    session.flush();
                    session.clear();
                }
            }
        }
    }

    @Transactional
    public void atualizar(Usuario usuario) {
        Session session = this.sessionFactory.getCurrentSession();
        session.update(usuario);
        for (Perfil p : usuario.getPerfilList()) {
            session.saveOrUpdate(p);
        }
    }

    @Transactional
    public void removePerfil(List<Perfil> perfilList) {
        for (Perfil p : perfilList) {
            this.sessionFactory.getCurrentSession().delete(p);
        }
    }

    @Transactional
    public void excluir(Usuario usuario) {
        this.sessionFactory.getCurrentSession().delete(usuario);
    }

    @Transactional(readOnly = true)
    public List<Usuario> findAll() {
        try {
            List<Usuario> usuarioList = this.sessionFactory.getCurrentSession().createQuery("FROM Usuario u ORDER BY u.nome ASC").list();
            return usuarioList;
        } catch (HibernateException e) {
            System.out.println("query error: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Transactional(readOnly = true)
    public List<Usuario> find(int firstResult, int maxResult) {
        try {
            List<Usuario> usuarioList = this.sessionFactory.getCurrentSession()
                    .createQuery("FROM Usuario u ORDER BY u.nome ASC")
                    .setFirstResult(firstResult)
                    .setMaxResults(maxResult)
                    .list();
            return usuarioList;
        } catch (HibernateException e) {
            System.out.println("query error: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Transactional(readOnly = true)
    public Usuario findById(Long id) {
        Usuario usuario;
        try {
            usuario = (Usuario) this.sessionFactory.getCurrentSession().get(Usuario.class, id);
        } catch (HibernateException e) {
            usuario = null;
        }
        return usuario;
    }

    @Transactional(readOnly = true)
    public Usuario findByLogin(String login) {
        Usuario usuario;
        try {
            usuario = (Usuario) this.sessionFactory.getCurrentSession()
                    .createCriteria(Usuario.class)
                    .add(Restrictions.eq("login", login)).uniqueResult();
        } catch (HibernateException e) {
            usuario = null;
        }
        return usuario;
    }
    
    

    @Transactional(readOnly = true)
    public List<Usuario> find(String termo) {
        try {
            return this.sessionFactory.getCurrentSession()
                    .createCriteria(Usuario.class)
                    .add(Restrictions.or(Restrictions.eq("matricula", termo),
                            Restrictions.like("nome", termo, MatchMode.ANYWHERE).ignoreCase()))
                    .setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY)
                    .list();
        } catch (HibernateException e) {
            System.out.println("query error: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Transactional(readOnly = true)
    public List<Usuario> find(String termo, PerfilEnum tipoPerfil) {
        try {
            List<Usuario> usuarios = this.sessionFactory.getCurrentSession().
                    createQuery("SELECT u FROM Usuario u JOIN u.perfilList p WHERE p.tipoPerfil = :tipoPerfil AND lower(u.nome) LIKE lower(:termo)")
                    .setParameter("tipoPerfil", tipoPerfil)
                    .setParameter("termo", "%" + termo + "%")
                    .list();
            return usuarios;
        } catch (HibernateException e) {
            System.out.println("query error: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Transactional(readOnly = true)
    public List<Usuario> find(PerfilEnum tipoPerfil) {
        try {
            List<Usuario> usuarios = this.sessionFactory.getCurrentSession().
                    createQuery("SELECT u FROM Usuario u JOIN u.perfilList p WHERE p.tipoPerfil = :tipoPerfil")
                    .setParameter("tipoPerfil", tipoPerfil)
                    .list();
            return usuarios;
        } catch (HibernateException e) {
            System.out.println("query error: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Transactional(readOnly = true)
    public int count() {
        try {
            int result = ((Long) this.sessionFactory.getCurrentSession()
                    .createQuery("SELECT COUNT(u) FROM Usuario u")
                    .uniqueResult()).intValue();
            return result;
        } catch (HibernateException e) {
            System.out.println("query error: " + e.getMessage());
            return 0;
        }
    }

}
