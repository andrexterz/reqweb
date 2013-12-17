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
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author andre
 */
@Repository
public class CursoDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Transactional
    public void adicionar(Curso curso) {
        Date timestamp = Calendar.getInstance().getTime();
        curso.setDataModificacao(timestamp);
        this.sessionFactory.getCurrentSession().save(curso);

    }

    @Transactional
    public void atualizar(Curso curso) {
        Date timestamp = Calendar.getInstance().getTime();
        curso.setDataModificacao(timestamp);
        this.sessionFactory.getCurrentSession().update(curso);
    }

    @Transactional
    public void excluir(Curso curso) {
        this.sessionFactory.getCurrentSession().delete(curso);
    }

    @Transactional(readOnly = true)
    public List<Curso> listar() {
        try {
            List cursoList = this.sessionFactory.getCurrentSession().createQuery("FROM Curso c ORDER BY c.dataModificacao DESC").list();
            return cursoList;
        } catch (HibernateException e) {
            System.out.println("query error: " + e.getMessage());
            return new ArrayList<>();
        }

    }

    @Transactional(readOnly = true)
    public Curso buscar(Long id) {
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
    public List<Curso> procurar(String termo) {
        try {
            Query query = this.sessionFactory.getCurrentSession().
                    createSQLQuery("SELECT * FROM Curso c WHERE c.nome ~* :termo")
                    .addEntity(Curso.class);
            query.setParameter("termo", termo);
            return query.list();
        } catch (HibernateException e) {
            System.out.println("query error: " + e.getMessage());
            return new ArrayList<>();
        }
    }

}
