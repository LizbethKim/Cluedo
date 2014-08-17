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

	/**
	 * Creates a new instance of the Board. This initializes the playerLists, charLists, Coordinates
	 * of the rooms, current board state and everything required to play the game.
	 * @param x Board Size X
	 * @param y Board Size Y
	 */
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

	//Return moves remaining for the player. Didn't javadoc because self explanatory (According to google JavaDoc guidelines)
	public int getMovesLeft(){
		return currentMove;
	}

	/**
	 * Gives a terminal representation for the board. Helps with debugging without a UI
	 */
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


	/**
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



	/**
	 * Must ensure that the room passages are set correctly before attempting this.
	 * This will let the player take the passage on state 0 (before a dice roll)
	 * if there is one in the room. Otherwise the player will not be able to move
	 * and state doesn't change.
	 * @return Success/Failure
	 */
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

	/**
	 * When a player gets moved to a room because of a suggestion, they should be able
	 * to make a suggestion rather than move for their turn. This returns whether this
	 * is possible or not.
	 * @return Whether the player can suggest or not.
	 */
	public boolean canSuggest(){
		return playerList.get(currentPlayer).isSuggestable();
	}

	/**
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
	 * Starts the accusation process. If the state is 0 or after movement, you can accuse.
	 * During the accusation process, it will determine whether the accusation was a valid
	 * one by checking the card ranges. Upon being a valid accusation, it will then see if
	 * the suggestion was correct and end the game, or remove the player from the game if
	 * incorrect.
	 * @param suggestion 3 digit unique ENUM for the card combinations
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

	/**
	 * Checks whether the coordinate's a room and returns the room's ENUM or NOTHING
	 * @param coord Coordinates of the place you want to check
	 * @return
	 */
	public int getRoom(Coordinate coord){
		if (board[coord.getX()][coord.getY()] instanceof Room){
			return ((Room) board[coord.getX()][coord.getY()]).getName();
		} else if (board[coord.getX()][coord.getY()] instanceof UnreachableRoom){
			return 1000;
		} else {
			return NOTHING;
		}
	}

	/**
	 * For generating the "Highlighted" movable area on the map. Given a certain distance
	 * it will return a set of coordinates that are reachable from the current player's
	 * position.
	 * @return Set of Coordinates that player can reach
	 */
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

	/**
	 * Refuting mechanism. Will take a card ENUM and return a NOTHING if the player refuted nothing (passing)
	 * or the card's ENUM if the refute is successful, or SUCCESS if the player's suggestion goes through. If it's
	 * not the correct state to refute, returns FAIL.
	 * @param cardNum Card's ENUM value
	 * @return Fail/Success/Nothing result of the refute attempt
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

	/**
	 * Starts the next turn. Assumes that the UI is aware of the state and will not
	 * call nextTurn() improperly. Also just returns if the game is over.
	 */
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

	/**
	 * Takes in a dice roll from the UI. It's generated similarly to the way a real player would roll
	 * the dice. The roll is taken from the UI and stored in the "currentMove" field.
	 * @param roll Dice roll given by UI
	 * @return Success or Failure of the dice roll based on state
	 */
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

	/**
	 * Adds the character to the current game. Assumes multiples will not be sent to the board
	 * (Handled by a JDialog panel).
	 * @param chara The character's ENUM
	 * @return Success/Fail based on current state.
	 */
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
		board[x][y] = new Hallway();
	}
	public void addRoom(int x, int y, int room){
		board[x][y] = roomList.get(room);
	}

	/**
	 * Checks whether the move is a valid distance, then attempts to AStar. Upon success, updates the player's coordinates
	 * and returns SUCCESS. Also detects whether there are more moves possible, upon which it will return NOTHING, or FAIL
	 * if it's an invalid move location or too far. When moving into/out of a room, it will automatically pick the best
	 * exit to leave/enter the room. Also doesn't allow the player to move into the same room.
	 * @param endPoint The coordinates to attempt to move to
	 * @return Results of the move attempt. SUCCESS/FAIL/NOTHING
	 */
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


	/**
	 * Simple A* algorithm from start to finish.
	 * @param startPoint Start point of the A*
	 * @param endPoint End point of the A*
	 * @return AStar object that contains the path from start to finish and length
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

	/**
	 * Finds the best exit out of a room
	 * @param room Current room
	 * @param endPoint The coordinate you want to travel to/from
	 * @return Coordinates of the best exit
	 */
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

	/**
	 * This is very long. But essentially it's a recursive breadth (tehehe bread) first search from the origin
	 * with the terminating depth being the current move pool.
	 * @param coords Current Coordinates
	 * @param depth Current depth
	 * @param aStar a 2d array of booleans representing whether you can move to that location or not
	 * @param roomsVisited Ensures you don't highlight the rest of the room, or revisit the room you're currently in
	 * @return Set of Coordinates you can go to from current coordinate
	 */
	private Set<Coordinate> recurseMoves(Coordinate coords, int depth, boolean[][] aStar, List<Integer> roomsVisited){
		Set<Coordinate> current = new HashSet<Coordinate>();
		if (!coords.equals(playerList.get(currentPlayer).getCoords())){ //Checks that it's not the original coordinate player started on
			if (board[coords.getX()][coords.getY()] instanceof Room){
				if (!roomsVisited.contains(((Room) board[coords.getX()][coords.getY()]).getName())){ //Ensures that the room hasn't been visited yet
					current.add(coords);
					roomsVisited.add(((Room) board[coords.getX()][coords.getY()]).getName()); //Adds to the rooms visited node
				}
			} else {
				current.add(coords);
			}
		}
		if (depth == currentMove) return current;
		aStar[coords.getX()][coords.getY()] = false; //Sets flag to false so you don't revisit this coordinate
		if (coords.getX() + 1 < board.length && aStar[coords.getX()+1][coords.getY()]){
			boolean[][] aSt = new boolean[board.length][board[0].length];
			for (int i = 0; i < board.length; i++){ //Deep clone of the current 2d array to recurse
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

	/**
	 * Just returns the heuristic (absolute distance) from one point to another
	 * @param startPoint Starting point
	 * @param endPoint End point
	 * @return Integer of how many squares away it is
	 */
	private int getDistance(Coordinate startPoint, Coordinate endPoint){
		return (Math.abs(startPoint.getX() - endPoint.getX()) + Math.abs(startPoint.getY() - endPoint.getY()));
	}

	/**
	 * Adding player to the game. Adds a character through their enum. Boolean guard there to see whether it's
	 * a player character or a non-player object
	 * @param chara Character Enum
	 * @param player Boolean guard for playability
	 * @return Returns the player object that was created
	 */
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

	/**
	 * Generates room coordinates for the center of the room. Skips 0 for aesthetics to make
	 * it relate to the room ENUMs
	 */
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

	/**
	 * Creates the roomlist, which contains every room and all their exits. Hard coded for CLUEDO
	 */
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

	/**
	 * Creates a new 2d array of all the places that can currently be travelled on the board
	 * @return 2d Array of the travellable places
	 */
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


	/**
	 * Creates the character list of all characters. Player list just handles playable characters.
	 */
	private void populateCharList(){
		charList.add(new Player(0, 0, 0, false));
		for (int i = 1; i < 7; i++){
			charList.add(addPlayer(i, false));
		}
	}

	/**
	 * Distributes the cards and picks 3 random ones to be the solution for the game.
	 */
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