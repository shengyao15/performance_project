package com.hp.it.innovation.collaboration.dto;

import java.util.List;

public class TeamDTO extends ComponentDTO {

    /**
     *
     */
    private static final long serialVersionUID = -2978060364552187465L;
    private String teamName;
    private TeamDTO parent;
    private List<TeamDTO> subTeams;
    private List<Long> members;
    
    public String getTeamName() {
        return teamName;
    }
    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }
    public TeamDTO getParent() {
        return parent;
    }
    public void setParent(TeamDTO parent) {
        this.parent = parent;
    }
    public List<TeamDTO> getSubTeams() {
        return subTeams;
    }
    public void setSubTeams(List<TeamDTO> subTeams) {
        this.subTeams = subTeams;
    }
    public List<Long> getMembers() {
        return members;
    }
    public void setMembers(List<Long> members) {
        this.members = members;
    }
	@Override
	public int hashCode() {
		return getName().hashCode();
	}
	@Override
	public boolean equals(Object obj) {
		TeamDTO t = (TeamDTO)obj;
		if(getName() != null && getName().equals(t.getName())){
			return true;
		}else{
			return false;
		}
		
	}
 
}
