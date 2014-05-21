package com.hp.it.innovation.collaboration.builder;

import java.util.ArrayList;

import com.hp.it.innovation.collaboration.builder.base.AbstractComponentBuilder;
import com.hp.it.innovation.collaboration.dto.RoleDTO;
import com.hp.it.innovation.collaboration.model.Role;

public class RoleBuilder extends AbstractComponentBuilder<Role, RoleDTO> {

    @Override
    public RoleDTO getComponent(Role entity) {
        RoleDTO roleDTO = null;
        if (entity != null) {
            roleDTO = new RoleDTO();
            transferBaseEntityToDTO(entity, roleDTO, false);
            roleDTO.setRoleName(entity.getRoleName());
            roleDTO.setMembers(new ArrayList<Long>());
            if(entity.getMembers()!=null){
                for(Long member:entity.getMembers()){
                    roleDTO.getMembers().add(member);
                }
            }
        }
        return roleDTO;
    }

    @Override
    public Role getComponent(RoleDTO dto) {
        Role role = null;
        if (dto != null) {
            role = new Role();
            transferBaseDTOToEntity(dto, role, false);
            role.setRoleName(dto.getRoleName());
            role.setMembers(new ArrayList<Long>());
            if(dto.getMembers()!=null){
                for(Long member:dto.getMembers()){
                    role.getMembers().add(member);
                }
            }
        }
        return role;
    }

}
