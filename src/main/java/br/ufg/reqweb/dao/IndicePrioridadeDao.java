/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.reqweb.dao;

import br.ufg.reqweb.model.IndicePrioridade;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author andre
 */
@Repository
@Scope(value = "singleton")
public class IndicePrioridadeDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Transactional
    public void adicionar(IndicePrioridade indicePrioridade) {
        this.sessionFactory.getCurrentSession().save(indicePrioridade);
    }

    @Transactional
    public void adicionar(List<IndicePrioridade> indicePrioridade) {
        for (IndicePrioridade ip: indicePrioridade) {
            this.sessionFactory.getCurrentSession().save(ip);
        }
    }

    @Transactional
    public void atualizar(IndicePrioridade indicePrioridade) {
        this.sessionFactory.getCurrentSession().update(indicePrioridade);
    }

    @Transactional
    public void excluir(IndicePrioridade indicePrioridade) {
        this.sessionFactory.getCurrentSession().delete(indicePrioridade);
    }

    @Transactional
    public void excluir(List<IndicePrioridade> indicePrioridade) {
        for (IndicePrioridade ip: indicePrioridade) {
            this.sessionFactory.getCurrentSession().delete(ip);
        }
    }
    
    @Transactional
    public void excluir() {
        this.sessionFactory.getCurrentSession().createQuery("DELETE FROM IndicePrioridade")
                .executeUpdate();
    }

    @Transactional(readOnly = true)
    public List<IndicePrioridade> findAll() {
        try {
            return this.sessionFactory.getCurrentSession()
                    .createQuery("FROM IndicePrioridade ip ORDER BY ip.discente.nome ASC")
                    .list();
        } catch (HibernateException e) {
            System.out.println("query error: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    @Transactional(readOnly = true)
    public List<IndicePrioridade> find(int first, int maxResult) {
        try {
            return this.sessionFactory.getCurrentSession()
                    .createQuery("FROM IndicePrioridade ip ORDER BY ip.discente.nome ASC")
                    .setFirstResult(first)
                    .setMaxResults(maxResult)
                    .list();

        } catch (HibernateException e) {
            System.out.println("query error: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Transactional(readOnly = true)
    public IndicePrioridade findById(Long id) {
        IndicePrioridade indicePrioridade;
        try {
            indicePrioridade = (IndicePrioridade) this.sessionFactory.getCurrentSession()
                    .get(IndicePrioridade.class, id);
        } catch (HibernateException e) {
            System.out.println("query error: " + e.getMessage());
            indicePrioridade = null;
        }
        return indicePrioridade;
    }

    @Transactional(readOnly = true)
    public List<IndicePrioridade> find(String termo) {
        try {
            return this.sessionFactory.getCurrentSession()
                    .createCriteria(IndicePrioridade.class)
                    .createAlias("discente","d")
                    .add(Restrictions.or(Restrictions.eq("d.matricula", termo),
                            Restrictions.like("d.nome", termo, MatchMode.ANYWHERE).ignoreCase()))
                    .setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY)
                    .list();
        } catch (HibernateException e) {
            System.out.println("query error: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Transactional(readOnly = true)
    public int count() {
        try {
            int result = ((Long) this.sessionFactory.getCurrentSession()
                    .createQuery("SELECT COUNT(ip) FROM IndicePrioridade ip")
                    .uniqueResult()).intValue();
            return result;
        } catch (HibernateException e) {
            System.out.println("query error: " + e.getMessage());
            return 0;
        }
    }
}
