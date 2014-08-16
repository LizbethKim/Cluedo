package cluedo.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import cluedo.Coordinate;
import cluedo.board.Board;

@SuppressWarnings("serial")
public class BoardCanvas extends JPanel{

	private Image boardPic;
	private final int width;
	private final int height;

	private final int boardLeft;
	private final int boardTop;
	private final double squareWidth;

	private Color highlightCol = new Color(0, 255, 0, 100);

	private Set<Coordinate> highlighted = new HashSet<Coordinate>();
	private Map<Integer, Color> colors;

	private Board game;
	List<Coordinate> shifts;
	
	/**
	 * @param big Whether the boards should be large or not
	 */
	public BoardCanvas (boolean big, Board game) {
		this.game = game;
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
		colors = new HashMap<Integer, Color>();
		
		colors.put(Board.SCARLETT, Color.red);
		colors.put(Board.MUSTARD, new Color (255,205,90));
		colors.put(Board.WHITE, Color.white);
		colors.put(Board.GREEN, Color.green.darker());
		colors.put(Board.PEACOCK, Color.blue.brighter());
		colors.put(Board.PLUM, new Color(170, 70 ,255));
		
		shifts = new ArrayList<Coordinate>();
		shifts.add(new Coordinate(-1, 0));
		shifts.add(new Coordinate(-2, 0));
		shifts.add(new Coordinate(1, 0));
		shifts.add(new Coordinate(0, -1));
		shifts.add(new Coordinate(0, 1));
		shifts.add(new Coordinate(-1, -1));
		shifts.add(new Coordinate(-2, -1));
		shifts.add(new Coordinate(1, -1));
		shifts.add(new Coordinate(-1, 1));
		shifts.add(new Coordinate(-2, 1));
		shifts.add(new Coordinate(1, 1));		
		
		// TODO position weapons
		try {
		    boardPic = ImageIO.read(new File("assets/board.jpg"));
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.black);
		g.fillRect(0, 0, width, height);
		g.drawImage(boardPic, 1, 0, width, height, this);

		// path highlighting
		g.setColor(highlightCol);
		for (Coordinate c: highlighted) {
			g.fillRect(boardLeft + (int)(squareWidth*c.getX()), boardTop + (int)(squareWidth*c.getY()), (int)squareWidth + 1, (int)squareWidth + 1);
		}
		
		
		List<Coordinate> used = new ArrayList<Coordinate>();
		for (int chara : colors.keySet()) {
			
			Coordinate c = game.getPlayerCoords(chara);
			int room = game.getRoom(c);
			if (room == Board.NOTHING || !used.contains(c)) {
				g.setColor(Color.black);
				g.fillOval(boardLeft + (int)(squareWidth*c.getX()) + 2, boardTop + (int)(squareWidth*c.getY()) + 2, (int)squareWidth - 4, (int)squareWidth - 4);
				g.setColor(colors.get(chara));
				g.fillOval(boardLeft + (int)(squareWidth*c.getX()) + 4, boardTop + (int)(squareWidth*c.getY()) + 4, (int)squareWidth - 8, (int)squareWidth - 8);
				used.add(c);
			} else {
				// this deals with spreading players out within rooms
				for (Coordinate shift: shifts) {
					Coordinate shifted = Coordinate.addCoords(c, shift);
					if (!used.contains(shifted) && game.getRoom(shifted) == room) {
						c = shifted;
						break;
					}
				}
				g.setColor(Color.black);
				g.fillOval(boardLeft + (int)(squareWidth*c.getX()) + 2, boardTop + (int)(squareWidth*c.getY()) + 2, (int)squareWidth - 4, (int)squareWidth - 4);
				g.setColor(colors.get(chara));				
				g.fillOval(boardLeft + (int)(squareWidth*c.getX()) + 4, boardTop + (int)(squareWidth*c.getY()) + 4, (int)squareWidth - 8, (int)squareWidth - 8);
				used.add(c);
			}
			
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
	
	public void highlight(Collection<Coordinate> cs) {
		for (Coordinate c: cs) {
			highlighted.add(c);
		}
	}

	public void unHighlight(Collection<Coordinate> cs) {
		for (Coordinate c: cs) {
			highlighted.remove(c);
		}
	}

	public void clearHighlight() {
		highlighted = new HashSet<Coordinate>();
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

	public void restart(Board game) {
		this.game = game;
		highlighted = new HashSet<Coordinate>();
		
	}

}
