package cluedo.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import cluedo.Coordinate;
import cluedo.board.Board;


/**
 * The main window of the UI for the game. Also has control responsibilities.
 *
 * @author kelsey
 *
 */
public class CluedoUI extends JFrame implements MouseListener, ActionListener {

	private BoardCanvas canvas;
	private DicePane dicePane;
	private CardCanvas cardCanvas;
	private CheckPane list;

	private final int boardCanvasTop = 45;
	private final int bottomPaneTop;
	private final int cardCanvasLeft = 130;
	private final int checkPaneLeft;

	private Board game;
	private int currentPlayer = 1;
	private Map<Integer, String> characters;
	private Map<Integer, String> players;

	public static boolean go;	// FIXME!!!

	public CluedoUI(Board game) {
		super("Cluedo");
		this.game = game;
		characters = new HashMap<Integer, String>();
		characters.put(Board.SCARLETT, "Miss Scarlett");
		characters.put(Board.MUSTARD, "Colonel Mustard");
		characters.put(Board.WHITE, "Mrs White");
		characters.put(Board.GREEN, "Rev Green");
		characters.put(Board.PEACOCK, "Miss Peacock");
		characters.put(Board.PLUM, "Professor Plum");
		players = new HashMap<Integer, String>();

		// Set up the menus
		JMenuBar menuBar = new JMenuBar();
		JMenu gameOptionMenu = new JMenu("Game");
		// JMenu playerOptionMenu = new JMenu("Player"); TODO
		JMenuItem quit = new JMenuItem("Quit");
		quit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(NORMAL);
			}

		});
		JMenuItem restart = new JMenuItem("Restart");
		restart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO make new model? eg
			}

		});
		gameOptionMenu.add(restart);
		gameOptionMenu.add(quit);
		JMenu fileOptionMenu = new JMenu("File");
		JMenuItem rules = new JMenuItem("Rules");
		rules.setActionCommand("Rules");
		rules.addActionListener(this);

		JMenuItem help = new JMenuItem("Help");
		help.setActionCommand("Help");
		help.addActionListener(this);

		fileOptionMenu.add(rules);
		fileOptionMenu.add(help);
		menuBar.add(fileOptionMenu);
		menuBar.add(gameOptionMenu);
		setJMenuBar(menuBar);

		// set up the canvases and panes
		canvas = new BoardCanvas(true, game); // create canvas
		cardCanvas = new CardCanvas();
		dicePane = new DicePane();
		list = new CheckPane();
		List<String> init = new ArrayList<String>();// TODO delete
		init.add("plum");
		init.add("leadpipe");
		init.add("ballroom");
		init.add("lounge");
		init.add("mustard");
		init.add("green");
		cardCanvas.updateCards(init); // TODO delete

		JButton endTurn = new JButton("End Turn");
		endTurn.setActionCommand("End Turn");
		endTurn.addActionListener(this);

		JPanel south = new JPanel();
		south.setLayout(new BorderLayout());
		south.add(cardCanvas, BorderLayout.EAST);
		south.add(dicePane, BorderLayout.WEST);

		JPanel left = new JPanel();
		left.setLayout(new BorderLayout());
		left.add(canvas, BorderLayout.CENTER); // add canvas
		left.add(south, BorderLayout.SOUTH);

		JPanel right = new JPanel();
		right.setLayout(new BorderLayout(0, 20));
		right.add(endTurn, BorderLayout.NORTH);
		right.add(list, BorderLayout.CENTER);

		setLayout(new BorderLayout()); // use border layer
		add(left, BorderLayout.WEST);
		add(right, BorderLayout.EAST);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setResizable(false);
		setVisible(true);
		addMouseListener(this);

		bottomPaneTop = canvas.getHeight() + boardCanvasTop;
		checkPaneLeft = canvas.getWidth();

		JPanel panel = new JPanel();
		panel.add(new JLabel("How many players?"));
		DefaultComboBoxModel<Integer> model = new DefaultComboBoxModel<Integer>();
		model.addElement(3);
		model.addElement(4);
		model.addElement(5);
		model.addElement(6);
		JComboBox<Integer> comboBox = new JComboBox<Integer>(model);
		panel.add(comboBox);

		JOptionPane.showMessageDialog(this, panel, "Welcome to Cluedo!", JOptionPane.PLAIN_MESSAGE);

		SelectPlayerDialog s = new SelectPlayerDialog(this, characters, players, (int)comboBox.getSelectedItem());
		while (!this.go) {	/// TODO gah
			try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}

		// FIXME executes before dialog finishes
		for (Integer p: players.keySet()) {
			game.addPlayer(p);
		}
		game.startGame();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// Move to a square on canvas
		if (canvas.contains(e.getX(), e.getY() - boardCanvasTop)) {
			int xCoord = (int) ((e.getX() - canvas.getBoardLeft()) / canvas.getSquareWidth());
			int yCoord = (int) ((e.getY() - canvas.getBoardTop() - boardCanvasTop) / canvas.getSquareWidth());
			Coordinate c = new Coordinate(xCoord, yCoord);
			if (game.getState() == 1) {
				game.move(c);
				canvas.repaint();
			} //else if (game.getState() == 0 && something to do with middle room ) {
				//initialise accusation!
			//} else if (game.getState() == 0 && something something corner room) {
				//secret passage
			//}
		// Dice roll
		} else if (dicePane.contains(e.getX(), e.getY() - bottomPaneTop) && game.getState() == 0) {
			int newRoll = dicePane.rollDice();
			game.rollDice(newRoll);
		}

	}

//	public static void main(String[] args) {
//		new CluedoUI();
//
//	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("End Turn")) {
			game.nextTurn();
			currentPlayer = game.currentPlayer();
			list.setPlayer(0);
			cardCanvas.updateCards(new ArrayList<String>());
			JOptionPane.showMessageDialog(CluedoUI.this, "It's " + players.get(currentPlayer) + "'s turn as " + characters.get(currentPlayer) +"!");
			list.setPlayer(currentPlayer);
			// cardCanvas.updateCards(game.getCards(currentPlayer)); TODO make getCards
		} else if (e.getActionCommand().equals("Rules")) {
			try {
				JFrame ruleWindow = new JFrame();
				ruleWindow.setPreferredSize(new Dimension(630, 700));
				ruleWindow.setLayout(new BorderLayout());
				String text = new Scanner(new File("assets/rules.txt")).useDelimiter("\\Z").next();
				JTextArea ta = new JTextArea(text);
				JScrollPane sp = new JScrollPane(ta);
				sp.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
				ruleWindow.add(sp, BorderLayout.CENTER);
				ruleWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				ruleWindow.pack();
				ruleWindow.setResizable(false);
				ruleWindow.setVisible(true);
			} catch (FileNotFoundException err) {
				System.out.println(err);
			}
		} else if (e.getActionCommand().equals("Help")) {
			try {
				JFrame ruleWindow = new JFrame();
				ruleWindow.setPreferredSize(new Dimension(460, 400));
				ruleWindow.setLayout(new BorderLayout());
				String text = new Scanner(new File("assets/help.txt")).useDelimiter("\\Z").next();
				JTextArea ta = new JTextArea(text);
				JScrollPane sp = new JScrollPane(ta);
				sp.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
				ruleWindow.add(sp, BorderLayout.CENTER);
				ruleWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				ruleWindow.pack();
				ruleWindow.setResizable(false);
				ruleWindow.setVisible(true);
			} catch (FileNotFoundException err) {
				System.out.println(err);
			}
		}

	}

	// @Override
	// public void keyTyped(KeyEvent e) {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// public void keyPressed(KeyEvent e) {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// public void keyReleased(KeyEvent e) {
	// // TODO Auto-generated method stub
	//
	// }

}
