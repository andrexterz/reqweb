/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.reqweb.dao;

import java.util.List;
import java.util.Map;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
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
public class ReportDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Transactional
    public List<Map<String, ?>> listIndicePrioridadeMap() {
        Query query = this.sessionFactory.getCurrentSession()
                .createSQLQuery("select u.matricula, u.nome, c.nome as curso, ip.indiceprioridade from usuario u join indiceprioridade ip on ip.discente_id=u.id join perfil p on p.usuario_id=u.id join curso c on p.curso_id=c.id order by curso asc, ip.indiceprioridade desc");
        query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        List<Map<String, Object>> queryResult = query.list();
        return query.list();
    }
    
    @Transactional
    public List<Map<String, ?>> listjusteDeMatriculaMap() {
        Query query = this.sessionFactory.getCurrentSession()
                .createSQLQuery("select u.matricula, u.nome, c.nome as curso, ip.indiceprioridade from usuario u join indiceprioridade ip on ip.discente_id=u.id join perfil p on p.usuario_id=u.id join curso c on p.curso_id=c.id order by curso asc, ip.indiceprioridade desc");
        query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        List<Map<String, Object>> queryResult = query.list();
        return query.list();
    }
}
