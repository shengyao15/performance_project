package com.hp.it.innovation.collaboration.dto;

import java.util.List;

public class RoleDTO extends ComponentDTO {

    /**
     *
     */
    private static final long serialVersionUID = 8834187174631288924L;

    private String roleName;
    private List<Long> members;
    public List<Long> getMembers() {
        return members;
    }

    public void setMembers(List<Long> members) {
        this.members = members;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }
}
