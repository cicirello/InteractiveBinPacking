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

import javax.swing.UIManager;
import javax.swing.JFrame;
import javax.swing.UnsupportedLookAndFeelException;
import java.awt.Font;
import java.awt.Color;

/**
 * This class implements the frame holding the application,
 * and is also the entry point to the application, containing the
 * main method.  It handles everything needed to start up the app.
 * 
 * @author Vincent A. Cicirello (https://www.cicirello.org/). 
 * @version June 2020 (most recent update)
 */
public class PackingFrame extends JFrame {
	
	/** Font used for all elements in the main JPanel and its sub-panels. */
	public static final Font font = new Font("SansSerif", Font.BOLD, 16);
	
	/**
	 * Constructs the UI.
	 */
	public PackingFrame() {
		UI ui = new UI(this);
		add(ui);
		setJMenuBar(new MenuBar(this, ui.getApplicationState()));
		getContentPane().setBackground(Color.WHITE);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Interactive Bin Packing Application");
		pack();
		setSize(680, 650);
		setResizable(false);
		setLocationRelativeTo(null);
		setVisible(true);
		About about = new About(this); 
	}
	
	/**
	 * Starts up the application.
	 * @param args Ignored.  Application has no command line arguments.
	 */
	public static void main(String[] args) {
		try {
			// Set "system" look and feel which will vary by OS
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} 
		// catch blocks deliberately empty to just default to the
		// default java look and feel
		catch (UnsupportedLookAndFeelException e) {}
		catch (ClassNotFoundException e) {}
		catch (InstantiationException e) {}
		catch (IllegalAccessException e) {}
		
		JFrame f = new PackingFrame(); 
	}
}