package cluedo.models;

public class CharCard implements Card{

	private int card;
	
	public CharCard(int card){
		this.card = card;
	}
	
	public int getCard(){
		return card;
	}
}
