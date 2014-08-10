package cluedo.board;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cluedo.Coordinate;
import cluedo.models.Card;
import cluedo.models.Hallway;
import cluedo.models.Player;
import cluedo.models.Room;
import cluedo.models.Square;
import cluedo.models.Wall;

public class Board {
	
	//Success/Failure
	public static final int NOTHING = 0;
	public static final int SUCCESS = 7;
	public static final int INVALIDCARD = 8;
	//public static final int NEXTTURN = 9; Not used atm. Perhaps there will be a use for it
	
	//Characters
	public static final int SCARLETT = 1;
	public static final int MUSTARD = 2;
	public static final int WHITE = 3;
	public static final int GREEN = 4;
	public static final int PEACOCK = 5;
	public static final int PLUM = 6;
	
	//Weapons
	public static final int CANDLESTICK = 10;
	public static final int DAGGER = 20;
	public static final int PIPE = 30;
	public static final int REVOLVER = 40;
	public static final int ROPE = 50;
	public static final int SPANNER = 60;
	
	//Rooms
	public static final int KITCHEN = 100;
	public static final int BALLROOM = 200;
	public static final int CONSERVATORY = 300;
	public static final int BILLARD = 400;
	public static final int LIBRARY = 500;
	public static final int STUDY = 600;
	public static final int HALL = 700;
	public static final int LOUNGE = 800;
	public static final int DINING = 900;

	
	private Square[][] board;
	private List<Room> roomList = new ArrayList<Room>();
	private List<Player> playerList = new ArrayList<Player>();
	private int numPlayers;
	private int currentPlayer;
	private int currentMove;
	private int currentSuggest;		//Current set of cards being suggested
	private int refutePlayer;		//Used for when going around to refute a suggestion/accusation
	private int currentAccuse;		//Current set of cards being accused
	private boolean[][] aStarBoard;
	
	/*
	 * States: -1 = Initialization
	 * 0 = Start Turn (Roll dice/Secret Passage/Make accusation)
	 * 1 = Move
	 * 2 = Suggest/Accuse
	 * 3 = Refute
	 */
	private int currentState;
	
	public Board(int x, int y){
		board = new Square[x][y];
		currentState = -1;
		generateRoomList();
		currentSuggest = 0;
		currentAccuse = 0;
	}
	
	/*
	 * Sets the number of players, sorts players into order, sets current player 
	 * creates new "Can go" map, sets state to first state. 
	 */
	@SuppressWarnings("unchecked")
	public boolean startGame(){
		if (currentState != -1) return false;
		Collections.sort(playerList);
		numPlayers = playerList.size()-1;
		currentState = 0;
		currentPlayer = 0;
		refutePlayer = 0;
		aStarBoard = aStarBoard();
		return true;
	}
	
	public int currentPlayer(){
		return playerList.get(currentPlayer).getChar();
	}
	
	//This may be confusing, but was mainly used for testing purposes. Please ignore.
	public Player getCurrentPlayer(){
		return playerList.get(currentPlayer);
	}
	
	//Must ensure Player's room is set correctly before attempting this 
	public boolean takePassage(){
		if (currentState == 0){
			if (playerList.get(currentPlayer).currentRoom() != NOTHING){
				Coordinate temp = playerList.get(currentPlayer).getCoords();
				int passage = ((Room) (board[temp.getX()][temp.getY()])).getPassage();
				if (passage != NOTHING){
					playerList.get(currentPlayer).setRoom(passage);
					//Gets the room from the roomList using the ID given by the passage. Looks more complicated than it is
					playerList.get(currentPlayer).setCoords(roomList.get(convertRoom(passage)).getExits().get(0));
					moveState();
					moveState();
					return true;
				}
			}
		}
		return false;
	}
	
	/*
	 * Suggest mechanism, suggest an ENUM combination (3 numbers). Returns true if it's valid to suggest at the time
	 * else returns false if invalid. CAN ONLY SUGGEST IN STATE 2 -AFTER- the movement.
	 */
	public boolean suggest(int suggestion){
		if (currentState == 2){
			this.currentSuggest = suggestion;
			refutePlayer = currentPlayer + 1;
			moveState();
			return true;
		}
		return false;
	}
	/*
	 * Refuting mechanism. Will take a card ENUM, and return a NOTHING if the player refuted nothing (passing)
	 * or the card ENUM if the refute is successful, or SUCCESS if the player's suggestion goes through.
	 */
	public int refute(int cardNum){
		if (cardNum == 0) {
			moveRefute();
			if (refutePlayer == currentPlayer){
				nextTurn();
				return SUCCESS;
			}
			return NOTHING;
		}
		if (playerList.get(refutePlayer).hasCard(cardNum)){
			if (findRoom(currentSuggest) == findRoom(cardNum)) {
				nextTurn();
				return findRoom(currentSuggest);
			} else if (findWeapon(currentSuggest) == findWeapon(cardNum)){
				nextTurn();
				return findWeapon(currentSuggest);
			} else if (findChar(currentSuggest) == findChar(cardNum)){
				nextTurn();
				return findChar(currentSuggest);
			}
		}
		moveRefute();
		if (refutePlayer == currentPlayer){
			nextTurn();
			return SUCCESS;
		}
		return INVALIDCARD;
	}
	
	//Starts the next turn. Should only be called by Board. 
	private void nextTurn(){
		movePlayer();
		currentState = 0;
	}
	
	//Moves the variable to the next player/state. Again should only be called by Board
	private void moveRefute(){
		refutePlayer = (refutePlayer + 1) % numPlayers;
	}
	
	private void moveState(){
		currentState = (currentState + 1) % 4;
	}
	
	private void movePlayer(){
		currentPlayer = (currentPlayer + 1) % numPlayers;
	}
	
	
	//Converts a number into a single digit room ID number to access roomList (from a 3 digit combo)
	public int convertRoom(int room){
		return ((room/100)%10);
	}
	
	//Converts a number into the appropriate ENUM values for comparisons (from a 3 digit combo)
	public int findRoom(int room){
		return ((room/100)%10)*100;
	}
	
	public int findWeapon(int weapon){
		return ((weapon/10)%10)*10;
	}
	
	public int findChar(int character){
		return (character%10);
	}
	
	public boolean rollDice(int roll){
		if (currentState == 0) {
			currentMove = roll;
			return true;
		}
		return false;
	}
	
	public int getState(){
		return currentState;
	}
	
	public boolean addPlayer(int chara){
		if (currentState != -1){
			return false;
		} else {
			switch(chara){
				case SCARLETT:
					playerList.add(new Player(SCARLETT, 7, 24));
					break;
				case MUSTARD:
					playerList.add(new Player(MUSTARD, 0, 17));
					break;
				case WHITE:
					playerList.add(new Player(WHITE, 9, 0));
					break;
				case GREEN:
					playerList.add(new Player(GREEN, 14, 0));
					break;
				case PEACOCK:
					playerList.add(new Player(PEACOCK, 23, 6));
					break;
				case PLUM:
					playerList.add(new Player(PLUM, 23, 19));
					break;
			}
			return true;
		}
	}
	
	private void generateRoomList(){
		roomList.add(new Room(NOTHING));
		roomList.add(new Room(KITCHEN));
		roomList.add(new Room(BALLROOM));
		roomList.add(new Room(CONSERVATORY));
		roomList.add(new Room(BILLARD));
		roomList.add(new Room(LIBRARY));
		roomList.add(new Room(STUDY));
		roomList.add(new Room(HALL));
		roomList.add(new Room(LOUNGE));
		roomList.add(new Room(DINING));
		roomList.get(1).addPassage(STUDY);
		roomList.get(1).addExit(new Coordinate(4, 6));
		roomList.get(2).addExit(new Coordinate(8, 5));
		roomList.get(2).addExit(new Coordinate(9, 7));
		roomList.get(2).addExit(new Coordinate(14, 7));
		roomList.get(2).addExit(new Coordinate(15, 5));
		roomList.get(3).addExit(new Coordinate(19, 5));
		roomList.get(4).addExit(new Coordinate(18, 9));
		roomList.get(4).addExit(new Coordinate(22, 12));
		roomList.get(5).addExit(new Coordinate(20, 14));
		roomList.get(5).addExit(new Coordinate(17, 16));
		roomList.get(6).addExit(new Coordinate(17, 21));
		roomList.get(7).addExit(new Coordinate(14, 20));
		roomList.get(7).addExit(new Coordinate(12, 18));
		roomList.get(7).addExit(new Coordinate(11, 18));
		roomList.get(8).addExit(new Coordinate(6, 19));
		roomList.get(9).addExit(new Coordinate(6, 15));
		roomList.get(9).addExit(new Coordinate(7, 12));
		roomList.get(6).addPassage(KITCHEN);
		roomList.get(3).addPassage(LOUNGE);
		roomList.get(8).addPassage(CONSERVATORY);
	}
	
	public void addWall(int x, int y) {
		board[x][y] = new Wall();		
	}
	public void addHallway(int x, int y) {
		board[x][y] = new Hallway(new Coordinate(x, y));
	}
	public void addRoom(int x, int y, int room){
		board[x][y] = roomList.get(room);
	}
	
	public void printBoard(){
		for (int i = 0; i < board[0].length; i++){
			for (int j = 0; j < board.length; j++){
				boolean isPlayer = false;
				boolean isRoom = false;
				for (Player p: playerList){
					if (p.at(new Coordinate(j, i))){
						System.out.print("P");
						isPlayer = true;
					}
				}
				if (!isPlayer){
					for (Room r: roomList){
						for (Coordinate c: r.getExits()){
							if (c.getX() == j && c.getY() == i){
								System.out.print("X");
								isRoom = true;
							}
						}
					}
				}
				if (!isPlayer && !isRoom){
					if (board[j][i] instanceof Wall){
						System.out.print("W");
					} else if (board[j][i] instanceof Room){
						System.out.print("E");
					} else if (board[j][i] instanceof Hallway){
						System.out.print(" ");
					}
				}
			}
			System.out.println("");
		}
	}
	
	private boolean[][] aStarBoard(){
		boolean[][] aStarBoard = new boolean[board.length][board[0].length];
		for (int i = 0; i < board[0].length; i++){
			for (int j = 0; j < board.length; j++){
				if (board[j][i] instanceof Hallway || board[j][i] instanceof Room){
					aStarBoard[j][i] = true;
				} else {
					aStarBoard[j][i] = false;
				}
			}
		}
		for (Player p: playerList){
			Coordinate loc = p.getCoords();
			aStarBoard[loc.getX()][loc.getY()] = false;
		}
		return aStarBoard;
	}

}
