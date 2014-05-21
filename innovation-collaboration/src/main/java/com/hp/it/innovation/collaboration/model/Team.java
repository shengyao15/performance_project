package com.hp.it.innovation.collaboration.model;

import java.util.List;

public class Team extends Component {

    /**
     *
     */
    private static final long serialVersionUID = 1957697051745158006L;
    
    private String teamName;
    private Team parent;
    private List<Team> subTeams;
    private List<Long> members;

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setParent(Team parent) {
        this.parent = parent;
    }

    public Team getParent() {
        return parent;
    }

    public void setSubTeams(List<Team> subTeams) {
        this.subTeams = subTeams;
    }

    public List<Team> getSubTeams() {
        return subTeams;
    }

    public List<Long> getMembers() {
        return members;
    }

    public void setMembers(List<Long> members) {
        this.members = members;
    }
    
}
