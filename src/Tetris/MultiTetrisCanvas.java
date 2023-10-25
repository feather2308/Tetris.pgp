package Tetris;

import java.awt.*;
import javax.swing.*;

import Tetris.Block.Empty;
import Tetris.Block.Piece;

@SuppressWarnings("serial")
public class MultiTetrisCanvas extends JPanel{
	protected Thread worker;
	protected Color colors[];
	protected TetrisData data;
	protected MyTetris myTetris;
	protected Piece current;
	
	protected int w = 25;
	protected int margin = 20;
	protected boolean stop;
	
	protected Graphics bufferGraphics = null;
	protected Image offscreen;
	
	public MultiTetrisCanvas(MyTetris myTetris) {
		this.myTetris = myTetris;
		
		data = new TetrisData(this.myTetris);
		current = new Empty(this.data);
		
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
		
		data.clear();
		repaint();
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
		
		if(current != null) {
			for(int i = 0; i < current.getR().length; i++) {
				bufferGraphics.setColor(colors[current.getType()]);
				bufferGraphics.fill3DRect(margin / 2 + w * (current.getX() + current.getC()[i]), margin/2 + w * (current.getY() + current.getR()[i]) + w, w, w, true);
			}
		}
		
		g.drawImage(offscreen, 0, 0, null);
	}
	
	public Dimension getPreferredSize() {
		int tw = w * TetrisData.COL + margin;
		int th = w * TetrisData.ROW + margin;
		return new Dimension(tw, th);
	}
}