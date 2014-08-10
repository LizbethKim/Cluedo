package cluedo.models;

import java.util.ArrayList;
import java.util.List;

import cluedo.Coordinate;


public class Room implements Square{
	private int name;
	private List<Card> cards;
	private int passage;
	private List<Coordinate> exits;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + name;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Room other = (Room) obj;
		if (name != other.name)
			return false;
		return true;
	}

	public Room(int name){
		this.name = name;
		this.passage = 0;
		cards = new ArrayList<Card>();
		exits = new ArrayList<Coordinate>();
	}

	public void addExit(Coordinate coord){
		exits.add(coord);
	}
	
	public List<Coordinate> getExits(){
		return exits;
	}
	
	public int getName(){
		return name;
	}
	
	public List<Card> getCards(){
		return cards;
	}
	
	public void addCard(Card card){
		cards.add(card);
	}
	
	public void addPassage(int x){
		passage = x;
	}
	
	public int getPassage(){
		return passage;
	}

}
