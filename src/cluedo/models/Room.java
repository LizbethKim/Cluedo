package cluedo.models;

import java.util.List;


public class Room implements Square{
	private int name;
	private int cards;
	private int passage;
	private List<Exit> exits;

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
	}

	public int getName(){
		return name;
	}
	
	public int getCards(){
		return cards;
	}
	
	public void addCard(){
		
	}
	
	public void addPassage(int x){
		passage = x;
	}
	
	public int getPassage(){
		return passage;
	}
}
