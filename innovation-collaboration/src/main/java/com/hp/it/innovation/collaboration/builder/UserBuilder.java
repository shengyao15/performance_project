package com.hp.it.innovation.collaboration.builder;

import java.util.ArrayList;

import com.hp.it.innovation.collaboration.builder.base.AbstractComponentBuilder;
import com.hp.it.innovation.collaboration.dto.RoleDTO;
import com.hp.it.innovation.collaboration.dto.TeamDTO;
import com.hp.it.innovation.collaboration.dto.UserDTO;
import com.hp.it.innovation.collaboration.model.Role;
import com.hp.it.innovation.collaboration.model.Team;
import com.hp.it.innovation.collaboration.model.User;

public class UserBuilder extends AbstractComponentBuilder<User, UserDTO> {
    @Override
    public UserDTO getComponent(User entity) {
        UserDTO userDTO = null;
        if (entity != null) {
            userDTO = new UserDTO();
            transferBaseEntityToDTO(entity, userDTO, false);
            userDTO.setDisplayName(entity.getDisplayName());
            userDTO.setEmail(entity.getEmail());
            userDTO.setGender(entity.getGender());
            userDTO.setHeaderURL(entity.getHeaderURL());
            userDTO.setPassword(entity.getPassword());
            userDTO.setStatus(entity.getStatus());
            TeamBuilder teamBuilder = new TeamBuilder();
            if (entity.getTeams() != null && entity.getTeams().size() != 0) {
                userDTO.setTeams(new ArrayList<TeamDTO>());
                for (Team team : entity.getTeams()) {
                    userDTO.getTeams().add(teamBuilder.getComponent(team));
                }
            }
            RoleBuilder roleBuilder = new RoleBuilder();
            if (entity.getRoles() != null && entity.getRoles().size() != 0) {
                userDTO.setRoles(new ArrayList<RoleDTO>());
                for (Role role : entity.getRoles()) {
                    userDTO.getRoles().add(roleBuilder.getComponent(role));
                }
            }
        }
        return userDTO;
    }

    @Override
    public User getComponent(UserDTO dto) {
        User user = null;
        if (dto != null) {
            user = new User();
            transferBaseDTOToEntity(dto, user, false);
            user.setDisplayName(dto.getDisplayName());
            user.setEmail(dto.getEmail());
            user.setGender(dto.getGender());
            user.setHeaderURL(dto.getHeaderURL());
            user.setPassword(dto.getPassword());
            user.setStatus(dto.getStatus());
            RoleBuilder roleBuilder = new RoleBuilder();
            if (dto.getRoles() != null && dto.getRoles().size() != 0) {
                user.setRoles(new ArrayList<Role>());
                for (RoleDTO roleDTO : dto.getRoles()) {
                    user.getRoles().add(roleBuilder.getComponent(roleDTO));
                }
            }
            TeamBuilder teamBuilder = new TeamBuilder();
            if (dto.getTeams() != null && dto.getTeams().size() != 0) {
                user.setTeams(new ArrayList<Team>());
                for (TeamDTO teamDTO : dto.getTeams()) {
                    user.getTeams().add(teamBuilder.getComponent(teamDTO));
                }
            }
        }
        return user;
    }
}
