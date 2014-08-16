package cluedo.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class InfoPane extends JPanel {
	private String message1;
	private String message2;
	private Color col;
	private Font f;
	private int x;
	private int y;
	
	private int width = 200;
	private int height = 190;
	
	
	public InfoPane() {
		message1 = "";
		message2 = "";
	}
	
	public void displayMovesLeft(int movesLeft) {
		message1 = movesLeft + " moves left";
		message2 = "";
		col = Color.red.darker();
		f = new Font("Sans Serif", Font.BOLD, 14);
		x = 30;
		y = 80;
		repaint();
	}
	
	public void showTurnEnd() {
		message1 = "End your turn"; 
		message2 = "when you're ready";
		col = Color.red.darker();
		f = new Font("Sans Serif", Font.BOLD, 14);
		x = 20;
		y = 70;
		repaint();
	}
	
	public void clear() {
		message1 = "";
		message2 = "";
		repaint();
	}
	
	@Override
	public void paintComponent(Graphics g) {
		g.clearRect(0,0,width, height);
		g.setColor(col);
		g.setFont(f);
		g.drawString(message1, x, y);
		g.drawString(message2, x, y + 20);
	}

	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(width,height);
	}

	
}
