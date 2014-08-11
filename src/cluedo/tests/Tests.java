package cluedo.tests;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import cluedo.AStar;
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
	
	@Test
	public void testAStarFuuu(){
		Board board = makeBoard();
		board.addPlayer(3);
		board.addPlayer(2);
		board.addPlayer(1);
		board.startGame();
		AStar path = board.aStar(new Coordinate(9, 16), new Coordinate(15, 16));
		assertTrue(path.getLength() == 8);
		assertTrue(path.getHeuristic() == 0);
	}
	
	@Test
	public void testMovement(){
		Board board = makeBoard();
		board.addPlayer(1);
		board.startGame();
		board.rollDice(2);
		assertTrue(board.move(new Coordinate(7, 22)) == 7); //7 is the success enum
		board.nextTurn();
		board.rollDice(2);
		assertTrue(board.move(new Coordinate(7, 19)) == 9); //9 is the fail enum
		board.nextTurn();
		board.rollDice(2);
		assertTrue(board.move(new Coordinate(7, 23)) == 0); //0 is the NOTHING enum, means there's still more moves to go.
	}
	
	@Test
	public void listSync(){
		Board board = makeBoard();
		board.addPlayer(1);
		board.addPlayer(3);
		board.startGame();
		Player cur = board.getCurrentPlayer();
		assertTrue(cur.getCoords().equals(board.getPlayerCoords(1)));
		board.nextTurn();
		cur = board.getCurrentPlayer();
		assertTrue(cur.getCoords().equals(board.getPlayerCoords(3)));
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
