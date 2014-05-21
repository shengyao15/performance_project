package com.hp.it.innovation.collaboration.service.intf;

import java.util.List;

import com.hp.it.innovation.collaboration.dao.intf.UserDAO;
import com.hp.it.innovation.collaboration.dto.ServiceResponseDTO;
import com.hp.it.innovation.collaboration.dto.UserDTO;
import com.hp.it.innovation.collaboration.model.User;
import com.hp.it.innovation.collaboration.service.BaseComponentService;

public interface UserService extends BaseComponentService<User, UserDTO, UserDAO> {
    public List<UserDTO> retrieveAllUsers();
    
    public UserDTO registerUser(UserDTO userDTO);
    
    public boolean checkUniqueName(String username);
    
    public ServiceResponseDTO checkLogin(UserDTO userDTO);
    
    public UserDTO findUserByUniqueName(String name);
}
