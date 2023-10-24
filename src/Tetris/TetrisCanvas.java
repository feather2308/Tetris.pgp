package Tetris;

import java.awt.*;
import javax.swing.*;

import Tetris.Block.AE;
import Tetris.Block.Bar;
import Tetris.Block.CP;
import Tetris.Block.Dg;
import Tetris.Block.El;
import Tetris.Block.Er;
import Tetris.Block.Ho;
import Tetris.Block.Kl;
import Tetris.Block.Kr;
import Tetris.Block.Piece;
import Tetris.Block.Square;
import Tetris.Block.Tee;

import java.awt.event.*;

@SuppressWarnings("serial")
public class TetrisCanvas extends JPanel implements Runnable, KeyListener {
	protected Thread worker;
	protected Color colors[];
	protected int w = 25;
	protected TetrisData data;
	protected int margin = 20;
	protected boolean stop, makeNew;
	
	protected Piece current;
	protected Piece next;
	protected Piece hold;
	protected Piece miri;
	
	protected int interval = 2000;
	protected int level = TetrisData.BASE_SPEED;
	protected int lineTmp, aLine = 0;
	protected int itemBizarrePieceCount, tmpIBPC;
	protected int itemBizarrePieceCounter = 0;
	protected boolean useItem = true;
	
	protected Graphics bufferGraphics = null;
	protected Image offscreen;
	
	protected SoundHandler soundHandler;
	
	public TetrisCanvas(SoundHandler soundHandler) {
		data = new TetrisData();
		this.soundHandler = soundHandler;
		addKeyListener(this);
		colors = new Color[11];
		colors[0] = new Color(133, 133, 133);//미리보기색
		colors[1] = new Color(255, 0, 0);	//빨간색
		colors[2] = new Color(0, 255, 0);	//녹색
		colors[3] = new Color(0, 200, 255);	//노란색
		colors[4] = new Color(255, 255, 0);	//하늘색
		colors[5] = new Color(255, 150, 0);	//황토색
		colors[6] = new Color(210, 0, 240);	//보라색
		colors[7] = new Color(40, 0, 240);	//파란색
		colors[8] = new Color(238, 238, 238); //배경색
		colors[9] = new Color(80, 80, 80);	//검은회색
		colors[10] = new Color(200, 191, 231); //아이템블럭 색깔
	}
	
	public void start() {
		data.clear();
		lineTmp = data.getLine();
		itemBizarrePieceCount = 0;
		itemBizarrePieceCounter = 0;
		tmpIBPC = itemBizarrePieceCount;
		pieceMake();
		makeNew = true;
		stop = false;
		
		worker = new Thread(this);
		worker.start();

		requestFocus();
		MyTetris.getLblScoreLabel().setText("Score: " + data.getScore());
		MyTetris.getLblLineLabel().setText("Line: " + data.getLine());
		MyTetris.getLblLevelLabel().setText("Level: 1");
		repaint();
	}
	
	public void stop() {
		stop = true;
		current = null;
		miri = null;
		next = null;
		hold = null;
	}
	
	public void paint(Graphics g) {
		offscreen = createImage(getSize().width,getSize().height);
		bufferGraphics = offscreen.getGraphics();
		paintComponents(bufferGraphics);
		
		for(int i = 0; i < TetrisData.ROW; i++) {
			for(int k = 0; k < TetrisData.COL; k++) {
				if(data.getAt(i, k) == 0) {
					bufferGraphics.setColor(colors[data.getAt(i, k)]);
					bufferGraphics.draw3DRect(margin/2 + w * k, margin/2 + w * i + w, w, w, true);
				} else {
					bufferGraphics.setColor(colors[data.getAt(i, k)]);
					bufferGraphics.fill3DRect(margin/2 + w * k, margin/2 + w * i + w, w, w, true);
				}
			}
		}
		
		if(miri != null) {
			while(miri.moveDown() != true) { }
			for(int i = 0; i < miri.getR().length; i++) {
				bufferGraphics.setColor(colors[0]);
				bufferGraphics.fill3DRect(margin / 2 + w * (miri.getX() + miri.getC()[i]), margin/2 + w * (miri.getY() + miri.getR()[i]) + w, w, w, true);
			}
		}
		
		if(current != null) {
			for(int i = 0; i < current.getR().length; i++) {
				bufferGraphics.setColor(colors[current.getType()]);
				bufferGraphics.fill3DRect(margin / 2 + w * (current.getX() + current.getC()[i]), margin/2 + w * (current.getY() + current.getR()[i]) + w, w, w, true);
			}
		}

		if(next != null) {
	    	int nextX = TetrisData.COL * 2;
	    	int XPaint = getXPaint(next);
            int nextY = 5;
            int YPaint = getYPaint(next);
	        for (int i = 0; i < next.getR().length; i++) {
	            bufferGraphics.setColor(colors[next.getType()]);
	            bufferGraphics.fill3DRect(XPaint + (w - 10) * (nextX + next.getC()[i]), YPaint + (w - 10) * (nextY + next.getR()[i]), w - 10, w - 10, true);
	        }
		}
		    
	    if(hold != null) {
	    	int holdX = TetrisData.COL * 2;
	    	int XPaint = getXPaint(hold);
	    	int holdY = 11;
            int YPaint = getYPaint(hold);
	    	for(int i = 0; i < hold.getR().length; i++) {
	    		bufferGraphics.setColor(colors[hold.getType()]);
	    		bufferGraphics.fill3DRect(XPaint + (w - 10) * (holdX + hold.getC()[i]), YPaint + (w - 10) * (holdY + hold.getR()[i]), w - 10, w - 10, true);
	    	}
	    }
	    
	    g.drawImage(offscreen, 0, 0, null);
	}
	
	private int getXPaint(Piece piece) {
		switch(piece.getMaxX() - piece.getMinX()) {
			case 1:
			case 3:
				return 7;
			case 2:
				return 1;
			default:
				return margin / 2;
		}
	}
	
	private int getYPaint(Piece piece) {
		switch(piece.getMaxY() - piece.getMinY()) {
			case 0:
				return 3;
			case 1:
				return 10;
			default:
				return margin / 2;
		}
	}

	public Dimension getPreferredSize() {
		int tw = w * TetrisData.COL + margin;
		int th = w * TetrisData.ROW + margin;
		return new Dimension(tw, th);
	}
	
	public void run() {
		while(!stop) {
			try {
				if(makeNew) {
					current = next;
					miri = current.deepCopy();
					gameCheck(false);
					if(itemBizarrePieceCount > tmpIBPC) {
						itemBizarrePiece();
						tmpIBPC++;
					}
					else pieceMake();
					makeNew = false;
					lineTmp = data.getLine();
				} else {
					if(current.moveDown()) {
						makeNew = true;
						gameCheck(true);
						soundHandler.playSound("src/block.wav");
						current = null;
						miri = null;
						data.removeLiness();
						try{data.attackLiness();} catch(Exception e) {System.out.println(e);}
					}
					try {
						Thread.sleep(interval/level);
					} catch(Exception e) { }
				}
				repaint();
			} catch(Exception e) {System.out.println(e);}
		}
		try {if(MyTetris.canChange) MyTetris.getMntmMenuItem().setEnabled(true);}
		catch(Exception e) {System.out.println(e);}
	}
	
	public void keyPressed(KeyEvent e) {
		if(current == null) return;
		
		switch(e.getKeyCode()) {
			case 37:	//왼쪽 화살표
				current.moveLeft();
				miri = current.deepCopy();
				repaint();
				break;  
			case 39:	//오른쪽 화살표
				current.moveRight();
				miri = current.deepCopy();
				repaint();
				break;
			case 38:	//윗쪽 화살표
			case 90:	//'z' 키
				current.rotate();
				miri = current.deepCopy();
				repaint();
				break;
			case 40:	//아랫쪽 화살표
				if(current.moveDown()) {
					makeNew = true;
					gameCheck(true);
					soundHandler.playSound("src/block.wav");
					current = null;
					miri = null;
					data.removeLiness();
					try{data.attackLiness();} catch(Exception e1) {System.out.println(e1);}
					worker.interrupt();
				}
				repaint();
				break;
			case 32:	//스페이스바
				while(!current.moveDown()) data.setScore(10);
				makeNew = true;
				gameCheck(true);
				soundHandler.playSound("src/block.wav");
				current = null;
				miri = null;
				data.removeLiness();
				try{data.attackLiness();} catch(Exception e1) {System.out.println(e1);}
				repaint();
				worker.interrupt();
				break;
			case 67:	//'c' 키
				if(current.getSave()) {
					if(hold == null) {
						hold = current;
						rotateSavePiece(hold);
						current = next;
						miri = current.deepCopy();
						pieceMake();
						current.convertSave();
					} else {
						Piece temp = hold;
						hold = current;
						rotateSavePiece(hold);
						current = temp;
						current.convertSave();
						current.resetPosition();
						miri = current.deepCopy();
					}
					worker.interrupt();
				}
				break;
			case 65:	//'a' 키
				if(useItem) {
					if(data.getScore()>=5000) {
						data.itemRemoveLine();
						data.setScore(-5000);
						MyTetris.getLblScoreLabel().setText("Score: " + data.getScore());
						repaint();
					}
				}
				break;
			case 83:	//'s' 키
				if(useItem) {
					if(data.getScore()>=1000) {
						itemChangePiece();
						miri = current.deepCopy();
						data.setScore(-1000);
						MyTetris.getLblScoreLabel().setText("Score: " + data.getScore());
						repaint();
					}
				}
				break;
			case 68:	//'d' 키
				if(useItem) {
					if(data.getScore()>=15000) {
						itemBizarrePieceCounter++;
						data.setScore(-15000);
						MyTetris.getLblScoreLabel().setText("Score: " + data.getScore());
					}
				}
				break;
		}
	}

	public void keyReleased(KeyEvent e) { }
	public void keyTyped(KeyEvent e) { }
	
	private int temp = 7;
	
	public void pieceMake() {
		int random = (int)(Math.random() * Integer.MAX_VALUE) % 7;
		if(temp == random) random = (int)(Math.random() * Integer.MAX_VALUE) % 7;
		temp = random;
        switch(random) {
            case 0:
                next = new Bar(data);
                break;
            case 1:
                next = new Tee(data);
                break;
            case 2:
                next = new El(data);
                break;
            case 3:
                next = new Square(data);
                break;
            case 4:
                next = new Er(data);
                break;
            case 5:
                next = new Kl(data);
                break;
            case 6:
                next = new Kr(data);
                break;
            default:
                pieceMake();
                break;
        }
	}
	
	private void itemChangePiece() {
		int random = (int)(Math.random() * Integer.MAX_VALUE) % 7;
		if(temp == random) random = (int)(Math.random() * Integer.MAX_VALUE) % 7;
		switch(random) {
	        case 0:
	            current = new Bar(data);
	            break;
	        case 1:
	        	current = new Tee(data);
	            break;
	        case 2:
	        	current = new El(data);
	            break;
	        case 3:
	        	current = new Square(data);
	            break;
	        case 4:
	        	current = new Er(data);
	            break;
	        case 5:
	        	current = new Kl(data);
	            break;
	        case 6:
	        	current = new Kr(data);
	            break;
	        default:
	            itemChangePiece();
	            break;
	    }
	}
	
	private void itemBizarrePiece() {
		int random = (int)(Math.random() * Integer.MAX_VALUE) % 4;
		if(temp == random) random = (int)(Math.random() * Integer.MAX_VALUE) % 4;
		temp = random;
        switch(random) {
            case 0:
                next = new AE(data);
                break;
            case 1:
                next = new CP(data);
                break;
            case 2:
                next = new Dg(data);
                break;
            case 3:
                next = new Ho(data);
                break;
            default:
                pieceMake();
                break;
        }
	}
	
	public void rotateSavePiece(Piece save) {
		while(save.getRoteType() == 3 || save.getRoteType() > 4) {
			if (save.getRoteType() == 3) {
				save.rotate4();
				save.roteTypemm();
			}
			else if(save.getRoteType() > 4) {
				save.rotate4();
				save.rotate4();
				save.rotate4();
				save.roteTypemm();
			}
		}
	}
	
	public void gameCheck(boolean downSetting) {
		if(current==null) return;
		if(current.copy(downSetting)) {
			stop();
//			JOptionPane.showMessageDialog(this, "게임끝\n점수: " + score);
			MyTetris.getGameOver().getScoreLabel().setText("점수: " + data.getScore());
			MyTetris.getGameOver().getRecordScore().getScoreField().setText(data.getScore() + "");
			MyTetris.getGameOver().setVisible(true);
		}
	}
}
