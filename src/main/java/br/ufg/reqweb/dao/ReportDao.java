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
import br.ufg.reqweb.model.Usuario;
import java.util.ArrayList;
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
                        "select dis.nome as discente, dis.matricula, doc.email, doc.nome as docente, i.tipodedocumento, c.nome as curso\n"
                        + "from requerimento r\n"
                        + "join itemrequerimento i on i.requerimento_id=r.id\n"
                        + "join usuario dis on r.usuario_id=dis.id\n"
                        + "join perfil p_dis on p_dis.usuario_id=dis.id\n"
                        + "join curso c on p_dis.curso_id=c.id\n"
                        + "join perfil p_doc on p_doc.curso_id=p_dis.curso_id and p_doc.tipoperfil = :tipoPerfil\n"
                        + "join usuario doc on p_doc.usuario_id=doc.id\n"
                        + "where r.tiporequerimento = :tipoRequerimento and c.id = :cursoId and r.status = :status"
                );
        query.setString("tipoRequerimento", TipoRequerimentoEnum.DOCUMENTO_DE_ESTAGIO.name());
        query.setString("tipoPerfil", PerfilEnum.COORDENADOR_DE_ESTAGIO.name());
        query.setLong("cursoId", curso.getId());
        query.setString("status", RequerimentoStatusEnum.EM_ANDAMENTO.name());
        query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        return query.list();
    }

    @Transactional(readOnly = true)
    public List<Map<String, ?>> listDocumentoDeEstagioMap(RequerimentoStatusEnum status) {
        Query query = this.sessionFactory.getCurrentSession()
                .createSQLQuery(
                        "select dis.nome as discente, dis.matricula, doc.email, doc.nome as docente, i.tipodedocumento, c.nome as curso\n"
                        + "from requerimento r\n"
                        + "join itemrequerimento i on i.requerimento_id=r.id\n"
                        + "join usuario dis on r.usuario_id=dis.id\n"
                        + "join perfil p_dis on p_dis.usuario_id=dis.id\n"
                        + "join curso c on p_dis.curso_id=c.id\n"
                        + "join perfil p_doc on p_doc.curso_id=p_dis.curso_id and p_doc.tipoperfil=:tipoPerfil\n"
                        + "join usuario doc on p_doc.usuario_id=doc.id\n"
                        + "where r.tiporequerimento = :tipoRequerimento and r.status = :status"
                );
        query.setString("tipoRequerimento", TipoRequerimentoEnum.DOCUMENTO_DE_ESTAGIO.name());
        query.setString("tipoPerfil", PerfilEnum.COORDENADOR_DE_ESTAGIO.name());
        query.setString("status", status.name());
        query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        return query.list();
    }

    @Transactional(readOnly = true)
    public List<Map<String, ?>> listSegundaChamadaDeProvaMap(RequerimentoStatusEnum status, Curso curso) {
        Query query = this.sessionFactory.getCurrentSession()
                .createSQLQuery(
                        "select dis.nome as discente, dis.matricula, doc.nome as docente, doc.email, d.nome as disciplina, t.nome as turma, c.nome as curso, i.dataprovaa as data_prova, r.datacriacao as data_requerimento\n"
                        + "from requerimento r\n"
                        + "join itemrequerimento i on i.requerimento_id=r.id\n"
                        + "join turma t on i.turma_id=t.id\n"
                        + "join disciplina d on t.disciplina_id=d.id\n"
                        + "join usuario dis on r.usuario_id=dis.id\n"
                        + "join perfil p on p.usuario_id=dis.id\n"
                        + "join curso c on p.curso_id=c.id\n"
                        + "join usuario doc on t.docente_id=doc.id\n"
                        + "where r.tiporequerimento = :tipoRequerimento and r.status = :status\n"
                        + (curso == null ? "" : "and c.id = :cursoId")
                );
        if (curso != null) {
            query.setLong("cursoId", curso.getId());
        }
        query.setString("tipoRequerimento", TipoRequerimentoEnum.SEGUNDA_CHAMADA_DE_PROVA.name());
        query.setString("status", status.name());
        query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        return query.list();
    }

    @Transactional(readOnly = true)
    public List<Map<String, ?>> listSegundaChamadaDeProvaMap(RequerimentoStatusEnum status, Usuario usuario) {
        Query query = this.sessionFactory.getCurrentSession()
                .createSQLQuery(
                        "select dis.nome as discente, dis.matricula, doc.nome as docente, doc.email, d.nome as disciplina, t.nome as turma, c.nome as curso, i.dataprovaa as data_prova, r.datacriacao as data_requerimento\n"
                        + "from requerimento r\n"
                        + "join itemrequerimento i on i.requerimento_id=r.id\n"
                        + "join turma t on i.turma_id=t.id\n"
                        + "join disciplina d on t.disciplina_id=d.id\n"
                        + "join usuario dis on r.usuario_id=dis.id\n"
                        + "join perfil p on p.usuario_id=dis.id\n"
                        + "join curso c on p.curso_id=c.id\n"
                        + "join usuario doc on t.docente_id=doc.id\n"
                        + "where r.tiporequerimento = :tipoRequerimento and doc.id = :usuarioId and r.status = :status"
                );
        query.setString("tipoRequerimento", TipoRequerimentoEnum.SEGUNDA_CHAMADA_DE_PROVA.name());
        query.setString("status", status.name());
        query.setLong("usuarioId", usuario.getId());
        query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        return query.list();
    }

    @Transactional(readOnly = true)
    public List<Map<String, ?>> listTotalRequerimento(PerfilEnum perfil, Curso curso, List<RequerimentoStatusEnum> statusList, List<TipoRequerimentoEnum> tipoRequerimentoList) {
        List<String> tipoRequerimentoNames = new ArrayList<>();
        List<String> statusNames = new ArrayList<>();
        for (TipoRequerimentoEnum tipoRequerimento : tipoRequerimentoList) {
            tipoRequerimentoNames.add(tipoRequerimento.name());
        }
        for (RequerimentoStatusEnum status : statusList) {
            statusNames.add(status.name());
        }
        Query query;
        if (perfil == null) {
            query = this.sessionFactory.getCurrentSession().createSQLQuery(
                    "select c.nome as curso, r.tiporequerimento as requerimento,\n"
                    + "r.status as status, count(r.tiporequerimento) as total\n"
                    + "from requerimento r\n"
                    + "join usuario u on u.id=r.usuario_id\n"
                    + "join perfil p on p.usuario_id=u.id\n"
                    + "join curso c on c.id=p.curso_id\n"
                    + "group by c.nome, r.tiporequerimento, r.status\n"
                    + "having r.status in :status\n"
                    + "and r.tiporequerimento in :tipoRequerimento\n"
                    + (curso == null ? "" : "and c.id = :cursoId\n")
                    + "order by c.nome asc, r.tiporequerimento asc"
            );
            if (curso != null) {
                query.setLong("cursoId", curso.getId());
            }
        } else {
            query = this.sessionFactory.getCurrentSession().createSQLQuery(
                    "select coord.email, cdis.nome as curso,\n"
                    + "r.tiporequerimento as requerimento,\n"
                    + "r.status as status,\n"
                    + "count(r.tiporequerimento) as total\n"
                    + "from requerimento r\n"
                    + "join usuario dis on dis.id=r.usuario_id\n"
                    + "join perfil pdis on pdis.usuario_id=dis.id\n"
                    + "join curso cdis on cdis.id=pdis.curso_id\n"
                    + "join perfil pcoord on pcoord.curso_id=cdis.id\n"
                    + "and pcoord.tipoperfil = :perfil\n"
                    + "join usuario coord on pcoord.usuario_id=coord.id\n"
                    + "group by coord.email, cdis.nome, r.tiporequerimento, r.status\n"
                    + "having r.status in :status\n"
                    + "and r.tiporequerimento in :tipoRequerimento\n"
                    + "order by cdis.nome asc, r.tiporequerimento asc;\n"
            );
            query.setParameter("perfil", perfil.name());
        }
        query.setParameterList("status", statusNames);
        query.setParameterList("tipoRequerimento", tipoRequerimentoNames);
        query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        return query.list();
    }

    @Transactional(readOnly = true)
    public List<Map<String, ?>> listRequerimentoByStatusMap(RequerimentoStatusEnum status) {
        Query query = this.sessionFactory.getCurrentSession().createSQLQuery(
                "select u.email, r.tiporequerimento as requerimento, r.status from requerimento r\n"
                + "join usuario u on u.id=r.usuario_id\n"
                + "where status = :status"
        );
        query.setString("status", status.name());
        query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        return query.list();
    }

    @Transactional(readOnly = true)
    public List<Map<String, ?>> listUsuarioMap(PerfilEnum tipoPerfil, Curso curso) {
        Query query = this.sessionFactory.getCurrentSession()
                .createSQLQuery(
                        "select u.matricula, u.nome, c.nome as curso, u.email, p.tipoperfil\n"
                        + "from usuario u\n"
                        + "join perfil p on p.usuario_id=u.id\n"
                        + "full outer join curso c on p.curso_id=c.id\n"
                        + "where p.tipoperfil = :tipoPerfil\n"
                        + (curso == null ? "" : "and c.id = :cursoId\n")
                        + "order by c.nome asc, u.nome asc;"
                );
        if (curso != null) {
            query.setLong("cursoId", curso.getId());
        }
        query.setString("tipoPerfil", tipoPerfil.name());
        query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        return query.list();
    }
}
