/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.reqweb.dao;

import br.ufg.reqweb.model.Curso;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
                .createSQLQuery("select u.matricula, u.nome, c.nome as curso, ip.indiceprioridade\n"
                        + "from usuario u\n"
                        + "join indiceprioridade ip on ip.discente_id=u.id\n"
                        + "join perfil p on p.usuario_id=u.id\n"
                        + "join curso c on p.curso_id=c.id\n"
                        + "order by curso asc, ip.indiceprioridade desc");
        query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        return query.list();
    }
    
    @Transactional
    public List<Map<String, ?>> listjusteDeMatriculaMap(Curso curso) {
        Query query = this.sessionFactory.getCurrentSession()
                .createSQLQuery(
                        "select d.nome as disciplina, u.matricula, u.nome as discente, t.nome as turma, i.tipodeajuste\n"
                        + "from itemrequerimento i\n"
                        + "join turma t on i.turma_id=t.id\n"
                        + "join disciplina d on t.disciplina_id=d.id\n"
                        + "join requerimento r on i.requerimento_id=r.id\n"
                        + "join  usuario u on r.usuario_id=u.id\n"
                        + "where d.curso_id = :cursoId\n"
                        + "order by d.id asc, t.id asc, i.tipodeajuste asc, u.nome asc");
        query.setLong("cursoId", curso.getId());
        query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        return query.list();
    }
}
