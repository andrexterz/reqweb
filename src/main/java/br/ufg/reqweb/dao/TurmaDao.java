/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.reqweb.dao;

import br.ufg.reqweb.model.Curso;
import br.ufg.reqweb.model.Periodo;
import br.ufg.reqweb.model.Turma;
import br.ufg.reqweb.model.Usuario;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
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
                        .add(Restrictions.like("d.nome", termo.toUpperCase(), MatchMode.ANYWHERE))
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
    public List<Turma> find(Usuario docente) {
        try {
            Criteria criteria = this.sessionFactory.getCurrentSession().createCriteria(Turma.class);
            criteria.add(Restrictions.eq("docente", docente));
            return criteria.list();
        } catch (HibernateException | NumberFormatException e) {
            System.out.println("query error: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Transactional(readOnly = true)
    public List<Turma> find(String termo, Curso curso, boolean periodoAtivo) {
        try {
            Criteria criteria = this.sessionFactory.getCurrentSession().createCriteria(Turma.class);
            if (periodoAtivo) {
                criteria.createAlias("periodo", "p")
                        .add(Restrictions.eq("p.ativo", periodoAtivo));
            }
            if (termo != null && !termo.isEmpty()) {
                criteria.createAlias("disciplina", "d")
                        .add(Restrictions.like("d.nome", termo.toUpperCase(), MatchMode.ANYWHERE));
            }
            if (curso != null) {
                criteria.add(Restrictions.and(Restrictions.eq("d.curso", curso)));
            }
            criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
            return criteria.list();
        } catch (HibernateException | NumberFormatException e) {
            System.out.println("query error: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Transactional(readOnly = true)
    public List<Turma> find(int firstResult, int maxResult, String sortField, String sortOrder, Map<String, Object> filters) {
        if (filters == null) {
            filters = new HashMap();
        }
        try {
            Criteria criteria = this.sessionFactory.getCurrentSession().createCriteria(Turma.class);
            criteria.createAlias("disciplina", "d");
            for (String field : filters.keySet()) {
                if (field.equals("termo")) {
                    criteria.add(Restrictions.and(Restrictions.like("d.nome", filters.get(field).toString(), MatchMode.ANYWHERE).ignoreCase()));
                }
                if (field.equals("periodo")) {
                    criteria.add(Restrictions.and(Restrictions.eq("periodo", filters.get(field))));
                }
                if (field.equals("curso")) {
                    criteria.add(Restrictions.and(Restrictions.eq("d.curso", filters.get(field))));
                }
            }
            if ((sortField != null && !sortField.isEmpty()) && (sortOrder != null && !sortOrder.isEmpty())) {
                System.out.format("sorted by: %s, ordering %s\n", sortField, sortOrder);
                if (sortOrder.toLowerCase().equals("asc")) {
                    criteria.addOrder(Property.forName(sortField).asc());
                }
                if (sortOrder.toLowerCase().equals("desc")) {
                    criteria.addOrder(Property.forName(sortField).desc());
                }
            }
            criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
            return criteria.list();
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

    @Transactional(readOnly = true)
    public int count(Map<String, Object> filters) {
        if (filters == null) {
            filters = new HashMap();
        }
        try {
            Criteria criteria = this.sessionFactory.getCurrentSession().createCriteria(Turma.class);
            criteria.createAlias("disciplina", "d");
            for (String field : filters.keySet()) {
                if (field.equals("termo")) {
                    criteria.add(Restrictions.and(Restrictions.like("d.nome", filters.get(field).toString(), MatchMode.ANYWHERE).ignoreCase()));
                }
                if (field.equals("periodo")) {
                    criteria.add(Restrictions.and(Restrictions.eq("periodo", filters.get(field))));
                }
                if (field.equals("curso")) {
                    criteria.add(Restrictions.and(Restrictions.eq("d.curso", filters.get(field))));
                }
            }
            criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
            return ((Long) criteria.setProjection(Projections.rowCount()).uniqueResult()).intValue();
        } catch (HibernateException e) {
            System.out.println("query error: " + e.getMessage());
            return 0;
        }

    }

}
