/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.reqweb.dao;

import br.ufg.reqweb.model.Usuario;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author André
 */
@Repository
public class UsuarioDao {
    @Autowired
    private SessionFactory sessionFactory;
    
    @Transactional
    public void adicionar(Usuario usuario) {
        this.sessionFactory.getCurrentSession().save(usuario);
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
    public List<Usuario> listar() {
        try {
            List<Usuario> usuarioList = this.sessionFactory.getCurrentSession().createQuery("FROM Usuario u ORDER BY u.nome ASC").list();
            return usuarioList;
        } catch(HibernateException e) {
            System.out.println("query error: " + e.getMessage());
            return new ArrayList<>();            
        }
    }    
    
    @Transactional(readOnly = true)
    public Usuario buscar(Long id) {
        Usuario usuario;
        try {
            usuario = (Usuario) this.sessionFactory.getCurrentSession().get(Usuario.class, id);
        } catch (HibernateException e) {
            usuario = null;
        }
        return usuario;
    }
    
    @Transactional(readOnly = true)    
    public List<Usuario> procurar(String termo) {
        return null;
    }
    
}
