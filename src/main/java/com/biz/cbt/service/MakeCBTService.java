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

public class MakeCBTService {

	SqlSessionFactory sessionFactory;
	List<QuesAndAnsVO> qnaList;

	List<String> cList; // 전체 정답인 문제 번호를 담을 리스트
	List<String> wList; // 전체 오답인 문제 번호를 담을 리스트

	List<String> c5List; // 5문제마다 정답인 문제 번호를 담을 리스트
	List<String> w5List; // 5문제마다 오답인 문제 번호를 담을 리스트

	QuesAndAnsVO qnavo;

	Scanner scan;

	/*
	 * 생성자에서 member변수들을 초기화
	 */
	public MakeCBTService() {
		scan = new Scanner(System.in);
		qnaList = new ArrayList();

		cList = new ArrayList();
		wList = new ArrayList();
		c5List = new ArrayList();
		w5List = new ArrayList();

		qnavo = new QuesAndAnsVO();

		OracleSqlFactory sqlFactory = new OracleSqlFactory();

		this.sessionFactory = sqlFactory.getSessionFactory();
	}

	/*
	 * 첫 메뉴를 보여주는 method
	 */
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

	/*
	 * 위의 startMenu()에서 1(문제입력)을 입력했을 때 실행되는 method 문제 등록(insert), 수정(update),
	 * 삭제(delete) 및 종료 method
	 */
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

	/*
	 * 문제모음.txt파일에 있는 문제들을 읽어오는 method (초기의 15문제) [ 문제:답안1:답안2:답안3:답안4:정답 ] 형식으로
	 * 저장되어있음.
	 */
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

	/*
	 * dao의 selecteAll()를 실행시켜서 문제들을 담은 리스트를 모두 보여주는 method
	 */
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

	/*
	 * 초기의 15문제를 DB에 추가시키는 method
	 */
	public void ques15insert(QuesAndAnsVO vo) {
		SqlSession session = this.sessionFactory.openSession();

		MakeCBTDao dao = session.getMapper(MakeCBTDao.class);

		int intRet = dao.insert(vo);

		session.commit();
		session.close();

	}

	/*
	 * 초기의 15문제가 아닌 새로 추가시키고자 할 때 실행되는 method
	 */
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

	/*
	 * 문제를 삭제시킬 때 문제 번호(id)는 DB 칼럼에서 PRIMARY KEY이다. 그래서 그 번호를 매개변수로 전달받아서 그 번호에 해당하는
	 * 정보만 삭제시키는 method
	 */
	public int delete(String id) {
		SqlSession session = this.sessionFactory.openSession();

		MakeCBTDao dao = session.getMapper(MakeCBTDao.class);

		int intRet = dao.delete(id);

		session.commit();
		session.close();

		return intRet;
	}

	/*
	 * update 역시 위의 delete와 동일하게 id 값을 매개변수로 받아 그 id에 해당하는 문제만 수정하는 method
	 */
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

	/*
	 * 위의 inputQues() 에서 2(문제 풀이)를 키보드로 입력했을 때 실행되는 method
	 */
	public void study() {

		SqlSession session = this.sessionFactory.openSession();

		MakeCBTDao dao = session.getMapper(MakeCBTDao.class);

		int intCountAns = 0; // 정답 문항 개수를 담을 변수

		/*
		 * DB에 담겨있는 문제들을 return 받아서 리스트에 담기
		 */
		List<QuesAndAnsVO> cbtList = dao.selectAll();

		int intSize = cbtList.size();

		/*
		 * 문제 개수만큼 for문을 돌림.
		 */
		for (int i = 0; i < intSize; i++) {
			System.out.println("=========================================================");
			System.out.println(cbtList.get(i).getId() + "." + cbtList.get(i).getCb_question());
			System.out.println("---------------------------------------------------------");

			/*
			 * 답안들을 섞기전에 각 문제마다 4개의 답안들을 배열에 넣고
			 */
			String[] strAnswer = { cbtList.get(i).getCb_ans1(), cbtList.get(i).getCb_ans2(),
					cbtList.get(i).getCb_ans3(), cbtList.get(i).getCb_ans4() };

			/*
			 * Collections.shuffle(Arrays.asList(배열명))을 이용해서 배열을 섞어준다.
			 */
			Collections.shuffle(Arrays.asList(strAnswer));

			/*
			 * console에 출력할 때에는 섞인 답안들이 나오게 된다.
			 */
			System.out.println("1:" + strAnswer[0]);
			System.out.println("2:" + strAnswer[1]);
			System.out.println("3:" + strAnswer[2]);
			System.out.println("4:" + strAnswer[3]);

			/*
			 * 오답일 때 한번의 기회를 더 주기 위해 이중 for문을 사용
			 */
			for (int j = 0; j < 2; j++) {
				System.out.print("정답 입력 >>> ");
				String strAns = scan.nextLine();
				int intAns = Integer.valueOf(strAns);

				String strCans = cbtList.get(i).getCb_cans();

				/*
				 * intAns는 위의 사용자로부터 입력받은 선택답안의 번호임. 
				 * ex ) 사용자가 2번을 입력했다면 strAnswer[2 - 1]답안을 선택한 것임.
				 *      사용자가 선택한 답안과 리스트에 저장된 정답과 비교해서 같으면 정답
				 *      													   다르면 오답
				 */
				if (strAnswer[intAns - 1].equals(strCans.trim())) {
					System.out.println("정답입니다.");

					// 정답이면 전체 정답 문제 번호를 담을 리스트와 5개씩 정답 문제 번호를 담을 리스트에 문제 번호를 추가
					c5List.add(cbtList.get(i).getId());
					cList.add(cbtList.get(i).getId());

					// 정답이면 + 1 씩 증가
					intCountAns += 1;
					break;

				} else {
					System.out.println("오답입니다.");

					/*
					 * 아래의 if문은 문제를 처음 풀었을 때 오답이고 다시 풀기를 선택하고 다시 풀었어도 오답이였을 때 실행되는 코드
					 */
					if (j == 1) {

						/*
						 * 한번의 기회가 소멸되었음을 알려주고 전체 오답과 5문제 오답리스트에 저장
						 */
						wList.add(cbtList.get(i).getId());
						w5List.add(cbtList.get(i).getId());
						System.out.println("한번의 기회 소멸");
						break;
					}
					System.out.print("다음 문제 : Enter, 다시 풀기 : 0 >>> ");
					String strRet = scan.nextLine();
					
					/*
					 * 밑의 if문은 문제를 처음 풀었는 데 오답일 경우에 실행이 된다.
					 */
					if (strRet.equals("0")) {
						
						/*
						 * 마지막 기회임을 알려줌
						 */
						System.out.println("< Last Chance >");
						continue;
					} else {
						/*
						 * 다시 풀지 않고 다음 문제로 넘어가기
						 */
						System.out.println("다음 문제로 넘어갑니다.");
						break;
					}
				}
			}

			/*
			 * 5문제마다 정답문항과 오답문항을 표시해주는 코드
			 */
			if ((i + 1) % 5 == 0) {
				System.out.print("정답 문항 : ");
				for (String cCount : c5List) {
					System.out.print(cCount + ", ");
				}

				System.out.print("오답 문항 : ");
				for (String wCount : w5List) {
					System.out.print(wCount + ", ");
				}

				/*
				 * 1~5, 6~10, 11~15 각 5문제마다 보여주기 위해 리스트를 clear
				 */
				c5List.clear();
				w5List.clear();
				System.out.println();
			}

			/*
			 * 모든 문제를 다 풀거나 사용자가 중간에 문제풀이를 그만두기를 선택하면 점수를 표시, 전체 정답 및 오답문항을 보여주고 끝냄.
			 */
			System.out.print("다음 문제 및 다시 풀기 : Enter, 문제풀이 그만두기 : 0 입력 >>> ");
			String strStop = scan.nextLine();
			if (strStop.equals("0")) {
				System.out.println("점수를 표시하고 프로그램을 종료합니다.");
				int intScore = intCountAns * 5;

				System.out.println("정답 문항 : " + cList);
				System.out.println("오답 문항 : " + wList);
				System.out.println("점수는 " + intScore + "점입니다.");

				return;
			}
		}
		System.out.println("정답 문항 : " + cList);
		System.out.println("오답 문항 : " + wList);
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