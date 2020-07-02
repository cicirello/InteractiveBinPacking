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
import java.util.Collections;
import java.util.Comparator;

/**
 * This class represents a single bin for the bin packing problem.
 * 
 * @author Vincent A. Cicirello (https://www.cicirello.org/). 
 * @version June 2020 (most recent update)
 */
public class Bin {
	
	private ArrayList<Item> contents;
	private int capacity;
	private int used;
	private String name;
	
	/**
	 * Construct a bin with a default capacity of 100.
	 * @param name A name for the bin
	 */
	public Bin(String name) {
		contents = new ArrayList<Item>(); 
		capacity = 100;
		used = 0;
		this.name = name;
	}
	
	/**
	 * Construct a bin.
	 * @param name A name for the bin
	 * @param capacity The capacity of the bin (maximum number of units it can store).
	 * @throws IllegalArgumentException if capacity is not positive
	 */
	public Bin(String name, int capacity) {
		if (capacity <= 0) throw new IllegalArgumentException("capacity must be positive");
		contents = new ArrayList<Item>(); 
		this.capacity = capacity;
		used = 0;
		this.name = name;
	}
	
	/**
	 * Gets the first item in the bin.
	 * @return the first item in the bin, or null if empty
	 */
	public Item peek() {
		if (contents.size() > 0) return contents.get(0);
		else return null;
	}
	
	/**
	 * Checks if the bin already contains an item.
	 * @param i The item to check.
	 * @return true if the bin contains the item and false if it does not.
	 */
	public boolean contains(Item i) {
		return contents.contains(i);
	}
	
	/**
	 * Checks if an item is the largest in a bin.
	 * @param i The item to check
	 * @return true if i is the largest in the bin
	 */
	public boolean isLargest(Item i) {
		for (Item e : contents) {
			if (e.size() > i.size()) return false;
		}
		return contains(i);
	}
	
	/**
	 * Checks whether the bin has sufficient remaining space for
	 * a new item to fit.
	 * @param i An item to check.
	 * @return true if the bin has sufficient remaining space for i to fit
	 */
	public boolean fits(Item i) {
		int space = capacity - used;
		return i.size() <= space;
	}
	
	/**
	 * Checks if the bin is full to capacity.
	 * @return true if the bin is full, false if there is still space remaining
	 */
	public boolean isFull() {
		return capacity <= used;   
	}
	
	/**
	 * Gets the amount of remaining space.
	 * @return number of units of space remaining
	 */	 
	public int space() {
		return capacity - used;
	}
	
	/**
	 * Gets the bin's capacity.
	 * @return the capacity of the bin
	 */
	public int capacity() {
		return capacity;
	}
	
	/**
	 * Gets the amount of space already used.
	 * @return number of units of the bin already used by items
	 */
	public int used() {
		return used;
	}
	
	/**
	 * Gets a list of the items in the bin.
	 * @return a list of the items in the bin
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Item> getContents() {
		return (ArrayList<Item>)contents.clone();   
	}
	
	/**
	 * Adds the item if it fits.
	 * @param i The item to add to the bin.
	 * @return true if the item was added, and false if it didn't fit.
	 */
	public boolean add(Item i) {
		if (fits(i)) {
			contents.add(i);
			used += i.size();
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Adds all of the items that fit.
	 * @param these The items to add to the bin.
	 */
	public void add(ArrayList<Item> these) {
		for (Item e : these) {
			add(e);   
		}
	}
	
	/**
	 * Removes an item from the bin.
	 * @param i The item to remove.
	 * @return true if the item was removed, and false if the bin didn't contain the item
	 */
	public boolean remove(Item i) {
		if (contents.remove(i)) {
			used -= i.size();
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Removes all items from the bin.
	 */
	public void removeAll() {
		used = 0;
		contents.removeAll(contents);
	}
	
	/**
	 * Formats a String of all of the items in the bin.
	 * @return a String listing all of the objects in the bin
	 */
	public String contentsToString() {
		String result = "";
		int i = 0;
		for ( ; i < contents.size()-1; i++) {
			result += contents.get(i) + ", ";
		}
		if (contents.size() > 0) {
			result += contents.get(i);
		} else {
			result = "empty";
		}
		return result;
	}
	
	/**
	 * Checks if the bin is empty.
	 * @return true if the bin is empty
	 */
	public boolean isEmpty() {
		return contents.size()==0;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	/**
	 * Sorts the bin contents by size.
	 * @param decreasing If true, then sorts from largest to smallest.  Otherwise,
	 * sorts from smallest to largest.
	 */
	public void sort(final boolean decreasing) {
		class SortContents implements Comparator<Item> {
			public int compare(Item i1, Item i2) {
				int val = i1.size() - i2.size();
				if (decreasing) val *= -1;
				return val;
			}
		}
		Collections.sort(contents, new SortContents());
	}
}
