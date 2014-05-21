package com.hp.it.innovation.collaboration.service.intf;

import java.util.List;

import com.hp.it.innovation.collaboration.dao.intf.RoleDAO;
import com.hp.it.innovation.collaboration.dto.RoleDTO;
import com.hp.it.innovation.collaboration.model.Role;
import com.hp.it.innovation.collaboration.service.BaseComponentService;

public interface RoleService extends BaseComponentService<Role, RoleDTO, RoleDAO> {
    public List<RoleDTO> retrieveAllRoles();
    
    public void addRole(RoleDTO roleDTO);
    
    public RoleDTO findRoleByUniqueName(String name);
    
}
