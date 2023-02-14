package com.douzone.jblog.security;

import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import com.douzone.jblog.service.UserService;
import com.douzone.jblog.vo.UserVo;

public class AuthInterceptor implements HandlerInterceptor {
	@Autowired
	UserService userService;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		if(!(handler instanceof HandlerMethod)) { //DefaultServletHandler가 처리하는 경우
			return true;
		}
		
		HandlerMethod handlerMethod = (HandlerMethod)handler;
		Auth auth = handlerMethod.getMethodAnnotation(Auth.class);
		
		if(auth == null) { //인증 필요 없는 경우
			return true;
		}
		
		HttpSession session = request.getSession();
		UserVo authUser = (UserVo)session.getAttribute("authUser");
		if(authUser == null) {
			//인증 안 된 사용자 -> 로그인으로 보내기
			response.sendRedirect(request.getContextPath() + "/user/login");
			return false;
		}
		//PathVariable로 넘어오는 blog id 가져오기
		//{id}/admin/~~ path: "/zzang9/admin~~"  id(키):zzang(값)
//		String blogId = (String)request.getAttribute("id");					
		Map<?, ?> pathVariables = (Map<?,?>)request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		String blogId = (String)pathVariables.get("id");
		System.out.println(userService.findUser(blogId));
//		if(!userService.findUser(blogId)) {
//			response.sendRedirect(request.getContextPath() + "/error");
//			return false;
//		}
		//authUser와 blogId가 같지 않은 경우 -> 블로그로 보내기
		if(!Objects.equals(authUser.getId(), blogId)) {
			response.sendRedirect(request.getContextPath()+"/blog/"+blogId);
			return false;
		}
		return true;
	}

}
