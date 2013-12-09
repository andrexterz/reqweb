/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.reqweb.dao;

import br.ufg.reqweb.model.PeriodoAjuste;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author André
 */
@Repository
public class PeriodoAjusteDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Transactional
    public void adicionar(PeriodoAjuste periodoAjuste) {
        this.sessionFactory.getCurrentSession().save(periodoAjuste);
    }

    @Transactional
    public void atualizar(PeriodoAjuste periodoAjuste) {
        this.sessionFactory.getCurrentSession().update(periodoAjuste);
    }

    @Transactional
    public void excluir(PeriodoAjuste periodoAjuste) {
        this.sessionFactory.getCurrentSession().delete(periodoAjuste);
    }

    @Transactional(readOnly = true)
    public List listar() {
        try {
            List ajusteList = this.sessionFactory.getCurrentSession().createQuery("FROM PeriodoAjuste p ORDER BY p.ano DESC").list();
            return ajusteList;
        } catch (HibernateException e) {
            System.out.println("query error: " + e.getMessage());
            return new ArrayList<PeriodoAjuste>();
        }
    }
    
    @Transactional(readOnly = true)    
    public PeriodoAjuste buscar(Long id) {
        PeriodoAjuste periodoAjuste;
        try {
            periodoAjuste = (PeriodoAjuste) this.sessionFactory.getCurrentSession().get(PeriodoAjuste.class, id);
        } catch (HibernateException e) {
            System.out.println("query error: " + e.getMessage());
            periodoAjuste = null;
        }
        return periodoAjuste;
    }

    @Transactional(readOnly = true)
    public List procurar(String termo) {
        try {
            Query query = this.sessionFactory.getCurrentSession().
                    createSQLQuery("SELECT * FROM PeriodoAjuste p WHERE p.ano = :termo")
                    .addEntity(PeriodoAjuste.class);
            query.setParameter("termo", Integer.parseInt(termo));
            return query.list();
        } catch (HibernateException | NumberFormatException e) {
            System.out.println("query error: " + e.getMessage());
            return new ArrayList<PeriodoAjuste>();
        }
    }

}
