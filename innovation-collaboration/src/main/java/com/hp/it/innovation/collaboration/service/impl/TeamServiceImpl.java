package com.hp.it.innovation.collaboration.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.hp.it.innovation.collaboration.builder.TeamBuilder;
import com.hp.it.innovation.collaboration.dao.intf.TeamDAO;
import com.hp.it.innovation.collaboration.dto.TeamDTO;
import com.hp.it.innovation.collaboration.model.Team;
import com.hp.it.innovation.collaboration.service.common.AbstractBaseComponentServiceImpl;
import com.hp.it.innovation.collaboration.service.intf.TeamService;

public class TeamServiceImpl extends AbstractBaseComponentServiceImpl<Team, TeamDTO, TeamDAO> implements TeamService {

    @Override
    public List<TeamDTO> retrieveAllTeams() {
        @SuppressWarnings("unchecked")
        List<Team> teamList = (List<Team>)getDao().queryByHQL("from Team");
        List<TeamDTO> teamDTOList = new ArrayList<TeamDTO>();
        TeamBuilder teamBuilder = new TeamBuilder();
        for(Team team:teamList){
            TeamDTO teamDTO = teamBuilder.getComponent(team);
            teamDTOList.add(teamDTO);
        }
        return teamDTOList;
    }

    @Override
    public void addTeam(TeamDTO teamDTO) {
        TeamBuilder teamBuilder = new TeamBuilder();
        Team team = teamBuilder.getComponent(teamDTO);
        save(team);
    }

    @Override
    public TeamDTO findTeamByUniqueName(String name) {
        Team entity = new Team();
        entity.setName(name);
        Team team = getDao().queryByName(entity);
        TeamBuilder teamBuilder = new TeamBuilder();
        TeamDTO teamDTO = teamBuilder.getComponent(team);
        return teamDTO;
    }
    
}
