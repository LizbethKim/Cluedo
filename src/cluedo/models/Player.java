package cluedo.models;

import java.util.List;

import cluedo.Coordinate;

public class Player implements Comparable{

	private int character;
	private Coordinate coords;
	private List<Card> cards;
	
	public Player(int character, int x, int y){
		this.character = character;
		coords = new Coordinate(x, y);
	}
	
	public int getChar(){
		return character;
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
	
}
