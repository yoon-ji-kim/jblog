package com.douzone.jblog.repository;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.douzone.jblog.vo.PostVo;

@Repository
public class PostRepository {
	@Autowired
	private SqlSession sqlSession;

	public void insert(PostVo vo) {
		sqlSession.insert("post.insert", vo);
	}

	public void deleteBycategoryNo(Long no) {
		sqlSession.delete("post.delete",no);
	}

	public List<PostVo> findByCategoryNo(Long categoryNo, String id) {
		Map<String, Object> map = Map.of("categoryNo", categoryNo, "id", id);
		return sqlSession.selectList("post.findByCategoryNo", map);
	}

	public PostVo findByPostNo(Long postNo) {
		return sqlSession.selectOne("post.findByPostNo", postNo);
	}
}
