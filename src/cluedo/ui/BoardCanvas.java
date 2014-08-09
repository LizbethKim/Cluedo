package cluedo.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import cluedo.Coordinate;

public class BoardCanvas extends JPanel{

	private Image boardPic;
	private final int width;
	private final int height;

	private final int boardLeft;
	private final int boardTop;
	private final double squareWidth;

	private Color highlightCol = new Color(0, 255, 0, 100);

	private List<Coordinate> highlighted = new ArrayList<Coordinate>();
	private Map<Integer, Coordinate> tokens;
	/**
	 * @param big Whether the boards should be large or not
	 */
	public BoardCanvas (boolean big) {
		if (big) {
			width = 730;
			height = 760;
			boardLeft = 12;
			boardTop = 12;
			squareWidth = 29.42;			
		} else {
			width = 630;
			height = 656;
			boardLeft = 11;
			boardTop = 11;
			squareWidth = 25.38;
		}	
		// TODO position players initially
		// TODO position weapons
		try {
		    boardPic = ImageIO.read(new File("board.jpg"));
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.black);
		g.fillRect(0, 0, width, height);
		g.drawImage(boardPic, 1, 0, width, height, this);

//		g.setColor(Color.yellow);	TODO delete eventually
//		for (double i = boardTop; i < height; i += squareWidth) {
//			g.drawLine(0, (int)i, width, (int)i);
//		}
//		for (double i = boardLeft; i < width; i += squareWidth) {
//			g.drawLine((int)i, 0, (int)i, height);
//		}
	
		g.setColor(highlightCol);
		for (Coordinate c: highlighted) {
			g.fillRect(boardLeft + (int)(squareWidth*c.getX()) - 1, boardTop + (int)(squareWidth*c.getY()) - 1, (int)squareWidth + 2, (int)squareWidth + 2);
		}

	}

	
	/**
	 * Allows squares on the board to be highlighted 
	 * @param c
	 */
	public void highlight(Coordinate c) {
		highlighted.add(c);
	}

	public void unHighlight(Coordinate c) {
		highlighted.remove(c);
	}

	public void clearHighlight() {
		highlighted = new ArrayList<Coordinate>();
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(width,height);
	}
	
	public int getBoardLeft() {
		return boardLeft;
	}

	public int getBoardTop() {
		return boardTop;
	}

	public double getSquareWidth() {
		return squareWidth;
	}
	
	public int getBoardHeight() {
		return height;
	}

}
