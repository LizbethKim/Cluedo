package cluedo.tests;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import cluedo.Coordinate;
import cluedo.Main;
import cluedo.board.Board;
import cluedo.models.Player;


public class Tests {
	
	@Test
	public void cardTest(){
		Board board = makeBoard();
		assertTrue(board.findWeapon(512) == 10);
		assertTrue(board.findRoom(512) == 500);
		assertTrue(board.findChar(512) == 2);
		assertTrue(board.convertRoom(512) == 5);
	}
	
	@Test
	public void passageTest(){
		Board board = makeBoard();
		board.addPlayer(3);
		board.startGame();
		Player cur = board.getCurrentPlayer();
		cur.setCoords(new Coordinate(4, 6));
		cur.setRoom(100);
		assertTrue(board.takePassage());
		assertTrue(cur.getCoords().equals(new Coordinate(17, 21)));
		assertTrue(cur.currentRoom() == 600);
	}
	
	public Board makeBoard(){
		try {
			Board board = Main.createBoardFromFile("board.txt");
			return board;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
