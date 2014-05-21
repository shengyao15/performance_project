package com.hp.it.innovation.collaboration.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.hp.it.innovation.collaboration.builder.UserBuilder;
import com.hp.it.innovation.collaboration.common.UserStatusEnum;
import com.hp.it.innovation.collaboration.dao.intf.UserDAO;
import com.hp.it.innovation.collaboration.dto.ServiceResponseDTO;
import com.hp.it.innovation.collaboration.dto.UserDTO;
import com.hp.it.innovation.collaboration.model.User;
import com.hp.it.innovation.collaboration.service.common.AbstractBaseComponentServiceImpl;
import com.hp.it.innovation.collaboration.service.intf.UserService;
import com.hp.it.innovation.collaboration.utilities.Constants;

public class UserServiceImpl extends AbstractBaseComponentServiceImpl<User, UserDTO, UserDAO> implements UserService {

    public List<UserDTO> retrieveAllUsers() {
        String hql = "from User u order by u.id";
        UserBuilder builder = new UserBuilder();
        List<User> userEntities = (List<User>)getDao().queryByHQL(hql);
        List<UserDTO> users = new ArrayList<UserDTO>();
        for (User entity : userEntities) {
            users.add(builder.getComponent(entity));
        }
        return users;
    }

    @Override
    public UserDTO registerUser(UserDTO userDTO) {
        UserBuilder builder = new UserBuilder();
        userDTO.setStatus(UserStatusEnum.ACTIVE.toString());
        User user = builder.getComponent(userDTO);
        user = getDao().save(user);
        return builder.getComponent(user);
    }

    @Override
    public boolean checkUniqueName(String username) {
        String hql = "from User u where u.name='" + username + "'";
        List<User> userEntities = (List<User>)getDao().queryByHQL(hql);
        if (userEntities.size() > 0) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public ServiceResponseDTO checkLogin(UserDTO userDTO) {
        ServiceResponseDTO response = null;
        if (userDTO != null) {
            response = new ServiceResponseDTO();
            UserBuilder builder = new UserBuilder();
            User user = builder.getComponent(userDTO);
            user = getDao().queryByName(user);
            UserDTO result = builder.getComponent(user);
            if (user == null) {
                response.setStatus(Constants.LOGIN_FAILURE_USERNAME_ERROR);
            } else if (UserStatusEnum.INACTIVE.toString().equals(result.getStatus())) {
                response.setStatus(Constants.LOGIN_FAILURE_USER_INVALID);
            } else if (userDTO.getName().equals(result.getName())
                       && !userDTO.getPassword().equals(result.getPassword())) {
                response.setStatus(Constants.LOGIN_FAILURE_PASSWORD_ERROR);
            } else {
                response.setStatus(Constants.LOGIN_SUCCESS);
                response.setResult(result);
            }
        }
        return response;
    }

    @Override
    public UserDTO findUserByUniqueName(String name) {
        User entity = new User();
        entity.setName(name);
        User user = getDao().queryByName(entity);
        UserBuilder userBuilder = new UserBuilder();
        UserDTO userDTO = userBuilder.getComponent(user);
        return userDTO;
    }
    
    
}
