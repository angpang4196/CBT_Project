package com.biz.cbt.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
id 			NUMBER 		   PRIMARY KEY,
cb_question nVARCHAR2(125) NOT NULL,        -- 문제
cb_ans1 	nVARCHAR2(125) NOT NULL,        -- 답안1
cb_ans2	 	nVARCHAR2(125) NOT NULL,        -- 답안2
cb_ans3 	nVARCHAR2(125) NOT NULL,        -- 답안3
cb_ans4 	nVARCHAR2(125) NOT NULL,        -- 답안4
cb_cans 	nVARCHAR2(125) NOT NULL         -- 정답
*/

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuesAndAnsVO {

	private String id; // 문제번호
	private String cb_question; // 문제

	private String cb_ans1; // 답안1
	private String cb_ans2; // 답안2
	private String cb_ans3; // 답안3
	private String cb_ans4; // 답안4

	private String cb_cans; // 정답

}
