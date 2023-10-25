package Tetris;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JOptionPane;

public class ServerHandler extends Thread {
	private ServerSocket socket = null;
	private Socket client = null;
	private BufferedReader in = null;
	private PrintWriter out = null;
	
	private MyTetris myTetris = null;
	
	public ServerHandler(MyTetris myTetris, int port){
		this.myTetris = myTetris;
		try {
			socket = new ServerSocket(port);
		} catch (IOException e) {JOptionPane.showMessageDialog(null, e);}
	}
	
	public void run() {
		try {
			System.out.println("헉runserver");
			
			client = socket.accept();
			myTetris.getUConnectCheckBox().setSelected(true);
			
			in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			out = new PrintWriter(new OutputStreamWriter(client.getOutputStream()));

			String outStr;

			while(true) {
				outStr = (myTetris.getReadyCheckBox().isSelected() ? 1 : 0) + "p" +
						 (myTetris.getAttackCheckBox().isSelected() ? 1 : 0) + "p" +
						 (myTetris.getItemCheckBox().isSelected() ? 1 : 0);
				out.println(outStr);
				out.flush();
	
				if(in.read() == 1) myTetris.getUReadyCheckBox().setSelected(true);
				else myTetris.getUReadyCheckBox().setSelected(false);
	
				if((myTetris.getReadyCheckBox().isSelected() ? true : false) && (myTetris.getUReadyCheckBox().isSelected() ? true : false)) break;
			}
			
			myTetris.renderUIMulti();
			myTetris.tetrisCanvas.start();
			
			outStr = "1p" + (myTetris.getAttackCheckBox().isSelected() ? 1 : 0) + "p" + (myTetris.getItemCheckBox().isSelected() ? 1 : 0);
			out.println(outStr);
			System.out.println(in.read());
			
			myTetris.tetrisCanvas.data.setAttack(myTetris.getAttackCheckBox().isSelected());
			myTetris.tetrisCanvas.useItem = myTetris.getItemCheckBox().isSelected();
			
			try {Function.handlerRun(myTetris, in, out, myTetris.enemyScore);} catch(Exception e) {System.out.println(e);}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e);
			myTetris.getUConnectCheckBox().setSelected(false);
			try {socket.close();} catch (IOException e1) {JOptionPane.showMessageDialog(null, e1);}
			myTetris.renderUIBase();
			myTetris.renderUIBaseServerTab(myTetris.volume);
		}
	}
}