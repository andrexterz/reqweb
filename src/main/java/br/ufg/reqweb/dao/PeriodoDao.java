/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.reqweb.dao;

import br.ufg.reqweb.model.Periodo;
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
public class PeriodoDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Transactional
    public void adicionar(Periodo periodo) {
        this.sessionFactory.getCurrentSession().save(periodo);
    }

    @Transactional
    public void atualizar(Periodo periodo) {
        this.sessionFactory.getCurrentSession().update(periodo);
    }

    @Transactional
    public void excluir(Periodo periodo) {
        this.sessionFactory.getCurrentSession().delete(periodo);
    }

    @Transactional(readOnly = true)
    public List listar() {
        try {
            List periodoList = this.sessionFactory.getCurrentSession().createQuery("FROM Periodo p ORDER BY p.ano DESC").list();
            return periodoList;
        } catch (HibernateException e) {
            System.out.println("query error: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    @Transactional(readOnly = true)    
    public Periodo buscar(Long id) {
        Periodo periodo;
        try {
            periodo = (Periodo) this.sessionFactory.getCurrentSession().get(Periodo.class, id);
        } catch (HibernateException e) {
            System.out.println("query error: " + e.getMessage());
            periodo = null;
        }
        return periodo;
    }

    @Transactional(readOnly = true)
    public List procurar(String termo) {
        try {
            Query query = this.sessionFactory.getCurrentSession().
                    createSQLQuery("SELECT * FROM Periodo p WHERE p.ano = :termo")
                    .addEntity(Periodo.class);
            query.setParameter("termo", Integer.parseInt(termo));
            return query.list();
        } catch (HibernateException | NumberFormatException e) {
            System.out.println("query error: " + e.getMessage());
            return new ArrayList<>();
        }
    }

}
