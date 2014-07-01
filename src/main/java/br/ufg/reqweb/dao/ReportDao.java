/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.reqweb.dao;

import br.ufg.reqweb.model.Periodo;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.hibernate.Query;
import org.hibernate.Session;
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
    public String findAjustes(Periodo periodo) {
        Session session = this.sessionFactory.getCurrentSession();
        return "";
    }

    @Transactional
    public ObjectMapper findIndicePrioridade() {
        Session session = this.sessionFactory.getCurrentSession();
        Query query = session.createSQLQuery("select u.matricula, u.nome, c.nome as curso, ip.indiceprioridade from usuario u join indiceprioridade ip on ip.discente_id=u.id join perfil p on p.usuario_id=u.id join curso c on p.curso_id=c.id order by ip.indiceprioridade desc");
        query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        List<Map<String, Object>> queryResult = query.list();
        ObjectMapper json = new ObjectMapper();
        try {
            json.writeValueAsBytes(queryResult);
            System.out.format("\n\tresult size: %d -> json data: %s\n", queryResult.size(), json.toString());
            return json;
        } catch (IOException e) {
            System.out.println("error: " + e.getMessage());
            return null;
        }
    }
}
