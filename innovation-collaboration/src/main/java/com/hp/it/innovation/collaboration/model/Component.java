package com.hp.it.innovation.collaboration.model;

import java.io.Serializable;
import java.util.Date;

public abstract class Component implements Serializable, Cloneable {

    /**
     *
     */
    private static final long serialVersionUID = -8712846786379186665L;

    private long id;
    private String name;
    private Date createDate;
    private Date updateDate;
    private Site site;
    
    public Component() {
    }
    
    public Component(Component c) {
        setId(c.getId());
        setName(c.getName());
        setCreateDate(c.getCreateDate());
        setUpdateDate(c.getUpdateDate());
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public Site getSite() {
        return site;
    }

    @Override
    public int hashCode() {
        final int prime  = 31;
        int hashCode = 1;
        hashCode = prime * hashCode + (int)(+serialVersionUID ^ (serialVersionUID >>> 32));
        hashCode = prime * hashCode + (int)(+id ^ (id >>> 32));
        hashCode = prime * hashCode + (name == null ? 0 : name.hashCode());
        hashCode = prime * hashCode + (createDate == null ? 0 : createDate.hashCode());
        hashCode = prime * hashCode + (updateDate == null ? 0 : updateDate.hashCode());
        hashCode = prime * hashCode + (site == null ? 0 : site.hashCode());
        return hashCode;
    }


}
