package com.hp.it.innovation.collaboration.dto;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UserTeamPrivilegeDTO extends ComponentDTO {
    
	private Map<String,String> availableTeams = new HashMap<String,String>();
    private Map<String,String> selectedTeams = new HashMap<String,String>();
	public Map<String, String> getAvailableTeams() {
		return availableTeams;
	}
	public void setAvailableTeams(Map<String, String> availableTeams) {
		this.availableTeams = availableTeams;
	}
	public Map<String, String> getSelectedTeams() {
		return selectedTeams;
	}
	public void setSelectedTeams(Map<String, String> selectedTeams) {
		this.selectedTeams = selectedTeams;
	}
    
}
