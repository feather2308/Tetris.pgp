package Tetris;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*

함수들 모음집
 
*/

public class Function {
	public static void handlerRun(MyTetris myTetris, BufferedReader in, PrintWriter out, EnemyScore enemyScore) throws IOException {
		boolean check = true;
		int tempALine = 0;
		
		//output 변수들
		int[][] data;
		int[] r, c;
		int death, score = 0, x, y, CPType, outLine, itemBPC;
		
		String outStr;
		
		//input 변수들
		int inDeath = 0;
		int[][] inData;
		int[] inCPr, inCPc;

		String inStr;
		String[] inStrFix = {""};
		
		while(true) {
			if(myTetris.tetrisCanvas.stop) death = 1;
			else death = 0;
			score = myTetris.tetrisCanvas.data.getScore();
			data = myTetris.tetrisCanvas.data.getData();
			outLine = myTetris.tetrisCanvas.data.getLine();
			
			if(myTetris.tetrisCanvas.current != null) {
				r = myTetris.tetrisCanvas.current.getR();
				c = myTetris.tetrisCanvas.current.getC();
				x = myTetris.tetrisCanvas.current.getX();
				y = myTetris.tetrisCanvas.current.getY();
				CPType = myTetris.tetrisCanvas.current.getType();
			}
			else {
				r = new int[] {0, 0, 0, 0};
				c = new int[] {0, 0, 0, 0};
				x = 0;
				y = 0;
				CPType = 8;
			}
			
			itemBPC = myTetris.tetrisCanvas.itemBizarrePieceCounter;
			
			outStr = death + "p"									//tetrisCanvas.stop					Boolean 자료형 -> convert int 자료형
					+ score + "p"									//tetrisCanvas.data.getScore()		int 자료형
					+ Function.convertIntArrayToString(data) + "p"	//tetrisCanvas.data.getData()		int[][] 자료형 즉, data의 data임.
					+ Function.convertIntArrayToString(r) + "p"		//tetrisCanvas.current.r			int[] 자료형
					+ Function.convertIntArrayToString(c) + "p"		//tetrisCanvas.current.c			int[] 자료형
					+ x + "p"										//tetrisCanvas.current.center.x		int 자료형
					+ y + "p"										//tetrisCanvas.current.center.y		int 자료형
					+ CPType + "p"									//tetrisCanvas.current.getType()	int 자료형
					+ outLine + "p"									//tetrisCanvas.data.getLine()		int 자료형
					+ itemBPC;										//tetrisCanvas.itemBizarrePieceCounter int 자료형
			
			out.println(outStr);
			out.flush();
			
			if(check) {
				System.out.println(in.readLine());
				check = false;
				continue;
			}
			
			inStr = in.readLine();
			
			if(inStr != null) {
				inStrFix = Function.convertStringDiv(inStr); 	//inStrFix[0, 1, 2, 3, 4, 5, 6, 7, 8, 9]
																//0 = 죽음			1 = 점수				2 = data.data 테이블임.
																//3 = 현재조각 r[]		4 = 현재조각 c[]
																//5 = 현재조각 센터 x	6 = 현재조각 센터 y		7 = 현재조각 타입
																//8 = 데이터 라인		9 = 괴상한 조각 카운터
				inDeath = Integer.parseInt(inStrFix[0]);
				inData = Function.convertStringToIntArray(inStrFix[2]);
				if(inData.length == TetrisData.ROW && inData[0].length == TetrisData.COL) {
					myTetris.multiTetrisCanvas.data.setData(Function.convertStringToIntArray(inStrFix[2]));
					myTetris.multiTetrisCanvas.repaint();
				}
				inCPr = Function.convertStringToIntArray(inStrFix[3], true);
				inCPc = Function.convertStringToIntArray(inStrFix[4], true);
				if(inCPr.length > 1 && inCPc.length > 1) {
					myTetris.multiTetrisCanvas.current.setR(inCPr);
					myTetris.multiTetrisCanvas.current.setC(inCPc);
					myTetris.multiTetrisCanvas.current.setX(Integer.parseInt(inStrFix[5]));
					myTetris.multiTetrisCanvas.current.setY(Integer.parseInt(inStrFix[6]));
					myTetris.multiTetrisCanvas.current.setType(Integer.parseInt(inStrFix[7]));
				}
				if(tempALine != Integer.parseInt(inStrFix[8]) / 4) {
					myTetris.tetrisCanvas.aLine += (Integer.parseInt(inStrFix[8]) / 4 - tempALine > 0 ? Integer.parseInt(inStrFix[8]) / 4 - tempALine : 0);
					tempALine = Integer.parseInt(inStrFix[8]) / 4;
				}
				myTetris.tetrisCanvas.itemBizarrePieceCount = Integer.parseInt(inStrFix[9]);
			}

			if(inDeath == 1 && myTetris.tetrisCanvas.stop) break;
		}
		
		outStr = 1 + "p"
				+ score + "p"
				+ Function.convertIntArrayToString(data) + "p"
				+ Function.convertIntArrayToString(r) + "p"
				+ Function.convertIntArrayToString(c) + "p"
				+ 0 + "p"
				+ 0 + "p"
				+ 8 + "p"
				+ -1 + "p"
				+ 0;
		
		out.println(outStr);
		out.flush();
		
		if(inStr != null) {
			inStrFix = Function.convertStringDiv(inStr);
			
			inDeath = Integer.parseInt(inStrFix[0]);
			inData = Function.convertStringToIntArray(inStrFix[2]);
			if(inData.length == TetrisData.ROW && inData[0].length == TetrisData.COL) {
				myTetris.multiTetrisCanvas.data.setData(Function.convertStringToIntArray(inStrFix[2]));
				myTetris.multiTetrisCanvas.repaint();
			}
			myTetris.multiTetrisCanvas.current = null;
		}
		
		enemyScore.getMeLabel().setText("Me");
		enemyScore.getMeScoreLabel().setText(score+"");
		enemyScore.getEnemyLabel().setText("Foe");
		enemyScore.getEnemyScoreLabel().setText(inStrFix[1]);
		String win;
		if(score > Integer.parseInt(inStrFix[1])) win = "<< Win";
		else if(score < Integer.parseInt(inStrFix[1])) win = "Win >>";
		else win = "Draw";
		enemyScore.getWinLabel().setText(win);
		enemyScore.setVisible(true);
	}
	
	public static String convertIntArrayToString(int[][] array) {
        StringBuilder sb = new StringBuilder();
        
        for (int[] row : array) {
            for (int num : row) {
                sb.append(num).append("s");
            }
            sb.append("n");
        }
        return sb.toString();
    }
	
    public static String convertIntArrayToString(int[] array) {
    	StringBuilder sb = new StringBuilder();
    	
    	for(int num : array) {
    		sb.append(num).append("s");
    	}
    	return sb.toString();
    }
    
    public static int[][] convertStringToIntArray(String str) {
        String[] rows = str.trim().split("n");
        int[][] array = new int[TetrisData.ROW][TetrisData.COL];
        
        for (int i = 0; i < TetrisData.ROW; i++) {
            String[] nums = rows[i].trim().split("s");
            for (int j = 0; j < TetrisData.COL; j++) {
                array[i][j] = Integer.parseInt(nums[j]);
            }
        }
        return array;
    }
    
    public static int[] convertStringToIntArray(String str, boolean p) {
    	String[] num = str.trim().split("s");
    	int[] array = new int[num.length];
    	
    	for(int i = 0; i < num.length; i++) {
    		array[i] = Integer.parseInt(num[i]);
    	}
    	return array;
    }
    
    public static String[] convertStringDiv(String str) {
    	String[] reStr = str.trim().split("p");
    	return reStr;
    }
    
    public static String readLeaderBoard() {
    	String str = "";
    	try {
	    	File file = new File(System.getProperty("java.io.tmpdir") + "/tlb.cTeam");
	    	
	    	FileReader file_reader = new FileReader(file);
	    	int cur = 0;
	    	while((cur = file_reader.read()) != -1) {
	    		str = str + (char)cur;
	    	}
	    	file_reader.close();
    	} catch(Exception e) {
    		try {
				str = makeLeaderBoard();
			} catch (Exception e1) {
				System.out.println(e);
				System.out.println(e1);
				return "-1";
			}
		}
    	return str;
    }
    
    public static void saveLeaderBoard(String str) {
    	try {
			OutputStream output = new FileOutputStream(System.getProperty("java.io.tmpdir") + "/tlb.cTeam");
			byte[] by = str.getBytes();
			output.write(by);
			output.close();
		} catch (Exception e) {
			System.out.println(e);
		}
    }
    
    public static String makeLeaderBoard() throws Exception {
    	OutputStream output = new FileOutputStream(System.getProperty("java.io.tmpdir") + "/tlb.cTeam");
    	String str = "10000, AAA"
    			+ ", 9000, BBB"
    			+ ", 8000, CCC"
    			+ ", 7000, DDD"
    			+ ", 6000, EEE"
    			+ ", 5000, FFF"
    			+ ", 4000, GGG"
    			+ ", 3000, HHH"
    			+ ", 2000, III"
    			+ ", 1000, JJJ";
		byte[] by = str.getBytes();
		output.write(by);
		output.close();
		return str;
    }
    
    public static void refreshLeaderBoard() {
    	int i;
    	String leaderBoardRawData = readLeaderBoard();
    	
		String[] leaderBRFix = leaderBoardRawData.split(",\\s*");

		String[] strScorelist = new String[leaderBRFix.length / 2];
		String[] strNamelist = new String[leaderBRFix.length / 2];
		for (i = 0; i < leaderBRFix.length; i += 2) {
		    strScorelist[i / 2] = leaderBRFix[i].replaceAll("\\s+", "");
		    strNamelist[i / 2] = leaderBRFix[i + 1].replaceAll("\\s+", "");
		}

		i = 0;
		for (String score : strScorelist) {
			MyTetris.getLeaderBoard().getScoreLabel()[i].setText(score);
			i++;
		}
		
		i = 0;
		for (String name : strNamelist) {
			MyTetris.getLeaderBoard().getNameLabel()[i].setText(name);
			i++;
		}
		MyTetris.getLeaderBoard().setVisible(true);
    }
    
    public static void recordLeaderBoard(int score, String name) throws Exception {
    	String leaderBoardRawData = readLeaderBoard();
    	
    	String[] rawData = leaderBoardRawData.split(", ");
        List<String> leaderBoardList = new ArrayList<>(Arrays.asList(rawData));
    	
    	for (int i = 0; i < leaderBoardList.size(); i += 2) {
            int currentScore = Integer.parseInt(leaderBoardList.get(i));
            if (score > currentScore) {
                leaderBoardList.add(i, name);
                leaderBoardList.add(i, String.valueOf(score));
                leaderBoardList.remove(20);
                leaderBoardList.remove(20);
                break;
            }
        }
    	
        StringBuilder newLeaderBoard = new StringBuilder();
        for (String item : leaderBoardList) {
            newLeaderBoard.append(item).append(", ");
        }
        newLeaderBoard.setLength(newLeaderBoard.length() - 2);

    	OutputStream output = new FileOutputStream(System.getProperty("java.io.tmpdir") + "/tlb.cTeam");
		byte[] by = newLeaderBoard.toString().getBytes();
		output.write(by);
		output.close();
    }
}