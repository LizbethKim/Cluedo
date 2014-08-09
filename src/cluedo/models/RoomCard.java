package cluedo.models;

public class RoomCard implements Card{

	private int room;
	
	public RoomCard(int room){
		this.room = room;
	}
	
	public int getCard(){
		return room;
	}
}
