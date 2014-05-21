package com.hp.it.innovation.collaboration.dto;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UserRolePrivilegeDTO extends ComponentDTO {
    
	private Map<String,String> availableRoles = new HashMap<String,String>();
    private Map<String,String> selectedRoles = new HashMap<String,String>();
	public Map<String, String> getAvailableRoles() {
		return availableRoles;
	}
	public void setAvailableRoles(Map<String, String> availableRoles) {
		this.availableRoles = availableRoles;
	}
	public Map<String, String> getSelectedRoles() {
		return selectedRoles;
	}
	public void setSelectedRoles(Map<String, String> selectedRoles) {
		this.selectedRoles = selectedRoles;
	}
    
}
