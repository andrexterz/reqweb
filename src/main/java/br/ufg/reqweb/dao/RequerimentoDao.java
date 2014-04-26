/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.reqweb.dao;

import br.ufg.reqweb.model.BaseModel;
import br.ufg.reqweb.model.ItemRequerimento;
import br.ufg.reqweb.model.Requerimento;
import br.ufg.reqweb.model.TipoRequerimentoEnum;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
public class RequerimentoDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Transactional
    public void adicionar(Requerimento requerimento) {
        Session session = this.sessionFactory.getCurrentSession();
        requerimento.setDataCriacao(new Date());
        session.save(requerimento);
        for (ItemRequerimento item : requerimento.getItemRequerimentoList()) {
            session.save(item);
        }
    }

    @Transactional
    public void atualizar(Requerimento requerimento) {
        Session session = this.sessionFactory.getCurrentSession();
        requerimento.setDataModificacao(new Date());
        session.update(requerimento);
        for (ItemRequerimento item : requerimento.getItemRequerimentoList()) {
            session.saveOrUpdate(item);
        }
    }

    @Transactional
    public void excluir(Requerimento requerimento) {
        this.sessionFactory.getCurrentSession().delete(requerimento);
    }

    @Transactional(readOnly = true)
    public Requerimento findById(Long id) {
        Requerimento requerimento;
        try {
            requerimento = (Requerimento) this.sessionFactory.getCurrentSession().get(Requerimento.class, id);
        } catch (HibernateException e) {
            requerimento = null;
        }
        return requerimento;
    }

    public List<Requerimento> find(String termo) {
        return null;
    }

    public List<Requerimento> find(String termo, TipoRequerimentoEnum tipoRequerimento) {
        return null;
    }
    
    public List<Requerimento> find(int first, int pageSize) {
        return null;
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
