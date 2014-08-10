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
public class CluedoUI extends JFrame implements MouseListener {

	private BoardCanvas canvas;
	private DicePane dicePane;
	private CardCanvas cardCanvas;
	private CheckPane list;

	private final int boardCanvasTop = 45;
	private final int bottomPaneTop;
	private final int cardCanvasLeft = 130;
	private final int checkPaneLeft;

	//private Model game;
	private int currentPlayer = 1;
	private Map<Integer, String> characters;
	private Map<String, Integer> players;

	public CluedoUI() {
		super("Cluedo");
		characters = new HashMap<Integer, String>();
		characters.put(Board.SCARLETT, "Miss Scarlett"); 
		characters.put(Board.MUSTARD, "Colonel Mustard");
		characters.put(Board.WHITE, "Mrs White");
		characters.put(Board.GREEN, "Rev Green");
		characters.put(Board.PEACOCK, "Miss Peacock");
		characters.put(Board.PLUM, "Professor Plum");

		// Set up the menus
		JMenuBar menuBar = new JMenuBar();
		JMenu gameOptionMenu = new JMenu("Game");
		JMenu playerOptionMenu = new JMenu("Player");
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
		rules.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					JFrame ruleWindow = new JFrame();
					ruleWindow.setPreferredSize(new Dimension(620, 700));
					ruleWindow.setLayout(new BorderLayout());
					String text = new Scanner(new File("rules.txt")).useDelimiter("\\Z").next();
					JTextArea ta = new JTextArea(text);
					JScrollPane sp = new JScrollPane(ta);
					ruleWindow.add(sp, BorderLayout.CENTER);
					ruleWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
					ruleWindow.pack();
					ruleWindow.setResizable(false);
					ruleWindow.setVisible(true);
				} catch (FileNotFoundException e) {
					System.out.println(e);
				}
			}
		});
		JMenuItem help = new JMenuItem("Help");
		help.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					JFrame ruleWindow = new JFrame();
					ruleWindow.setPreferredSize(new Dimension(450, 400));
					ruleWindow.setLayout(new BorderLayout());
					String text = new Scanner(new File("help.txt")).useDelimiter("\\Z").next();
					JTextArea ta = new JTextArea(text);
					JScrollPane sp = new JScrollPane(ta);
					ruleWindow.add(sp, BorderLayout.CENTER);
					ruleWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
					ruleWindow.pack();
					ruleWindow.setResizable(false);
					ruleWindow.setVisible(true);
				} catch (FileNotFoundException e) {
					System.out.println(e);
				}
			}
		});
		fileOptionMenu.add(rules);
		fileOptionMenu.add(help);
		menuBar.add(fileOptionMenu);
		menuBar.add(gameOptionMenu);
		setJMenuBar(menuBar);

		// set up the canvases and panes
		canvas = new BoardCanvas(false); // create canvas
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
		endTurn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO IMPORTANT END TURN HERE
				// currentPlayer = game.endTurn();
				list.setPlayer(0);
				cardCanvas.updateCards(new ArrayList<String>());
				JOptionPane.showMessageDialog(CluedoUI.this, "It's " + characters.get(currentPlayer) + "'s turn!");
				list.setPlayer(currentPlayer);
				// cardCanvas.updateCards(game.getCards(currentPlayer)); TODO make getCards

			}
		});

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
		// addKeyListener(this);
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

		int result = JOptionPane.showConfirmDialog(null, panel, "Welcome to Cluedo!", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
		System.out.println(comboBox.getSelectedItem());	// TODO
		if (result == JOptionPane.OK_OPTION) {
			SelectPlayers s = new SelectPlayers(characters, players, (int)comboBox.getSelectedItem());
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (canvas.contains(e.getX(), e.getY() - boardCanvasTop)) {
			int xCoord = (int) ((e.getX() - canvas.getBoardLeft()) / canvas.getSquareWidth());
			int yCoord = (int) ((e.getY() - canvas.getBoardTop() - boardCanvasTop) / canvas.getSquareWidth());
			canvas.highlight(new Coordinate(xCoord, yCoord));
			canvas.repaint();
		} else if (dicePane.contains(e.getX(), e.getY() - bottomPaneTop)) { // , y)e.getX() >= 0 && e.getX() <= dicePane.getWidth()) {
			// TODO ask model whether dice roll is ok. if not, display message and/or ignore
			int newRoll = dicePane.rollDice();
			// TODO send back the roll
		}

	}

	public static void main(String[] args) {
		new CluedoUI();

	}

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
