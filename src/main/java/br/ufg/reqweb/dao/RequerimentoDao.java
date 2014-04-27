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
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import org.hibernate.FetchMode;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
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
            Requerimento requerimento = (Requerimento) this.sessionFactory.getCurrentSession().get(Requerimento.class,id);
            Set<ItemRequerimento> items = requerimento.getItemRequerimentoSet();
            Hibernate.initialize(items);
            return requerimento;
        } catch (HibernateException e) {
            System.out.println("query error: " + e.getCause());
        }
        return null;
    }

    @Transactional(readOnly = true)
    public List<Requerimento> findByNomeDiscente(String nome) {
        System.out.println("find by nome");
        try {
            List<Requerimento> requerimentos;
            requerimentos = this.sessionFactory.getCurrentSession()
                    .createCriteria(Requerimento.class)
                    .add(Restrictions.like("requerimento.discente.nome", nome, MatchMode.ANYWHERE))
                    .list();

            return requerimentos;
        } catch (HibernateException e) {
            System.out.println("query error: " + e.getCause());
            return new ArrayList<>();
        }
    }

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

    @Transactional(readOnly = true)
    public List<Requerimento> findAll() {
        try {
            return this.sessionFactory.getCurrentSession().createQuery("FROM Requerimento r").list();
        } catch (HibernateException e) {
            System.out.println("query error: " + e.getMessage());
            return new ArrayList<>();
        }
    }

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
}
