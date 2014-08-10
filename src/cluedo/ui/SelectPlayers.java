package cluedo.ui;

import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JRadioButton;

import cluedo.board.Board;

public class SelectPlayers extends JFrame implements ActionListener {	
	private Map<String, Integer> playerInfo;
	private final Map<Integer, String> characters;
	JRadioButton sButton;
	JRadioButton mButton;
	JRadioButton wButton;
	JRadioButton gButton;
	JRadioButton peaButton;
	JRadioButton plumButton;

	public SelectPlayers(Map<Integer, String> characters, Map<String, Integer> playerInfo, int numPlayers) {
		this.characters = characters;
		this.playerInfo = playerInfo;
		sButton = new JRadioButton(characters.get(Board.SCARLETT));
	    sButton.setActionCommand("scarlett");
	    sButton.setSelected(true);

	    mButton = new JRadioButton(characters.get(Board.MUSTARD));
	    mButton.setActionCommand("mustard");

	    wButton = new JRadioButton(characters.get(Board.WHITE));
	    wButton.setActionCommand("white");
	    
	    gButton = new JRadioButton(characters.get(Board.GREEN));
	    gButton.setActionCommand("green");
	    
	    peaButton = new JRadioButton(characters.get(Board.PEACOCK));
	    peaButton.setActionCommand("peacock");
	    
	    plumButton = new JRadioButton(characters.get(Board.PLUM));
	    plumButton.setActionCommand("plum");
	    
	    ButtonGroup group = new ButtonGroup();
	    group.add(sButton);
	    group.add(mButton);
	    group.add(wButton);
	    group.add(gButton);
	    group.add(peaButton);
	    group.add(plumButton);
	    
	    sButton.addActionListener(this);
	    mButton.addActionListener(this);
	    wButton.addActionListener(this);
	    gButton.addActionListener(this);
	    peaButton.addActionListener(this);
	    plumButton.addActionListener(this);
	    
	    
	    
	    
	}

	@Override
	public void actionPerformed(ActionEvent a) {
		switch (a.getActionCommand()) {
			case "scarlett":
				sButton.setEnabled(false);
				//TODO
		
		}
			
		
	}

}
