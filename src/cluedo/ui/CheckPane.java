package cluedo.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;


@SuppressWarnings("serial")
public class CheckPane extends JPanel {
	
	private String[] cardNames = { "Professor Plum", "Colonel Mustard", "Miss Scarlett", "Mrs White", "Rev Green", "Miss Peacock", "Spanner", "Dagger", "Rope", "Candlestick", "Revolver", "Lead Pipe", "Kitchen", "Dining Room", "Ballroom", "Study", "Conservatory", "Lounge", "Billiard Room", "Library", "Hall" };
	private Map<Integer, JList<CheckableItem>> lists;	// each player's list
	private int currentPlayer = 1;
	
	
	@SuppressWarnings("unchecked")
	public CheckPane() {
		
		// create and populate the map for players' lists
		lists = new HashMap<Integer, JList<CheckableItem>>();
		for (int i = 1; i < 7; i++) {
			final JList<CheckableItem> list = new JList<CheckableItem>(createData(cardNames));
			list.setCellRenderer(new CheckListRenderer());
			list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			list.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
			    int index = list.locationToIndex(e.getPoint());
			    CheckableItem item = (CheckableItem) list.getModel()
			        .getElementAt(index);
			    item.setSelected(!item.isSelected());
			    Rectangle rect = list.getCellBounds(index, index);
			    list.repaint(rect);
			  }
			});
			
			lists.put(i, list);
		}
	
		TitledBorder titled = new TitledBorder("Cards Seen");
		setBorder(titled);
		add(lists.get(currentPlayer), BorderLayout.CENTER);
		
	}

	/**
	 * Allows you to change the check list shown depending whose turn it is.
	 * @param player The current player whose list should display
	 */
	public void setPlayer(int player) {
		remove(lists.get(currentPlayer));
		revalidate();
		if (lists.get(player) != null) {
			currentPlayer = player;
			add(lists.get(currentPlayer), BorderLayout.CENTER);
			revalidate();
		}
		repaint();
	}
   

    
    private CheckableItem[] createData(String[] strs) {
        int n = strs.length;
        CheckableItem[] items = new CheckableItem[n];
        for (int i = 0; i < n; i++) {
          items[i] = new CheckableItem(strs[i]);
        }
        return items;
    }

    private class CheckableItem {
        private String str;

        private boolean isSelected;

        public CheckableItem(String str) {
          this.str = str;
          isSelected = false;
        }

        public void setSelected(boolean b) {
          isSelected = b;
        }

        public boolean isSelected() {
          return isSelected;
        }

        public String toString() {
          return str;
        }
    }
    
    @Override
	public Dimension getPreferredSize() {
		return new Dimension(200,700);
	}
    
    
	@SuppressWarnings("rawtypes")
	private class CheckListRenderer extends JCheckBox implements ListCellRenderer {

        public CheckListRenderer() {
          setBackground(UIManager.getColor("List.textBackground"));
 
          setForeground(UIManager.getColor("List.textForeground"));
        }

        public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean hasFocus) {
          setEnabled(list.isEnabled());
          setSelected(((CheckableItem) value).isSelected());
          setFont(list.getFont());
          setText(value.toString());
          return this;
        }
      }

	public void restart() {
		lists = new HashMap<Integer, JList<CheckableItem>>();
		for (int i = 1; i < 7; i++) {
			final JList<CheckableItem> list = new JList<CheckableItem>(createData(cardNames));
			list.setCellRenderer(new CheckListRenderer());
			list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			list.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
			    int index = list.locationToIndex(e.getPoint());
			    CheckableItem item = (CheckableItem) list.getModel()
			        .getElementAt(index);
			    item.setSelected(!item.isSelected());
			    Rectangle rect = list.getCellBounds(index, index);
			    list.repaint(rect);
			  }
			});
			
			lists.put(i, list);
		}
		this.currentPlayer = 1;
	}
    
}
