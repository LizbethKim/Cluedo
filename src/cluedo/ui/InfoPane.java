package cluedo.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.LayoutManager;

import javax.swing.JPanel;

public class InfoPane extends JPanel {
	private String message;
	private Color col;
	private Font f;
	private int x;
	private int y;
	
	private int width = 200;
	private int height = 190;
	
	
	public InfoPane() {
		message = "";
	}
	
	public void displayMovesLeft(int movesLeft) {
		message = movesLeft + " moves left";
		col = Color.red.darker();
		f = new Font("Sans Serif", Font.BOLD, 14);
		x = 30;
		y = 90;
		repaint();
	}
	
	public void clear() {
		message = "";
		repaint();
	}
	
	@Override
	public void paintComponent(Graphics g) {
		g.clearRect(0,0,width, height);
		g.setColor(col);
		g.setFont(f);
		g.drawString(message, x, y);
	}

	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(width,height);
	}
}
