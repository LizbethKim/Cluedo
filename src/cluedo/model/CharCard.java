package cluedo.model;

public class CharCard implements Card{

	private int card;
	/**
	 * Character card
	 * @param card Card enum
	 */
	public CharCard(int card){
		this.card = card;
	}

	public int getCard(){
		return card;
	}
}
