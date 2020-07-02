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

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JMenuBar;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.ButtonGroup;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.util.Scanner;

/**
 * This class implements the menu bar and menus.
 * 
 * @author Vincent A. Cicirello (https://www.cicirello.org/). 
 * @version June 2020 (most recent update)
 */
public class MenuBar extends JMenuBar {
	
	private PackingFrame f;
	private ApplicationState state;
	private JMenuItem sortItem;
	private JMenuItem sortItemInc;
	private Tutorial tutorial;
	private Help help;
		
	/**
	 * Constructs the menu bar.
	 * @param f The main app frame
	 * @param state The state of the application
	 */
	public MenuBar(PackingFrame f, ApplicationState state) {
		super();
		this.f = f;
		this.state = state;
		add(initModeMenu());
		add(initProblemMenu());
		add(initOperationsMenu());
		add(initInfoMenu());
	}
	
	/*
	 * Initializes the mode menu
	 */
	private JMenu initModeMenu() {
		JMenu mode = new JMenu("Mode");
		
		JRadioButtonMenuItem practice = new JRadioButtonMenuItem("Practice");
		practice.setSelected(true);
		practice.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					state.setMode(ApplicationState.MODE_PRACTICE);
					sortItem.setEnabled(true);
					sortItemInc.setEnabled(true);
					// Deliberately doesn't call reset() so that
					// user can try to use practice mode to optimize
					// a solution from one of the heuristic modes.
				}
			}
		} );
		
		JRadioButtonMenuItem ff = new JRadioButtonMenuItem("First-Fit");
		ff.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					state.setMode(ApplicationState.MODE_FIRST_FIT);
					sortItem.setEnabled(false);
					sortItemInc.setEnabled(false);
					state.reset();
				}
			}
		} );
		
		JRadioButtonMenuItem ffd = new JRadioButtonMenuItem("First-Fit Decreasing");
		ffd.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					state.setMode(ApplicationState.MODE_FIRST_FIT_DECREASING);
					sortItem.setEnabled(true);
					sortItemInc.setEnabled(true);
					state.reset();
				}
			}
		} );
		
		JRadioButtonMenuItem bf = new JRadioButtonMenuItem("Best-Fit");
		bf.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					state.setMode(ApplicationState.MODE_BEST_FIT);
					sortItem.setEnabled(false);
					sortItemInc.setEnabled(false);
					state.reset();
				}
			}
		} );
		
		JRadioButtonMenuItem bfd = new JRadioButtonMenuItem("Best-Fit Decreasing");
		bfd.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					state.setMode(ApplicationState.MODE_BEST_FIT_DECREASING);
					sortItem.setEnabled(true);
					sortItemInc.setEnabled(true);
					state.reset();
				}
			}
		} );
		
		mode.add(practice);
		mode.add(ff);
		mode.add(ffd);
		mode.add(bf);
		mode.add(bfd);
		ButtonGroup buttons = new ButtonGroup();
		buttons.add(practice);
		buttons.add(ff);
		buttons.add(ffd);
		buttons.add(bf);
		buttons.add(bfd);
		return mode;
	}
	
	/*
	 * Initializes the problem menu
	 */
	@SuppressWarnings("unchecked")
	private JMenu initProblemMenu() {
		JMenu problemMenu = new JMenu("Problem");
		JMenuItem defaultProblem = new JMenuItem("Default Instance");
		JMenuItem randomProblem = new JMenuItem("Random Instance");
		JMenuItem selectProblem = new JMenuItem("Select Instance Number");
		
		defaultProblem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] weights = {36, 33, 39, 43, 7, 19, 37, 8, 29, 28, 37, 23, 29, 
					10, 22, 11, 33, 9, 17, 30};
				state.setNewInstance(new Floor(weights));
			}
		});
		problemMenu.add(defaultProblem);
		
		randomProblem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				state.setNewInstance(new Floor(20,50,20));
			}
		});
		problemMenu.add(randomProblem);
		
		selectProblem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String seedStr = "";
				Scanner scan = new Scanner(seedStr);
				int seed = 0;
				do {
					seedStr = JOptionPane.showInputDialog(f, "Enter problem instance number (an integer):", "Problem Instance Selection", JOptionPane.QUESTION_MESSAGE);
					if (seedStr == null) break;
					scan = new Scanner(seedStr);
				} while (!scan.hasNextInt());
				
				if (seedStr != null) {
					seed = scan.nextInt();
					state.setNewInstance(new Floor(20,50,20,seed));
				}
			}
		});
		problemMenu.add(selectProblem);
		
		return problemMenu;
	}
	
	/*
	 * Initializes the operations menu
	 */
	private JMenu initOperationsMenu() {
		JMenu opsMenu = new JMenu("Operations");
		sortItem = new JMenuItem("Sort Decreasing");
		sortItemInc = new JMenuItem("Sort Increasing");
		opsMenu.add(sortItem);
		opsMenu.add(sortItemInc);
		
		class SortListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				 state.sortFloor(e.getSource()==sortItem);
			}
		}
		sortItem.addActionListener(new SortListener());
		sortItemInc.addActionListener(new SortListener());
		
		return opsMenu;
	}
	
	/*
	 * Initializes the info menu
	 */
	private JMenu initInfoMenu() {
		JMenu helpMenu = new JMenu("Info");
		JMenuItem insItem = new JMenuItem("Tutorial");
		helpMenu.add(insItem);
		insItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (tutorial == null) {
					tutorial = new Tutorial(f);
				} else {
					tutorial.setVisible(true);
				}
			}
		});
		
		JMenuItem helpItem = new JMenuItem("Help");
		helpMenu.add(helpItem);
		helpItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (help == null) {
					help = new Help(f);
				} else {
					help.setVisible(true);
				}
			}
		});
		
		JMenuItem abItem = new JMenuItem("About");
		helpMenu.add(abItem);
		abItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				About about = new About(f); 
			}
		});
		
		return helpMenu;
	}
}