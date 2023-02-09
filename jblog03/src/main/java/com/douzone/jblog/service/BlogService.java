package com.douzone.jblog.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.douzone.jblog.repository.BlogRepository;
import com.douzone.jblog.repository.CategoryRepository;
import com.douzone.jblog.repository.PostRepository;
import com.douzone.jblog.vo.BlogVo;
import com.douzone.jblog.vo.CategoryVo;
import com.douzone.jblog.vo.PostVo;

@Service
public class BlogService {
	@Autowired
	BlogRepository blogRepository;

	@Autowired
	CategoryRepository categoryRepository;
	
	@Autowired
	PostRepository postRepository;
	public BlogVo getBlog(String id) {
		return blogRepository.findById(id);
	}

	public void updateBlog(BlogVo vo) {
		blogRepository.update(vo);
	}

	public Map<String, Object> getMainMap(String id, Long categoryNo, Long postNo) {
		BlogVo blog = blogRepository.findById(id);
		Map<String, Object> result = new HashMap<>();
		//카테고리 리스트
		List<CategoryVo> categoryList =categoryRepository.findById(id);
		//카테고리 선택 안하면 list 제일 앞 category에 해당하는 게시글 리스트
		if(categoryNo == null) {
			categoryNo = categoryList.get(0).getNo();
		}
		List<PostVo> postList = postRepository.findByCategoryNo(categoryNo);
		PostVo vo = null;
		if(!postList.isEmpty()) {
			//카테고리 게시글 중 제일 최근 작성된 게시글 찾기
			vo = postList.get(0);			
		}
		if(postNo != null) {
			//게시글 선택 시 보여줄 게시글 찾기
			vo = postRepository.findByPostNo(postNo);
		}
		result.put("blog", blog);
		result.put("category", categoryList);
		result.put("postlist", postList);
		result.put("post", vo);
		return result;
	}
	
}
