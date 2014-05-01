/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.reqweb.dao;

import br.ufg.reqweb.model.ItemRequerimento;
import br.ufg.reqweb.model.Requerimento;
import br.ufg.reqweb.model.TipoRequerimentoEnum;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.hibernate.Hibernate;
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

/**
 *
 * @author André
 */
@Repository
@Scope(value = "singleton")
public class RequerimentoDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Transactional
    public void adicionar(Requerimento requerimento) {
        Session session = this.sessionFactory.getCurrentSession();
        requerimento.setDataCriacao(Calendar.getInstance().getTime());
        session.save(requerimento);
    }

    @Transactional
    public void atualizar(Requerimento requerimento) {
        Session session = this.sessionFactory.getCurrentSession();
        requerimento.setDataModificacao(Calendar.getInstance().getTime());
        session.update(requerimento);
    }

    @Transactional
    public void excluir(Requerimento requerimento) {
        this.sessionFactory.getCurrentSession().delete(requerimento);
    }

    @Transactional(readOnly = true)
    public Requerimento findById(Long id) {
        try {
            Requerimento requerimento = (Requerimento) this.sessionFactory.getCurrentSession().get(Requerimento.class, id);
            Set<ItemRequerimento> items = requerimento.getItemRequerimentoList();
            Hibernate.initialize(items);
            return requerimento;
        } catch (HibernateException e) {
            System.out.println("query error: " + e.getCause());
        }
        return null;
    }

    /**
     * find by discente nome
     *
     * @param termo
     * @return
     */
    @Transactional(readOnly = true)
    public List<Requerimento> find(String termo) {
        try {
            List<Requerimento> requerimentos;
            requerimentos = this.sessionFactory.getCurrentSession()
                    .createCriteria(Requerimento.class)
                    .createAlias("discente", "d")
                    .add(Restrictions.or(Restrictions.eq("d.matricula", termo),
                                    Restrictions.like("d.nome", termo, MatchMode.ANYWHERE).ignoreCase()))
                    .setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY)
                    .list();

            return requerimentos;
        } catch (HibernateException e) {
            System.out.println("query error: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     *
     * @param tipoRequerimento
     * @return
     */
    @Transactional(readOnly = true)
    public List<Requerimento> find(TipoRequerimentoEnum tipoRequerimento) {
        try {
            return this.sessionFactory.getCurrentSession()
                    .createCriteria(Requerimento.class)
                    .add(Restrictions.eq("tipoRequerimento", tipoRequerimento))
                    .list();
        } catch (HibernateException e) {
            System.out.println("query error: " + e.getCause());
            return new ArrayList<>();
        }
    }

    /**
     *
     * @param login
     * @param tipoRequerimento
     * @return
     */
    @Transactional(readOnly = true)
    public List<Requerimento> find(String login, TipoRequerimentoEnum tipoRequerimento) {
        try {
            return this.sessionFactory.getCurrentSession()
                    .createCriteria(Requerimento.class)
                    .createAlias("discente", "d")
                    .add(Restrictions.eq("d.login", login).ignoreCase())
                    .add(Restrictions.eq("tipoRequerimento", tipoRequerimento))
                    .list();
        } catch (HibernateException e) {
            System.out.println("query error: " + e.getCause());
            return new ArrayList<>();
        }
    }

    /**
     * find by dataCriacao interval
     *
     * @param dateA
     * @param dateB
     * @return
     */
    @Transactional(readOnly = true)
    public List<Requerimento> find(Date dateA, Date dateB) {
        try {
            return this.sessionFactory.getCurrentSession()
                    .createCriteria(Requerimento.class)
                    .add(Restrictions.and(Restrictions.between("dataCriacao", dateA, dateB)))
                    .list();
        } catch (HibernateException e) {
            System.out.println("query error: " + e.getCause());
            return new ArrayList<>();
        }
    }
    
    /**
     * 
     * @param login
     * @param dateA
     * @param dateB
     * @return 
     */
    @Transactional(readOnly = true)
    public List<Requerimento> find(String login, Date dateA, Date dateB) {
        try {
            return this.sessionFactory.getCurrentSession()
                    .createCriteria(Requerimento.class)
                    .createAlias("discente", "d")
                    .add(Restrictions.eq("d.login", login).ignoreCase())
                    .add(Restrictions.and(Restrictions.between("dataCriacao", dateA, dateB)))
                    .list();
        } catch (HibernateException e) {
            System.out.println("query error: " + e.getCause());
            return new ArrayList<>();
        }
    }
    /**
     * 
     * @param first
     * @param pageSize
     * @return 
     */
    @Transactional(readOnly = true)
    public List<Requerimento> find(int first, int pageSize) {
        try {
            List<Requerimento> requerimentos;
            requerimentos = this.sessionFactory.getCurrentSession()
                    .createQuery("FROM Requerimento r")
                    .setFirstResult(first)
                    .setMaxResults(pageSize)
                    .list();
            return requerimentos;

        } catch (HibernateException e) {
            System.out.println("query error: " + e.getCause());
            return new ArrayList<>();
        }
    }
    
    /**
     * 
     * @param login
     * @param first
     * @param pageSize
     * @return 
     */
    @Transactional(readOnly = true)
    public List<Requerimento> find(String login, int first, int pageSize) {
        try {
            List<Requerimento> requerimentos;
            requerimentos = this.sessionFactory.getCurrentSession()
                    .createQuery("FROM Requerimento r WHERE r.discente.login = :login")
                    .setParameter("login", login)
                    .setFirstResult(first)
                    .setMaxResults(pageSize)
                    .list();
            return requerimentos;

        } catch (HibernateException e) {
            System.out.println("query error: " + e.getCause());
            return new ArrayList<>();
        }
    }    

    @Transactional(readOnly = true)
    public List<Requerimento> findAll() {
        try {
            return this.sessionFactory.getCurrentSession().createQuery("FROM Requerimento r").list();
        } catch (HibernateException e) {
            System.out.println("query error: " + e.getCause());
            return new ArrayList<>();
        }
    }
    
    /**
     * 
     * @return 
     */
    @Transactional(readOnly = true)
    public int count() {
        try {
            int result = ((Long) this.sessionFactory.getCurrentSession()
                    .createQuery("SELECT COUNT(r) FROM Requerimento r")
                    .uniqueResult()).intValue();
            return result;
        } catch (HibernateException e) {
            System.out.println("query error: " + e.getMessage());
            return 0;
        }
    }
    
    /**
     * 
     * @param login
     * @return 
     */
    @Transactional(readOnly = true)
    public int count(String login) {
        try {
            int result = ((Long) this.sessionFactory.getCurrentSession()
                    .createQuery("SELECT COUNT(r) FROM Requerimento r WHERE r.discente.login = :login")
                    .setParameter("login", login)
                    .uniqueResult()).intValue();
            return result;
        } catch (HibernateException e) {
            System.out.println("query error: " + e.getMessage());
            return 0;
        }
    }
}
