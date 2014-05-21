package com.hp.it.innovation.collaboration.model;

public class Site extends Component {

    /**
     *
     */
    private static final long serialVersionUID = 6169836345104187039L;

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
