package cluedo.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

//import javafx.embed.swing.JFXPanel;
//import javafx.scene.media.Media;
//import javafx.scene.media.MediaPlayer;

import javax.swing.BorderFactory;
import javax.swing.Box;
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
import javax.swing.SwingUtilities;

import cluedo.Coordinate;
import cluedo.Main;
import cluedo.model.Board;

/**
 * The main window of the UI for the game. Also has control responsibilities.
 * @author kelsey
 */
@SuppressWarnings("serial")
public class CluedoUI extends JFrame implements MouseListener, ActionListener {
	// swing components
	private BoardCanvas canvas;
	private DicePane dicePane;
	private CardCanvas cardCanvas;
	private CheckPane list;
	private InfoPane infoPane;

	private final int boardCanvasTop = 45;
	private final int bottomPaneTop;
	
	// game data
	private Board game;
	private int currentPlayer = 1;
	private Map<Integer, String> players;
	
//	private MediaPlayer mp;
//	private Media medSong;

	/**
	 * Creates and displays the GUI. 
	 * @param game The game to display and start.
	 */
	public CluedoUI(Board game) {
		super("Cluedo");
		this.game = game;
		players = new HashMap<Integer, String>();

		// Set up the menus
		JMenuBar menuBar = new JMenuBar();
		
		JMenu gameOptionMenu = new JMenu("Game");
		JMenu fileOptionMenu = new JMenu("File");
		
		JMenuItem quit = new JMenuItem("Quit");
		quit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(NORMAL);
			}
		});
		JMenuItem restart = new JMenuItem("Restart");
		restart.setActionCommand("Restart");
		restart.addActionListener(this);
		JMenuItem rules = new JMenuItem("Rules");
		rules.setActionCommand("Rules");
		rules.addActionListener(this);
		JMenuItem help = new JMenuItem("Help");
		help.setActionCommand("Help");
		help.addActionListener(this);
		
		gameOptionMenu.add(restart);
		gameOptionMenu.add(quit);
		fileOptionMenu.add(rules);
		fileOptionMenu.add(help);
		menuBar.add(fileOptionMenu);
		menuBar.add(gameOptionMenu);
		setJMenuBar(menuBar);

		// set up the canvases and panes
		canvas = new BoardCanvas(false, game); // create canvas
		cardCanvas = new CardCanvas();
		dicePane = new DicePane();
		list = new CheckPane();
		infoPane = new InfoPane();

		JButton endTurn = new JButton("End Turn");
		endTurn.setActionCommand("End Turn");
		endTurn.addActionListener(this);
		endTurn.setPreferredSize(new Dimension(150, 30));

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
		right.add(infoPane, BorderLayout.SOUTH);

		setLayout(new BorderLayout());
		add(left, BorderLayout.WEST);
		add(right, BorderLayout.EAST);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setResizable(false);
		setVisible(true);
		addMouseListener(this);

		bottomPaneTop = canvas.getHeight() + boardCanvasTop;
		
		
		//Unfortunately due to the fact that JavaFX isn't on most machines this code may not work.
		//If you want sound, install JavaFX And uncomment this code
//		final CountDownLatch latch = new CountDownLatch(1);
//		SwingUtilities.invokeLater(new Runnable() {
//		    public void run() {
//		        new JFXPanel(); // initializes JavaFX environment
//		        latch.countDown();
//				URL resource = Main.class.getResource("/DowntonAbbey.mp3");
//				medSong = new Media(resource.toString());
//				mp = new MediaPlayer(medSong);
//				mp.setCycleCount(MediaPlayer.INDEFINITE);
//				mp.play();
//		    }
//		});
		this.restart(game);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// Click a square on the board
		if (canvas.contains(e.getX(), e.getY() - boardCanvasTop)) {
			int xCoord = (int) ((e.getX() - canvas.getBoardLeft()) / canvas.getSquareWidth());
			int yCoord = (int) ((e.getY() - canvas.getBoardTop() - boardCanvasTop) / canvas.getSquareWidth());
			Coordinate clickedCoord = new Coordinate(xCoord, yCoord);
			if (game.getState() == 1) {
				// A move is to be made
				int moveResult = game.move(clickedCoord);
				infoPane.displayMovesLeft(game.getMovesLeft());
				canvas.clearHighlight();
				canvas.highlight(game.getPossibleMoves());
				canvas.repaint();
				if (moveResult == Board.SUCCESS && game.getRoom() != Board.NOTHING) {
					canvas.clearHighlight();
					canvas.repaint();
					this.doSuggestion();
				} else if (game.getMovesLeft() == 0) {
					infoPane.showTurnEnd();
				}
			} else if (game.getState() == 0 && game.getRoom(clickedCoord) == Board.MIDDLE) {
				// An accusation is to be made
				int guess = new SuggestDialog(this, 0).getGuess();
				int accResult = game.accuse(guess);
				if (accResult == Board.SUCCESS) {
					if (game.getState() == 5) {
						gameOver(currentPlayer);
					}
				} else if (accResult == Board.FAIL) {
					JOptionPane.showMessageDialog(this, "Incorrect guess, you lose!");
					JOptionPane.showMessageDialog(this, players.get(currentPlayer) + " has lost and may no longer play except to refute.");
					if (game.getState() == 5) {
						gameOver(0);
					} else {
						this.endTurn();
					}
				} else {
					System.out.println("guess should not have been made");
				}
			} else if (game.getState() == 0 && game.getRoom() == game.getRoom(clickedCoord) && game.canSuggest()) {
				// They have just been teleported into a room and wish to make a suggestion
				this.doSuggestion();
			} else if (game.getState() == 0 && 
					((game.getRoom() == Board.KITCHEN && game.getRoom(clickedCoord) == Board.STUDY)
							|| (game.getRoom() == Board.STUDY && game.getRoom(clickedCoord) == Board.KITCHEN)
							|| (game.getRoom() == Board.CONSERVATORY && game.getRoom(clickedCoord) == Board.LOUNGE)
							|| (game.getRoom() == Board.LOUNGE && game.getRoom(clickedCoord) == Board.CONSERVATORY))) {
				// Trying to take a passage
				if (game.takePassage()) {
					canvas.repaint();
					this.doSuggestion();
					
				}
				canvas.repaint();
			}
		// Dice roll
		} else if (dicePane.contains(e.getX(), e.getY() - bottomPaneTop) && game.getState() == 0) {
			int newRoll = dicePane.rollDice();
			dicePane.setRolled(true);
			infoPane.displayMovesLeft(newRoll);
			game.rollDice(newRoll);
			canvas.clearHighlight();
			canvas.highlight(game.getPossibleMoves());
			canvas.repaint();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("End Turn")) {
			if ((game.getState() == 2 && game.getMovesLeft() == 0) || game.getState() == 3) {
				this.endTurn();
			}
		} else if (e.getActionCommand().equals("Rules")) {
			this.showRules();
		} else if (e.getActionCommand().equals("Help")) {
			this.showHelp();
		} else if (e.getActionCommand().equals("Restart")) {
			try {
				this.restart(Main.createBoardFromFile("/board.txt"));
			} catch (IOException err) {
				System.out.println(err);
			}
		}
	}
	
	private void endTurn() {
		dicePane.setRolled(false);
		game.nextTurn();
		infoPane.clear();
		currentPlayer = game.currentPlayer();
		list.setPlayer(0);
		cardCanvas.updateCards(new ArrayList<Integer>());
		JOptionPane.showMessageDialog(CluedoUI.this, "It's " + players.get(currentPlayer) + "'s turn as " + CluedoUI.asString(currentPlayer) +"!");
		list.setPlayer(currentPlayer);
		cardCanvas.updateCards(game.getPlayerCards());
	}
	
	private void doSuggestion() {
		int guess = new SuggestDialog(this, game.getRoom()).getGuess();
		if (game.suggest(guess)) {
			canvas.repaint();
			list.setPlayer(0);
			infoPane.clear();
			cardCanvas.updateCards(new ArrayList<Integer>());
			this.refute(guess);
			list.setPlayer(currentPlayer);
			cardCanvas.updateCards(game.getPlayerCards());
			infoPane.showTurnEnd();
		}
	}
	
	private void refute(int guess) {
		if (game.getState() != 3) {
			return;
		}
		String[] cards;
		int count;
		do {
			List<Integer> cardsToRefuteFrom = game.getPlayerCards(game.getRefutePlayer());
			cards = new String[3];
			count = 0;
			for(int card: cardsToRefuteFrom) {
				if (card == game.findChar(guess) || card == game.findRoom(guess) || card == game.findWeapon(guess)) {
					cards[count] = asString(card);
					count++;
				}
			}
			if (cards[0] == null) {
				JOptionPane.showMessageDialog(this, players.get(game.getRefutePlayer()) + " couldn't refute the suggestion!");
				if (game.refute(0) == Board.SUCCESS) {
					JOptionPane.showMessageDialog(this, "<html><p>No one could refute" + players.get(currentPlayer) + "'s suggestion of<br>[" + asString(game.findChar(guess)) + " with the " + asString(game.findWeapon(guess)).toLowerCase() + " in the " + asString(game.findRoom(guess)).toLowerCase() +"]</p></html>");
					return;
				}
			}
		} while (cards[0] == null);
		
		String[] menuCards = new String[count];
		for (int i = 0; i < count; i++) {
			menuCards[i] = cards[i];
		}

		JComboBox<String> comboBox = new JComboBox<String> (menuCards);
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(new JLabel("<html>Refute " + players.get(currentPlayer) + "'s suggestion of<br>[" + asString(game.findChar(guess)) + " with the " + asString(game.findWeapon(guess)).toLowerCase() + " in the " + asString(game.findRoom(guess)).toLowerCase() +"]<br>with which card?</html>"), BorderLayout.NORTH);
		panel.add(Box.createRigidArea(new Dimension(0,12)), BorderLayout.CENTER);
		panel.add(comboBox, BorderLayout.SOUTH);
		JOptionPane.showOptionDialog(this, panel, players.get(game.getRefutePlayer()) + "'s turn to refute",  JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[]{"Refute"}, null);		
		int refution = asInt(comboBox.getSelectedItem().toString());
		if (game.refute(refution) == refution) {
			JOptionPane.showMessageDialog(this, players.get(game.getRefutePlayer()) + " refutes with " + asString(refution));
		}
	}
	
	private void gameOver(int winner) {
		if (winner == 0) {
			JOptionPane.showMessageDialog(this, "Game over! All players lost");
		} else {
			JOptionPane.showMessageDialog(this, "Game over, " + players.get(winner) + " won!");
		}
		int playAgain = JOptionPane.showConfirmDialog(this, "Play again?", "", JOptionPane.YES_NO_OPTION);
		if (playAgain == JOptionPane.YES_OPTION) {
			try {
				this.restart(Main.createBoardFromFile("board.txt"));
			} catch (IOException err) {
				System.out.println(err);
			}
		} else {
			System.exit(0);
		}
	}
	
	private void showHelp() {
		JFrame ruleWindow = new JFrame();
		ruleWindow.setPreferredSize(new Dimension(460, 400));
		ruleWindow.setLayout(new BorderLayout());
		Scanner sc = new Scanner(getClass().getResourceAsStream("/help.txt"));
		String text = sc.useDelimiter("\\Z").next();
		sc.close();
		JTextArea ta = new JTextArea(text);
		JScrollPane sp = new JScrollPane(ta);
		sp.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		ruleWindow.add(sp, BorderLayout.CENTER);
		ruleWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		ruleWindow.pack();
		ruleWindow.setResizable(false);
		ruleWindow.setVisible(true);
	}
	
	private void showRules() {
		JFrame ruleWindow = new JFrame();
		ruleWindow.setPreferredSize(new Dimension(630, 700));
		ruleWindow.setLayout(new BorderLayout());
		Scanner sc = new Scanner(getClass().getResourceAsStream("/rules.txt"));
		String text = sc.useDelimiter("\\Z").next();
		sc.close();
		JTextArea ta = new JTextArea(text);
		JScrollPane sp = new JScrollPane(ta);
		sp.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		ruleWindow.add(sp, BorderLayout.CENTER);
		ruleWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		ruleWindow.pack();
		ruleWindow.setResizable(false);
		ruleWindow.setVisible(true);
	}
	
	/*
	 * Starts the game passed in, shows initial setup dialog.
	 */
	private void restart(Board game) {
		
		this.game = game;
		this.canvas.restart(game);
		this.dicePane.restart();
		this.cardCanvas.restart();
		this.list.restart();
		this.currentPlayer = 1;
		this.infoPane.clear();
		players = new HashMap<Integer, String>();
		repaint();
		
		JOptionPane.showMessageDialog(this, "See 'help'and 'rules' in the game menu to learn how to play.", "Welcome to Cluedo!", JOptionPane.PLAIN_MESSAGE);
		
		JPanel panel = new JPanel();
		panel.add(new JLabel("How many players?"));
		final JComboBox<Integer> comboBox = new JComboBox<Integer>(new Integer[]{3, 4, 5, 6});
		panel.add(comboBox);
		
		JOptionPane.showMessageDialog(this, panel, "Welcome to Cluedo!", JOptionPane.PLAIN_MESSAGE);

		SuccessThread r = new SuccessThread (game, comboBox);
		
		r.start();
	}

	private class SuccessThread extends Thread {
        private Board game;
		private SelectPlayerDialog s;
		private JComboBox<Integer> comboBox;
		
        SuccessThread(Board game, JComboBox<Integer> comboBox) {
            this.game = game;
            this.comboBox = comboBox;
        }

		@Override
		public void run() {
			s = new SelectPlayerDialog(CluedoUI.this, players, (int)comboBox.getSelectedItem());
			while (!s.done()) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			s.dispose();
			
			for (Integer p: players.keySet()) {
				game.addPlayer(p);
			}
			game.startGame();

			currentPlayer = game.currentPlayer();
			JOptionPane.showMessageDialog(CluedoUI.this, "It's " + players.get(currentPlayer) + "'s turn as " + CluedoUI.asString(currentPlayer) +"!");
			list.setPlayer(currentPlayer);
			cardCanvas.updateCards(game.getPlayerCards());
		}
    }
	
	/**
	 * Turns the integer values used within the program into the string that
	 * is its name.
	 * @param gamePart the integer related to the component
	 */
	public static String  asString (int gamePart) {
		switch (gamePart) {
			case Board.SCARLETT: return "Miss Scarlett";
			case Board.MUSTARD: return "Colonel Mustard";
			case Board.WHITE: return "Mrs White";
			case Board.GREEN: return "Rev Green";
			case Board.PEACOCK: return "Miss Peacock";
			case Board.PLUM: return "Professor Plum";
			case Board.CANDLESTICK: return "Candlestick";
			case Board.DAGGER: return "Dagger";
			case Board.PIPE: return "Lead Pipe";
			case Board.REVOLVER: return "Revolver";
			case Board.ROPE: return "Rope";
			case Board.SPANNER: return "Spanner";
			case Board.KITCHEN: return "Kitchen";
			case Board.BALLROOM: return "Ballroom";
			case Board.CONSERVATORY: return "Conservatory";
			case Board.BILLARD: return "Billiard Room";
			case Board.LIBRARY: return "Library";
			case Board.STUDY: return "Study";
			case Board.HALL: return "Hall";
			case Board.LOUNGE: return "Lounge";
			case Board.DINING: return "Dining Room";
			default: return null;
		}
	}

	/**
	 * Turns the integer values used within the program into the string that
	 * is its name.
	 * @param gamePart the string related to the component
	 */
	public static int  asInt (String gamePart) {
		switch (gamePart) {
			case "Miss Scarlett": return Board.SCARLETT;
			case "Colonel Mustard": return Board.MUSTARD;
			case "Mrs White": return Board.WHITE;
			case "Rev Green": return Board.GREEN;
			case "Miss Peacock": return Board.PEACOCK;
			case "Professor Plum": return Board.PLUM;
			case "Candlestick": return Board.CANDLESTICK;
			case "Dagger": return Board.DAGGER;
			case "Lead Pipe": return Board.PIPE;
			case "Revolver": return Board.REVOLVER;
			case "Rope": return Board.ROPE;
			case "Spanner": return Board.SPANNER;
			case "Kitchen": return Board.KITCHEN;
			case "Ballroom": return Board.BALLROOM;
			case "Conservatory": return Board.CONSERVATORY;
			case "Billiard Room": return Board.BILLARD;
			case "Library": return Board.LIBRARY;
			case "Study": return Board.STUDY;
			case "Hall": return Board.HALL;
			case "Lounge": return Board.LOUNGE;
			case "Dining Room": return Board.DINING;
			default: return 0;
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {}
	@Override
	public void mouseReleased(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
}
