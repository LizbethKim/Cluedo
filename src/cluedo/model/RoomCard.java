package cluedo.model;

public class RoomCard implements Card{

	private int room;

	/**
	 * Room card
	 * @param room Room ENUM
	 */
	public RoomCard(int room){
		this.room = room;
	}

	public int getCard(){
		return room;
	}
}
