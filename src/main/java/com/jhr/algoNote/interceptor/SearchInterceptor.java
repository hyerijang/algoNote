package com.jhr.algoNote.interceptor;

import com.jhr.algoNote.repository.query.ProblemSearch;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 로그인 된 경우 model에 유저 이름과 사진을 추가함
 */
public class SearchInterceptor implements HandlerInterceptor {

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        ProblemSearch problemSearch= ProblemSearch.builder().build();
        modelAndView.addObject(problemSearch);
    }
}
