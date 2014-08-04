/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.reqweb.dao;

import br.ufg.reqweb.model.Curso;
import br.ufg.reqweb.model.PerfilEnum;
import br.ufg.reqweb.model.Usuario;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
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

@Repository
@Scope(value = "singleton")
public class UsuarioDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Transactional
    public void adicionar(List<Usuario> usuarios) {
        Session session = this.sessionFactory.getCurrentSession();
        int counter = 0;
        boolean userExists;
        List<String> loginNames = session.createQuery("SELECT login FROM Usuario u").list();
        for (Usuario usuario : usuarios) {
            userExists = loginNames.contains(usuario.getLogin());
            if (!userExists) {
                session.save(usuario);
                if (++counter % 20 == 0) {
                    session.flush();
                    session.clear();
                }
            }
        }
    }

    @Transactional
    public void atualizar(Usuario usuario) {
        Session session = this.sessionFactory.getCurrentSession();
        session.update(usuario);
    }

    @Transactional
    public void atualizar(List<Usuario> usuarios) {
        Session session = this.sessionFactory.getCurrentSession();
        int counter = 0;
        for (Usuario u: usuarios) {
            session.update(u);
            if (++counter % 20 == 0) {
                session.flush();
                session.clear();
            }
        }
    }

    @Transactional
    public void excluir(Usuario usuario) {
        this.sessionFactory.getCurrentSession().delete(usuario);
    }

    @Transactional(readOnly = true)
    public List<Usuario> findAll() {
        try {
            List<Usuario> usuarioList = this.sessionFactory.getCurrentSession().createQuery("FROM Usuario u ORDER BY u.nome ASC").list();
            return usuarioList;
        } catch (HibernateException e) {
            System.out.println("query error: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Transactional(readOnly = true)
    public Usuario findById(Long id) {
        Usuario usuario;
        try {
            usuario = (Usuario) this.sessionFactory.getCurrentSession().get(Usuario.class, id);
        } catch (HibernateException e) {
            usuario = null;
        }
        return usuario;
    }

    @Transactional(readOnly = true)
    public Usuario findByLogin(String login) {
        Usuario usuario;
        try {
            usuario = (Usuario) this.sessionFactory.getCurrentSession()
                    .createCriteria(Usuario.class)
                    .add(Restrictions.eq("login", login))
                    .uniqueResult();
        } catch (HibernateException e) {
            usuario = null;
        }
        return usuario;
    }

    @Transactional(readOnly = true)
    public List<Usuario> find(String termo) {
        try {
            return this.sessionFactory.getCurrentSession()
                    .createCriteria(Usuario.class)
                    .add(Restrictions.or(Restrictions.eq("matricula", termo),
                                    Restrictions.like("nome", termo, MatchMode.ANYWHERE).ignoreCase()))
                    .setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY)
                    .list();
        } catch (HibernateException e) {
            System.out.println("query error: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Transactional(readOnly = true)
    public List<Usuario> find(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters) {
        Criteria criteria = this.sessionFactory.getCurrentSession().createCriteria(Usuario.class);
        if (filters == null) {
            filters = new HashMap();
        }
        try {
            if (!filters.isEmpty()) {
                for (String field : filters.keySet()) {
                    if (field.equals("termo")) {
                        String termo = (String) filters.get("termo");
                        criteria.add(Restrictions.or(Restrictions.like("nome", termo, MatchMode.ANYWHERE).ignoreCase(),
                                Restrictions.eq("matricula", termo)));
                    }
                    if (field.equals("tipoPerfil")) {
                        PerfilEnum tipoPerfil = (PerfilEnum) filters.get("tipoPerfil");
                        criteria.createAlias("perfilList", "p");
                        criteria.add(Restrictions.and(Restrictions.eq("p.tipoPerfil", tipoPerfil)));

                        Curso curso = (Curso) filters.get("curso");
                        if (curso != null) {
                            System.out.println("curso: " + curso.getNome());
                            criteria.add(Restrictions.and(Restrictions.eq("p.curso", curso)));
                        } else {
                            System.out.println("curso is null");
                        }
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
                criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
                criteria.setFirstResult(first);
                criteria.setMaxResults(pageSize);
                return criteria.list();
            } else {
                return this.sessionFactory.getCurrentSession()
                        .createQuery(String.format("SELECT DISTINCT u FROM Usuario u order by u.%1$s %2$s", sortField, sortOrder))
                        .setFirstResult(first)
                        .setMaxResults(pageSize)
                        .list();
            }
        } catch (HibernateException e) {
            System.out.println("query error: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Transactional(readOnly = true)
    public List<Usuario> find(String termo, PerfilEnum tipoPerfil) {
        try {
            Criteria criteria = this.sessionFactory.getCurrentSession().createCriteria(Usuario.class);
            if (termo != null && !termo.isEmpty()) {

                criteria.add(Restrictions.or(Restrictions.eq("matricula", termo),
                        Restrictions.like("nome", termo, MatchMode.ANYWHERE).ignoreCase()));
            }

            if (tipoPerfil != null) {
                criteria.createAlias("perfilList", "p");
                criteria.add(Restrictions.and(Restrictions.eq("p.tipoPerfil", tipoPerfil)));
            }
            criteria.addOrder(Property.forName("nome").asc());
            criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
            return criteria.list();
        } catch (HibernateException e) {
            System.out.println("find() -> query error: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Transactional(readOnly = true)
    public List<Usuario> find(PerfilEnum tipoPerfil) {
        try {
            List<Usuario> usuarios = this.sessionFactory.getCurrentSession().
                    createQuery("SELECT u FROM Usuario u JOIN u.perfilList p WHERE p.tipoPerfil = :tipoPerfil ORDER BY p.curso, u.nome ASC")
                    .setParameter("tipoPerfil", tipoPerfil)
                    .list();
            return usuarios;
        } catch (HibernateException e) {
            System.out.println("query error: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Transactional(readOnly = true)
    public List<Usuario> find(PerfilEnum tipoPerfil, Curso curso) {
        try {
            Criteria criteria = this.sessionFactory.getCurrentSession()
                    .createCriteria(Usuario.class)
                    .createCriteria("perfilList")
                    .add(Restrictions.eq("tipoPerfil", tipoPerfil));
            if (curso != null) {
                criteria.add(Restrictions.eq("curso", curso));
            }
            return criteria.list();

        } catch (HibernateException e) {
            System.out.println("query error: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Transactional(readOnly = true)
    public int count() {
        try {
            int result = ((Long) this.sessionFactory.getCurrentSession()
                    .createQuery("SELECT COUNT(u) FROM Usuario u")
                    .uniqueResult()).intValue();
            return result;
        } catch (HibernateException e) {
            System.out.println("query error: " + e.getMessage());
            return 0;
        }
    }

    @Transactional(readOnly = true)
    public int count(Map<String, Object> filters) {
        try {
            Criteria criteria = this.sessionFactory.getCurrentSession().createCriteria(Usuario.class);
            if (filters == null) {
                filters = new HashMap();
            }
            for (String field : filters.keySet()) {
                if (field.equals("termo")) {
                    String termo = (String) filters.get("termo");
                    criteria.add(Restrictions.or(Restrictions.like("nome", termo, MatchMode.ANYWHERE).ignoreCase(),
                            Restrictions.eq("matricula", termo)));
                }
                if (field.equals("tipoPerfil")) {
                    PerfilEnum tipoPerfil = (PerfilEnum) filters.get("tipoPerfil");
                    criteria.createAlias("perfilList", "p");
                    criteria.add(Restrictions.and(Restrictions.eq("p.tipoPerfil", tipoPerfil)));
                    Curso curso = (Curso) filters.get("curso");
                    if (curso != null) {
                        System.out.println("curso: " + curso.getNome());
                        criteria.add(Restrictions.and(Restrictions.eq("p.curso", curso)));
                    } else {
                        System.out.println("curso is null");
                    }
                }
            }
            criteria.setProjection(Projections.distinct(Projections.id()));
            return ((Long) criteria.setProjection(Projections.rowCount()).uniqueResult()).intValue();
        } catch (HibernateException e) {
            System.out.println("count -> query error: " + e.getMessage());
            return 0;
        }
    }
}
