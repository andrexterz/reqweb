/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufg.reqweb.dao;

import br.ufg.reqweb.model.Turma;

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
 * @author andre
 */

@Repository
public class TurmaDao {
    
    @Autowired
    private SessionFactory sessionFactory;
    
    @Transactional
    public void adicionar(Turma turma) {
        this.sessionFactory.getCurrentSession().save(turma);
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
    public List<Turma> listar() {
        return null;
    }

    @Transactional(readOnly = true)
    public Turma buscar(Long id) {
        Turma turma;
        try {
            turma = (Turma) this.sessionFactory.getCurrentSession().get(Turma.class, id);
        } catch (HibernateException e) {
            System.out.println("query error: " + e.getMessage());
            turma = null;
        }
        return turma;
    }

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<Turma> procurar(String termo) {
        try {
            Query query = this.sessionFactory.getCurrentSession().
                    createSQLQuery("SELECT * FROM Turma t WHERE t.nome = :termo")
                    .addEntity(Turma.class);
            query.setParameter("termo", termo);
            return query.list();
        } catch (HibernateException | NumberFormatException e) {
            System.out.println("query error: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
