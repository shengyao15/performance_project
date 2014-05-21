package com.hp.it.innovation.collaboration.model;

import java.util.List;

public class User extends Component {

    /**
     *
     */
    private static final long serialVersionUID = 7113468743566343527L;
    
    private String displayName;
    private String password;
    private String email;
    private String headerURL;
    private String gender;
    private String status;
    private List<Team> teams;
    private List<Role> roles;
    
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
    public List<Team> getTeams() {
        return teams;
    }
    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }
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
        this.email = email;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getStatus() {
        return status;
    }
    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
    public List<Role> getRoles() {
        return roles;
    }
    
}
