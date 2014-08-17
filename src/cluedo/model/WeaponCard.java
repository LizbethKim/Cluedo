package cluedo.model;

public class WeaponCard implements Card{

	private int card;

	/**
	 * Weapon card
	 * @param card Weapon ENUM
	 */
	public WeaponCard(int card){
		this.card = card;
	}

	public int getCard(){
		return card;
	}
}
