package com.douzone.jblog.controller;


import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.douzone.jblog.security.Auth;
import com.douzone.jblog.service.BlogService;
import com.douzone.jblog.service.CategoryService;
import com.douzone.jblog.service.FileUploadService;
import com.douzone.jblog.service.PostService;
import com.douzone.jblog.vo.BlogVo;
import com.douzone.jblog.vo.CategoryVo;
import com.douzone.jblog.vo.PostVo;

@Controller
@RequestMapping("/{id:(?!assets).*}")
public class BlogController {
	@Autowired
	BlogService blogService;
	
	@Autowired
	FileUploadService fileUploadService;
	
	@Autowired
	CategoryService categoryService;
	
	@Autowired
	PostService postService;
	
	@Autowired
	private ServletContext servletContext;
	
	@RequestMapping({"", "/{category}", "/{category}/{postNo}"})
	public String blog(
			@PathVariable("id")String id,
			@PathVariable("category")Optional<Long> categoryNo,
			@PathVariable("postNo")Optional<Long> postNo,
			Model model) {
		Long category = 0L;
		Long post = 0L;
		//타입검사
		if(postNo.isPresent()) {
			post = postNo.get();
			category = categoryNo.get();
		}else if(categoryNo.isPresent()) {
			category = categoryNo.get();
		}
		Map<String, Object> map = blogService.getMainMap(id,category, post);
		model.addAllAttributes(map);
		return "blog/main";
	}
	
	@Auth
	@RequestMapping("/admin/basic")
	public String adminBasic(@PathVariable("id")String id, Model model) {
		BlogVo vo = blogService.getBlog(id);
		model.addAttribute("blog", vo);
		return "blog/admin-basic";
	}
	
	@Auth
	@RequestMapping("/admin/update")
	public String adminUpdate(BlogVo vo, @PathVariable("id")String id, @RequestParam("logo-file")MultipartFile file) {
		vo.setId(id);
		if(!file.isEmpty()) {
			String url = fileUploadService.restore(file);
			vo.setProfile(url);
		}
		blogService.updateBlog(vo);
		servletContext.setAttribute("blogvo", vo);
		return "redirect:/"+vo.getId();
	}
	
	@Auth
	@RequestMapping("/admin/category")
	public String adminCategory(@PathVariable("id")String id, Model model) {
		List<CategoryVo> list = categoryService.findById(id);
		model.addAttribute("list", list);
		return "blog/admin-category";
	}
	
	@Auth
	@RequestMapping(value="/admin/write", method = RequestMethod.GET)
	public String adminWrite(@PathVariable("id")String id, Model model) {
		List<CategoryVo> categoryList = categoryService.findById(id);
		model.addAttribute("categoryList" , categoryList);
		return "blog/admin-write";
	}
	
	@Auth
	@RequestMapping(value="/admin/write", method = RequestMethod.POST)
	public String postWrite(@PathVariable("id")String id, PostVo vo, Model model) {
		postService.insert(vo);
		model.addAttribute("category", vo.getCategoryNo());
		return "redirect:/"+id+"/{category}";
	}
	
	@Auth
	@RequestMapping(value="/admin/category/insert", method = RequestMethod.POST)
	public String categoryInsert(CategoryVo vo,@PathVariable("id")String id) {
		vo.setId(id);
		categoryService.insert(vo);
		return "redirect:/"+vo.getId()+"/admin/category";
	}
	
	@Auth
	@RequestMapping("/admin/category/delete/{no}")
	public String categoryDelete(@PathVariable("id")String id, @PathVariable("no")Long no) {
		postService.delete(no);
		categoryService.delete(id, no);
		return "redirect:/"+id+"/admin/category";
	}
}
