/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.reqweb.dao;

import br.ufg.reqweb.model.Curso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Scope(value = "singleton")
public class CursoDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Transactional
    public void adicionar(Curso curso) {
        Date timestamp = Calendar.getInstance().getTime();
        this.sessionFactory.getCurrentSession().save(curso);

    }

    @Transactional
    public void atualizar(Curso curso) {
        Date timestamp = Calendar.getInstance().getTime();
        this.sessionFactory.getCurrentSession().update(curso);
    }

    @Transactional
    public void excluir(Curso curso) {
        this.sessionFactory.getCurrentSession().delete(curso);
    }

    @Transactional(readOnly = true)
    public List<Curso> findAll() {
        try {
            List<Curso> cursoList = this.sessionFactory.getCurrentSession()
                    .createQuery("FROM Curso c ORDER BY c.dataModificacao DESC")
                    .list();
            return cursoList;
        } catch (HibernateException e) {
            System.out.println("query error: " + e.getMessage());
            return new ArrayList<>();
        }

    }

    @Transactional(readOnly = true)
    public Curso findById(Long id) {
        Curso curso;
        try {
            curso = (Curso) this.sessionFactory.getCurrentSession().get(Curso.class, id);
        } catch (HibernateException e) {
            System.out.println("query error: " + e.getMessage());
            curso = null;
        }
        return curso;
    }

    @Transactional(readOnly = true)
    public Curso findBySigla(String sigla) {
        Curso curso;
        try {
            curso = (Curso) this.sessionFactory.getCurrentSession()
                    .createQuery("FROM Curso c WHERE c.sigla = :sigla")
                    .setParameter("sigla", sigla)
                    .uniqueResult();
        } catch (HibernateException e) {
            System.out.println("query error: " + e.getMessage());
            curso = null;
        }
        return curso;
    }

    @Transactional(readOnly = true)
    public Curso findByMatriz(String matriz) {
        Curso curso;
        try {
            curso = (Curso) this.sessionFactory.getCurrentSession()
                    .createQuery("FROM Curso c WHERE c.matriz = :matriz")
                    .setParameter("matriz", matriz)
                    .uniqueResult();
        } catch (HibernateException e) {
            System.out.println("query error: " + e.getMessage());
            curso = null;
        }
        return curso;
    }

    @Transactional(readOnly = true)
    public List<Curso> find(String termo) {
        try {
            List<Curso> cursos = this.sessionFactory.getCurrentSession().
                    createQuery("FROM Curso c WHERE c.nome LIKE :termo")
                    .setParameter("termo", "%" + termo.toUpperCase() + "%")
                    .list();
            return cursos;
        } catch (HibernateException e) {
            System.out.println("query error: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Transactional(readOnly = true)
    public int count() {
        try {
            int result = ((Long) this.sessionFactory.getCurrentSession()
                    .createQuery("SELECT COUNT(c) FROM Curso c")
                    .uniqueResult()).intValue();
            return result;
        } catch (HibernateException e) {
            System.out.println("query error: " + e.getMessage());
            return 0;
        }
    }

}
