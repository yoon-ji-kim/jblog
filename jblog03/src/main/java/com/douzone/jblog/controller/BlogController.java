package com.douzone.jblog.controller;


import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.douzone.jblog.service.BlogService;
import com.douzone.jblog.service.CategoryService;
import com.douzone.jblog.service.FileUploadService;
import com.douzone.jblog.service.PostService;
import com.douzone.jblog.vo.BlogVo;
import com.douzone.jblog.vo.CategoryVo;
import com.douzone.jblog.vo.PostVo;

@Controller
@RequestMapping("/blog")
public class BlogController {
	@Autowired
	BlogService blogService;
	
	@Autowired
	FileUploadService fileUploadService;
	
	@Autowired
	CategoryService categoryService;
	
	@Autowired
	PostService postService;
	
	@RequestMapping("/{id}")
	public String blog(@PathVariable("id")String id,@RequestParam(value="category", required = false)Long categoryNo,@RequestParam(value = "postNo", required = false)Long postNo, Model model) {
		Map<String, Object> map = blogService.getMainMap(id,categoryNo, postNo);
		model.addAllAttributes(map);
		return "blog/main";
	}
	
	@RequestMapping("/{id}/admin/basic")
	public String adminBasic(@PathVariable("id")String id, Model model) {
		BlogVo vo = blogService.getBlog(id);
		model.addAttribute("blog", vo);
		return "blog/admin-basic";
	}
	
	@RequestMapping("/{id}/admin/update")
	public String adminUpdate(BlogVo vo, @PathVariable("id")String id, @RequestParam("logo-file")MultipartFile file) {
		vo.setId(id);
		if(!file.isEmpty()) {
			String url = fileUploadService.restore(file);
			vo.setProfile(url);
		}
		System.out.println(vo);
		blogService.updateBlog(vo);
		return "redirect:/blog/"+vo.getId();
	}
	
	@RequestMapping("/{id}/admin/category")
	public String adminCategory(@PathVariable("id")String id, Model model) {
		List<CategoryVo> list = categoryService.findById(id);
		model.addAttribute("list", list);
		return "blog/admin-category";
	}
	
	@RequestMapping(value="/{id}/admin/write", method = RequestMethod.GET)
	public String adminWrite(@PathVariable("id")String id, Model model) {
		List<CategoryVo> categoryList = categoryService.findById(id);
		model.addAttribute("categoryList" , categoryList);
		return "blog/admin-write";
	}
	@RequestMapping(value="/{id}/admin/write", method = RequestMethod.POST)
	public String postWrite(@PathVariable("id")String id, PostVo vo) {
		postService.insert(vo);
		return "redirect:/blog/"+id+"?category="+vo.getCategoryNo();
	}
	@RequestMapping(value="/{id}/admin/category/insert", method = RequestMethod.POST)
	public String categoryInsert(CategoryVo vo,@PathVariable("id")String id) {
		vo.setId(id);
		categoryService.insert(vo);
		return "redirect:/blog/"+vo.getId()+"/admin/category";
	}
	
	@RequestMapping("/{id}/admin/category/delete/{no}")
	public String categoryDelete(@PathVariable("id")String id, @PathVariable("no")Long no) {
		postService.delete(no);
		categoryService.delete(id, no);
		return "redirect:/blog/"+id+"/admin/category";
	}
}
