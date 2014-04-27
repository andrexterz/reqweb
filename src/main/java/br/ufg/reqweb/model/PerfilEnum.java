/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufg.reqweb.model;

import br.ufg.reqweb.components.LocaleBean;
import java.io.Serializable;

/**
 *
 * @author andre
 */
public enum PerfilEnum implements Serializable {

        ADMINISTRADOR           ("100", "administrador"),
        DOCENTE                 ("101", "docente"),
        COORDENADOR_DE_CURSO    ("101", "coordenadorDeCurso"),
        COORDENADOR_DE_ESTAGIO  ("101", "coordenadorDeEstagio"),
        SECRETARIA              ("201", "secretaria"),
        DISCENTE                ("500", "discente");

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
        
        public String getPapelLocale() {
            return LocaleBean.getMessageBundle().getString(papel);
        }

      	public static PerfilEnum getPerfil(int id) {
            return PerfilEnum.values()[id];
        }
}
