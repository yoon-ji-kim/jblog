package com.douzone.jblog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.douzone.jblog.repository.BlogRepository;
import com.douzone.jblog.repository.CategoryRepository;
import com.douzone.jblog.repository.UserRepository;
import com.douzone.jblog.vo.BlogVo;
import com.douzone.jblog.vo.CategoryVo;
import com.douzone.jblog.vo.UserVo;

@Service
public class UserService {
	@Autowired
	UserRepository userRepository;

	@Autowired
	BlogRepository blogRepository;
	@Autowired
	CategoryRepository categoryRepository;
	public void join(UserVo userVo) {
		userRepository.insert(userVo);
		BlogVo vo = new BlogVo();
		vo.setId(userVo.getId());
		blogRepository.insert(vo);
		CategoryVo categoryVo = new CategoryVo();
		categoryVo.setName("게시글");
		categoryVo.setId(userVo.getId());
		categoryRepository.insert(categoryVo);
	}
	public UserVo getUser(UserVo vo) {
		return userRepository.findByIdAndPassword(vo.getId(), vo.getPassword());
	}
	public boolean findUser(String id) {
		return userRepository.findById(id);
	}
}
