/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.reqweb.dao;

import br.ufg.reqweb.model.Arquivo;
import br.ufg.reqweb.model.Atendimento;
import br.ufg.reqweb.model.ItemRequerimento;
import br.ufg.reqweb.model.Requerimento;
import br.ufg.reqweb.model.TipoRequerimentoEnum;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Property;
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
            Hibernate.initialize(requerimento.getItemRequerimentoList());
            return requerimento;
        } catch (HibernateException e) {
            System.out.println("query error: " + e.getMessage());
        }
        return null;
    }

    /**
     * find by discente nome
     *
     * @param termo
     * @param sortField
     * @param sortOrder
     * @return
     */
    @Transactional(readOnly = true)
    public List<Requerimento> find(String termo, String sortField, String sortOrder) {
        try {
            List<Requerimento> requerimentos;
            Criteria criteria = this.sessionFactory.getCurrentSession()
                    .createCriteria(Requerimento.class)
                    .createAlias("discente", "d")
                    .add(Restrictions.or(Restrictions.eq("d.matricula", termo),
                                    Restrictions.like("d.nome", termo, MatchMode.ANYWHERE).ignoreCase()))
                    .setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);

            if ((sortField != null && !sortField.isEmpty()) && (sortOrder != null && !sortOrder.isEmpty())) {
                if (sortOrder.toLowerCase().equals("asc")) {
                    criteria.addOrder(Property.forName(sortField).asc());
                }
                if (sortOrder.toLowerCase().equals("desc")) {
                    criteria.addOrder(Property.forName(sortField).desc());
                }
            }
            return criteria.list();
        } catch (HibernateException e) {
            System.out.println("query error: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     *
     * @param tipoRequerimento
     * @param sortField
     * @param sortOrder
     * @return
     */
    @Transactional(readOnly = true)
    public List<Requerimento> find(TipoRequerimentoEnum tipoRequerimento, String sortField, String sortOrder) {
        try {
            Criteria criteria = this.sessionFactory.getCurrentSession()
                    .createCriteria(Requerimento.class)
                    .add(Restrictions.eq("tipoRequerimento", tipoRequerimento));
            if ((sortField != null && !sortField.isEmpty()) && (sortOrder != null && !sortOrder.isEmpty())) {
                if (sortOrder.toLowerCase().equals("asc")) {
                    criteria.addOrder(Property.forName(sortField).asc());
                }
                if (sortOrder.toLowerCase().equals("desc")) {
                    criteria.addOrder(Property.forName(sortField).desc());
                }
            }
            return criteria.list();
        } catch (HibernateException e) {
            System.out.println("query error: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     *
     * @param login
     * @param tipoRequerimento
     * @param sortField
     * @param sortOrder
     * @return
     */
    @Transactional(readOnly = true)
    public List<Requerimento> find(String login, TipoRequerimentoEnum tipoRequerimento, String sortField, String sortOrder) {
        try {
            Criteria criteria = this.sessionFactory.getCurrentSession()
                    .createCriteria(Requerimento.class)
                    .createAlias("discente", "d")
                    .add(Restrictions.eq("d.login", login).ignoreCase())
                    .add(Restrictions.eq("tipoRequerimento", tipoRequerimento));
            if ((sortField != null && !sortField.isEmpty()) && (sortOrder != null && !sortOrder.isEmpty())) {
                if (sortOrder.toLowerCase().equals("asc")) {
                    criteria.addOrder(Property.forName(sortField).asc());
                }
                if (sortOrder.toLowerCase().equals("desc")) {
                    criteria.addOrder(Property.forName(sortField).desc());
                }
            }
            return criteria.list();
        } catch (HibernateException e) {
            System.out.println("query error: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * find by dataCriacao interval
     *
     * @param dateA
     * @param dateB
     * @param sortField
     * @param sortOrder
     * @return
     */
    @Transactional(readOnly = true)
    public List<Requerimento> find(Date dateA, Date dateB, String sortField, String sortOrder) {
        try {
            Criteria criteria = this.sessionFactory.getCurrentSession()
                    .createCriteria(Requerimento.class)
                    .add(Restrictions.and(Restrictions.between("dataCriacao", dateA, dateB)));
            if ((sortField != null && !sortField.isEmpty()) && (sortOrder != null && !sortOrder.isEmpty())) {
                if (sortOrder.toLowerCase().equals("asc")) {
                    criteria.addOrder(Property.forName(sortField).asc());
                }
                if (sortOrder.toLowerCase().equals("desc")) {
                    criteria.addOrder(Property.forName(sortField).desc());
                }
            }
            return criteria.list();
        } catch (HibernateException e) {
            System.out.println("query error: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     *
     * @param login
     * @param dateA
     * @param dateB
     * @param sortField
     * @param sortOrder
     * @return
     */
    @Transactional(readOnly = true)
    public List<Requerimento> find(String login, Date dateA, Date dateB, String sortField, String sortOrder) {
        try {
            Criteria criteria = this.sessionFactory.getCurrentSession()
                    .createCriteria(Requerimento.class)
                    .createAlias("discente", "d")
                    .add(Restrictions.eq("d.login", login).ignoreCase())
                    .add(Restrictions.and(Restrictions.between("dataCriacao", dateA, dateB)));
            if ((sortField != null && !sortField.isEmpty()) && (sortOrder != null && !sortOrder.isEmpty())) {
                if (sortOrder.toLowerCase().equals("asc")) {
                    criteria.addOrder(Property.forName(sortField).asc());
                }
                if (sortOrder.toLowerCase().equals("desc")) {
                    criteria.addOrder(Property.forName(sortField).desc());
                }
            }
            return criteria.list();
        } catch (HibernateException e) {
            System.out.println("query error: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     *
     * @param first
     * @param pageSize
     * @param sortField
     * @param sortOrder
     * @return
     */
    @Transactional(readOnly = true)
    public List<Requerimento> find(int first, int pageSize, String sortField, String sortOrder) {
        try {
            Criteria criteria = this.sessionFactory.getCurrentSession()
                    .createCriteria(Requerimento.class)
                    .setFirstResult(first)
                    .setMaxResults(pageSize);
            if ((sortField != null && !sortField.isEmpty()) && (sortOrder != null && !sortOrder.isEmpty())) {
                if (sortOrder.toLowerCase().equals("asc")) {
                    criteria.addOrder(Property.forName(sortField).asc());
                }
                if (sortOrder.toLowerCase().equals("desc")) {
                    criteria.addOrder(Property.forName(sortField).desc());
                }
            }
            return criteria.list();
        } catch (HibernateException e) {
            System.out.println("query error: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     *
     * @param login
     * @param first
     * @param pageSize
     * @param sortField
     * @param sortOrder
     * @return
     */
    @Transactional(readOnly = true)
    public List<Requerimento> find(String login, int first, int pageSize, String sortField, String sortOrder) {
        try {
            Criteria criteria = this.sessionFactory.getCurrentSession()
                    .createCriteria(Requerimento.class)
                    .createAlias("discente", "d")
                    .add(Restrictions.eq("d.login", login).ignoreCase())
                    .setFirstResult(first)
                    .setMaxResults(pageSize);
            if ((sortField != null && !sortField.isEmpty()) && (sortOrder != null && !sortOrder.isEmpty())) {
                if (sortOrder.toLowerCase().equals("asc")) {
                    criteria.addOrder(Property.forName(sortField).asc());
                }
                if (sortOrder.toLowerCase().equals("desc")) {
                    criteria.addOrder(Property.forName(sortField).desc());
                }
            }
            return criteria.list();
        } catch (HibernateException e) {
            System.out.println("query error: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Transactional(readOnly = true)
    public List<Requerimento> find(String sortField, String sortOrder, Map<String, Object> filters) {
        if (filters == null) filters = new HashMap();
        try {
            Criteria criteria = this.sessionFactory.getCurrentSession().createCriteria(Requerimento.class);
            for (String field : filters.keySet()) {
                /**
                 * login eq / tipoRequerimento eq / termo like / dataCriacao between
                 */
                if (field.equals("login")) {
                    criteria.createAlias("discente", "d");
                    criteria.add(Restrictions.eq("d.login", filters.get(field)));
                }
                if (field.equals("tipoRequerimento")) {
                    criteria.add(Restrictions.eq(field, filters.get(field)));
                }
                if (field.equals("termo")) {
                    criteria.createAlias("discente", "d");
                    criteria.add(Restrictions.or(Restrictions.eq("d.matricula", filters.get("termo")),
                            Restrictions.like("d.nome",filters.get("termo").toString(), MatchMode.ANYWHERE).ignoreCase()))
                            .setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
                }
                if (field.equals("dataCriacao")) {
                    Date[] arrayDate = (Date[]) filters.get("dataCriacao");
                    criteria.add(Restrictions.and(Restrictions.between("dataCriacao", arrayDate[0], arrayDate[1])));
                }
            }
            if ((sortField != null && !sortField.isEmpty()) && (sortOrder != null && !sortOrder.isEmpty())) {
                if (sortOrder.toLowerCase().equals("asc")) {
                    criteria.addOrder(Property.forName(sortField).asc());
                }
                if (sortOrder.toLowerCase().equals("desc")) {
                    criteria.addOrder(Property.forName(sortField).desc());
                }
            }
            return criteria.list();
        } catch (HibernateException e) {
            System.out.println("query error: " + e.getMessage());
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
    public List<ItemRequerimento> findItemRequerimentoList(Requerimento requerimento) {
        try {
            Criteria criteria = this.sessionFactory.getCurrentSession().createCriteria(ItemRequerimento.class);
            criteria.add(Restrictions.eq("requerimento", requerimento));
            return criteria.list();
        } catch (HibernateException e) {
            System.out.println("query error: " + e.getMessage());
            return new ArrayList<>();
        }        
    }
    
    @Transactional(readOnly = true)
    public List<Atendimento> findAtendimento(Requerimento requerimento) {
        try {
            Criteria criteria = this.sessionFactory.getCurrentSession().createCriteria(Atendimento.class);
            criteria.add(Restrictions.eq("requerimento", requerimento));
            return criteria.list();
        } catch (HibernateException e) {
            System.out.println("query error: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    @Transactional(readOnly = true)
    public List<Arquivo> findArquivos(ItemRequerimento itemRequerimento) {
        try {
            Criteria criteria = this.sessionFactory.getCurrentSession().createCriteria(Arquivo.class);
            criteria.add(Restrictions.eq("itemRequerimento", itemRequerimento));
            return criteria.list();
        } catch (HibernateException e) {
            System.out.println("query error: " + e.getMessage());
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
