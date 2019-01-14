package com.biz.cbt.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.biz.cbt.sql.CbtSQL;
import com.biz.cbt.vo.QuesAndAnsVO;

public interface MakeCBTDao {

	@Select(CbtSQL.CB_ALL)
	public List<QuesAndAnsVO> selectAll();

	@Insert(CbtSQL.CB_INSERT)
	public int insert(QuesAndAnsVO vo);

	@Update(CbtSQL.CB_UPDATE)
	public int update(QuesAndAnsVO vo);

	@Delete(CbtSQL.CB_DELETE)
	public int delete(String cb_question);

}
