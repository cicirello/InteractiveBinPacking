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

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
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
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.File;
import java.io.FileReader;

/**
 * This class implements the menu bar and menus.
 * 
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public class MenuBar extends JMenuBar {
	
	private InteractiveBinPacking f;
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
	public MenuBar(InteractiveBinPacking f, ApplicationState state) {
		super();
		this.f = f;
		this.state = state;
		add(initModeMenu());
		add(initProblemMenu());
		add(initOperationsMenu());
		add(initSessionMenu());
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
				state.setNewInstance(new Floor(weights), "Default");
			}
		});
		problemMenu.add(defaultProblem);
		
		randomProblem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				state.setNewInstance(new Floor(20,50,20), "Random");
			}
		});
		problemMenu.add(randomProblem);
		
		selectProblem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String seedStr = null;
				Scanner scan = null;
				int seed = 0;
				do {
					if (scan != null) {
						scan.close();
						scan = null;
					}
					seedStr = getProblemInstanceNumberFromUser();
					if (seedStr == null) break;
					scan = new Scanner(seedStr);
				} while (!scan.hasNextInt());
				
				if (scan != null) {
					seed = scan.nextInt();
					scan.close();
					state.setNewInstance(new Floor(20,50,20,seed), "#"+seed);
				}
			}
		});
		problemMenu.add(selectProblem);
		
		return problemMenu;
	}
	
	/*
	 * package private to support testing (i.e., to enable overriding
	 * with an operation that can be done in a headless state)
	 */
	String getProblemInstanceNumberFromUser() {
		return JOptionPane.showInputDialog(f, "Enter problem instance number (an integer):", "Problem Instance Selection", JOptionPane.QUESTION_MESSAGE);
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
		
		JMenuItem lb = new JMenuItem("Compute Lower Bound");
		lb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int bound = state.lowerBound();
				String message = "Lower Bound: " + bound 
					+ " bins.\n\nA lower bound on the optimal solution to\nthis instance is "
					+ bound
					+ " bins. You may or may\nnot be able to find a solution using that\nnumber of bins, but you definitely can't\ndo it with fewer bins.";
				displayLowerBoundMessage(message);
			}
		});
		opsMenu.add(lb);
		
		return opsMenu;
	}
	
	/*
	 * package private to support testing (i.e., to enable overriding with
	 * something that can be executed in a headless state).
	 */
	void displayLowerBoundMessage(String message) {
		JOptionPane.showMessageDialog(f, message, "Lower Bound", JOptionPane.INFORMATION_MESSAGE);
	}
	
	/*
	 * Initializes the info menu
	 */
	private JMenu initSessionMenu() {
		JMenu sessionMenu = new JMenu("Session");
		
		JMenuItem viewSessionLog = new JMenuItem("View Current Session Log");
		sessionMenu.add(viewSessionLog);
		viewSessionLog.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String session = state.getFormattedLogData();
				InfoDialog sessionDialog = new InfoDialog(f, "Current Session Log", session, true, true, false);
			}
		});
		
		JMenuItem saveSessionLog = new JMenuItem("Save Current Session Log");
		sessionMenu.add(saveSessionLog);
		saveSessionLog.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter(
					"Interactive Bin Packing Session Logs (*.ibp)", "ibp");
				chooser.setFileFilter(filter);
				chooser.setAcceptAllFileFilterUsed​(false);
				int returnVal = chooser.showSaveDialog(f);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					File logFile = chooser.getSelectedFile();
					if (!logFile.getPath().endsWith(".ibp")) {
						logFile = new File(logFile.getPath() + ".ibp");
					}
					if (!logFile.exists() || JOptionPane.showConfirmDialog(f,
						"The chosen file exists. Are you sure you want to replace it?",
						"Confirm file replacement",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.WARNING_MESSAGE
						) == JOptionPane.YES_OPTION)	{
						try {
							PrintWriter out = new PrintWriter(logFile, StandardCharsets.UTF_8);
							state.saveSessionLog(out);
							out.close();
						} catch (IOException ex) {
							displayErrorMessage("An error occurred during file output!");
						}
					}
				}
			}
		});
		
		JMenuItem openSessionLog = new JMenuItem("Open Past Session Log");
		sessionMenu.add(openSessionLog);
		openSessionLog.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter(
					"Interactive Bin Packing Session Logs (*.ibp)", "ibp");
				chooser.setFileFilter(filter);
				chooser.setAcceptAllFileFilterUsed​(false);
				int returnVal = chooser.showOpenDialog(f);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					File logFile = chooser.getSelectedFile();
					if (logFile.exists()) {
						if (logFile.getPath().endsWith(".ibp")) {
							try (FileReader in = new FileReader(logFile, StandardCharsets.UTF_8)) {
								String session = state.loadSessionLog(in);
								if (session != null) {
									InfoDialog sessionDialog = new InfoDialog(
										f, 
										"Session Log: " + logFile.getName(), 
										session, 
										true, true, false
									);
								} else {
									displayErrorMessage("The chosen file has either been altered since generated or it is not an Interactive Bin Packing session log.");
								}
							} catch(IOException ex) {
								displayErrorMessage("An error occurred during file input!");
							}
						} else {
							displayErrorMessage("The chosen file doesn't have the extension (*.ibp) of an Interactive Bin Packing session log!");
						}
					} else {
						displayErrorMessage("Your chosen file doesn't exist!");
					}
				}
			}
		});
		
		return sessionMenu;
	}
	
	void displayErrorMessage(String message) {
		JOptionPane.showMessageDialog(
			f, 
			message,
			"Error",
			JOptionPane.ERROR_MESSAGE
		);
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
					tutorial.activate();
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
					help.activate();
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
	
	/*
	 * package private for testing
	 */
	 Tutorial getTutorial() { return tutorial; }
	 
	 /*
	 * package private for testing
	 */
	 Help getHelp() { return help; }
}