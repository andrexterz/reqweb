/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.reqweb.dao;

import br.ufg.reqweb.model.Perfil;
import br.ufg.reqweb.model.Usuario;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class UsuarioDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Transactional
    public void adicionar(Usuario usuario) {
        Session session = this.sessionFactory.getCurrentSession();
        session.save(usuario);
        for (Perfil p : usuario.getPerfilList()) {
            session.save(p);
        }

    }

    @Transactional
    public void adicionar(List<Usuario> usuarios) {
        Session session = this.sessionFactory.getCurrentSession();
        for (Usuario usuario : usuarios) {
            session.save(usuario);
            for (Perfil p : usuario.getPerfilList()) {
                session.save(p);
                
            }
        }
    }

    @Transactional
    public void atualizar(Usuario usuario) {
        this.sessionFactory.getCurrentSession().update(usuario);

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

    /**
     *
     * @param id
     * @return
     *
     */
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
    public List<Usuario> find(String termo) {
        try {
            List<Usuario> usuarios = this.sessionFactory.getCurrentSession().
                    createSQLQuery("SELECT * FROM Usuario u WHERE u.nome ~* :termo")
                    .addEntity(Usuario.class).setParameter("termo", termo)
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
