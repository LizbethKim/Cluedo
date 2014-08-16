package cluedo.ui;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import cluedo.board.Board;

public class SuggestDialog {
	private JPanel panel;
	private JComboBox<String> characters;
	private JComboBox<String> weapons;
	private JComboBox<String> rooms;
	private boolean accusation;
	/**
	 * @param room is the room that the suggestion is being made from. 
	 * 0 if this is an accusation
	 */
	public SuggestDialog(int room) {
		panel = new JPanel();
		panel.add(new JLabel("It was "));
		DefaultComboBoxModel<String> charStrings = new DefaultComboBoxModel<String>();
		charStrings.addElement(CluedoUI.asString(Board.SCARLETT));
		charStrings.addElement(CluedoUI.asString(Board.MUSTARD));
		charStrings.addElement(CluedoUI.asString(Board.GREEN));
		charStrings.addElement(CluedoUI.asString(Board.WHITE));
		charStrings.addElement(CluedoUI.asString(Board.PEACOCK));
		charStrings.addElement(CluedoUI.asString(Board.PLUM));
		characters = new JComboBox<String>(charStrings);
		panel.add(characters);
		panel.add(new JLabel(" with the "));
		DefaultComboBoxModel<String> weaponStrings = new DefaultComboBoxModel<String>();
		weaponStrings.addElement(CluedoUI.asString(Board.SPANNER));
		weaponStrings.addElement(CluedoUI.asString(Board.CANDLESTICK));
		weaponStrings.addElement(CluedoUI.asString(Board.ROPE));
		weaponStrings.addElement(CluedoUI.asString(Board.PIPE));
		weaponStrings.addElement(CluedoUI.asString(Board.REVOLVER));
		weaponStrings.addElement(CluedoUI.asString(Board.DAGGER));
		weapons = new JComboBox<String>(weaponStrings);
		panel.add(weapons);
		panel.add(new JLabel(" in the "));
		DefaultComboBoxModel<String> roomStrings = new DefaultComboBoxModel<String>();
		if (room == 0) {
			accusation = true;
			roomStrings.addElement(CluedoUI.asString(Board.STUDY));
			roomStrings.addElement(CluedoUI.asString(Board.DINING));
			roomStrings.addElement(CluedoUI.asString(Board.BILLARD));
			roomStrings.addElement(CluedoUI.asString(Board.LOUNGE));
			roomStrings.addElement(CluedoUI.asString(Board.BALLROOM));
			roomStrings.addElement(CluedoUI.asString(Board.KITCHEN));
			roomStrings.addElement(CluedoUI.asString(Board.HALL));
			roomStrings.addElement(CluedoUI.asString(Board.LIBRARY));
			roomStrings.addElement(CluedoUI.asString(Board.CONSERVATORY));
		} else {
			roomStrings.addElement(CluedoUI.asString(room));
		}
		rooms = new JComboBox<String>(roomStrings);
		panel.add(rooms);		
	}
	
	public int getGuess() {
		if (accusation) {
			JOptionPane.showMessageDialog(null, panel, "Make an accusation:", JOptionPane.PLAIN_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(null, panel, "Make a suggestion:", JOptionPane.PLAIN_MESSAGE);
		}
		return CluedoUI.asInt(characters.getSelectedItem().toString()) 
				+ CluedoUI.asInt(weapons.getSelectedItem().toString())
				 + CluedoUI.asInt(rooms.getSelectedItem().toString());
	}

}
