/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.reqweb.dao;

import br.ufg.reqweb.model.Curso;
import br.ufg.reqweb.model.PerfilEnum;
import br.ufg.reqweb.model.Periodo;
import br.ufg.reqweb.model.RequerimentoStatusEnum;
import br.ufg.reqweb.model.TipoRequerimentoEnum;
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

    @Transactional(readOnly = true)
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
    
    @Transactional(readOnly = true)
    public List<Map<String, ?>> listIndicePrioridadeMap(Curso curso) {
        Query query = this.sessionFactory.getCurrentSession()
                .createSQLQuery("select u.matricula, u.nome, c.nome as curso, ip.indiceprioridade\n"
                        + "from usuario u\n"
                        + "join indiceprioridade ip on ip.discente_id=u.id\n"
                        + "join perfil p on p.usuario_id=u.id\n"
                        + "join curso c on p.curso_id=c.id\n"
                        + "where c.id=:cursoId\n"
                        + "order by curso asc, ip.indiceprioridade desc");
        query.setLong("cursoId", curso.getId());
        query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        return query.list();
    }

    @Transactional(readOnly = true)
    public List<Map<String, ?>> listAjusteDeMatriculaMap(Curso curso, Periodo periodo) {
        Query query = this.sessionFactory.getCurrentSession()
                .createSQLQuery(
                        "select d.nome as disciplina, u.matricula, u.nome as discente, t.nome as turma, i.tipodeajuste\n"
                        + "from itemrequerimento i\n"
                        + "join turma t on i.turma_id=t.id\n"
                        + "join periodo p on t.periodo_id=p.id\n"
                        + "join disciplina d on t.disciplina_id=d.id\n"
                        + "join requerimento r on i.requerimento_id=r.id\n"
                        + "join  usuario u on r.usuario_id=u.id\n"
                        + "where r.tiporequerimento=:tipoRequerimento and d.curso_id=:cursoId and p.id=:periodoId\n"
                        + "order by d.id asc, t.id asc, i.tipodeajuste asc, u.nome asc");
        query.setString("tipoRequerimento", TipoRequerimentoEnum.AJUSTE_DE_MATRICULA.name());
        query.setLong("cursoId", curso.getId());
        query.setLong("periodoId", periodo.getId());
        query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        return query.list();
    }

    @Transactional(readOnly = true)
    public List<Map<String, ?>> listDocumentoDeEstagioMap(Curso curso) {
        Query query = this.sessionFactory.getCurrentSession()
                .createSQLQuery(
                        "select dis.nome as discente, dis.matricula, doc.nome as docente, doc.email, i.tipodedocumento, c.nome as curso\n"
                        + "from requerimento r\n"
                        + "join itemrequerimento i on i.requerimento_id=r.id\n"
                        + "join usuario dis on r.usuario_id=dis.id\n"
                        + "join perfil p on p.usuario_id in (select usuario_id from perfil where tipoperfil=:tipoPerfil)\n"
                        + "join usuario doc on doc.id in (p.usuario_id)\n"
                        + "join curso c on c.id=p.curso_id\n"
                        + "where r.tiporequerimento = :tipoRequerimento  and p.curso_id = :cursoId and r.status = :status"
                );
        query.setString("tipoRequerimento", TipoRequerimentoEnum.DOCUMENTO_DE_ESTAGIO.name());
        query.setString("tipoPerfil",PerfilEnum.COORDENADOR_DE_ESTAGIO.name());
        query.setLong("cursoId", curso.getId());
        query.setString("status", RequerimentoStatusEnum.EM_ANDAMENTO.name());
        query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        return query.list();
    }
}
