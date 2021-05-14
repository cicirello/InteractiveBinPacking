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

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JOptionPane;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import java.awt.Insets;
import java.awt.Desktop;
import java.io.IOException;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

/**
 * This class implements the About dialog, which displays
 * the copyright and license notices.
 * 
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public class About extends JDialog {
	
	/**
	 * Constructs the About dialog.
	 * @param f The frame for the application.
	 */
	public About(JFrame f) {
		super(f, "About", true);
		
		JEditorPane contents = new JEditorPane();
		contents.setEditable(false);
		contents.setMargin(new Insets(10,10,10,10));
		contents.addHyperlinkListener(new HyperlinkListener() {
			@Override
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if (HyperlinkEvent.EventType.ACTIVATED.equals(e.getEventType())) {
					Desktop desktop = Desktop.getDesktop();
					try {
						desktop.browse(e.getURL().toURI());
					} catch (Exception ex) {
						JOptionPane.showMessageDialog(f, "Your system administrator doesn't allow me to open a web browser.\nYou will need to open a web browser and enter the address manually.", "Opening Web Browser Disallowed by Administrator", JOptionPane.INFORMATION_MESSAGE);
					}
				}
			}
		});
		
		try {
			contents.setPage(InteractiveBinPacking.class.getResource("/html/about.html"));
		} catch (IOException e) {
			JOptionPane.showMessageDialog(f, "Unexpected error: About text is missing.", "Error", JOptionPane.ERROR_MESSAGE);
		}
		
		JScrollPane scroll = new JScrollPane(contents);
		
		add(scroll);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		pack();
		setSize(450, 475);
		setLocationRelativeTo(f);
		setResizable(false);
		setVisible(true);
	}
}