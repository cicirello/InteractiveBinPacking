/*
 * Interactive Bin Packing.
 * Copyright (C) 2008, 2010, 2020  Vincent A. Cicirello
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
import java.net.URL;

/**
 * This class implements the a dialog window for displaying
 * information to the user.  The content is formatted with html
 * and supports hyperlinks, both externally in a browser as well
 * as hyperlinks within the dialog.  It also supports scrolling.
 * It relies on Java's
 * JEditorPane, which only supports html3 at the present time.
 * 
 * @author Vincent A. Cicirello (https://www.cicirello.org/). 
 * @version June 2020 (most recent update)
 */
public class InfoDialog extends JDialog {
	
	/**
	 * Constructs an InfoDialog.
	 * @param f The frame for the application.  The initial location of
	 * the InfoDialog will be centered over f.
	 * @param title The title of the dialog
	 * @param htmlFilePath The path, with filename, to the html file.
	 */
	public InfoDialog(JFrame f, String title, String htmlFilePath) {
		// pass null for first param so dialog not forced to be always on top of app
		super((JFrame)null, title, false);
		
		final URL url = PackingFrame.class.getResource(htmlFilePath);
		JEditorPane contents = new JEditorPane();
		JScrollPane scroll = new JScrollPane(contents);
		contents.setEditable(false);
		contents.setMargin(new Insets(10,10,10,10));
		contents.addHyperlinkListener(new HyperlinkListener() {
			@Override
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if (HyperlinkEvent.EventType.ACTIVATED.equals(e.getEventType())) {
					URL link = e.getURL();
					if (link != null && link.sameFile(url)) { 
						contents.scrollToReference(link.getRef());
					} else if (link != null) {
						Desktop desktop = Desktop.getDesktop();
						try {
							desktop.browse(link.toURI());
						} catch (Exception ex) {
							JOptionPane.showMessageDialog(f, "Your system administrator doesn't allow me to open a web browser.\nYou will need to open a web browser and enter the address manually.", "Opening Web Browser Disallowed by Administrator", JOptionPane.INFORMATION_MESSAGE);
						}
					}
				}
			}
		});
		try {
			contents.setPage(url);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(f, "Unexpected error: Content file is missing.", "Error", JOptionPane.ERROR_MESSAGE);
		}
		add(scroll);
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		pack();
		setSize(450, 475);
		setLocationRelativeTo(f);
		setVisible(true);
	}
	
}
