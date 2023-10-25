package Tetris;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class MyTetris extends JFrame {	
	public final static char gjr = '헉', huk = '헉', 헉 = '헉';
	
	private TetrisCanvas tetrisCanvas;
	private MultiTetrisCanvas multiTetrisCanvas;
	private JMenuItem mntmStartMenuItem;
	private JLabel lblScoreLabel;
	private JLabel lblLineLabel;
	private JLabel lblLevelLabel;
	
	private JCheckBox lobbyServerConnectCheckBox;
	private JCheckBox lobbyClientConnectCheckBox;
	private JCheckBox lobbyServerReadyCheckBox;
	private JCheckBox lobbyClientReadyCheckBox;
	private JCheckBox lobbyAttackCheckBox;
	private JCheckBox lobbyItemCheckBox;
	
	private JLabel nextPieceLabel;
	private JLabel holdPieceLabel;
	
	private SoundHandler soundHandler;
	
	protected static boolean serverOpen = false;
	protected static boolean connect = false;
	protected boolean canChange = true;

	private final OpenServer openServer = new OpenServer(); {
		openServer.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		openServer.addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent e) {
				doOpen();
			}
		});
	}
	
	private final ConnectServer connectServer = new ConnectServer(); {
		connectServer.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		connectServer.addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent e) {
				doConnect();
			}
		});
	}
	
	private final EnemyScore enemyScore = new EnemyScore();
	private static final LeaderBoard leaderBoard = new LeaderBoard();
	private static final GameOver gameOver = new GameOver();
	private final keyHelp keyHelp = new keyHelp();
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MyTetris frame = new MyTetris();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public MyTetris() {
		setBounds(100, 100, 350, 500);
		renderUIBase();
		
		soundHandler = new SoundHandler("src/bgm.wav");
		soundHandler.controlSound(0.8f);
	}

	public TetrisCanvas getTetrisCanvas() {
		return tetrisCanvas;
	}
	public JLabel getLblScoreLabel() {
		return lblScoreLabel;
	}
	public JLabel getLblLineLabel() {
		return lblLineLabel;
	}
	public JLabel getLblLevelLabel() {
		return lblLevelLabel;
	}
	public JMenuItem getMntmMenuItem() {
		return mntmStartMenuItem;
	}
	public static LeaderBoard getLeaderBoard() {
		return leaderBoard;
	}
	public static GameOver getGameOver() {
		return gameOver;
	}
	public JCheckBox getUConnectCheckBox() {
		if(!serverOpen) return lobbyServerConnectCheckBox;
		else return lobbyClientConnectCheckBox;
	}
	public JCheckBox getReadyCheckBox() {
		if(serverOpen) return lobbyServerReadyCheckBox;
		else return lobbyClientReadyCheckBox;
	}
	public JCheckBox getUReadyCheckBox() {
		if(!serverOpen) return lobbyServerReadyCheckBox;
		else return lobbyClientReadyCheckBox;
	}
	public JCheckBox getAttackCheckBox() {
		return lobbyAttackCheckBox;
	}
	public JCheckBox getItemCheckBox() {
		return lobbyItemCheckBox;
	}
	
	/*
	
	UI 시작 
	
	*/
	
	private void renderUIBase() {
		this.getContentPane().removeAll();

		setTitle("테트리스");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(getBounds().x, getBounds().y, 350, 500);
		
		JPanel baseCardContentPane = new JPanel();
		baseCardContentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		this.setContentPane(baseCardContentPane);
		this.revalidate();
		baseCardContentPane.setLayout(new CardLayout(0, 0));
		
		CardLayout baseCardLayout =	(CardLayout)baseCardContentPane.getLayout();
		
		//
		// baseContentPane 시작
		//
		
		JPanel baseContentPane = new JPanel();
		baseContentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		baseCardContentPane.add(baseContentPane, "Base");
		
		GridBagLayout gbl_baseContentPane = new GridBagLayout();
		GridBagConstraints gbc_baseTetrisText = new GridBagConstraints();
		gbc_baseTetrisText.gridwidth = 3;
		
		gbl_baseContentPane.columnWidths = new int[] {30, 60, 0, 60, 30};
		gbl_baseContentPane.rowHeights = new int[] {30, 0, 30, 30, 30, 30, 30, 30};
		gbl_baseContentPane.columnWeights = new double[]{0.0, 0.0, 1.0};
		gbl_baseContentPane.rowWeights = new double[]{0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0};
		baseContentPane.setLayout(gbl_baseContentPane);
	
		JTextField baseTetrisText = new JTextField();
		baseTetrisText.setEditable(false);
		baseTetrisText.setHorizontalAlignment(SwingConstants.CENTER);
		baseTetrisText.setText("TETRIS");

		gbc_baseTetrisText.insets = new Insets(0, 0, 5, 5);
		gbc_baseTetrisText.fill = GridBagConstraints.BOTH;
		gbc_baseTetrisText.gridx = 1;
		gbc_baseTetrisText.gridy = 1;
		baseContentPane.add(baseTetrisText, gbc_baseTetrisText);
		baseTetrisText.setColumns(10);
	
		//버튼 추가
		JButton baseMultiPlayButton = new JButton("게임하기");
		baseMultiPlayButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
	            baseCardLayout.show(baseCardContentPane, "Server");
				setTitle("테트리스 게임하기");
			}
		});
		
		GridBagConstraints gbc_baseMultiPlayButton = new GridBagConstraints();
		gbc_baseMultiPlayButton.fill = GridBagConstraints.BOTH;
		gbc_baseMultiPlayButton.insets = new Insets(0, 0, 5, 5);
		gbc_baseMultiPlayButton.gridx = 2;
		gbc_baseMultiPlayButton.gridy = 3;
		baseContentPane.add(baseMultiPlayButton, gbc_baseMultiPlayButton);
		
		JButton baseLeaderBoardButton = new JButton("점수판 보기");
		baseLeaderBoardButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				function.refreshLeaderBoard();
			}
		});
		
		GridBagConstraints gbc_baseLeaderBoardButton = new GridBagConstraints();
		gbc_baseLeaderBoardButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_baseLeaderBoardButton.insets = new Insets(0, 0, 5, 5);
		gbc_baseLeaderBoardButton.gridx = 2;
		gbc_baseLeaderBoardButton.gridy = 4;
		baseContentPane.add(baseLeaderBoardButton, gbc_baseLeaderBoardButton);
		
		JButton baseKeyHelpButton = new JButton("도움말");
		baseKeyHelpButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				keyHelp.setVisible(true);
			}
		});
		
		GridBagConstraints gbc_baseKeyHelpButton = new GridBagConstraints();
		gbc_baseKeyHelpButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_baseKeyHelpButton.insets = new Insets(0, 0, 5, 5);
		gbc_baseKeyHelpButton.gridx = 2;
		gbc_baseKeyHelpButton.gridy = 5;
		baseContentPane.add(baseKeyHelpButton, gbc_baseKeyHelpButton);
		
		JButton baseExitButton = new JButton("나가기");
		baseExitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		
		GridBagConstraints gbc_baseExitButton = new GridBagConstraints();
		gbc_baseExitButton.insets = new Insets(0, 0, 5, 5);
		gbc_baseExitButton.fill = GridBagConstraints.BOTH;
		gbc_baseExitButton.gridx = 2;
		gbc_baseExitButton.gridy = 6;
		baseContentPane.add(baseExitButton, gbc_baseExitButton);
		
		//
		// baseContentPane 끝
		//
		// baseMultiContentPane 시작
		//
		
		JPanel baseMultiContentPane = new JPanel();
		baseMultiContentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		baseCardContentPane.add(baseMultiContentPane, "Server");
		
		GridBagLayout gbl_baseServerContentPane = new GridBagLayout();
		
		gbl_baseServerContentPane.columnWidths = new int[] {30, 60, 0, 60, 30};
		gbl_baseServerContentPane.rowHeights = new int[] {30, 0, 30, 30, 30, 30, 30, 30};
		gbl_baseServerContentPane.columnWeights = new double[]{0.0, 0.0, 1.0, 0.0, 0.0};
		gbl_baseServerContentPane.rowWeights = new double[]{0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0};
		baseMultiContentPane.setLayout(gbl_baseServerContentPane);
		GridBagConstraints gbc_baseServerTetrisText = new GridBagConstraints();
		gbc_baseServerTetrisText.gridwidth = 3;
		
		JTextField baseServerTetrisText = new JTextField();
		baseServerTetrisText.setEditable(false);
		baseServerTetrisText.setHorizontalAlignment(SwingConstants.CENTER);
		baseServerTetrisText.setText("TETRIS");
			
		gbc_baseServerTetrisText.insets = new Insets(0, 0, 5, 5);
		gbc_baseServerTetrisText.fill = GridBagConstraints.BOTH;
		gbc_baseServerTetrisText.gridx = 1;
		gbc_baseServerTetrisText.gridy = 1;
		baseMultiContentPane.add(baseServerTetrisText, gbc_baseServerTetrisText);
		baseServerTetrisText.setColumns(10);
		GridBagConstraints gbc_baseServerBackButton = new GridBagConstraints();
		
		JButton baseServerBackButton = new JButton("뒤로가기");
		baseServerBackButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
        baseCardLayout.show(baseCardContentPane, "Base");
				setTitle("테트리스");
			}
		});
		GridBagConstraints gbc_baseServerMultiPlayButton = new GridBagConstraints();
		
		JButton baseServerMultiPlayButton = new JButton("서버 접속하기");
		baseServerMultiPlayButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				connectServer.setVisible(true);
			}
		});
		GridBagConstraints gbc_baseServerSinglePlayButton = new GridBagConstraints();
		
		JButton baseServerSinglePlayButton = new JButton("서버 열기");
		baseServerSinglePlayButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openServer.setVisible(true);
			}
		});
		
		JButton baseSinglePlayButton = new JButton("혼자 놀기");
		baseSinglePlayButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				renderUISingle();
			}
		});
		GridBagConstraints gbc_baseSinglePlayButton = new GridBagConstraints();
		gbc_baseSinglePlayButton.fill = GridBagConstraints.BOTH;
		gbc_baseSinglePlayButton.insets = new Insets(0, 0, 5, 5);
		gbc_baseSinglePlayButton.gridx = 2;
		gbc_baseSinglePlayButton.gridy = 3;
		baseMultiContentPane.add(baseSinglePlayButton, gbc_baseSinglePlayButton);
		
		gbc_baseServerSinglePlayButton.fill = GridBagConstraints.BOTH;
		gbc_baseServerSinglePlayButton.insets = new Insets(0, 0, 5, 5);
		gbc_baseServerSinglePlayButton.gridx = 2;
		gbc_baseServerSinglePlayButton.gridy = 4;
		
		baseMultiContentPane.add(baseServerSinglePlayButton, gbc_baseServerSinglePlayButton);

		gbc_baseServerMultiPlayButton.fill = GridBagConstraints.BOTH;
		gbc_baseServerMultiPlayButton.insets = new Insets(0, 0, 5, 5);
		gbc_baseServerMultiPlayButton.gridx = 2;
		gbc_baseServerMultiPlayButton.gridy = 5;
		baseMultiContentPane.add(baseServerMultiPlayButton, gbc_baseServerMultiPlayButton);

		gbc_baseServerBackButton.fill = GridBagConstraints.BOTH;
		gbc_baseServerBackButton.insets = new Insets(0, 0, 5, 5);
		gbc_baseServerBackButton.gridx = 2;
		gbc_baseServerBackButton.gridy = 6;
		baseMultiContentPane.add(baseServerBackButton, gbc_baseServerBackButton);
		
		//스피커 이미지 라벨
		JLabel speakerImageLabel = new JLabel();
		JLabel speakerImageLabel_2 = new JLabel();
		BufferedImage image;
		try {
			image = ImageIO.read(new File("src/speaker.png"));

			speakerImageLabel.setIcon(new ImageIcon(image.getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
			speakerImageLabel_2.setIcon(new ImageIcon(image.getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		GridBagConstraints gbc_speakerImageLabel = new GridBagConstraints();
		gbc_speakerImageLabel.insets = new Insets(0, 0, 5, 5);
		gbc_speakerImageLabel.fill = GridBagConstraints.EAST;
		gbc_speakerImageLabel.gridx = 1;
		gbc_speakerImageLabel.gridy = 7;
		baseContentPane.add(speakerImageLabel, gbc_speakerImageLabel);
		
		GridBagConstraints gbc_speakerImageLabel_2 = new GridBagConstraints();
		gbc_speakerImageLabel_2.insets = new Insets(0, 0, 5, 5);
		gbc_speakerImageLabel_2.fill = GridBagConstraints.EAST;
		gbc_speakerImageLabel_2.gridx = 1;
		gbc_speakerImageLabel_2.gridy = 7;
		baseMultiContentPane.add(speakerImageLabel_2, gbc_speakerImageLabel_2);
		
		//볼륨 조절 슬라이더
		JSlider volumeSlider = new JSlider(JSlider.HORIZONTAL, 20, 100, 80);
		JSlider volumeSlider_2 = new JSlider(JSlider.HORIZONTAL, 20, 100, 80);
		
        // 볼륨 조절 슬라이더 리스너
        volumeSlider.addChangeListener(e -> {
            int volume = volumeSlider.getValue();
            volumeSlider_2.setValue(volume);
            if(volume == 20) {
            	soundHandler.controlSound(0);
            } else {
                soundHandler.controlSound((float) volume / 100);
            }
        });
        
		GridBagConstraints gbc_volumeSlider = new GridBagConstraints();
		gbc_volumeSlider.insets = new Insets(0, 0, 5, 5);
		gbc_volumeSlider.fill = GridBagConstraints.BOTH;
		gbc_volumeSlider.gridx = 2;
		gbc_volumeSlider.gridy = 7;
		baseContentPane.add(volumeSlider, gbc_volumeSlider);
		
		// 볼륨 조절 슬라이더 리스너
        volumeSlider_2.addChangeListener(e -> {
            int volume = volumeSlider_2.getValue();
            volumeSlider.setValue(volume);
            if(volume == 20) {
            	soundHandler.controlSound(0);
            } else {
                soundHandler.controlSound((float) volume / 100);
            }
        });
        
		GridBagConstraints gbc_volumeSlider_2 = new GridBagConstraints();
		gbc_volumeSlider_2.insets = new Insets(0, 0, 5, 5);
		gbc_volumeSlider_2.fill = GridBagConstraints.BOTH;
		gbc_volumeSlider_2.gridx = 2;
		gbc_volumeSlider_2.gridy = 7;
		baseMultiContentPane.add(volumeSlider_2, gbc_volumeSlider_2);
	}

	private void renderUIBaseServerTab() {
		CardLayout baseCardLayout =	(CardLayout)this.getContentPane().getLayout();
		baseCardLayout.show(this.getContentPane(), "Server");
	}
	
	private void renderUISingle() {
		this.getContentPane().removeAll();
		
		setTitle("테트리스 혼자 놀기");
		setBounds(getBounds().x, getBounds().y, 380, 620);
		
		JPanel singleContentPane = new JPanel();
		singleContentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		this.setContentPane(singleContentPane);
		this.revalidate();	
		singleContentPane.setLayout(new BorderLayout(0, 0));
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu gameMenu = new JMenu("게임");
		menuBar.add(gameMenu);
		
		mntmStartMenuItem = new JMenuItem("시작");
		mntmStartMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tetrisCanvas.start();
				mntmStartMenuItem.setEnabled(false);
			}
		});
		gameMenu.add(mntmStartMenuItem);
		
		JMenuItem mntmExitMenuItem = new JMenuItem("종료");
		mntmExitMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		gameMenu.add(mntmExitMenuItem);
		
		tetrisCanvas = new TetrisCanvas(soundHandler, this);
		singleContentPane.add(tetrisCanvas, BorderLayout.CENTER);
		tetrisCanvas.setLayout(null);
		
		nextPieceLabel = new JLabel("-NEXT-");
		nextPieceLabel.setBounds(285, 40, 45, 15);
		nextPieceLabel.setHorizontalAlignment(SwingConstants.CENTER);
		tetrisCanvas.add(nextPieceLabel);
		
		holdPieceLabel = new JLabel("-HOLD-");
		holdPieceLabel.setBounds(285, 130, 44, 15);
		holdPieceLabel.setHorizontalAlignment(SwingConstants.CENTER);
		tetrisCanvas.add(holdPieceLabel);
		
		lblScoreLabel = new JLabel("Score");
		lblScoreLabel.setBounds(270, 475, 95, 15);
		lblScoreLabel.setHorizontalAlignment(SwingConstants.LEFT);
		tetrisCanvas.add(lblScoreLabel);
		
		lblLineLabel = new JLabel("Line");
		lblLineLabel.setBounds(270, 495, 60, 15);
		lblLineLabel.setHorizontalAlignment(SwingConstants.LEFT);
		tetrisCanvas.add(lblLineLabel);
		
		lblLevelLabel = new JLabel("Level");
		lblLevelLabel.setBounds(270, 515, 60, 15);
		lblLevelLabel.setHorizontalAlignment(SwingConstants.LEFT);
		tetrisCanvas.add(lblLevelLabel);
	}
	
	private void renderUIMulti() {
		this.getContentPane().removeAll();
		
		setTitle("테트리스 같이 놀기");
		setBounds(getBounds().x, getBounds().y, 650, 600);
		
		JPanel multiContentPane = new JPanel();
		multiContentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.setContentPane(multiContentPane);
		this.revalidate();	
		multiContentPane.setLayout(new BorderLayout(0, 0));
		
		tetrisCanvas = new TetrisCanvas(soundHandler, this);
		multiContentPane.add(tetrisCanvas, BorderLayout.CENTER);
		tetrisCanvas.setLayout(null);
		
		nextPieceLabel = new JLabel("-NEXT-");
		nextPieceLabel.setBounds(285, 40, 45, 15);
		nextPieceLabel.setHorizontalAlignment(SwingConstants.CENTER);
		tetrisCanvas.add(nextPieceLabel);
		
		holdPieceLabel = new JLabel("-HOLD-");
		holdPieceLabel.setBounds(285, 130, 44, 15);
		holdPieceLabel.setHorizontalAlignment(SwingConstants.CENTER);
		tetrisCanvas.add(holdPieceLabel);
		
		lblScoreLabel = new JLabel("Score");
		lblScoreLabel.setBounds(270, 475, 95, 15);
		lblScoreLabel.setHorizontalAlignment(SwingConstants.LEFT);
		tetrisCanvas.add(lblScoreLabel);
		
		lblLineLabel = new JLabel("Line");
		lblLineLabel.setBounds(270, 495, 60, 15);
		lblLineLabel.setHorizontalAlignment(SwingConstants.LEFT);
		tetrisCanvas.add(lblLineLabel);
		
		lblLevelLabel = new JLabel("Level");
		lblLevelLabel.setBounds(270, 515, 60, 15);
		lblLevelLabel.setHorizontalAlignment(SwingConstants.LEFT);
		tetrisCanvas.add(lblLevelLabel);
		
		multiTetrisCanvas = new MultiTetrisCanvas(this);
		multiContentPane.add(multiTetrisCanvas, BorderLayout.EAST);
		multiTetrisCanvas.setLayout(null);
	}
	
	private void renderUILobby() {
		this.getContentPane().removeAll();
		
		JPanel lobbyContentPane;
		JTextField txtTextfieldIsGood;
		JLabel lobbyServerLabel;
		JLabel lobbyConnectLabel;
		JLabel lobbyReadyLabel;
//		JCheckBox lobbyMiriCheckBox;
		JButton lobbyReadyButton;
		
		setTitle("서버 로비");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(getBounds().x, getBounds().y, 400, 250);
		lobbyContentPane = new JPanel();
		lobbyContentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		this.setContentPane(lobbyContentPane);
		this.revalidate();
		GridBagLayout gbl_lobbyContentPane = new GridBagLayout();
		gbl_lobbyContentPane.columnWidths = new int[] {100, 120, 120};
		gbl_lobbyContentPane.rowHeights = new int[] {25, 25, 40, 40, 0, 0};
		gbl_lobbyContentPane.columnWeights = new double[]{0.0, 0.0, 0.0};
		gbl_lobbyContentPane.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
		lobbyContentPane.setLayout(gbl_lobbyContentPane);
		
		txtTextfieldIsGood = new JTextField();
		txtTextfieldIsGood.setHorizontalAlignment(SwingConstants.CENTER);
		txtTextfieldIsGood.setEditable(false);
		try {
			if(serverOpen) txtTextfieldIsGood.setText("IP: " + InetAddress.getLocalHost().getHostAddress() + " | Port: " + openServer.port);
			else txtTextfieldIsGood.setText("IP: " + connectServer.ip + " | Port: " + connectServer.port);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		GridBagConstraints gbc_txtTextfieldIsGood = new GridBagConstraints();
		gbc_txtTextfieldIsGood.fill = GridBagConstraints.BOTH;
		gbc_txtTextfieldIsGood.gridwidth = 3;
		gbc_txtTextfieldIsGood.insets = new Insets(0, 0, 5, 0);
		gbc_txtTextfieldIsGood.gridx = 0;
		gbc_txtTextfieldIsGood.gridy = 0;
		lobbyContentPane.add(txtTextfieldIsGood, gbc_txtTextfieldIsGood);
		txtTextfieldIsGood.setColumns(10);
		
		lobbyServerLabel = new JLabel("서버");
		GridBagConstraints gbc_lobbyServerLabel = new GridBagConstraints();
		gbc_lobbyServerLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lobbyServerLabel.gridx = 1;
		gbc_lobbyServerLabel.gridy = 1;
		lobbyContentPane.add(lobbyServerLabel, gbc_lobbyServerLabel);
		
		JLabel lobbyClientLabel = new JLabel("클라이언트");
		GridBagConstraints gbc_lobbyClientLabel = new GridBagConstraints();
		gbc_lobbyClientLabel.insets = new Insets(0, 0, 5, 0);
		gbc_lobbyClientLabel.gridx = 2;
		gbc_lobbyClientLabel.gridy = 1;
		lobbyContentPane.add(lobbyClientLabel, gbc_lobbyClientLabel);
		
		lobbyConnectLabel = new JLabel("접속?");
		GridBagConstraints gbc_lobbyConnectLabel = new GridBagConstraints();
		gbc_lobbyConnectLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lobbyConnectLabel.gridx = 0;
		gbc_lobbyConnectLabel.gridy = 2;
		lobbyContentPane.add(lobbyConnectLabel, gbc_lobbyConnectLabel);
		
		lobbyServerConnectCheckBox = new JCheckBox("");
		lobbyServerConnectCheckBox.setEnabled(false);
		GridBagConstraints gbc_lobbyServerConnectCheckBox = new GridBagConstraints();
		gbc_lobbyServerConnectCheckBox.insets = new Insets(0, 0, 5, 5);
		gbc_lobbyServerConnectCheckBox.gridx = 1;
		gbc_lobbyServerConnectCheckBox.gridy = 2;
		lobbyContentPane.add(lobbyServerConnectCheckBox, gbc_lobbyServerConnectCheckBox);
		
		lobbyClientConnectCheckBox = new JCheckBox("");
		lobbyClientConnectCheckBox.setEnabled(false);
		GridBagConstraints gbc_lobbyClientConnectCheckBox = new GridBagConstraints();
		gbc_lobbyClientConnectCheckBox.insets = new Insets(0, 0, 5, 0);
		gbc_lobbyClientConnectCheckBox.gridx = 2;
		gbc_lobbyClientConnectCheckBox.gridy = 2;
		lobbyContentPane.add(lobbyClientConnectCheckBox, gbc_lobbyClientConnectCheckBox);
		
		lobbyReadyLabel = new JLabel("준비?");
		GridBagConstraints gbc_lobbyReadyLabel = new GridBagConstraints();
		gbc_lobbyReadyLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lobbyReadyLabel.gridx = 0;
		gbc_lobbyReadyLabel.gridy = 3;
		lobbyContentPane.add(lobbyReadyLabel, gbc_lobbyReadyLabel);
		
		lobbyServerReadyCheckBox = new JCheckBox("");
		lobbyServerReadyCheckBox.setEnabled(false);
		GridBagConstraints gbc_lobbyServerReadyCheckBox = new GridBagConstraints();
		gbc_lobbyServerReadyCheckBox.insets = new Insets(0, 0, 5, 5);
		gbc_lobbyServerReadyCheckBox.gridx = 1;
		gbc_lobbyServerReadyCheckBox.gridy = 3;
		lobbyContentPane.add(lobbyServerReadyCheckBox, gbc_lobbyServerReadyCheckBox);
		
		lobbyClientReadyCheckBox = new JCheckBox("");
		lobbyClientReadyCheckBox.setEnabled(false);
		GridBagConstraints gbc_lobbyClientReadyCheckBox = new GridBagConstraints();
		gbc_lobbyClientReadyCheckBox.insets = new Insets(0, 0, 5, 0);
		gbc_lobbyClientReadyCheckBox.gridx = 2;
		gbc_lobbyClientReadyCheckBox.gridy = 3;
		lobbyContentPane.add(lobbyClientReadyCheckBox, gbc_lobbyClientReadyCheckBox);
		
		lobbyReadyButton = new JButton("Ready");
		lobbyReadyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(serverOpen) {
					if(lobbyServerReadyCheckBox.isSelected() ? true : false) lobbyServerReadyCheckBox.setSelected(false);
					else lobbyServerReadyCheckBox.setSelected(true);
				} else {
					if(lobbyClientReadyCheckBox.isSelected() ? true : false) lobbyClientReadyCheckBox.setSelected(false);
					else lobbyClientReadyCheckBox.setSelected(true);
				}
			}
		});
		GridBagConstraints gbc_lobbyReadyButton = new GridBagConstraints();
		gbc_lobbyReadyButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_lobbyReadyButton.gridwidth = 3;
		gbc_lobbyReadyButton.insets = new Insets(0, 0, 5, 5);
		gbc_lobbyReadyButton.gridx = 0;
		gbc_lobbyReadyButton.gridy = 4;
		lobbyContentPane.add(lobbyReadyButton, gbc_lobbyReadyButton);
		
		lobbyAttackCheckBox = new JCheckBox("공격");
		GridBagConstraints gbc_lobbyAttackCheckBox = new GridBagConstraints();
		gbc_lobbyAttackCheckBox.insets = new Insets(0, 0, 0, 5);
		gbc_lobbyAttackCheckBox.gridx = 0;
		gbc_lobbyAttackCheckBox.gridy = 5;
		lobbyContentPane.add(lobbyAttackCheckBox, gbc_lobbyAttackCheckBox);
		
		lobbyItemCheckBox = new JCheckBox("아이템");
		GridBagConstraints gbc_lobbyItemCheckBox = new GridBagConstraints();
		gbc_lobbyItemCheckBox.insets = new Insets(0, 0, 0, 5);
		gbc_lobbyItemCheckBox.gridx = 1;
		gbc_lobbyItemCheckBox.gridy = 5;
		lobbyContentPane.add(lobbyItemCheckBox, gbc_lobbyItemCheckBox);
		
//		lobbyMiriCheckBox = new JCheckBox("미리 보기");
//		lobbyMiriCheckBox.setEnabled(false);
//		GridBagConstraints gbc_lobbyMiriCheckBox = new GridBagConstraints();
//		gbc_lobbyMiriCheckBox.gridx = 2;
//		gbc_lobbyMiriCheckBox.gridy = 5;
//		lobbyContentPane.add(lobbyMiriCheckBox, gbc_lobbyMiriCheckBox);
		
		lobbyServerConnectCheckBox.setSelected(serverOpen);
		lobbyClientConnectCheckBox.setSelected(!serverOpen);
		lobbyAttackCheckBox.setEnabled(serverOpen);
		lobbyItemCheckBox.setEnabled(serverOpen);
	}
	
	/*
	 
	서버 시작
	 
	 */
	
	public void doOpen() {
		if(serverOpen) {
			renderUILobby();
			serverHandler sh = new serverHandler(this, openServer.port);
			sh.start();
		}
	}
	
	public void doConnect() {
		if(connect) {
			renderUILobby();
			clientHandler ch = new clientHandler(this, connectServer.ip, connectServer.port);
			if(connect) ch.start();
		}
	}
	
	private class serverHandler extends Thread {
		private ServerSocket socket = null;
		private Socket client = null;
		private BufferedReader in = null;
		private PrintWriter out = null;
		
		private MyTetris myTetris = null;
		
		public serverHandler(MyTetris myTetris, int port){
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
				
				renderUIMulti();
				tetrisCanvas.start();
				
				outStr = "1p" + (myTetris.getAttackCheckBox().isSelected() ? 1 : 0) + "p" + (myTetris.getItemCheckBox().isSelected() ? 1 : 0);
				out.println(outStr);
				System.out.println(in.read());
				
				tetrisCanvas.data.setAttack(myTetris.getAttackCheckBox().isSelected());
				tetrisCanvas.useItem = myTetris.getItemCheckBox().isSelected();
				
				try {function.handlerRun(myTetris, in, out, enemyScore);} catch(Exception e) {System.out.println(e);}
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, e);
				myTetris.getUConnectCheckBox().setSelected(false);
				try {socket.close();} catch (IOException e1) {JOptionPane.showMessageDialog(null, e1);}
				renderUIBase();
				renderUIBaseServerTab();
			}
		}
	}
	
	private class clientHandler extends Thread {
		private Socket socket = null;
		private BufferedReader in = null;
		private PrintWriter out = null;
		
		private MyTetris myTetris = null;
		
		public clientHandler(MyTetris myTetris, String IP, int port) {
			this.myTetris = myTetris;
			try {
				socket = new Socket(IP, port);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, e);
				MyTetris.connect = false;
				renderUIBase();
				renderUIBaseServerTab();
			}
		}
		
		public void run() {
			System.out.println("헉run");
			try {
				myTetris.getUConnectCheckBox().setSelected(connect);
				
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
				
				String inStr;
				String[] inStrFix = {""};
				
				while(true) {
					out.write(myTetris.getReadyCheckBox().isSelected() ? 1 : 0);
					out.flush();
					
					inStr = in.readLine();
					
					if(inStr != null) {
						inStrFix = function.convertStringDiv(inStr);
						
						if(Integer.parseInt(inStrFix[0]) == 1) myTetris.getUReadyCheckBox().setSelected(true);
						else myTetris.getUReadyCheckBox().setSelected(false);
						myTetris.getAttackCheckBox().setSelected(Integer.parseInt(inStrFix[1]) == 1 ? true : false);
						myTetris.getItemCheckBox().setSelected(Integer.parseInt(inStrFix[2]) == 1 ? true : false);
					}
					
					if((myTetris.getReadyCheckBox().isSelected() ? true : false) && (myTetris.getUReadyCheckBox().isSelected() ? true : false)) 
						break;
				}
				
				renderUIMulti();
				tetrisCanvas.start();
				
				out.write(1);
				System.out.println(in.readLine());
				
				tetrisCanvas.data.setAttack(Integer.parseInt(inStrFix[1]) == 1 ? true : false);
				tetrisCanvas.useItem = myTetris.getItemCheckBox().isSelected();
				
				try {function.handlerRun(myTetris, in, out, enemyScore);} catch(Exception e) {System.out.println(e);}
			} catch(Exception e) {
				JOptionPane.showMessageDialog(null, e);
				MyTetris.connect = false;
				renderUIBase();
				renderUIBaseServerTab();
			}
		}
	}
	
	/*
	
	함수들 모음집
	 
	*/
	
	public class function {
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
						+ function.convertIntArrayToString(data) + "p"	//tetrisCanvas.data.getData()		int[][] 자료형 즉, data의 data임.
						+ function.convertIntArrayToString(r) + "p"		//tetrisCanvas.current.r			int[] 자료형
						+ function.convertIntArrayToString(c) + "p"		//tetrisCanvas.current.c			int[] 자료형
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
					inStrFix = function.convertStringDiv(inStr); 	//inStrFix[0, 1, 2, 3, 4, 5, 6, 7, 8, 9]
																	//0 = 죽음			1 = 점수				2 = data.data 테이블임.
																	//3 = 현재조각 r[]		4 = 현재조각 c[]
																	//5 = 현재조각 센터 x	6 = 현재조각 센터 y		7 = 현재조각 타입
																	//8 = 데이터 라인		9 = 괴상한 조각 카운터
					inDeath = Integer.parseInt(inStrFix[0]);
					inData = function.convertStringToIntArray(inStrFix[2]);
					if(inData.length == TetrisData.ROW && inData[0].length == TetrisData.COL) {
						myTetris.multiTetrisCanvas.data.setData(function.convertStringToIntArray(inStrFix[2]));
						myTetris.multiTetrisCanvas.repaint();
					}
					inCPr = function.convertStringToIntArray(inStrFix[3], true);
					inCPc = function.convertStringToIntArray(inStrFix[4], true);
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
					+ function.convertIntArrayToString(data) + "p"
					+ function.convertIntArrayToString(r) + "p"
					+ function.convertIntArrayToString(c) + "p"
					+ 0 + "p"
					+ 0 + "p"
					+ 8 + "p"
					+ -1 + "p"
					+ 0;
			
			out.println(outStr);
			out.flush();
			
			if(inStr != null) {
				inStrFix = function.convertStringDiv(inStr);
				
				inDeath = Integer.parseInt(inStrFix[0]);
				inData = function.convertStringToIntArray(inStrFix[2]);
				if(inData.length == TetrisData.ROW && inData[0].length == TetrisData.COL) {
					myTetris.multiTetrisCanvas.data.setData(function.convertStringToIntArray(inStrFix[2]));
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
}