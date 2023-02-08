package com.douzone.jblog.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import com.douzone.jblog.vo.UserVo;

public class AuthInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		//1. handler 종류 확인
		if(!(handler instanceof HandlerMethod)) { //DefaultServletHandler가 처리하는 경우
			 return true;
		}
		//2. casting
		HandlerMethod handlerMethod = (HandlerMethod)handler;
		//3. Handler Method의 @Auth 가져오기
		Auth auth = handlerMethod.getMethodAnnotation(Auth.class);
	
		//4. method에 @Auth가 없는 경우(인증 필요 없는 경우) 
		if(auth == null ) {
			return true;
		}
		//5. @Auth가 annotation이 붙어 있기 때문에 (Authenfication) 여부 확인
		HttpSession session = request.getSession();
		UserVo authUser= (UserVo)session.getAttribute("authUser");
		if(authUser == null) {
			//인증이 안됨
			response.sendRedirect(request.getContextPath() + "/user/login");
			//응답 후 끝냄
			return false;
		}
		
		request.setAttribute("authUser", session.getAttribute("authUser"));
		//6. 인증확인
		return true;
	}

}
