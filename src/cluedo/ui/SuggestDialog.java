package cluedo.ui;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import cluedo.board.Board;

public class SuggestDialog {
	private JFrame parent;
	private JPanel panel;
	private JComboBox<String> characters;
	private JComboBox<String> weapons;
	private JComboBox<String> rooms;
	private boolean accusation;
	
	/**
	 * @param room is the room that the suggestion is being made from. 
	 * 0 if this is an accusation
	 */
	public SuggestDialog(JFrame parent, int room) {
		this.parent = parent;
		
		DefaultComboBoxModel<String> charStrings = new DefaultComboBoxModel<String>();
		charStrings.addElement(CluedoUI.asString(Board.SCARLETT));
		charStrings.addElement(CluedoUI.asString(Board.MUSTARD));
		charStrings.addElement(CluedoUI.asString(Board.GREEN));
		charStrings.addElement(CluedoUI.asString(Board.WHITE));
		charStrings.addElement(CluedoUI.asString(Board.PEACOCK));
		charStrings.addElement(CluedoUI.asString(Board.PLUM));
		characters = new JComboBox<String>(charStrings);
		
		DefaultComboBoxModel<String> weaponStrings = new DefaultComboBoxModel<String>();
		weaponStrings.addElement(CluedoUI.asString(Board.SPANNER));
		weaponStrings.addElement(CluedoUI.asString(Board.CANDLESTICK));
		weaponStrings.addElement(CluedoUI.asString(Board.ROPE));
		weaponStrings.addElement(CluedoUI.asString(Board.PIPE));
		weaponStrings.addElement(CluedoUI.asString(Board.REVOLVER));
		weaponStrings.addElement(CluedoUI.asString(Board.DAGGER));
		weapons = new JComboBox<String>(weaponStrings);
		
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
		
		panel = new JPanel();
		panel.add(new JLabel("It was "));
		panel.add(characters);
		panel.add(new JLabel(" with the "));
		panel.add(weapons);
		panel.add(new JLabel(" in the "));
		panel.add(rooms);		
	}
	
	/**
	 * Actually displays the dialog. Returns a 3-digit number to represent
	 * the guess that's been selected.
	 * @return
	 */
	public int getGuess() {
		if (accusation) {
			JOptionPane.showMessageDialog(parent, panel, "Make an accusation:", JOptionPane.PLAIN_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(parent, panel, "Make a suggestion:", JOptionPane.PLAIN_MESSAGE);
		}
		return CluedoUI.asInt(characters.getSelectedItem().toString()) 
				+ CluedoUI.asInt(weapons.getSelectedItem().toString())
				 + CluedoUI.asInt(rooms.getSelectedItem().toString());
	}
}
