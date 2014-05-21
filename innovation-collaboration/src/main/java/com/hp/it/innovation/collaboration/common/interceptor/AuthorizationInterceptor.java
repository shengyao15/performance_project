package com.hp.it.innovation.collaboration.common.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.hp.it.innovation.collaboration.common.exception.UnauthorizedException;
import com.hp.it.innovation.collaboration.dto.UserDTO;
import com.hp.it.innovation.collaboration.utilities.Constants;

public class AuthorizationInterceptor extends HandlerInterceptorAdapter {

    @Override
    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler,
                           ModelAndView modelAndView) throws Exception {
        UserDTO user = (UserDTO)request.getSession().getAttribute(Constants.CURRENT_USER_KEY);
        
        if (user == null) {
            throw new UnauthorizedException();
        }
        super.postHandle(request, response, handler, modelAndView);
    }
}
