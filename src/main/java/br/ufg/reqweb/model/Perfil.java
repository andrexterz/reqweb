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
public enum Perfil implements Serializable {

        ADMINISTRADOR           ("100", "Administrador"),        
        COORDENADOR_DE_CURSO    ("101", "Coordenador de Curso"),
        COORDENADOR_DE_ESTAGIO  ("101", "Coordenador de Estágio"),
        DOCENTE                 ("101", "Docente"),
        SECRETARIA              ("201", "Secretaria"),
        DISCENTE                ("500", "Discente");

        private Perfil(String grupo, String papel) {
            this.grupo = grupo;
            this.papel = papel;
        }

        private final String grupo;
        private final String papel;
        private Curso curso = null;

        public int getId() {
            return ordinal();
        }

        public String getGrupo() {
            return grupo;
        }

        public String getPapel() {
            return papel;
        }

        public Curso getCurso() {
            return curso;
        }

        public void setCurso(Curso curso) {
            this.curso = curso;
        }

      	public static Perfil getPerfil(int id) {
            return Perfil.values()[id];
        }
}
