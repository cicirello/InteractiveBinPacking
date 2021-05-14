/*
 * Interactive Bin Packing.
 * Copyright (C) 2008, 2010, 2020-2021  Vincent A. Cicirello
 *
 * This file is part of Interactive Bin Packing.
 * 
 * Interactive Bin Packing is free software: 
 * you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Interactive Bin Packing is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
 
 package org.cicirello.ibp;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.border.EtchedBorder;
import java.awt.BorderLayout;
import java.awt.Color;

/**
 * This class implements the top panel of the UI, which lists
 * all of the items not in bins.
 * 
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public class TopPanel extends JPanel {
	
	private JTextArea floorItems;
	private ApplicationState state;
	
	/**
	 * Constructs the panel.
	 * @param state The state of the application, needed to access list of items
	 */
	public TopPanel(ApplicationState state) {
		super(new BorderLayout());
		this.state = state;
		JLabel l = new JLabel("Not yet in any bins: ");
		l.setFont(InteractiveBinPacking.font);
		add(BorderLayout.NORTH, l);
		floorItems = new JTextArea(3, 30);
		floorItems.setFont(InteractiveBinPacking.font);
		floorItems.setLineWrap(true);
		floorItems.setWrapStyleWord(true);
		floorItems.setText(state.getFloor().contentsToString());
		floorItems.setEditable(false);
		add(BorderLayout.CENTER, floorItems);
		setBorder(new EtchedBorder());
		setBackground(Color.WHITE);
	}
	
	/**
	 * Updates the display of items not yet in bins.
	 */
	public void refresh() {
		floorItems.setText(state.getFloor().contentsToString());
	}
}