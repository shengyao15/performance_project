package com.hp.it.innovation.collaboration.service.intf;

import java.util.List;

import com.hp.it.innovation.collaboration.dao.intf.TeamDAO;
import com.hp.it.innovation.collaboration.dto.TeamDTO;
import com.hp.it.innovation.collaboration.model.Team;
import com.hp.it.innovation.collaboration.service.BaseComponentService;

public interface TeamService extends BaseComponentService<Team, TeamDTO, TeamDAO> {
    public List<TeamDTO> retrieveAllTeams();
    
    public void addTeam(TeamDTO teamDTO);
    
    public TeamDTO findTeamByUniqueName(String name);
}
