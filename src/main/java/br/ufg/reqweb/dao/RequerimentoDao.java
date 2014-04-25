/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufg.reqweb.dao;

import br.ufg.reqweb.model.BaseModel;
import br.ufg.reqweb.model.Requerimento;
import java.util.List;
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

    @Transactional
    public void adicionar(Requerimento requerimento) {

    }

    @Transactional
    public void atualizar(Requerimento requerimento) {

    }

    @Transactional
    public void excluir(Requerimento requerimento) {

    }
    
    @Transactional(readOnly = true)
    public List<Requerimento> findById(Long id) {
        return null;

    }

    @Transactional(readOnly = true)
    public List<Requerimento> findAll() {
        return null;

    }
}
