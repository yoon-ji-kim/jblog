package com.douzone.jblog.controller;


import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.ServletContext;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
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
	
	@RequestMapping({"", "/{category}", "/{category}/{post}"})
	public String blog(
			@PathVariable("id")String id,
			@PathVariable("category")Optional<Long> categoryNo,
			@PathVariable("post")Optional<Long> postNo,
			Model model) {
		Long category = 0L;
		Long post = 0L;
//		String postType = postNo.get().getClass().getSimpleName();
//		String categoryType= categoryNo.get().getClass().getSimpleName();
		if(postNo.isPresent()) {
			//postNo 값이 들어왔으면
			post = postNo.get();
			category = categoryNo.get();
		}else if(categoryNo.isPresent()) {
			//categoryNo 값이 들어왔으면
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
	@RequestMapping(value="/admin/category", method = RequestMethod.GET)
	public String adminCategory(@ModelAttribute("categoryVo")CategoryVo vo,@PathVariable("id")String id, Model model) {
		List<CategoryVo> list = categoryService.findById(id);
		model.addAttribute("list", list);
		return "blog/admin-category";
	}
	
	@Auth
	@RequestMapping(value="/admin/category", method = RequestMethod.POST)
	public String categoryInsert(@PathVariable("id")String id,@ModelAttribute@Valid CategoryVo categoryVo, BindingResult result, Model model) {
		if(result.hasErrors()) {
			model.addAllAttributes(result.getModel());
			//return 시키면 category list 없는 문제 발생!
			List<CategoryVo> list = categoryService.findById(id);
			model.addAttribute("list", list);
			return "blog/admin-category";
		}
		categoryVo.setId(id);
		categoryService.insert(categoryVo);
		return "redirect:/"+categoryVo.getId()+"/admin/category";
	}
	
	@Auth
	@RequestMapping(value="/admin/write", method = RequestMethod.GET)
	public String adminWrite(@ModelAttribute("vo")PostVo vo ,@PathVariable("id")String id, Model model) {
		List<CategoryVo> categoryList = categoryService.findById(id);
		model.addAttribute("categoryList" , categoryList);
		return "blog/admin-write";
	}
	
	@Auth
	@RequestMapping(value="/admin/write", method = RequestMethod.POST)
	public String postWrite(@ModelAttribute@Valid PostVo vo, BindingResult result,@PathVariable("id")String id, Model model) {
		if(result.hasErrors()) {
			model.addAllAttributes(result.getModel());
			System.out.println(result.getModel());
			List<CategoryVo> categoryList = categoryService.findById(id);
			model.addAttribute("categoryList" , categoryList);
			return "blog/admin-write";
		}
		postService.insert(vo);
		model.addAttribute("category", vo.getCategoryNo());
		return "redirect:/"+id+"/{category}";
	}
	
	@Auth
	@RequestMapping("/admin/category/delete/{no}")
	public String categoryDelete(@PathVariable("id")String id, @PathVariable("no")Long no) {
		postService.delete(no);
		categoryService.delete(id, no);
		return "redirect:/"+id+"/admin/category";
	}
}
