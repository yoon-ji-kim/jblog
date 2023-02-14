package com.douzone.jblog.repository;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.douzone.jblog.vo.CategoryVo;

@Repository
public class CategoryRepository {
	@Autowired
	private SqlSession sqlSession;

	public void insert(CategoryVo categoryVo) {
		sqlSession.insert("category.insert", categoryVo);
	}

	public List<CategoryVo> findById(String id) {
		return sqlSession.selectList("category.findById", id);
	}

	public void delete(String id, Long no) {
		Map<String, Object> map = Map.of("id",id, "no",no);
		sqlSession.delete("category.deleteByIdAndNo", map);
	}
}
