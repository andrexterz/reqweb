/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.reqweb.dao;

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
                .createSQLQuery("select u.matricula, u.nome, c.nome as curso, ip.indiceprioridade from usuario u join indiceprioridade ip on ip.discente_id=u.id join perfil p on p.usuario_id=u.id join curso c on p.curso_id=c.id order by curso asc, ip.indiceprioridade desc");
        query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        List<Map<String, Object>> queryResult = query.list();
        return query.list();
    }
    
    @Transactional
    public Map<String, ?> listjusteDeMatriculaMap() {
        Query query = this.sessionFactory.getCurrentSession()
                .createSQLQuery(
                        "select d.nome as diciplina, u.matricula, u.nome as discente, t.nome as turma, i.tipodeajuste\n" +
                        "from itemrequerimento i\n" +
                        "join turma t on i.turma_id=t.id\n" +
                        "join disciplina d on t.disciplina_id=d.id\n" +
                        "join requerimento r on i.requerimento_id=r.id\n" +
                        "join  usuario u on r.usuario_id=u.id\n" +
                        "order by d.id asc, i.tipodeajuste asc, t.id asc, u.nome asc;");
        query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        Map<String, List<Map<String, Object>>> result = new HashMap();
        List<Map<String, Object>> queryResult = query.list();
        for (Map<String, Object> itemResult: queryResult) {
            String itemKey = String.format("%s: %s", itemResult.get("disciplina"), itemResult.get("turma"));
            Map<String, Object> itemList = new HashMap();
            itemList.put("matricula", itemResult.get("matricula"));
            itemList.put("discente", itemResult.get("discente"));
            itemList.put("tipodeajuste", itemResult.get("tipodeajuste"));
            if (!result.containsKey(itemKey)) {
                List<Map<String, Object>> itemValue = new ArrayList<>();
                itemValue.add(itemList);
                result.put(itemKey, itemValue);
            } else {
                result.get(itemKey).add(itemList);
            }
        }
        return result;
    }
}
