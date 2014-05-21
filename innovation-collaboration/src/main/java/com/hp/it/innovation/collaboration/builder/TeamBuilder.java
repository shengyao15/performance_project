package com.hp.it.innovation.collaboration.builder;

import java.util.ArrayList;

import com.hp.it.innovation.collaboration.builder.base.AbstractComponentBuilder;
import com.hp.it.innovation.collaboration.dto.TeamDTO;
import com.hp.it.innovation.collaboration.model.Team;

public class TeamBuilder extends AbstractComponentBuilder<Team, TeamDTO> {

    private TeamDTO getComponent(Team entity, boolean includeParent) {
        TeamDTO teamDTO = new TeamDTO();
        if (entity != null) {
            transferBaseEntityToDTO(entity, teamDTO, false);
            teamDTO.setTeamName(entity.getTeamName());
            teamDTO.setMembers(new ArrayList<Long>());
            if(entity.getMembers()!=null){
                for(Long member:entity.getMembers()){
                    teamDTO.getMembers().add(member);
                }
            }
            if (entity.getParent() != null && includeParent) {
                teamDTO.setParent(this.getComponent(entity.getParent()));
            }
            if (entity.getSubTeams() != null) {
                teamDTO.setSubTeams(new ArrayList<TeamDTO>());
                TeamDTO subTeamDTO = null;
                for (Team subTeam : entity.getSubTeams()) {
                    subTeamDTO = getComponent(subTeam, false);
                    subTeamDTO.setParent(teamDTO);
                    teamDTO.getSubTeams().add(subTeamDTO);
                }
            }
        }
        return teamDTO;
    }
    
    private Team getComponent(TeamDTO dto, boolean includeParent) {
        Team team = new Team();
        if (dto != null) {
            transferBaseDTOToEntity(dto, team, false);
            team.setTeamName(dto.getTeamName());
            team.setMembers(new ArrayList<Long>());
            if(dto.getMembers()!=null){
                for(Long member:dto.getMembers()){
                    team.getMembers().add(member);
                }
            }
            if (dto.getParent() != null && includeParent) {
                team.setParent(this.getComponent(dto.getParent()));
            }
            if (dto.getSubTeams() != null) {
                team.setSubTeams(new ArrayList<Team>());
                Team subTeam = null;
                for (TeamDTO subTeamDTO : dto.getSubTeams()) {
                    subTeam = getComponent(subTeamDTO, false);
                    subTeam.setParent(team);
                    team.getSubTeams().add(subTeam);
                }
            }
        }
        return team;
    }

    @Override
    public Team getComponent(TeamDTO dto) {
        if(dto == null){
            return null;
        }
        return getComponent(dto, true);
    }

    @Override
    public TeamDTO getComponent(Team entity) {
        if(entity == null){
            return null;
        }
        return getComponent(entity, true);
    }

}
