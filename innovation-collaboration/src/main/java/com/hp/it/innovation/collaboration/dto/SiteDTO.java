package com.hp.it.innovation.collaboration.dto;

public class SiteDTO extends ComponentDTO {

    /**
     *
     */
    private static final long serialVersionUID = 4852360397543950440L;

    private String siteHost;
    private String imageHost;
    
    public String getSiteHost() {
        return siteHost;
    }
    public void setSiteHost(String siteHost) {
        this.siteHost = siteHost;
    }
    public String getImageHost() {
        return imageHost;
    }
    public void setImageHost(String imageHost) {
        this.imageHost = imageHost;
    }
}
