package cluedo.model;

public class WeaponCard implements Card{

	private int card;
	
	public WeaponCard(int card){
		this.card = card;
	}
	
	public int getCard(){
		return card;
	}
}
