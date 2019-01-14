package com.biz.cbt.service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.biz.cbt.dao.MakeCBTDao;
import com.biz.cbt.dao.db.OracleSqlFactory;
import com.biz.cbt.vo.QuesAndAnsVO;
import com.biz.cbt.vo.ScoreVO;

public class MakeCBTService {

	SqlSessionFactory sessionFactory;
	List<QuesAndAnsVO> qnaList;
	List<ScoreVO> scList;

	List<String> cList;
	List<String> wList;

	QuesAndAnsVO qnavo;

	Scanner scan;

	public MakeCBTService() {
		scan = new Scanner(System.in);
		qnaList = new ArrayList();
		scList = new ArrayList();

		cList = new ArrayList();
		wList = new ArrayList();

		qnavo = new QuesAndAnsVO();

		OracleSqlFactory sqlFactory = new OracleSqlFactory();

		this.sessionFactory = sqlFactory.getSessionFactory();
	}

	public void startMenu() {
		System.out.println("=========================================================");
		System.out.println("정보처리기사 필기문제 CBT 프로그램");
		System.out.println("=========================================================");
		System.out.println("1. 문제입력 2. 문제풀이 3. 문제목록 0. 종료");
		System.out.print("선택 >>> ");
		String strChoice = scan.nextLine();
		System.out.println("---------------------------------------------------------");
		if (strChoice.equals("0")) {
			System.out.println("프로그램을 종료합니다.");
			return;
		}
		if (strChoice.equals("1")) {
			System.out.println("문제 입력 메뉴 실행");
			this.inputQues();

		}
		if (strChoice.equals("2")) {
			System.out.println("문제풀이 시작");
			this.study();
		}
		if (strChoice.equals("3")) {
			System.out.println("문제 목록 보기");
			this.viewCBT();
		}
	}

	public void inputQues() {
		System.out.println("=========================================================");
		System.out.println("1. 문제등록 2. 문제수정 3. 문제삭제 0. 종료");
		System.out.print("선택 >>> ");
		String strChoice = scan.nextLine();
		System.out.println("---------------------------------------------------------");

		if (strChoice.equals("0")) {
			System.out.println("메뉴 종료");
			return;
		}
		if (strChoice.equals("1")) {
			System.out.println("문제 등록 시작");
			this.insert();
		}
		if (strChoice.equals("2")) {
			System.out.println("문제 수정 시작");
			this.viewCBT();
			System.out.print("수정하실 문제번호 입력 >>> ");
			String strUpNum = scan.nextLine();
			this.update(strUpNum);
		}
		if (strChoice.equals("3")) {
			System.out.println("문제 삭제 시작");
			this.viewCBT();

			System.out.print("삭제시킬 문제 번호 입력 >>> ");
			String deleteNum = scan.nextLine();

			this.delete(deleteNum);
			System.out.println(deleteNum + "번 문제 삭제 완료");

		}
	}

	// 문제모음.txt파일에 있는 문제들을 읽어오는 method
	public void readFile() {
		FileReader fr;
		BufferedReader buffer;

		String strFile = "src/main/java/com/biz/cbt/문제모음.txt";

		try {
			fr = new FileReader(strFile);
			buffer = new BufferedReader(fr);

			while (true) {
				String reader = buffer.readLine();
				if (reader == null)
					break;
				String[] spQnA = reader.split(":");

				qnavo = new QuesAndAnsVO();

				qnavo.setCb_question(spQnA[0]);
				qnavo.setCb_ans1(spQnA[1]);
				qnavo.setCb_ans2(spQnA[2]);
				qnavo.setCb_ans3(spQnA[3]);
				qnavo.setCb_ans4(spQnA[4]);
				qnavo.setCb_cans(spQnA[5]);

				System.out.println(qnavo);

				ques15insert(qnavo);

				qnaList.add(qnavo);
			}

			System.out.println("15문제 추가 완료");
			buffer.close();
			fr.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public List<QuesAndAnsVO> viewCBT() {
		SqlSession session = this.sessionFactory.openSession();

		MakeCBTDao dao = session.getMapper(MakeCBTDao.class);

		List<QuesAndAnsVO> cbtList = dao.selectAll();

		for (QuesAndAnsVO vo : cbtList) {
			System.out.println(vo.getId() + "." + vo.getCb_question());
			System.out.println(vo.getCb_ans1());
			System.out.println(vo.getCb_ans2());
			System.out.println(vo.getCb_ans3());
			System.out.println(vo.getCb_ans4());
			System.out.println("정답 : " + vo.getCb_cans());
			System.out.println();

		}
		return cbtList;
	}

	public void ques15insert(QuesAndAnsVO vo) {
		SqlSession session = this.sessionFactory.openSession();

		MakeCBTDao dao = session.getMapper(MakeCBTDao.class);

		int intRet = dao.insert(vo);

		session.commit();
		session.close();

	}

	public void insert() {
		QuesAndAnsVO vo = input();

		if (vo == null)
			return;

		SqlSession session = this.sessionFactory.openSession();

		MakeCBTDao dao = session.getMapper(MakeCBTDao.class);

		int intRet = dao.insert(vo);

		qnaList.add(vo);

		session.commit();
		session.close();

		if (intRet > 0) {
			System.out.println("추가 완료");
		} else {
			System.out.println("입력 실패");
		}

	}

	public int delete(String cb_quesNum) {
		SqlSession session = this.sessionFactory.openSession();

		MakeCBTDao dao = session.getMapper(MakeCBTDao.class);

		int intRet = dao.delete(cb_quesNum);

		session.commit();
		session.close();

		return intRet;
	}

	public void update(String id) {
		QuesAndAnsVO vo = this.updateInput(id);

		if (vo == null)
			return;

		SqlSession session = this.sessionFactory.openSession();

		MakeCBTDao dao = session.getMapper(MakeCBTDao.class);

		int intRet = dao.update(vo);

		session.commit();
		session.close();

		if (intRet > 0) {
			System.out.println("수정 완료");
		} else {
			System.out.println("입력 실패");
		}
	}

	public void study() {

		SqlSession session = this.sessionFactory.openSession();

		MakeCBTDao dao = session.getMapper(MakeCBTDao.class);

		List<ScoreVO> cwqList = new ArrayList();

		int intCountAns = 0;

		List<QuesAndAnsVO> cbtList = dao.selectAll();

		int intSize = cbtList.size();

		for (int i = 0; i < intSize; i++) {
			System.out.println("=========================================================");
			System.out.println(cbtList.get(i).getId() + "." + cbtList.get(i).getCb_question());
			System.out.println("---------------------------------------------------------");

			String[] strAnswer = { cbtList.get(i).getCb_ans1(), cbtList.get(i).getCb_ans2(),
					cbtList.get(i).getCb_ans3(), cbtList.get(i).getCb_ans4() };

			Collections.shuffle(Arrays.asList(strAnswer));

			System.out.println("1:" + strAnswer[0]);
			System.out.println("2:" + strAnswer[1]);
			System.out.println("3:" + strAnswer[2]);
			System.out.println("4:" + strAnswer[3]);

			for (int j = 0; j < 2; j++) {
				System.out.print("정답 입력 >>> ");
				String strAns = scan.nextLine();
				int intAns = Integer.valueOf(strAns);

				String strCans = cbtList.get(i).getCb_cans();

				if (strAnswer[intAns - 1].equals(strCans.trim())) {
					System.out.println("정답입니다.");

					cList.add(cbtList.get(i).getId());

					intCountAns += 1;
					break;

				} else {
					System.out.println("오답입니다.");

					if (j == 1) {
						wList.add(cbtList.get(i).getId());
						System.out.println("한번의 기회 소멸");
						break;
					}
					System.out.print("다음 문제 : Enter, 다시 풀기 : 0 >>> ");
					String strRet = scan.nextLine();
					if (strRet.equals("0")) {
						System.out.println("< Last Chance >");
						continue;
					} else {
						System.out.println("다음 문제로 넘어갑니다.");
						break;
					}
				}
			}

			if ((i + 1) % 5 == 0) {
				System.out.print("정답 문항 : ");
				for (String cCount : cList) {
					System.out.print(cCount + ", ");
				}

				System.out.print("오답 문항 : ");
				for (String wCount : wList) {
					System.out.print(wCount + ", ");
				}

				cList.clear();
				wList.clear();
				System.out.println();
			}

			System.out.print("다음 문제 및 다시 풀기 : Enter, 문제풀이 그만두기 : 0 입력 >>> ");
			String strStop = scan.nextLine();
			if (strStop.equals("0")) {
				System.out.println("점수를 표시하고 프로그램을 종료합니다.");
				int intScore = intCountAns * 5;
				System.out.println("점수는 " + intScore + "점입니다.");
				return;
			}
		}
		System.out.println((intCountAns * 5) + "점입니다.");
		System.out.println("---------------------------------------------------------");
	}

	public QuesAndAnsVO updateInput(String strUpNum) {
		System.out.print("문항을 변경 >>> ");
		String strQues = scan.nextLine();
		System.out.print("답안 1번 변경 >>> ");
		String strAns1 = scan.nextLine();
		System.out.print("답안 2번 변경 >>> ");
		String strAns2 = scan.nextLine();
		System.out.print("답안 3번 변경 >>> ");
		String strAns3 = scan.nextLine();
		System.out.print("답안 4번 변경 >>> ");
		String strAns4 = scan.nextLine();
		System.out.print("정답 변경 >>> ");
		String strCAns = scan.nextLine();

		QuesAndAnsVO vo = new QuesAndAnsVO();

		vo.setId(strUpNum);
		vo.setCb_question(strQues);
		vo.setCb_ans1(strAns1);
		vo.setCb_ans2(strAns2);
		vo.setCb_ans3(strAns3);
		vo.setCb_ans4(strAns4);
		vo.setCb_cans(strCAns);

		qnaList.add(vo);

		return vo;

	}

	public QuesAndAnsVO input() {
		System.out.print("문항을 입력 >>> ");
		String strQues = scan.nextLine();
		System.out.print("답안 1번 입력 >>> ");
		String strAns1 = scan.nextLine();
		System.out.print("답안 2번 입력 >>> ");
		String strAns2 = scan.nextLine();
		System.out.print("답안 3번 입력 >>> ");
		String strAns3 = scan.nextLine();
		System.out.print("답안 4번 입력 >>> ");
		String strAns4 = scan.nextLine();
		System.out.print("정답 입력 >>> ");
		String strCAns = scan.nextLine();

		QuesAndAnsVO vo = new QuesAndAnsVO();

		vo.setId("SEQ_CBT.NEXTVAL");
		vo.setCb_question(strQues);
		vo.setCb_ans1(strAns1);
		vo.setCb_ans2(strAns2);
		vo.setCb_ans3(strAns3);
		vo.setCb_ans4(strAns4);
		vo.setCb_cans(strCAns);

		qnaList.add(vo);

		return vo;
	}

}