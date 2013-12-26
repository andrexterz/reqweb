/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.reqweb.model;

import java.io.Serializable;

/**
 *
 * @author andre
 */
public enum PerfilEnum implements Serializable {

        ADMINISTRADOR           ("100", "Administrador"),
        DOCENTE                 ("101", "Docente"),
        COORDENADOR_DE_CURSO    ("101", "Coordenador de Curso"),
        COORDENADOR_DE_ESTAGIO  ("101", "Coordenador de Estágio"),
        SECRETARIA              ("201", "Secretaria"),
        DISCENTE                ("500", "Discente");

        private PerfilEnum(String grupo, String papel) {
            this.grupo = grupo;
            this.papel = papel;
        }

        private final String grupo;
        private final String papel;

        public int getId() {
            return ordinal();
        }

        public String getGrupo() {
            return grupo;
        }

        public String getPapel() {
            return papel;
        }

      	public static PerfilEnum getPerfil(int id) {
            return PerfilEnum.values()[id];
        }
}
