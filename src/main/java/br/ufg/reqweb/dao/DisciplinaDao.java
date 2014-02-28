/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.reqweb.dao;

import br.ufg.reqweb.model.Curso;
import br.ufg.reqweb.model.Disciplina;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author andre
 */
@Repository
public class DisciplinaDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Transactional
    public void adicionar(Disciplina disciplina) {
        this.sessionFactory.getCurrentSession().save(disciplina);
    }

    @Transactional
    public void adicionar(List<Disciplina> disciplinas) {
        for (Disciplina d : disciplinas) {
            this.sessionFactory.getCurrentSession().save(d);
        }
    }

    @Transactional
    public void atualizar(Disciplina disciplina) {
        this.sessionFactory.getCurrentSession().update(disciplina);
    }

    @Transactional
    public void excluir(Disciplina disciplina) {
        this.sessionFactory.getCurrentSession().delete(disciplina);
    }

    @Transactional(readOnly = true)
    public Disciplina findById(Long id) {
        Disciplina disciplina;
        try {
            disciplina = (Disciplina) this.sessionFactory.getCurrentSession().get(Disciplina.class, id);
        } catch (HibernateException e) {
            System.out.println("query error: " + e.getMessage());
            disciplina = null;
        }
        return disciplina;
    }

    @Transactional(readOnly = true)
    public List<Disciplina> findByCurso(String termo, Curso curso) {
        try {
            List<Disciplina> disciplinas;
            if (termo.isEmpty()) {
                disciplinas = this.sessionFactory.getCurrentSession()
                        .createQuery("FROM Disciplina d WHERE d.curso.id = :cursoId")
                        .setParameter("cursoId", curso.getId())
                        .list();
            } else {
                disciplinas = this.sessionFactory.getCurrentSession()
                        .createCriteria(Disciplina.class)
                        .add(Restrictions.like("nome", "%" + termo.toUpperCase() + "%"))
                        .add(Restrictions.eq("curso.id", curso.getId()))
                        .list();
            }
            return disciplinas;
        } catch (HibernateException | NumberFormatException e) {
            System.out.println("query error: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Transactional(readOnly = true)
    public List<Disciplina> find(String termo) {
        try {
            List<Disciplina> disciplinas = this.sessionFactory.getCurrentSession()
                    .createSQLQuery("SELECT * FROM Disciplina d WHERE d.nome ~* :termo")
                    .addEntity(Disciplina.class)
                    .setParameter("termo", termo)
                    .list();
            return disciplinas;
        } catch (HibernateException | NumberFormatException e) {
            System.out.println("query error: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Transactional(readOnly = true)
    public List<Disciplina> find(int firstResult, int maxResult) {
        try {
            List<Disciplina> disciplinas = this.sessionFactory.getCurrentSession()
                    .createQuery("FROM Disciplina d ORDER BY d.nome")
                    .setFirstResult(firstResult)
                    .setMaxResults(maxResult)
                    .list();
            return disciplinas;
        } catch (HibernateException e) {
            System.out.println("query error: " + e.getMessage());
            return new ArrayList<>();

        }
    }

    @Transactional(readOnly = true)
    public List<Disciplina> findAll() {
        try {
            List<Disciplina> disciplinas = this.sessionFactory.getCurrentSession()
                    .createSQLQuery("SELECT * FROM Disciplina d")
                    .addEntity(Disciplina.class)
                    .list();
            return disciplinas;
        } catch (HibernateException | NumberFormatException e) {
            System.out.println("query error: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Transactional(readOnly = true)
    public int count() {
        try {
            int result = ((Long) this.sessionFactory.getCurrentSession()
                    .createQuery("SELECT COUNT(d) FROM Disciplina d")
                    .uniqueResult()).intValue();
            return result;
        } catch (HibernateException e) {
            System.out.println("query error: " + e.getMessage());
            return 0;
        }
    }
}
