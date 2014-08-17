package cluedo.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import cluedo.AStar;
import cluedo.Coordinate;

public class Board {

	//Success/Failure
	public static final int NOTHING = 0;
	public static final int SUCCESS = 7;
	public static final int INVALIDCARD = 8;
	public static final int FAIL = 9;

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
	public static final int MIDDLE = 1000;


	private Square[][] board;
	private List<Room> roomList;
	private List<Coordinate> roomCoords;
	private List<Player> playerList;
	private List<Player> charList;
	private int numPlayers;
	private int solution;
	private int currentPlayer;
	private int currentMove;
	private int currentSuggest;		//Current set of cards being suggested
	private int refutePlayer;		//Used for when going around to refute a suggestion/accusation
	private boolean[][] aStarBoard;

	/*
	 * States: -1 = Initialization
	 * 0 = Start Turn (Roll dice/Secret Passage/Make accusation)
	 * 1 = Move
	 * 2 = Suggest/Accuse
	 * 3 = Refute
	 * 5 = Game is over
	 */
	private int currentState;

	public Board(int x, int y){
		board = new Square[x][y];
		currentState = -1;
		currentSuggest = 0;
		roomList = new ArrayList<Room>();
		playerList = new ArrayList<Player>();
		charList = new ArrayList<Player>();
		roomCoords = new ArrayList<Coordinate>();
		populateCharList();
		generateRoomCoords();
		generateRoomList();
	}

	//Return moves remaining for the player
	public int getMovesLeft(){
		return currentMove;
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


	/*
	 * Sets the number of players, sorts players into order, sets current player
	 * creates new "Can go" map, sets state to first state.
	 */
	@SuppressWarnings("unchecked")
	public boolean startGame(){
		if (currentState != -1) return false;
		Collections.sort(playerList);
		numPlayers = playerList.size();
		currentState = 0;
		currentPlayer = 0;
		refutePlayer = 0;
		aStarBoard = aStarBoard();
		cardDistribution();
		return true;
	}

	public List<Integer> getPlayerCards(int chara){
		List<Integer> list = new ArrayList<Integer>();
		for (Player p:playerList){
			if (p.getChar() == chara){
				for (Card c:p.getCards()){
					list.add(c.getCard());
				}
				return list;
			}
		}
		return null;
	}

	//Returns the character ENUM for the current player
	public int currentPlayer(){
		return playerList.get(currentPlayer).getChar();
	}

	public int getRefutePlayer(){
		return playerList.get(refutePlayer).getChar();
	}

	//Returns the player coords of the player ENUM
	public Coordinate getPlayerCoords(int player){
		return charList.get(player).getCoords();
	}

	//This may be confusing, but was mainly used for testing purposes. Please ignore.
	public Player getCurrentPlayer(){
		return playerList.get(currentPlayer);
	}

	//Returns a list of Coordinates of each room
	public List<Coordinate> getRoomCoords(){
		return roomCoords;
	}

	//Returns the list of cards a player currently possesses as ENUMs
	public List<Integer> getPlayerCards(){
		List<Integer> playerCards = new ArrayList<Integer>();
		for (Card c:playerList.get(currentPlayer).getCards()){
			playerCards.add(c.getCard());
		}
		return playerCards;
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
					playerList.get(currentPlayer).setCoords(roomCoords.get(convertRoom(passage)));
					moveState();
					moveState();
					return true;
				}
			}
		}
		return false;
	}
	
	public void addMiddleRoom(int x, int y) {
		board[x][y] = new UnreachableRoom();
	}

	//Returns the current suggest attempt
	public int currentSuggest(){
		if (currentState == 2 || currentState == 3) {return currentSuggest;}
		return NOTHING;
	}

	public boolean canSuggest(){
		return playerList.get(currentPlayer).isSuggestable();
	}

	/*
	 * Suggest mechanism, suggest an ENUM combination (3 numbers). Returns true if it's valid to suggest at the time
	 * else returns false if invalid. CAN ONLY SUGGEST IN STATE 2 -AFTER- the movement.
	 */
	public boolean suggest(int suggestion){
		if (currentState == 2 || (currentState == 0 && playerList.get(currentPlayer).isSuggestable())){
			//Ensure that the suggestion is valid
			if (findRoom(suggestion) >= 100 && findRoom(suggestion) <= 900 && findWeapon(suggestion) >= 10 && findWeapon(suggestion) <= 60 && findChar(suggestion) >= 1 && findChar(suggestion) <= 6){
				playerList.get(currentPlayer).suggestable(false);
				Player accused = getPlayer(findChar(suggestion));
				accused.suggestable(true);
				accused.setCoords(roomCoords.get(convertRoom(suggestion)));
				accused.setRoom(findRoom(suggestion));
				this.currentSuggest = suggestion;
				refutePlayer = (currentPlayer + 1)%numPlayers;
				currentState = 3;
				return true;
			}
		}
		return false;
	}

	public int getSoln(){
		return solution;
	}

	/**
	 *
	 * @param suggestion
	 * @return SUCCESS if a correct guess, FAIL if incorrect, NOTHING if the
	 * guess was an invalid guess or made at an invalid time.
	 */
	public int accuse(int suggestion){
		if (currentState == 0 || currentState == 2){
			if (findRoom(suggestion) >= 100 && findRoom(suggestion) <= 900 && findWeapon(suggestion) >= 10 && findWeapon(suggestion) <= 60 && findChar(suggestion) >= 1 && findChar(suggestion) <= 6){
				if (suggestion == solution){
					gameEnd();
					return SUCCESS;
				}
				playerList.get(currentPlayer).remove();
				for (Player p:playerList){
					if (p.playable()) return FAIL;
				}
				currentState = 5;
				return FAIL;
			}
			return NOTHING;
		}
		return NOTHING;
	}

	//If not given an argument, getRoom() returns the room of the current player. ENUM of room or NOTHING
	public int getRoom(){
		return playerList.get(currentPlayer).currentRoom();
	}

	//If given coords, checks whether that's a room and returns the room's ENUM or NOTHING
	public int getRoom(Coordinate coord){
		if (board[coord.getX()][coord.getY()] instanceof Room){
			return ((Room) board[coord.getX()][coord.getY()]).getName();
		} else if (board[coord.getX()][coord.getY()] instanceof UnreachableRoom){
			return 1000;
		} else {
			return NOTHING;
		}
	}

	public Set<Coordinate> getPossibleMoves(){
		//System.out.println(currentMove);
		List<Integer> roomsVisited = new ArrayList<Integer>();
		if (playerList.get(currentPlayer).currentRoom() != 0){
			List<Coordinate> roomExits = roomList.get(convertRoom(playerList.get(currentPlayer).currentRoom())).getExits();
			Set<Coordinate> roomHighlight = new HashSet<Coordinate>();
			for (Coordinate c:roomExits){
				boolean[][] aSt = new boolean[board.length][board[0].length];
				for (int i = 0; i < board.length; i++){
					for (int j = 0; j < board[0].length; j++){
						aSt[i][j] = aStarBoard[i][j];
					}
				}
				aSt[c.getX()][c.getY()] = false;
				roomHighlight.addAll(recurseMoves(c, 0, aSt, roomsVisited));
			}
			return roomHighlight;
		}
		boolean[][] aSt = new boolean[board.length][board[0].length];
		for (int i = 0; i < board.length; i++){
			for (int j = 0; j < board[0].length; j++){
				aSt[i][j] = aStarBoard[i][j];
			}
		}
		return recurseMoves(playerList.get(currentPlayer).getCoords(), 0, aSt, roomsVisited);
	}

	
	/*
	 * Refuting mechanism. Will take a card ENUM, and return a NOTHING if the player refuted nothing (passing)
	 * or the card ENUM if the refute is successful, or SUCCESS if the player's suggestion goes through. If it's
	 * not the correct state to refute, returns FAIL
	 */
	public int refute(int cardNum){
		if (currentState != 3) return FAIL;
		if (cardNum == 0) {
			moveRefute();
			if (refutePlayer == currentPlayer){
				return SUCCESS;
			}
			return NOTHING;
		}
		if (playerList.get(refutePlayer).hasCard(cardNum)){
			if (findRoom(currentSuggest) == findRoom(cardNum)) {
				return findRoom(currentSuggest);
			} else if (findWeapon(currentSuggest) == findWeapon(cardNum)){
				return findWeapon(currentSuggest);
			} else if (findChar(currentSuggest) == findChar(cardNum)){
				return findChar(currentSuggest);
			}
		}
		moveRefute();
		if (refutePlayer == currentPlayer){
			return SUCCESS;
		}
		return INVALIDCARD;
	}

	//Starts the next turn. Should only be called by Board.
	public void nextTurn(){
		if (currentState == 5) return;
		int tempCurrentPlayer = currentPlayer;
		movePlayer();
		while (!playerList.get(currentPlayer).playable() && currentPlayer != tempCurrentPlayer){
			movePlayer();
		}
		if (currentPlayer == tempCurrentPlayer && !playerList.get(currentPlayer).playable()){
			gameEnd();
			return;
		}
		aStarBoard = aStarBoard();
		currentState = 0;
	}

	public void gameEnd(){
		currentState = 5;
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
			moveState();
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
			playerList.add(charList.get(chara));
			charList.get(chara).add();
			return true;
		}
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

	public int move(Coordinate endPoint){
		if (!(endPoint.getX() >= 0) && !(endPoint.getX() < aStarBoard.length) && !(endPoint.getY() >= 0) && !(endPoint.getY() < aStarBoard[0].length)) return FAIL;
		if (!aStarBoard[endPoint.getX()][endPoint.getY()]) return FAIL; //If it's impossible to move to the end position returns fail
		Coordinate start;
		if (playerList.get(currentPlayer).currentRoom() != 0){
			Room currentRoom = getRoom(playerList.get(currentPlayer).currentRoom()); //If player is in a room, it will automatically pick the best exit to leave to get to the position
			start = bestExit(currentRoom, endPoint);
		} else {
			start = playerList.get(currentPlayer).getCoords(); //Otherwise start position is the current coords
		}
		if (board[endPoint.getX()][endPoint.getY()] instanceof Room){
			if (board[start.getX()][start.getY()].equals(board[endPoint.getX()][endPoint.getY()])) return FAIL;
			Room endRoom = (Room) board[endPoint.getX()][endPoint.getY()];
			endPoint = bestExit(endRoom, start);
		}
		AStar path = aStar(start, endPoint);
		if (path.getLength() > currentMove) return FAIL; //If shortest distance to coordinate exceeds move distance, return fail
		AStar temp = path;
		while (temp != null){
			aStarBoard[path.getCoords().getX()][path.getCoords().getY()] = false; //Makes path taken untravellable if needing multiple moves
			temp = temp.getParent();
		}
		if (board[endPoint.getX()][endPoint.getY()] instanceof Room){
			//Sets Player's coordinates to the middle of the Room
			playerList.get(currentPlayer).setCoords(roomCoords.get(convertRoom(((Room) board[endPoint.getX()][endPoint.getY()]).getName())));
			playerList.get(currentPlayer).setRoom(((Room) board[endPoint.getX()][endPoint.getY()]).getName());
			currentMove = 0;
		} else {
			currentMove -= path.getLength(); //Removes the distance traveled from the move pool
			playerList.get(currentPlayer).setCoords(endPoint); //Updates player coordinates
			playerList.get(currentPlayer).setRoom(NOTHING);
		}
		if (currentMove == 0) { //If no more movement is possible, moves the state and returns success
			moveState();
			return SUCCESS;
		}
		return NOTHING; //If more movement is possible, returns 0 assuming more move commands will be given in the future.
	}


	/*
	 * Simple A* Algorithm, returns the path if possible, else null. Should never null as all the logic
	 * should be checked by the move command.
	 */
	public AStar aStar(Coordinate startPoint, Coordinate endPoint){
		boolean[][] tempStarBoard = aStarBoard();
		AStar root = new AStar(null, 0, getDistance(startPoint, endPoint),startPoint);
		Queue<AStar> fringe = new PriorityQueue<AStar>();
		fringe.add(root);
		AStar temp = null;
		tempStarBoard[startPoint.getX()][startPoint.getY()] = true;
		while (!fringe.isEmpty()){
			temp = fringe.poll();
			Coordinate node = temp.getCoords();
			if (!visited(node, tempStarBoard)){
				tempStarBoard[node.getX()][node.getY()] = false;
				if (node.equals(endPoint)){
					return temp;
				}
				List<Coordinate> exits = new ArrayList<Coordinate>();
				if (node.getX() - 1 >= 0 && tempStarBoard[node.getX()-1][node.getY()]){
					exits.add(new Coordinate(node.getX()-1, node.getY()));
				}
				if (node.getX() + 1 < board.length && tempStarBoard[node.getX()+1][node.getY()]){
					exits.add(new Coordinate(node.getX()+1, node.getY()));
				}
				if (node.getY() - 1 >= 0 && tempStarBoard[node.getX()][node.getY()-1]){
					exits.add(new Coordinate(node.getX(), node.getY()-1));
				}
				if (node.getY() + 1 < board[0].length && tempStarBoard[node.getX()][node.getY() + 1]){
					exits.add(new Coordinate(node.getX(), node.getY() + 1));
				}
				for (Coordinate c:exits){
					fringe.add(new AStar(temp, temp.getLength() + 1, getDistance(c, endPoint), c));
				}
			}
		}
		return null;
	}
	
	//Beware: Dragons. And private classes after this point.

	private boolean visited(Coordinate coords, boolean[][] aStarBoard){
		return !(aStarBoard[coords.getX()][coords.getY()]);
	}

	private Coordinate bestExit(Room room, Coordinate endPoint){
		int min = Integer.MAX_VALUE;
		Coordinate bestExit = new Coordinate(0, 0);
		for (Coordinate exit:room.getExits()){
			if (getDistance(exit, endPoint) < min){
				min = getDistance(exit, endPoint);
				bestExit = exit;
			}
		}
		return bestExit;
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

	
	private Set<Coordinate> recurseMoves(Coordinate coords, int depth, boolean[][] aStar, List<Integer> roomsVisited){
		Set<Coordinate> current = new HashSet<Coordinate>();
		if (!coords.equals(playerList.get(currentPlayer).getCoords())){
			if (board[coords.getX()][coords.getY()] instanceof Room){
				if (!roomsVisited.contains(((Room) board[coords.getX()][coords.getY()]).getName())){
					current.add(coords);
					roomsVisited.add(((Room) board[coords.getX()][coords.getY()]).getName());
				}
			} else {
				current.add(coords);
			}
		}
		if (depth == currentMove) return current;
		aStar[coords.getX()][coords.getY()] = false;
		if (coords.getX() + 1 < board.length && aStar[coords.getX()+1][coords.getY()]){
			boolean[][] aSt = new boolean[board.length][board[0].length];
			for (int i = 0; i < board.length; i++){
				for (int j = 0; j < board[0].length; j++){
					aSt[i][j] = aStar[i][j];
				}
			}
			Set<Coordinate> temp = recurseMoves(new Coordinate(coords.getX()+1, coords.getY()), depth+1, aSt, roomsVisited);
			current.addAll(temp);
		}
		if (coords.getX() - 1 >= 0 && aStar[coords.getX()-1][coords.getY()]){
			boolean[][] aSt = new boolean[board.length][board[0].length];
			for (int i = 0; i < board.length; i++){
				for (int j = 0; j < board[0].length; j++){
					aSt[i][j] = aStar[i][j];
				}
			}
			Set<Coordinate> temp = recurseMoves(new Coordinate(coords.getX()-1, coords.getY()), depth+1, aSt, roomsVisited);
			current.addAll(temp);
		}
		if (coords.getY() + 1 < board[0].length && aStar[coords.getX()][coords.getY()+1]){
			boolean[][] aSt = new boolean[board.length][board[0].length];
			for (int i = 0; i < board.length; i++){
				for (int j = 0; j < board[0].length; j++){
					aSt[i][j] = aStar[i][j];
				}
			}
			Set<Coordinate> temp = recurseMoves(new Coordinate(coords.getX(), coords.getY() + 1), depth+1, aSt, roomsVisited);
			current.addAll(temp);
		}
		if (coords.getY() - 1 >= 0 && aStar[coords.getX()][coords.getY()-1]){
			boolean[][] aSt = new boolean[board.length][board[0].length];
			for (int i = 0; i < board.length; i++){
				for (int j = 0; j < board[0].length; j++){
					aSt[i][j] = aStar[i][j];
				}
			}
			Set<Coordinate> temp = recurseMoves(new Coordinate(coords.getX(), coords.getY() - 1), depth+1, aSt, roomsVisited);
			current.addAll(temp);
		}
		return current;
	}

	private int getDistance(Coordinate startPoint, Coordinate endPoint){
		return (Math.abs(startPoint.getX() - endPoint.getX()) + Math.abs(startPoint.getY() - endPoint.getY()));
	}

	private Player addPlayer(int chara, boolean player){
		if (currentState != -1){
			return null;
		} else {
			switch(chara){
				case SCARLETT:
					return new Player(SCARLETT, 7, 24, player);
				case MUSTARD:
					return new Player(MUSTARD, 0, 17, player);
				case WHITE:
					return new Player(WHITE, 9, 0, player);
				case GREEN:
					return new Player(GREEN, 14, 0, player);
				case PEACOCK:
					return new Player(PEACOCK, 23, 6, player);
				case PLUM:
					return new Player(PLUM, 23, 19, player);
			}
			return null;
		}
	}

	private void generateRoomCoords(){
		roomCoords.add(new Coordinate(0, 0)); //Skipping 0 for aesthetics purposes
		roomCoords.add(new Coordinate(2, 4));
		roomCoords.add(new Coordinate(12, 4));
		roomCoords.add(new Coordinate(21, 2));
		roomCoords.add(new Coordinate(21, 10));
		roomCoords.add(new Coordinate(21, 16));
		roomCoords.add(new Coordinate(21, 23));
		roomCoords.add(new Coordinate(12, 21));
		roomCoords.add(new Coordinate(3, 21));
		roomCoords.add(new Coordinate(3, 12));
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


	private void populateCharList(){
		charList.add(new Player(0, 0, 0, false));
		for (int i = 1; i < 7; i++){
			charList.add(addPlayer(i, false));
		}
	}

	private void cardDistribution(){
		List<Card> allCards = new ArrayList<Card>();
		int chara = (int) ((Math.random()*6)+1);
		for (int i = 1; i < 7; i++){
			if (i == chara) continue;
			allCards.add(new CharCard(i));
		}
		int weapon = (int) ((Math.random()*6)+1)*10;
		for (int i = 10; i < 70; i = i + 10){
			if (i == weapon) continue;
			allCards.add(new WeaponCard(i));
		}
		int roome = (int) ((Math.random()*9)+1)*100;
		for (int i = 100; i < 1000; i = i + 100){
			if (i == roome) continue;
			allCards.add(new RoomCard(i));
		}
		solution = chara + weapon + roome;
		Collections.shuffle(allCards);
		int i = 0;
		for (Card c : allCards){
			playerList.get(i%numPlayers).addCard(c);
			i++;
		}
	}
	//For automating retrieving things from lists
	private Player getPlayer(int character){
		for (Player p:charList){
			if (p.getChar() == character) return p;
		}
		return null;
	}

	private Room getRoom(int room){
		for (Room r:roomList){
			if (r.getName() == room) return r;
		}
		return null;
	}

}