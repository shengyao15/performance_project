package com.hp.it.innovation.collaboration.dto;

import java.util.List;

import org.apache.commons.lang.StringUtils;

public class UserDTO extends ComponentDTO {

    /**
     *
     */
    private static final long serialVersionUID = -797071419764919883L;
    private String displayName;
    private String password;
    private String email;
    private String headerURL;
    private String gender;
    private String status;
    
    public String getDisplayName() {
        return displayName;
    }
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        if (StringUtils.isBlank(email)) {
            this.email = super.getName();
        } else {
            this.email = email;
        }
    }
    public String getHeaderURL() {
        return headerURL;
    }
    public void setHeaderURL(String headerURL) {
        this.headerURL = headerURL;
    }
    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public List<TeamDTO> getTeams() {
        return teams;
    }
    public void setTeams(List<TeamDTO> teams) {
        this.teams = teams;
    }
    public List<RoleDTO> getRoles() {
        return roles;
    }
    public void setRoles(List<RoleDTO> roles) {
        this.roles = roles;
    }
    private List<TeamDTO> teams;
    private List<RoleDTO> roles;
}
