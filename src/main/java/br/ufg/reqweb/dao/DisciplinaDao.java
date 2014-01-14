/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufg.reqweb.dao;

import br.ufg.reqweb.model.Disciplina;

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
public class DisciplinaDao {
    
    @Autowired
    private SessionFactory sessionFactory;
    
    @Transactional
    public void adicionar(Disciplina disciplina) {
        this.sessionFactory.getCurrentSession().save(disciplina);
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
    public List<Disciplina> listar() {
        return null;
    }

    @Transactional(readOnly = true)
    public Disciplina buscar(Long id) {
        Disciplina disciplina;
        try {
            disciplina = (Disciplina) this.sessionFactory.getCurrentSession().get(Disciplina.class, id);
        } catch (HibernateException e) {
            System.out.println("query error: " + e.getMessage());
            disciplina = null;
        }
        return disciplina;
    }

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<Disciplina> procurar(String termo) {
        try {
            Query query = this.sessionFactory.getCurrentSession()
                    .createSQLQuery("SELECT * FROM Disciplina d WHERE d.nome = :termo")
                    .addEntity(Disciplina.class);
            query.setParameter("termo", termo);
            return query.list();
        } catch (HibernateException | NumberFormatException e) {
            System.out.println("query error: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
