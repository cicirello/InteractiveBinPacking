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

import java.util.Random;

/**
 * This class represents the collection of items for a bin packing
 * instance that are not yet in any bin.  It also generates random
 * instances upon construction.
 * 
 * @author Vincent A. Cicirello (https://www.cicirello.org/). 
 * @version June 2020 (most recent update)
 */
public class Floor extends Bin {
	
	/**
	 * Generates a random instance of the bin packing problem.
	 * Through the use of a seed for the random number generator,
	 * this constructor will regenerate an identical instance if none
	 * of the parameters change.
	 * @param sizeLow The minimum size for an item.
	 * @param sizeHigh The maximum size for an item.
	 * @param numItems The number of items for the instance.
	 * @param seed The seed for the random number generator.
	 * @throws IllegalArgumentException if sizeHigh is less than sizeLow or if numItems is negative
	 */
	public Floor(int sizeLow, int sizeHigh, int numItems, int seed) {
		super("Floor", Integer.MAX_VALUE);
		if (sizeHigh < sizeLow || numItems < 0) throw new IllegalArgumentException();
		int[] itemSizes = new int[numItems];
		Random gen = new Random(seed);
		for (int i = 0; i < numItems; i++) {
			itemSizes[i] = gen.nextInt(1+sizeHigh-sizeLow) + sizeLow;
		}
		init(itemSizes);  
	}

	/**
	 * Generates a random instance of the bin packing problem.
	 * @param sizeLow The minimum size for an item.
	 * @param sizeHigh The maximum size for an item.
	 * @param numItems The number of items for the instance.
	 * @throws IllegalArgumentException if sizeHigh is less than sizeLow or if numItems is negative
	 */
	public Floor(int sizeLow, int sizeHigh, int numItems) {
		super("Floor", Integer.MAX_VALUE);
		if (sizeHigh < sizeLow || numItems < 0) throw new IllegalArgumentException();
		int[] itemSizes = new int[numItems];
		Random gen = new Random();
		for (int i = 0; i < numItems; i++) {
			itemSizes[i] = gen.nextInt(1+sizeHigh-sizeLow) + sizeLow;
		}
		init(itemSizes);  
	}

	/**
	 * Initializes the set of items not yet in bins with a list of item sizes.
	 * @param itemSizes A list of the sizes of the items.
	 */
	public Floor(int[] itemSizes) {
		super("Floor", Integer.MAX_VALUE);
		init(itemSizes);
	}
    
	/*
	 * internal helper method for initializing the items from a list of sizes.
	 */
    private void init(int[] itemSizes) {
        char n = 'A';
        String namePre = "";
        int numItems = itemSizes.length;
        for (int i = 1; i <= numItems; i++) {
            String name = namePre + n;
            if (n == 'Z') {
                n = 'A';
                namePre += "A";
            } else n += 1;
            int size = itemSizes[i-1]; 
            add(new Item(name,size));
        }
    }
}