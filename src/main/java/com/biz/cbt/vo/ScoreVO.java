package com.biz.cbt.vo;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScoreVO {

	private String cb_Cques; // 정답인 문항
	private String cb_Wques; // 오답인 문항
	private int countAns; // 정답인 개수

}
