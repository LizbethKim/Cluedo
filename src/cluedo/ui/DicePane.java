package cluedo.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * The panel of the UI that displays the dice roll
 * @author kelsey
 */
@SuppressWarnings("serial")
public class DicePane extends JPanel {
	private Random r;
	private int d1 = 6;
	private int d2 = 6;
	private boolean rolled;

	private final int width = 130;
	private final int height = 190;
	private final int d1Top = 20;
	private final int d2Top = 20 + 65 + 20;
	private final int dLeft = 65/2;
	private final int dieSize = 65;
	
	private Map<Integer, Image> dice;
	
	
	public DicePane() {
		r = new Random();
		dice = new HashMap<Integer, Image>();
		try {
			dice.put(1, ImageIO.read(getClass().getResource("/one.jpg")));
			dice.put(2, ImageIO.read(getClass().getResource("/two.jpg")));
			dice.put(3, ImageIO.read(getClass().getResource("/three.jpg")));
			dice.put(4, ImageIO.read(getClass().getResource("/four.jpg")));
			dice.put(5, ImageIO.read(getClass().getResource("/five.jpg")));
			dice.put(6, ImageIO.read(getClass().getResource("/six.jpg")));
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	/**
	 * Will roll the dice
	 * @return the amount rolled
	 */
	public int rollDice() {
		d1 = r.nextInt(6) + 1;
		d2 = r.nextInt(6) + 1;
		repaint();
		return d1 + d2;
	}
	
	/**
	 * Sets whether the dice have been rolled. If they have not
	 * yet been rolled, "click to roll" is displayed.
	 * @param rolled
	 */
	public void setRolled(boolean rolled) {
		this.rolled = rolled;
		repaint();
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.clearRect(0, 0, width, height);
		g.drawImage(dice.get(d1), dLeft, d1Top, dieSize, dieSize, this);
		g.drawImage(dice.get(d2), dLeft, d2Top, dieSize, dieSize, this);
		if (!rolled) {
			g.setFont(new Font(getFont().getFontName(), Font.BOLD, getFont().getSize() + 3));
			g.setColor(Color.orange);
			g.drawString("click to roll", dLeft - 10, d2Top - 5);
		}
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(width ,height);
	}
	
	/**
	 * Resets for a new game
	 */
	public void restart() {
		d1 = 6;
		d2 = 6;
		this.rolled = false;
	}
	
}
