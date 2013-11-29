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

/**
 *
 * @author André
 */
@Repository
public class PeriodoAjusteDao {

    @Autowired
    private SessionFactory sessionFactory;

    public void adicionar(PeriodoAjuste periodoAjuste) {
        this.sessionFactory.getCurrentSession().save(periodoAjuste);
    }

    public void atualizar(PeriodoAjuste periodoAjuste) {
        this.sessionFactory.getCurrentSession().update(periodoAjuste);
    }

    public void excluir(PeriodoAjuste periodoAjuste) {
        this.sessionFactory.getCurrentSession().delete(periodoAjuste);
    }

    public List listar() {
        try {
            List ajusteList = this.sessionFactory.getCurrentSession().createQuery("FROM PeriodoAjuste p ORDER BY p.ano DESC").list();
            return ajusteList;
        } catch (HibernateException e) {
            System.out.println("query error: " + e.getMessage());
            return new ArrayList<PeriodoAjuste>();
        }
    }

    public List procurar(String termo) {
        try {
            Query query = this.sessionFactory.getCurrentSession().
                    createSQLQuery("SELECT * FROM PeriodoAjuste p")
                    .addEntity(PeriodoAjuste.class);
            query.setParameter("termo", termo);
            return query.list();
        } catch (HibernateException e) {
            System.out.println("query error: " + e.getMessage());
            return new ArrayList<PeriodoAjuste>();
        }
    }

}
