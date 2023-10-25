package Tetris;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JOptionPane;

public class ClientHandler extends Thread {
	private Socket socket = null;
	private BufferedReader in = null;
	private PrintWriter out = null;
	
	private MyTetris myTetris = null;
	
	public ClientHandler(MyTetris myTetris, String IP, int port) {
		this.myTetris = myTetris;
		try {
			socket = new Socket(IP, port);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e);
			myTetris.connect = false;
			myTetris.renderUIBase();
			myTetris.renderUIBaseServerTab(myTetris.volume);
		}
	}
	
	public void run() {
		System.out.println("헉run");
		try {
			myTetris.getUConnectCheckBox().setSelected(myTetris.connect);
			
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
			
			String inStr;
			String[] inStrFix = {""};
			
			while(true) {
				out.write(myTetris.getReadyCheckBox().isSelected() ? 1 : 0);
				out.flush();
				
				inStr = in.readLine();
				
				if(inStr != null) {
					inStrFix = Function.convertStringDiv(inStr);
					
					if(Integer.parseInt(inStrFix[0]) == 1) myTetris.getUReadyCheckBox().setSelected(true);
					else myTetris.getUReadyCheckBox().setSelected(false);
					myTetris.getAttackCheckBox().setSelected(Integer.parseInt(inStrFix[1]) == 1 ? true : false);
					myTetris.getItemCheckBox().setSelected(Integer.parseInt(inStrFix[2]) == 1 ? true : false);
				}
				
				if((myTetris.getReadyCheckBox().isSelected() ? true : false) && (myTetris.getUReadyCheckBox().isSelected() ? true : false)) 
					break;
			}
			
			myTetris.renderUIMulti();
			myTetris.tetrisCanvas.start();
			
			out.write(1);
			System.out.println(in.readLine());
			
			myTetris.tetrisCanvas.data.setAttack(Integer.parseInt(inStrFix[1]) == 1 ? true : false);
			myTetris.tetrisCanvas.useItem = myTetris.getItemCheckBox().isSelected();
			
			try {Function.handlerRun(myTetris, in, out, myTetris.enemyScore);} catch(Exception e) {System.out.println(e);}
		} catch(Exception e) {
			JOptionPane.showMessageDialog(null, e);
			myTetris.connect = false;
			myTetris.renderUIBase();
			myTetris.renderUIBaseServerTab(myTetris.volume);
		}
	}
}