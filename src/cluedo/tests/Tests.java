package cluedo.tests;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;
import java.util.Set;

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
		List<Coordinate> meow = board.getRoomCoords();
		assertTrue(board.takePassage());
		assertTrue(cur.getCoords().equals(meow.get(6)));
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
		assertTrue(board.move(new Coordinate(7, 22)) == Board.SUCCESS); //7 is the success enum
		board.nextTurn();
		board.rollDice(2);
		assertTrue(board.move(new Coordinate(7, 19)) == Board.FAIL); //9 is the fail enum
		board.nextTurn();
		board.rollDice(5);
		assertTrue(board.move(new Coordinate(7, 21)) == Board.NOTHING); //0 is the NOTHING enum, means there's still more moves to go.
		assertTrue(board.move(new Coordinate(7, 20)) == Board.NOTHING);
		assertTrue(board.move(new Coordinate(7, 17)) == Board.SUCCESS);
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

	@Test
	public void testMiddleRoom(){
		Board board = makeBoard();
		assertTrue(board.getRoom(new Coordinate(11, 10)) == Board.MIDDLE); //Middle room detection
		board.addPlayer(1);
		board.startGame();
		board.rollDice(100);
		assertTrue(board.move(new Coordinate(11, 10)) == Board.FAIL);
	}

	@Test
	public void testAccuse(){
		Board board = makeBoard();
		board.addPlayer(1);
		board.startGame();
		int solution = board.getSoln();
		assertTrue(board.accuse(solution) == Board.SUCCESS);
	}

	@Test
	public void testAccuseFail(){
		Board board = makeBoard();
		board.addPlayer(1);
		board.startGame();
		int solution = board.getSoln();
		assertTrue(board.accuse(994) == 0);
		if (solution != 444) assertTrue(board.accuse(444) == Board.FAIL);
		else{assertTrue(board.accuse(443) == Board.FAIL);}
		board.nextTurn();
		assertTrue(board.getState() == 5);
	}
	
	@Test
	public void testCardSize_1(){
		Board board = makeBoard();
		board.addPlayer(1);
		board.addPlayer(2);
		board.addPlayer(6);
		board.startGame();
		assertTrue(board.getPlayerCards(1).size() == 6);
		assertTrue(board.getPlayerCards(2).size() == 6);
		assertTrue(board.getPlayerCards(6).size() == 6);
	}
	
	@Test
	public void testCardSize_2(){
		Board board = makeBoard();
		board.addPlayer(1);
		board.addPlayer(2);
		board.addPlayer(3);
		board.addPlayer(4);
		board.addPlayer(5);
		board.addPlayer(6);
		board.startGame();
		assertTrue(board.getPlayerCards(1).size() == 3);
		assertTrue(board.getPlayerCards(2).size() == 3);
		assertTrue(board.getPlayerCards(3).size() == 3);
		assertTrue(board.getPlayerCards(4).size() == 3);
		assertTrue(board.getPlayerCards(5).size() == 3);
		assertTrue(board.getPlayerCards(6).size() == 3);
	}
	
	@Test
	public void testRoomEntryExit(){
		Board board = makeBoard();
		board.addPlayer(1);
		board.startGame();
		board.rollDice(100);
		List<Coordinate> rooms = board.getRoomCoords();
		assertTrue(board.move(rooms.get(1)) == Board.SUCCESS);
		assertTrue(board.getCurrentPlayer().currentRoom() == 100);
		board.nextTurn();
		board.rollDice(100);
		assertTrue(board.move(rooms.get(6)) == Board.SUCCESS);
		assertTrue(board.getCurrentPlayer().currentRoom() == 600);
		board.nextTurn();
		board.rollDice(100);
		assertTrue(board.move(rooms.get(6)) == Board.FAIL);
	}
	
	@Test
	public void breadthFirstSearch(){
		Board board = makeBoard();
		board.addPlayer(1);
		board.startGame();
		board.rollDice(5);
		Set<Coordinate> meow = board.getPossibleMoves();
		System.out.println(meow.size());
		for (Coordinate c:meow){
			System.out.println(c);
		}
	}


	public Board makeBoard(){
		try {
			Board board = Main.createBoardFromFile("board.txt");
			return board;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
