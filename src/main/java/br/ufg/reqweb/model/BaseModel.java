/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.ufg.reqweb.model;

import java.io.Serializable;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 *
 * @author andre
 */
@MappedSuperclass 
public abstract class BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if ((obj != null) && (obj.getClass() == this.getClass())) {
            BaseModel other = (BaseModel) obj;
            if (other.getId() != null && this.getId() != null) {
                return other.getId().longValue() == this.getId().longValue();
            } else {
                return other.getId() == this.getId();
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "br.ufg.reqweb.model.BaseModel[ id=" + id + " ]";
    }
    
}
