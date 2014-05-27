/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.reqweb.dao;

import br.ufg.reqweb.model.Periodo;
import br.ufg.reqweb.model.Semestre;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author André
 */
@Repository
@Scope(value = "singleton")
public class PeriodoDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Transactional
    public void adicionar(Periodo periodo) {
        Session session = this.sessionFactory.getCurrentSession();
        session.save(periodo);
        if (periodo.isAtivo() == true) {
            disableOthers(session, periodo);
        }
    }

    @Transactional
    public void atualizar(Periodo periodo) {
        Session session = this.sessionFactory.getCurrentSession();
        session.update(periodo);
        if (periodo.isAtivo() == true) {
            disableOthers(session, periodo);
        }
    }

    @Transactional
    public void excluir(Periodo periodo) {
        this.sessionFactory.getCurrentSession().delete(periodo);
    }

    @Transactional(readOnly = true)    
    public Periodo findById(Long id) {
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
    public Periodo find(int ano, Semestre semestre) {
        return (Periodo) this.sessionFactory.getCurrentSession()
                .createQuery("FROM Periodo p WHERE p.ano = :ano AND p.semestre =:semestre")
                .setParameter("ano", ano)
                .setParameter("semestre", semestre).uniqueResult();
    }
    
    @Transactional(readOnly = true)
    public List<Periodo> find(String termo) {
        try {
            List<Periodo> periodos = this.sessionFactory.getCurrentSession()
                    .createSQLQuery("SELECT * FROM Periodo p WHERE p.ano = :termo")
                    .addEntity(Periodo.class)
                    .setParameter("termo", Integer.parseInt(termo)).list();
            return periodos;
        } catch (HibernateException | NumberFormatException e) {
            System.out.println("query error: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    @Transactional(readOnly = true)
    public List<Periodo> find(boolean ativo) {
        try {
            List<Periodo> periodos = this.sessionFactory.getCurrentSession()
                    .createSQLQuery("SELECT * FROM Periodo p WHERE p.ano = :termo and p.ativo = :ativo")
                    .addEntity(Periodo.class)
                    .setParameter("ativo", ativo).list();
            return periodos;
        } catch (HibernateException | NumberFormatException e) {
            System.out.println("query error: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Transactional(readOnly = true)
    public List<Periodo> findAll() {
        try {
            List<Periodo> periodoList = this.sessionFactory.getCurrentSession()
                    .createQuery("FROM Periodo p ORDER BY p.ano DESC").list();
            return periodoList;
        } catch (HibernateException e) {
            System.out.println("query error: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    private void disableOthers(Session session, Periodo periodo) {
        Criteria criteria = session.createCriteria(Periodo.class);
        List<Periodo> periodoList = criteria.list();
        int counter = 0;
        for (Periodo p: periodoList) {
            if (!Objects.equals(p.getId(), periodo.getId())) {
                p.setAtivo(false);
                session.save(p);
            }
            if (++counter % 20 == 0) {
                session.flush();
                session.clear();
            }
        }        
    }
}
