package com.hp.it.innovation.collaboration.dto;

import java.io.Serializable;
import java.util.Date;

public class ComponentDTO implements Cloneable, Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -5111412882238610038L;
    
    private long id;
    private String name;
    private Date createDate;
    private Date updateDate;
    private SiteDTO site;
    
    public ComponentDTO() {
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

    public void setSite(SiteDTO site) {
        this.site = site;
    }

    public SiteDTO getSite() {
        return site;
    }
    
}
