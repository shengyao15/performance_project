package com.hp.it.innovation.collaboration.model;

import java.util.List;

public class Role extends Component {

    /**
     *
     */
    private static final long serialVersionUID = -6093329498830462981L;

    private String roleName;
    private List<Long> members;

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }

    public List<Long> getMembers() {
        return members;
    }

    public void setMembers(List<Long> members) {
        this.members = members;
    }
    
    
}
