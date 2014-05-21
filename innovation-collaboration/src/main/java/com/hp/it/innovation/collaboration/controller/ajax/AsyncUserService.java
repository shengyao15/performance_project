package com.hp.it.innovation.collaboration.controller.ajax;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.hp.it.innovation.collaboration.dto.JsonResponseDTO;
import com.hp.it.innovation.collaboration.dto.ServiceResponseDTO;
import com.hp.it.innovation.collaboration.dto.UserDTO;
import com.hp.it.innovation.collaboration.service.common.ServiceFactory;
import com.hp.it.innovation.collaboration.service.intf.UserService;
import com.hp.it.innovation.collaboration.utilities.Constants;

@Controller
@RequestMapping("/asyncUser/*")
public class AsyncUserService {
    @RequestMapping(value="/login", produces="application/json")
    public @ResponseBody JsonResponseDTO login(HttpServletRequest request, @ModelAttribute(value = "userDTO") UserDTO userDTO) {
        JsonResponseDTO jsonResponseDTO = new JsonResponseDTO();
        ServiceResponseDTO responseDTO = ServiceFactory.getService(UserService.class).checkLogin(userDTO);
        if (responseDTO != null) {
            if (Constants.LOGIN_FAILURE_USERNAME_ERROR.equals(responseDTO.getStatus())) {
                jsonResponseDTO.setStatus(JsonResponseDTO.FAILURE_RESPONSE);
                jsonResponseDTO.setResult("E-mail address not exist! Be sure no special charactor input!");
            } else if (Constants.LOGIN_FAILURE_USER_INVALID.equals(responseDTO.getStatus())) {
                jsonResponseDTO.setStatus(JsonResponseDTO.FAILURE_RESPONSE);
                jsonResponseDTO.setResult("User status is inactive,please contact system administor!");
            } else if (Constants.LOGIN_FAILURE_PASSWORD_ERROR.equals(responseDTO.getStatus())) {
                jsonResponseDTO.setStatus(JsonResponseDTO.FAILURE_RESPONSE);
                jsonResponseDTO.setResult("Password incorrect,please re-input!");
            } else {
                jsonResponseDTO.setStatus(JsonResponseDTO.SUCCESS_RESPONSE);
                storeUserIntoSession((UserDTO)responseDTO.getResult());
            }
        }
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return jsonResponseDTO;
    }

    @RequestMapping(value="/checkUserName", produces="application/json")
    public @ResponseBody JsonResponseDTO checkUserNameExisting(@ModelAttribute(value = "userDTO") UserDTO userDTO) {
        JsonResponseDTO jsonResponse = new JsonResponseDTO();
        boolean notExisting = ServiceFactory.getService(UserService.class).checkUniqueName(userDTO.getName());
        if (!notExisting) {
            jsonResponse.setStatus(JsonResponseDTO.FAILURE_RESPONSE);
            jsonResponse.setResult("This user name is existing!!");
        } else {
            jsonResponse.setStatus(JsonResponseDTO.SUCCESS_RESPONSE);
        }
        return jsonResponse;
    }

    @RequestMapping(value="/register", produces="application/json")
    public @ResponseBody JsonResponseDTO register(@ModelAttribute(value = "userDTO") UserDTO userDTO) {
        JsonResponseDTO res = new JsonResponseDTO();
        userDTO = ServiceFactory.getService(UserService.class).registerUser(userDTO);
        res.setStatus(JsonResponseDTO.SUCCESS_RESPONSE);
        storeUserIntoSession(userDTO);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return res;
    }
    
    @RequestMapping(value="/logout", produces="application/json")
    public @ResponseBody JsonResponseDTO logout() throws IOException{
        JsonResponseDTO response = new JsonResponseDTO();
        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
        removeUserFromSession();
        if (request.getSession().getAttribute(Constants.CURRENT_USER_KEY) == null) {
            response.setStatus(JsonResponseDTO.SUCCESS_RESPONSE);
        } else {
            response.setStatus(JsonResponseDTO.FAILURE_RESPONSE);
        }
        return response;
    }

    private void storeUserIntoSession(UserDTO userDTO) {
        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
        request.getSession().setAttribute(Constants.CURRENT_USER_KEY, userDTO);
    }
    
    private void removeUserFromSession() {
        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
        request.getSession().removeAttribute(Constants.CURRENT_USER_KEY);
    }
}
