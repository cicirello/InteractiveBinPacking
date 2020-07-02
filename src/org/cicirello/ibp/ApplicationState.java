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

import java.util.ArrayList;

/**
 * This class is used to maintain the state of the application,
 * including the state of the solving of the current problem
 * instance (e.g., it encapsulates the Bin objects, etc) as well
 * as other state info such as the mode (practice mode vs one of the 
 * heuristic modes).  This includes the logic needed to determine
 * if a item/bin is consistent with a heuristic.
 *
 * @author Vincent A. Cicirello (https://www.cicirello.org/). 
 * @version June 2020 (most recent update)
 */
public class ApplicationState {
	
	/** Practice mode where user is free to move items around as they see fit. */
	public static final int MODE_PRACTICE = 0;
	
	/** In first-fit mode, the user steps through the first-fit heuristic. */
	public static final int MODE_FIRST_FIT = 1;
	
	/** In first-fit decreasing mode, the user steps through the first-fit decreasing heuristic. */
	public static final int MODE_FIRST_FIT_DECREASING = 2;
	
	/** In best-fit mode, the user steps through the best-fit heuristic. */
	public static final int MODE_BEST_FIT = 3;
	
	/** In best-fit decreasing mode, the user steps through the best-fit decreasing heuristic. */
	public static final int MODE_BEST_FIT_DECREASING = 4;
	
	private int modeSelection;
	private String modeString;
	private String modeName;
	
	private ArrayList<Bin> bins;
	private Floor theFloor;
	private ArrayList<Item> allItems;
		
	private CallBack onSetNewInstance;
	private CallBack onSortFloor;
	private CallBack onReset;
	
	/**
	 * Constructs an object that keeps track of the state of the application.
	 * @param numBins The number of initially empty bins to display to user
	 * @param instance A Floor object containing the items for the initial instance to display
	 * @param onSetNewInstance The call method of this object will be called whenever a new
	 * instance is set.
	 * @param onSortFloor The call method will be called whenever the items in the floor are sorted
	 * @param onReset This callback is called whenever the reset method is called.
	 */
	public ApplicationState(int numBins, Floor instance, CallBack onSetNewInstance, CallBack onSortFloor, CallBack onReset) {
		setMode(MODE_PRACTICE);
		this.onSetNewInstance = onSetNewInstance;
		this.onSortFloor = onSortFloor;
		this.onReset = onReset;
		theFloor = instance;
		allItems = theFloor.getContents();
		bins = new ArrayList<Bin>();
		String s = "Bin ";
		for (int i = 1; i <= numBins; i++) {
			bins.add(new Bin(s + i));
		}
	}
	
	/**
	 * Gets the current mode of the application.
	 * @return the current mode of the application
	 */
	public int getMode() {
		return modeSelection;
	}
	
	/**
	 * Gets the name of the current mode.
	 * @return the name of the current mode
	 */
	public String getModeString() {
		return modeString;
	}
	
	/**
	 * Gets a simple name for the mode in all lowercase, and no word "mode".
	 * @return a simple name for the current mode.
	 */
	public String getModeName() {
		return modeName;
	}
	
	/**
	 * Changes the mode of the application.
	 * @param mode The new mode to switch to.
	 */
	public void setMode(int mode) {
		modeSelection = mode;
		switch (mode) {
			case MODE_PRACTICE: 
				modeString = "Practice Mode"; 
				modeName = "practice"; 
				break;
			case MODE_FIRST_FIT: 
				modeString = "First-Fit Mode"; 
				modeName = "first-fit"; 
				break;
			case MODE_FIRST_FIT_DECREASING: 
				modeString = "First-Fit Decreasing Mode"; 
				modeName = "first-fit decreasing"; 
				break;
			case MODE_BEST_FIT: 
				modeString = "Best-Fit Mode"; 
				modeName = "best-fit";
				break;
			case MODE_BEST_FIT_DECREASING: 
				modeString = "Best-Fit Decreasing Mode"; 
				modeName = "best-fit decreasing";
				break;
		}
	}
	
	/**
	 * Resets the instance, removing all elements from bins,
	 * and listing in original order.
	 */
	public void reset() {
		theFloor.removeAll();
		for (Bin b : bins) {
			b.removeAll();    
		}
		theFloor.add(allItems);
		onReset.call();
	}
	
	/**
	 * Reinitializes the state with a new instance.
	 * @param instance A Floor object containing the new set of items.
	 */
	public void setNewInstance(Floor instance) {
		theFloor = instance;
		allItems = instance.getContents();
		for (Bin b : bins) {
			b.removeAll();    
		}
		onSetNewInstance.call();
	}
	
	/**
	 * Gets the Floor object.
	 * @return the Floor
	 */
	public Floor getFloor() {
		return theFloor;
	}
	
	/**
	 * Gets a list of all of the items.
	 * @return a list of all of the items
	 */
	public ArrayList<Item> getItems() {
		return allItems;
	}
	
	/**
	 * Gets a list of all of the bins.
	 * @return a list of all of the bins
	 */
	public ArrayList<Bin> getBins() {
		return bins;
	}
	
	/**
	 * Gets the bin for the first fit heuristic.
	 * @param i The item to put in a bin.
	 * @return the bin that the first fit heuristic would choose
	 */
	public Bin firstFitBin(Item i) {
		for (Bin b : bins) {
			if (b.fits(i)) return b;
		}
		return null;
	}
	
	/**
	 * Checks if a bin is the bin that the best fit heuristic would choose.
	 * @param i The item to put in a bin.
	 * @param b The bin
	 * @return true if b is the best fit bin for i
	 */
	public boolean isBestFitBin(Item i, Bin b) {
		if (!b.fits(i) || b==theFloor) return false;
		int space = b.space();
		for (Bin other : bins) {
			if (other.fits(i) && other.space() < space) return false;
		}
		return true;
	}
	
	/**
	 * Sorts the items that are not in bins.
	 * @param decreasing if true, then the sort is from largest to smallest.  otherwise,
	 * sorts from smallest to largest.
	 */
	public void sortFloor(final boolean decreasing) {
		theFloor.sort(decreasing);
		onSortFloor.call();
	}
	
	
}