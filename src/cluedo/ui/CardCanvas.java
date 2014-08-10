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
			cards.put("plum", ImageIO.read(new File("assets/plum.jpg")));
			cards.put("scarlett", ImageIO.read(new File("assets/scarlett.jpg")));
			cards.put("green", ImageIO.read(new File("assets/green.jpg")));
			cards.put("white", ImageIO.read(new File("assets/white.jpg")));
			cards.put("peacock", ImageIO.read(new File("assets/peacock.jpg")));
			cards.put("mustard", ImageIO.read(new File("assets/mustard.jpg")));
			cards.put("spanner", ImageIO.read(new File("assets/spanner.jpg")));
			cards.put("dagger", ImageIO.read(new File("assets/dagger.jpg")));
			cards.put("rope", ImageIO.read(new File("assets/rope.jpg")));
			cards.put("candlestick", ImageIO.read(new File("assets/candlestick.jpg")));
			cards.put("revolver", ImageIO.read(new File("assets/revolver.jpg")));
			cards.put("leadpipe", ImageIO.read(new File("assets/leadpipe.jpg")));
			cards.put("kitchen", ImageIO.read(new File("assets/kitchen.jpg")));
			cards.put("diningroom", ImageIO.read(new File("assets/diningroom.jpg")));
			cards.put("ballroom", ImageIO.read(new File("assets/ballroom.jpg")));
			cards.put("study", ImageIO.read(new File("assets/study.jpg")));
			cards.put("conservatory", ImageIO.read(new File("assets/conservatory.jpg")));
			cards.put("lounge", ImageIO.read(new File("assets/lounge.jpg")));
			cards.put("billiardroom", ImageIO.read(new File("assets/billiardroom.jpg")));
			cards.put("library", ImageIO.read(new File("assets/library.jpg")));
			cards.put("hall", ImageIO.read(new File("assets/hall.jpg")));

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
