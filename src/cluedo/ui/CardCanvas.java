package cluedo.ui;

import java.awt.Color;
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

import cluedo.Coordinate;

/**
 * The panel of the UI that displays the cards in the hand of the current player
 * @author kelsey
 *
 */
public class CardCanvas extends JPanel {
	private List<String> cardsToDisplay;
	private Map<String, Image> cards; // TODO change String to enum, get Liz to store
	
	private final int  height = 190;
	private final int width = 500;
	private final int space = 2;
	private final int cardTop = 25;
	private final int cardWidth = 80;
	private final int cardHeight = 118;

	public CardCanvas() {
		cardsToDisplay = new ArrayList<String>();
		cards = new HashMap<String, Image>();
		try {
			cards.put("plum", ImageIO.read(new File("plum.jpg")));
			cards.put("scarlett", ImageIO.read(new File("scarlett.jpg")));
			cards.put("green", ImageIO.read(new File("green.jpg")));
			cards.put("white", ImageIO.read(new File("white.jpg")));
			cards.put("peacock", ImageIO.read(new File("peacock.jpg")));
			cards.put("mustard", ImageIO.read(new File("mustard.jpg")));
			cards.put("spanner", ImageIO.read(new File("spanner.jpg")));
			cards.put("dagger", ImageIO.read(new File("dagger.jpg")));
			cards.put("rope", ImageIO.read(new File("rope.jpg")));
			cards.put("candlestick", ImageIO.read(new File("candlestick.jpg")));
			cards.put("revolver", ImageIO.read(new File("revolver.jpg")));
			cards.put("leadpipe", ImageIO.read(new File("leadpipe.jpg")));
			cards.put("kitchen", ImageIO.read(new File("kitchen.jpg")));
			cards.put("diningroom", ImageIO.read(new File("diningroom.jpg")));
			cards.put("ballroom", ImageIO.read(new File("ballroom.jpg")));
			cards.put("study", ImageIO.read(new File("study.jpg")));
			cards.put("conservatory", ImageIO.read(new File("conservatory.jpg")));
			cards.put("lounge", ImageIO.read(new File("lounge.jpg")));
			cards.put("billiardroom", ImageIO.read(new File("billiardroom.jpg")));
			cards.put("library", ImageIO.read(new File("library.jpg")));
			cards.put("hall", ImageIO.read(new File("hall.jpg")));

		} catch (IOException e) {
			System.out.println(e);
		}

	}
	
	protected void paintComponent(Graphics g) {
		g.clearRect(0, 0, width, height);
		int left = (width + space - cardsToDisplay.size()*(cardWidth + space))/2;
		for (String s: cardsToDisplay) {
			g.drawImage(cards.get(s), left, cardTop, cardWidth, cardHeight, this);
			//g.drawImage(cards.get(s), left, cardTop, this);
			left += cardWidth + space;
		}

	}
	
	/**
	 * To update which cards are shown in the card panel.
	 * @param cards A list of the card names
	 */
	public void updateCards (List<String> cards) {
		this.cardsToDisplay = cards;
		if (cards.size() > 6) {
			throw new IllegalArgumentException();
		}
		repaint();
		
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(width,height);
	}
}
