package cluedo.models;

import java.util.List;

import cluedo.Coordinate;
import cluedo.board.Board;

public class Player implements Comparable{

	private int character;
	private Coordinate coords;
	private List<Card> cards;
	private int room;
	private boolean playable;
	
	public Player(int character, int x, int y, boolean playable){
		this.character = character;
		coords = new Coordinate(x, y);
		room = Board.NOTHING;
		this.playable = playable;
	}
	
	public int getChar(){
		return character;
	}
	
	public void remove(){
		playable = false;
	}
	
	public List<Card> getCards(){
		return cards;
	}
	
	public boolean hasCard(int card){
		for (Card c: cards){
			if (c.getCard() == card){
				return true;
			}
		}
		return false;
	}
	
	public void addCard(Card card){
		cards.add(card);
	}
	
	public int currentRoom(){
		return room;
	}
	
	public void setRoom(int room){
		this.room = room;
	}
	
	@Override
	public int compareTo(Object o) {
		if (!(o instanceof Player)){throw new UnsupportedOperationException();}
		return (int) Math.signum(this.character-((Player) o).getChar());
	}
	
	public Coordinate getCoords(){
		return coords;
	}

	public boolean at(Coordinate coordinate) {
		if (coords.equals(coordinate)) return true;
		return false;
	}

	public void setCoords(Coordinate coordinate) {
		this.coords = coordinate;
	}
	
}
