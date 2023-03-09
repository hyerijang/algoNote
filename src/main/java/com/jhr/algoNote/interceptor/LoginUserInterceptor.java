package com.jhr.algoNote.interceptor;

import com.jhr.algoNote.config.auth.dto.SessionUser;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 로그인 된 경우 model에 유저 이름과 사진을 추가함
 */
public class LoginUserInterceptor implements HandlerInterceptor {


    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        //로그인된 유저정보를 가져온다.
        SessionUser user = (SessionUser) request.getSession().getAttribute("user");
        //로그인이 된 경우 유저 이름과 사진을 model에 추가한다.
        if (user != null) {
            modelAndView.getModelMap().addAttribute("userName", user.getName());
            modelAndView.getModelMap().addAttribute("userImg", user.getPicture());
        }

    }
}
