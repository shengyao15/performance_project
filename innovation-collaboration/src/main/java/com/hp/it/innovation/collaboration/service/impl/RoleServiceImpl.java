package com.hp.it.innovation.collaboration.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.hp.it.innovation.collaboration.builder.RoleBuilder;
import com.hp.it.innovation.collaboration.dao.intf.RoleDAO;
import com.hp.it.innovation.collaboration.dto.RoleDTO;
import com.hp.it.innovation.collaboration.model.Role;
import com.hp.it.innovation.collaboration.service.common.AbstractBaseComponentServiceImpl;
import com.hp.it.innovation.collaboration.service.intf.RoleService;

public class RoleServiceImpl extends AbstractBaseComponentServiceImpl<Role, RoleDTO, RoleDAO> implements RoleService {

    @Override
    public List<RoleDTO> retrieveAllRoles() {
        @SuppressWarnings("unchecked")
        List<Role> roleList = (List<Role>)getDao().queryByHQL("from Role");
        List<RoleDTO> roleDTOList = new ArrayList<RoleDTO>();
        RoleBuilder roleBuilder = new RoleBuilder();
        for(Role role:roleList){
            RoleDTO roleDTO = roleBuilder.getComponent(role);
            roleDTOList.add(roleDTO);
        }
        return roleDTOList;
    }

    @Override
    public void addRole(RoleDTO roleDTO) {
        RoleBuilder builder = new RoleBuilder();
        Role role = builder.getComponent(roleDTO);
        save(role);
    }

    @Override
    public RoleDTO findRoleByUniqueName(String name) {
        Role role = new Role();
        role.setName(name);
        role = getDao().queryByName(role);
        RoleBuilder builder = new RoleBuilder();
        return builder.getComponent(role);
    }

}
