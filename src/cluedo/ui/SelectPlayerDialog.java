package cluedo.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

/**
 * The dialog at the start of the game to choose characters.
 * Takes a map from String to Integer and populates it with
 * player names and the corresponding character chosen.
 * @author kelsey
 *
 */
@SuppressWarnings("serial")
public class SelectPlayerDialog extends JDialog implements ActionListener {
	private Map<Integer, String> playerInfo;
	private Map<String, ButtonRow> buttons;

	private ButtonGroup group;
	private JPanel whole;
	private String selectedPlayer = "Miss Scarlett";
	private int numToChoose;
	
	private boolean done;

	/**
	 * Creates a new SelectPlayerDialog to allow the specified number of players
	 * to select characters
	 * @param f The parent component
	 * @param playerInfo The HashMap to be populated
	 * @param numPlayers The number of players who need to select characters
	 */
	public SelectPlayerDialog(JFrame f, Map<Integer, String> playerInfo, int numPlayers) {
		super(f, "Select a Character");
		
		this.done = false;
		this.playerInfo = playerInfo;
		numToChoose = numPlayers;
		
		// Set up radioButtons
		this.buttons = new HashMap<String, ButtonRow>();
		for (int i = 1; i < 7; i++) {
			buttons.put(CluedoUI.asString(i), new ButtonRow(CluedoUI.asString(i)));
		}
		buttons.get("Miss Scarlett").b.setSelected(true);
		buttons.get("Miss Scarlett").showTextPane(true);
	    group = new ButtonGroup();
	    for (ButtonRow br: buttons.values()) {
	    	group.add(br.b);
	    }
	    
	    JButton okButton = new JButton("OK");
	    okButton.setActionCommand("ok");
	    okButton.setPreferredSize(new Dimension(100, 23));
	    okButton.addActionListener(this);
	    JPanel okPanel = new JPanel();
	    okPanel.setPreferredSize(new Dimension(400, 25));
	    okPanel.setLayout(new BorderLayout());
	    okPanel.add(okButton, BorderLayout.CENTER);

	    whole = new JPanel();
	    whole.setLayout(new BoxLayout(whole, BoxLayout.Y_AXIS));
	    for (ButtonRow br: buttons.values()) {
	    	whole.add(br);
	    }
	    whole.add(Box.createRigidArea(new Dimension(0, 10)));
	    whole.add(okPanel);
	    whole.setPreferredSize(new Dimension(400, 190));
	    whole.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	    
	    this.add(whole);
	    this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.pack();
		this.setResizable(false);
		this.setLocationRelativeTo(f);
		this.setVisible(true);
	}

	/** 
	 * @return Whether all players have been selected
	 */
	public boolean done() {
		return this.done;
	}
	
	@Override
	public void actionPerformed(ActionEvent a) {
		String command = a.getActionCommand();
		if (command == "ok") {
			String text = buttons.get(selectedPlayer).text.getText();
			if (text.length() > 0) {
				int character = CluedoUI.asInt(selectedPlayer);
				playerInfo.put(character, text);
				buttons.get(selectedPlayer).greyOut();
				if (playerInfo.size() == numToChoose) {
					this.done = true;
				}
			}
		} else {
			buttons.get(selectedPlayer).showTextPane(false);
			buttons.get(command).showTextPane(true);
			selectedPlayer = command;
		}
	}

	/*
	 * A row with a RadioButton and a text field
	 */
	private class ButtonRow extends JPanel {
		private JRadioButton b;
		private JTextField text;

		public ButtonRow (String label) {
			b = new JRadioButton(label);
			b.setActionCommand(label);
			b.addActionListener(SelectPlayerDialog.this);
			setLayout(new BorderLayout());
			add(b, BorderLayout.WEST);
			text = new JTextField(20);
			text.setEditable(true);
			text.setVisible(false);
			add(text, BorderLayout.EAST);
		}

		private void greyOut() {
			b.setEnabled(false);
			text.setEditable(false);
		}

		private void showTextPane (boolean show) {
			if (!b.isEnabled()) return;
			if (!show) {
				text.setText("");
			}
			text.setVisible(show);
			revalidate();
		}

		public Dimension getPreferredSize() {
			return new Dimension(400, 25);
		}
	}
}
