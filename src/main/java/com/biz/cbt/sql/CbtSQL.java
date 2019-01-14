package com.biz.cbt.sql;

public class CbtSQL {

	public static final String CB_ALL = " SELECT * FROM tbl_cbt ";

	public static final String CB_INSERT = " INSERT INTO tbl_cbt VALUES( SEQ_CBT.NEXTVAL, #{cb_question}, #{cb_ans1} "
			+ " , #{cb_ans2}, #{cb_ans3}, #{cb_ans4}, #{cb_cans}) ";

	public static final String CB_UPDATE = " UPDATE tbl_cbt SET cb_question = #{cb_question}, cb_ans1 = #{cb_ans1} "
			+ " , cb_ans2 = #{cb_ans2}, cb_ans3 = #{cb_ans3}, cb_ans4 = #{cb_ans4}, cb_cans = #{cb_cans} "
			+ " WHERE id = #{id} ";

	public static final String CB_DELETE = " DELETE FROM tbl_cbt WHERE id = #{id} ";

}
