/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.reqweb.dao;

import br.ufg.reqweb.model.Periodo;
import br.ufg.reqweb.model.Turma;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
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
public class TurmaDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Transactional
    public void adicionar(Turma turma) {
        this.sessionFactory.getCurrentSession().save(turma);
    }

    @Transactional
    public void adicionar(List<Turma> turmas) {
        for (Turma t : turmas) {
            this.sessionFactory.getCurrentSession().save(t);
        }
    }

    @Transactional
    public void atualizar(Turma turma) {
        this.sessionFactory.getCurrentSession().update(turma);
    }

    @Transactional
    public void excluir(Turma turma) {
        this.sessionFactory.getCurrentSession().delete(turma);
    }

    @Transactional(readOnly = true)
    public Turma findById(Long id) {
        Turma turma;
        try {
            turma = (Turma) this.sessionFactory.getCurrentSession().get(Turma.class, id);
        } catch (HibernateException e) {
            System.out.println("query error: " + e.getMessage());
            turma = null;
        }
        return turma;
    }

    @Transactional(readOnly = true)
    public List<Turma> find(String termo) {
        try {
            List<Turma> turmas = this.sessionFactory.getCurrentSession()
                    .createCriteria(Turma.class)
                    .createAlias("disciplina", "d")
                    .add(Restrictions.like("d.nome", termo, MatchMode.ANYWHERE).ignoreCase())
                    //.createQuery("SELECT t FROM Turma t JOIN t.disciplina d where d.nome LIKE :termo")
                    //.setParameter("termo", "%" + termo.toUpperCase() + "%")
                    .list();
            return turmas;
        } catch (HibernateException | NumberFormatException e) {
            System.out.println("query error: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Transactional(readOnly = true)
    public List<Turma> find(String termo, Periodo periodo) {
        try {
            List<Turma> turmas;
            if (termo.isEmpty()) {
                turmas = this.sessionFactory.getCurrentSession()
                        .createQuery("FROM Turma t WHERE t.periodo.id = :periodoId")
                        .setParameter("periodoId", periodo.getId())
                        .list();
            } else {
                turmas = this.sessionFactory.getCurrentSession()
                        .createCriteria(Turma.class)
                        .createAlias("disciplina", "d")
                        .add(Restrictions.like("d.nome",termo.toUpperCase(),MatchMode.ANYWHERE))
                        .add(Restrictions.eq("periodo.id", periodo.getId()))
                        .list();
            }
            return turmas;
        } catch (HibernateException | NumberFormatException e) {
            System.out.println("query error: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    @Transactional(readOnly = true)
    public List<Turma> find(String termo, boolean periodoAtivo) {
        try {
            List<Turma> turmas;
            if (termo.isEmpty()) {
                turmas = this.sessionFactory.getCurrentSession()
                        .createCriteria(Turma.class)
                        .add(Restrictions.eq("periodo.ativo", periodoAtivo)).list();
            } else {
                turmas = this.sessionFactory.getCurrentSession()
                        .createCriteria(Turma.class)
                        .createAlias("disciplina", "d")
                        .createAlias("periodo", "p")
                        .add(Restrictions.like("d.nome",termo.toUpperCase(),MatchMode.ANYWHERE))
                        .add(Restrictions.eq("p.ativo", periodoAtivo))
                        .list();
            }
            return turmas;
        } catch (HibernateException | NumberFormatException e) {
            System.out.println("query error: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Transactional(readOnly = true)
    public List<Turma> find(int firstResult, int maxResult) {
        try {
            List<Turma> turma = this.sessionFactory.getCurrentSession()
                    .createQuery("FROM Turma t ORDER BY t.nome")
                    .setFirstResult(firstResult)
                    .setMaxResults(maxResult)
                    .list();
            return turma;
        } catch (HibernateException e) {
            System.out.println("query error: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Transactional(readOnly = true)
    public List<Turma> findAll() {
        try {
            List<Turma> turmas = this.sessionFactory.getCurrentSession()
                    .createSQLQuery("SELECT * FROM Turma t")
                    .addEntity(Turma.class)
                    .list();
            return turmas;
        } catch (HibernateException | NumberFormatException e) {
            System.out.println("query error: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Transactional(readOnly = true)
    public int count() {
        try {
            int result = ((Long) this.sessionFactory.getCurrentSession()
                    .createQuery("SELECT COUNT(t) FROM Turma t")
                    .uniqueResult()).intValue();
            return result;
        } catch (HibernateException e) {
            System.out.println("query error: " + e.getMessage());
            return 0;
        }
    }

}
