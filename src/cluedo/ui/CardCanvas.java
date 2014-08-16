package cluedo.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import cluedo.board.Board;

/**
 * The panel of the UI that displays the cards in the hand of the current player
 * @author kelsey
 *
 */
@SuppressWarnings("serial")
public class CardCanvas extends JPanel {
	private Map<Integer, Image> cards; 
	private final int  height = 190;
	private final int width = 500;
	private final int space = 2;
	private final int cardTop = 25;
	private final int cardWidth = 80;
	private final int cardHeight = 118;
	private List<Integer> cardsToDisplay;

	public CardCanvas() {
		cardsToDisplay = new ArrayList<Integer>();
		cards = new HashMap<Integer, Image>();
		try {
			cards.put(Board.PLUM, ImageIO.read(new File("assets/" + CluedoUI.asString(Board.PLUM) + ".jpg")));
			cards.put(Board.SCARLETT, ImageIO.read(new File("assets/" + CluedoUI.asString(Board.SCARLETT) + ".jpg")));
			cards.put(Board.GREEN, ImageIO.read(new File("assets/" + CluedoUI.asString(Board.GREEN) + ".jpg")));
			cards.put(Board.WHITE, ImageIO.read(new File("assets/" + CluedoUI.asString(Board.WHITE) + ".jpg")));
			cards.put(Board.PEACOCK, ImageIO.read(new File("assets/" + CluedoUI.asString(Board.PEACOCK) + ".jpg")));
			cards.put(Board.MUSTARD, ImageIO.read(new File("assets/" + CluedoUI.asString(Board.MUSTARD) + ".jpg")));
			cards.put(Board.SPANNER, ImageIO.read(new File("assets/" + CluedoUI.asString(Board.SPANNER) + ".jpg")));
			cards.put(Board.DAGGER, ImageIO.read(new File("assets/" + CluedoUI.asString(Board.DAGGER) + ".jpg")));
			cards.put(Board.ROPE, ImageIO.read(new File("assets/" + CluedoUI.asString(Board.ROPE) + ".jpg")));
			cards.put(Board.CANDLESTICK, ImageIO.read(new File("assets/" + CluedoUI.asString(Board.CANDLESTICK) + ".jpg")));
			cards.put(Board.REVOLVER, ImageIO.read(new File("assets/" + CluedoUI.asString(Board.REVOLVER) + ".jpg")));
			cards.put(Board.PIPE, ImageIO.read(new File("assets/" + CluedoUI.asString(Board.PIPE) + ".jpg")));
			cards.put(Board.KITCHEN, ImageIO.read(new File("assets/" + CluedoUI.asString(Board.KITCHEN) + ".jpg")));
			cards.put(Board.DINING, ImageIO.read(new File("assets/" + CluedoUI.asString(Board.DINING) + ".jpg")));
			cards.put(Board.BALLROOM, ImageIO.read(new File("assets/" + CluedoUI.asString(Board.BALLROOM) + ".jpg")));
			cards.put(Board.STUDY, ImageIO.read(new File("assets/" + CluedoUI.asString(Board.STUDY) + ".jpg")));
			cards.put(Board.CONSERVATORY, ImageIO.read(new File("assets/" + CluedoUI.asString(Board.CONSERVATORY) + ".jpg")));
			cards.put(Board.LOUNGE, ImageIO.read(new File("assets/" + CluedoUI.asString(Board.LOUNGE) + ".jpg")));
			cards.put(Board.BILLARD, ImageIO.read(new File("assets/" + CluedoUI.asString(Board.BILLARD) + ".jpg")));
			cards.put(Board.LIBRARY, ImageIO.read(new File("assets/" + CluedoUI.asString(Board.LIBRARY) + ".jpg")));
			cards.put(Board.HALL, ImageIO.read(new File("assets/" + CluedoUI.asString(Board.HALL) + ".jpg")));
		} catch (IOException e) {
			System.out.println(e);
		}
	}
	
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.clearRect(0, 0, width, height);
		int left = (width + space - cardsToDisplay.size()*(cardWidth + space))/2;
		for (int card: cardsToDisplay) {
			g.drawImage(cards.get(card), left, cardTop, cardWidth, cardHeight, this);
			left += cardWidth + space;
		}
	}
	
	/**
	 * To update which cards are shown in the card panel.
	 * @param cards A list of the card numbers. Mustn't be 
	 * null or have more than 6 elements.
	 */
	public void updateCards (List<Integer> cards) {
		if (cards == null || cards.size() > 6) {
			throw new IllegalArgumentException();
		}
		this.cardsToDisplay = cards;
		repaint();
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(width,height);
	}

	public void restart() {
		cardsToDisplay = new ArrayList<Integer>();
	}
}
