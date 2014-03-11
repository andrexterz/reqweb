/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufg.reqweb.dao;

import br.ufg.reqweb.model.PerfilEnum;
import br.ufg.reqweb.model.Permissao;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author andre
 */

@Repository
public class PermissaoDao {

    @Autowired
    private SessionFactory sessionFactory;
    
    @Transactional
    public void adicionar(Permissao permissao) {
        this.sessionFactory.getCurrentSession().save(permissao);
    }
    
    @Transactional
    public void atualizar(Permissao permissao) {
        this.sessionFactory.getCurrentSession().update(permissao);
    }
    
    @Transactional
    public void excluir(Permissao permissao) {
        this.sessionFactory.getCurrentSession().delete(permissao);
    }
    
    @Transactional(readOnly = true)
    public Permissao findById(Long id) {
        Permissao permissao;
        try {
            permissao = (Permissao) this.sessionFactory.getCurrentSession().get(Permissao.class, id);
        } catch (HibernateException e) {
            System.out.println("query error: " + e.getMessage());
            permissao = null;
        }        
        return permissao;
    }
    
    @Transactional(readOnly = true)
    public List<Permissao> findByPerfil(PerfilEnum tipoPerfil) {
        List<Permissao> permissaoList = sessionFactory.getCurrentSession()
                .createQuery("SELECT p FROM Permissao p JOIN p.tipoPerfil t WHERE t = :tipoPerfil")
                .setParameter("tipoPerfil", tipoPerfil)
                .list();
        return permissaoList;
    }
    
    @Transactional(readOnly = true)
    public List<Permissao> find(String termo) {
        try {
            List<Permissao> permissoes = this.sessionFactory.getCurrentSession()
                    .createSQLQuery("SELECT * FROM Permissao p WHERE p.nome = :termo")
                    .addEntity(Permissao.class)
                    .setParameter("termo", termo)
                    .list();
            return permissoes;
        } catch (HibernateException | NumberFormatException e) {
            System.out.println("query error: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Transactional(readOnly = true)
    public List<Permissao> findAll() {
        try {
            List<Permissao> permissoes = this.sessionFactory.getCurrentSession()
                    .createSQLQuery("SELECT * FROM Permissao p")
                    .addEntity(Permissao.class)
                    .list();
            return permissoes;
        } catch (HibernateException | NumberFormatException e) {
            System.out.println("query error: " + e.getMessage());
            return new ArrayList<>();
        }
    }    
    
}
