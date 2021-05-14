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

import javax.swing.JFrame;

/**
 * This class implements the Tutorial dialog, which displays
 * explanations of the heuristics, the problem itself, and suggests
 * a sequence of steps to take using the application to practice
 * with the various heuristics.
 * 
 * @author Vincent A. Cicirello (https://www.cicirello.org/). 
 * @version June 2020 (most recent update)
 */
public class Tutorial extends InfoDialog {
	
	/**
	 * Constructs the Tutorial dialog.
	 * @param f The frame for the application.
	 */
	public Tutorial(JFrame f) {
		super(f, "Tutorial", "/html/tutorial.html");
	}
}
