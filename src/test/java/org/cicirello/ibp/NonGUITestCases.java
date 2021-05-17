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

import org.junit.*;
import static org.junit.Assert.*;
import java.util.ArrayList;


/**
 * JUnit tests for all of the non-GUI classes of the
 * Interactive Bin Packing Application.
 *
 */
public class NonGUITestCases {
	
	@Test
	public void testBinDefaultConstructor() {
		Bin b = new Bin("Bin Name");
		assertNull(b.peek());
		assertFalse(b.contains(new Item("A",5)));
		assertFalse(b.isLargest(new Item("A",5)));
		assertTrue(b.fits(new Item("A",100)));
		assertTrue(b.fits(new Item("A",99)));
		assertFalse(b.fits(new Item("A",101)));
		assertFalse(b.isFull());
		assertTrue(b.isEmpty());
		assertEquals(100, b.space());
		assertEquals(100, b.capacity());
		assertEquals(0, b.used());
		ArrayList<Item> contents = b.getContents();
		assertEquals(0, contents.size());
		assertEquals("Bin Name", b.toString());
		assertEquals("empty", b.contentsToString());
	}
	
	@Test
	public void testBinConstructor2() {
		Bin b = new Bin("Bin Name", 50);
		assertNull(b.peek());
		assertFalse(b.contains(new Item("A",5)));
		assertFalse(b.isLargest(new Item("A",5)));
		assertTrue(b.fits(new Item("A",50)));
		assertTrue(b.fits(new Item("A",49)));
		assertFalse(b.fits(new Item("A",51)));
		assertFalse(b.isFull());
		assertTrue(b.isEmpty());
		assertEquals(50, b.space());
		assertEquals(50, b.capacity());
		assertEquals(0, b.used());
		ArrayList<Item> contents = b.getContents();
		assertEquals(0, contents.size());
		assertEquals("Bin Name", b.toString());
		assertEquals("empty", b.contentsToString());
		
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new Bin("Bin Name", 0)
		);
	}
	
	@Test
	public void testBinAdd() {
		Item[] fillsExactlyToCapacityExceptLast = { new Item("A", 5), new Item("B", 80), new Item("C", 15), new Item("D", 1)};
		Item[] fillsAlmostToCapacityLastExceeds = { new Item("A", 80), new Item("B", 15), new Item("C", 6)};
		
		Item[] items = fillsExactlyToCapacityExceptLast;
		Bin b = new Bin("Bin Name");
		int[] expectedUsed = {5, 85, 100};
		for (int i = 0; i < items.length - 1; i++) {
			assertTrue(b.fits(items[i]));
			assertTrue(b.add(items[i]));
			assertFalse(b.isEmpty());
			if (i < items.length - 2) assertFalse(b.isFull());
			else assertTrue(b.isFull());
			assertEquals(items[0], b.peek());
			assertEquals(100, b.capacity());
			assertEquals(expectedUsed[i], b.used());
			assertEquals(100-expectedUsed[i], b.space());
			Item largest = i == 0 ? items[0] : items[1];
			assertTrue(b.isLargest(largest));
			assertFalse(b.isLargest(new Item("Z", 4)));
			assertFalse(b.isLargest(new Item("Z", 80)));
			for (int j = 0; j <= i; j++) {
				assertTrue(b.contains(items[j]));
			}
			for (int j = i+1; j < items.length; j++) {
				assertFalse(b.contains(items[j]));
			}
			assertEquals("Bin Name", b.toString());
		}
		assertFalse(b.fits(items[items.length - 1]));
		assertFalse(b.add(items[items.length - 1]));
		assertEquals(100, b.capacity());
		assertEquals(100, b.used());
		assertEquals(0, b.space());	
		
		items = fillsAlmostToCapacityLastExceeds;
		b = new Bin("Bin Name");
		expectedUsed = new int[] {80, 95};
		for (int i = 0; i < items.length - 1; i++) {
			assertTrue(b.fits(items[i]));
			assertTrue(b.add(items[i]));
			assertFalse(b.isEmpty());
			assertFalse(b.isFull());
			assertEquals(items[0], b.peek());
			assertEquals(100, b.capacity());
			assertEquals(expectedUsed[i], b.used());
			assertEquals(100-expectedUsed[i], b.space());
			Item largest = items[0];
			assertTrue(b.isLargest(largest));
			assertFalse(b.isLargest(new Item("Z", 4)));
			assertFalse(b.isLargest(new Item("Z", 80)));
			for (int j = 0; j <= i; j++) {
				assertTrue(b.contains(items[j]));
			}
			for (int j = i+1; j < items.length; j++) {
				assertFalse(b.contains(items[j]));
			}
			assertEquals("Bin Name", b.toString());
		}
		assertFalse(b.fits(items[items.length - 1]));
		assertFalse(b.add(items[items.length - 1]));
		assertEquals(100, b.capacity());
		assertEquals(95, b.used());
		assertEquals(5, b.space());	
	}
	
	@Test
	public void testBinAddList() {
		Item[] fitsExactly = { new Item("A", 5), new Item("B", 80), new Item("C", 15) };
		Item[] allButLastFitsFull = { new Item("A", 5), new Item("B", 80), new Item("C", 15), new Item("D", 1)};
		Item[] allButLastFitsNotFull = { new Item("A", 80), new Item("B", 15), new Item("C", 6)};
		
		Item[] items = fitsExactly;
		Bin b = new Bin("Bin Name");
		ArrayList<Item> al = new ArrayList<Item>();
		for (Item e : items) al.add(e);		
		b.add(al);
		for (int i = 0; i < items.length; i++) {
			assertTrue(b.contains(items[i]));
		}
		assertTrue(b.isLargest(new Item("B", 80)));
		assertFalse(b.isLargest(new Item("A", 5)));
		assertFalse(b.isLargest(new Item("C", 15)));
		assertFalse(b.isLargest(new Item("B", 81)));
		assertFalse(b.isLargest(new Item("Z", 80)));
		assertFalse(b.isEmpty());
		assertTrue(b.isFull());
		assertEquals(items[0], b.peek());
		assertEquals(100, b.capacity());
		assertEquals(100, b.used());
		assertEquals(0, b.space());
		
		items = allButLastFitsFull;
		b = new Bin("Bin Name");
		al = new ArrayList<Item>();
		for (Item e : items) al.add(e);		
		b.add(al);
		for (int i = 0; i < items.length - 1; i++) {
			assertTrue(b.contains(items[i]));
		}
		assertFalse(b.contains(items[items.length - 1]));
		assertTrue(b.isLargest(new Item("B", 80)));
		assertFalse(b.isLargest(new Item("A", 5)));
		assertFalse(b.isLargest(new Item("C", 15)));
		assertFalse(b.isLargest(new Item("B", 81)));
		assertFalse(b.isLargest(new Item("Z", 80)));
		assertFalse(b.isEmpty());
		assertTrue(b.isFull());
		assertEquals(items[0], b.peek());
		assertEquals(100, b.capacity());
		assertEquals(100, b.used());
		assertEquals(0, b.space());
		
		items = allButLastFitsNotFull;
		b = new Bin("Bin Name");
		al = new ArrayList<Item>();
		for (Item e : items) al.add(e);		
		b.add(al);
		for (int i = 0; i < items.length - 1; i++) {
			assertTrue(b.contains(items[i]));
		}
		assertFalse(b.contains(items[items.length - 1]));
		assertTrue(b.isLargest(new Item("A", 80)));
		assertFalse(b.isLargest(new Item("C", 5)));
		assertFalse(b.isLargest(new Item("B", 15)));
		assertFalse(b.isLargest(new Item("A", 81)));
		assertFalse(b.isLargest(new Item("Z", 80)));
		assertFalse(b.isEmpty());
		assertFalse(b.isFull());
		assertEquals(items[0], b.peek());
		assertEquals(100, b.capacity());
		assertEquals(95, b.used());
		assertEquals(5, b.space());
	}
	
	@Test
	public void testBinSortIncreasing() {
		Item[][] alreadySorted = { {new Item("A", 5)}, 
			{new Item("A", 5), new Item("B", 10)}, 
			{new Item("A", 5), new Item("B", 10), new Item("C", 15)}, 
			{new Item("A", 5), new Item("B", 10), new Item("C", 15), new Item("D", 20)},
			{new Item("A", 5), new Item("B", 10), new Item("C", 15), new Item("D", 20), new Item("E", 25)}
		};
		Item[][] reversed = { {new Item("A", 5)},
			{new Item("B", 10), new Item("A", 5)}, 
			{new Item("C", 15), new Item("B", 10), new Item("A", 5)}, 
			{new Item("D", 20), new Item("C", 15), new Item("B", 10), new Item("A", 5)},
			{new Item("E", 25), new Item("D", 20), new Item("C", 15), new Item("B", 10), new Item("A", 5)}
		};
		Item[][] jumbled = {  
			{new Item("C", 15), new Item("A", 5), new Item("D", 20), new Item("B", 10)},
			{new Item("C", 15), new Item("A", 5), new Item("E", 25), new Item("D", 20), new Item("B", 10)}
		};
		
		for (int i = 0; i < alreadySorted.length; i++) {
			Bin b = new Bin("Bin Name");
			for (int j = 0; j < alreadySorted[i].length; j++) {
				assertTrue(b.add(alreadySorted[i][j]));
			}
			ArrayList<Item> contents = b.getContents();
			for (int j = 0; j < alreadySorted[i].length; j++) {
				assertEquals(alreadySorted[i][j], contents.get(j));
			}
			b.sort(false);
			contents = b.getContents();
			for (int j = 0; j < alreadySorted[i].length; j++) {
				assertEquals(alreadySorted[i][j], contents.get(j));
			}
		}
		
		for (int i = 0; i < reversed.length; i++) {
			Bin b = new Bin("Bin Name");
			for (int j = 0; j < reversed[i].length; j++) {
				assertTrue(b.add(reversed[i][j]));
			}
			ArrayList<Item> contents = b.getContents();
			for (int j = 0; j < reversed[i].length; j++) {
				assertEquals(reversed[i][j], contents.get(j));
			}
			b.sort(false);
			contents = b.getContents();
			for (int j = 0; j < alreadySorted[i].length; j++) {
				assertEquals(alreadySorted[i][j], contents.get(j));
			}
		}
		
		for (int i = 0; i < jumbled.length; i++) {
			Bin b = new Bin("Bin Name");
			for (int j = 0; j < jumbled[i].length; j++) {
				assertTrue(b.add(jumbled[i][j]));
			}
			ArrayList<Item> contents = b.getContents();
			for (int j = 0; j < jumbled[i].length; j++) {
				assertEquals(jumbled[i][j], contents.get(j));
			}
			b.sort(false);
			contents = b.getContents();
			for (int j = 0; j < alreadySorted[i+3].length; j++) {
				assertEquals(alreadySorted[i+3][j], contents.get(j));
			}
		}
		
	}
	
	@Test
	public void testBinSortDecreasing() {
		Item[][] reversed = { {new Item("A", 5)}, 
			{new Item("A", 5), new Item("B", 10)}, 
			{new Item("A", 5), new Item("B", 10), new Item("C", 15)}, 
			{new Item("A", 5), new Item("B", 10), new Item("C", 15), new Item("D", 20)},
			{new Item("A", 5), new Item("B", 10), new Item("C", 15), new Item("D", 20), new Item("E", 25)}
		};
		Item[][] alreadySorted = { {new Item("A", 5)},
			{new Item("B", 10), new Item("A", 5)}, 
			{new Item("C", 15), new Item("B", 10), new Item("A", 5)}, 
			{new Item("D", 20), new Item("C", 15), new Item("B", 10), new Item("A", 5)},
			{new Item("E", 25), new Item("D", 20), new Item("C", 15), new Item("B", 10), new Item("A", 5)}
		};
		Item[][] jumbled = {  
			{new Item("C", 15), new Item("A", 5), new Item("D", 20), new Item("B", 10)},
			{new Item("C", 15), new Item("A", 5), new Item("E", 25), new Item("D", 20), new Item("B", 10)}
		};
		
		for (int i = 0; i < alreadySorted.length; i++) {
			Bin b = new Bin("Bin Name");
			for (int j = 0; j < alreadySorted[i].length; j++) {
				assertTrue(b.add(alreadySorted[i][j]));
			}
			ArrayList<Item> contents = b.getContents();
			for (int j = 0; j < alreadySorted[i].length; j++) {
				assertEquals(alreadySorted[i][j], contents.get(j));
			}
			b.sort(true);
			contents = b.getContents();
			for (int j = 0; j < alreadySorted[i].length; j++) {
				assertEquals(alreadySorted[i][j], contents.get(j));
			}
		}
		
		for (int i = 0; i < reversed.length; i++) {
			Bin b = new Bin("Bin Name");
			for (int j = 0; j < reversed[i].length; j++) {
				assertTrue(b.add(reversed[i][j]));
			}
			ArrayList<Item> contents = b.getContents();
			for (int j = 0; j < reversed[i].length; j++) {
				assertEquals(reversed[i][j], contents.get(j));
			}
			b.sort(true);
			contents = b.getContents();
			for (int j = 0; j < alreadySorted[i].length; j++) {
				assertEquals(alreadySorted[i][j], contents.get(j));
			}
		}
		
		for (int i = 0; i < jumbled.length; i++) {
			Bin b = new Bin("Bin Name");
			for (int j = 0; j < jumbled[i].length; j++) {
				assertTrue(b.add(jumbled[i][j]));
			}
			ArrayList<Item> contents = b.getContents();
			for (int j = 0; j < jumbled[i].length; j++) {
				assertEquals(jumbled[i][j], contents.get(j));
			}
			b.sort(true);
			contents = b.getContents();
			for (int j = 0; j < alreadySorted[i+3].length; j++) {
				assertEquals(alreadySorted[i+3][j], contents.get(j));
			}
		}
	}
	
	@Test
	public void testBinRemoveAll() {
		Item[][] items = { {new Item("A", 5)}, 
			{new Item("A", 5), new Item("B", 10)}, 
			{new Item("A", 5), new Item("B", 10), new Item("C", 15)}, 
			{new Item("A", 5), new Item("B", 10), new Item("C", 15), new Item("D", 20)},
			{new Item("A", 5), new Item("B", 10), new Item("C", 15), new Item("D", 20), new Item("E", 25)}
		};
		for (int i = 0; i < items.length; i++) {
			Bin b = new Bin("Bin Name");
			for (int j = 0; j < items[i].length; j++) {
				assertTrue(b.add(items[i][j]));
			}
			b.removeAll();
			assertNull(b.peek());
			assertFalse(b.contains(new Item("A",5)));
			assertFalse(b.isLargest(new Item("A",5)));
			assertTrue(b.fits(new Item("A",100)));
			assertTrue(b.fits(new Item("A",99)));
			assertFalse(b.fits(new Item("A",101)));
			assertFalse(b.isFull());
			assertTrue(b.isEmpty());
			assertEquals(100, b.space());
			assertEquals(100, b.capacity());
			assertEquals(0, b.used());
			ArrayList<Item> contents = b.getContents();
			assertEquals(0, contents.size());
			assertEquals("Bin Name", b.toString());
			assertEquals("empty", b.contentsToString());
		}
	}
	
	@Test
	public void testBinRemove() {
		Item[] items = {new Item("A", 5), new Item("B", 10), new Item("C", 15), new Item("D", 20) };
		for (int i = 0; i < items.length; i++) {
			Bin b = new Bin("Bin Name");
			for (int j = 0; j < items.length; j++) {
				assertTrue(b.add(items[j]));
			}
			assertTrue(b.remove(items[i]));
			for (int j = 0; j < items.length; j++) {
				if (i!=j) assertTrue(b.contains(items[j]));
				else assertFalse(b.contains(items[j]));
			}
			assertFalse(b.remove(items[i]));
			for (int j = 0; j < items.length; j++) {
				if (i!=j) assertTrue(b.contains(items[j]));
				else assertFalse(b.contains(items[j]));
			}
		}
	}
	
	@Test
	public void testBinContentsToString() {
		Item[] items = {new Item("A", 5), new Item("B", 10), new Item("C", 15), new Item("D", 20) };
		String[] expected = {
			"A(5)", "A(5), B(10)", "A(5), B(10), C(15)", "A(5), B(10), C(15), D(20)"
		};
		Bin b = new Bin("Bin Name");
		for (int i = 0; i < items.length; i++) {	
			assertTrue(b.add(items[i]));
			assertEquals(expected[i], b.contentsToString());
		}
	}
	
	@Test
	public void testItem() {
		String[] name = { "A", "B", "C" };
		int[] size = {5, 100, 42};
		String[] str = {"A(5)", "B(100)", "C(42)"};
		for (int i = 0; i < name.length; i++) {
			Item item = new Item(name[i], size[i]);
			assertEquals(size[i], item.size());
			assertEquals(name[i], item.name());
			assertEquals(str[i], item.toString());
			Item same = new Item(name[i], size[i]);
			assertEquals(item, same);
			assertEquals(item.hashCode(), same.hashCode());
			Item diffSize = new Item(name[i], size[i]+1);
			assertNotEquals(item, diffSize);
			Item diffName = new Item("Z", size[i]);
			assertNotEquals(item, diffName);
		}
		
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new Item("Item Name", 0)
		);
	}
	
	@Test
	public void testFloorFromArrayOFSizes() {
		int[][] sizes = { { 4 }, {3, 5}, {10, 2, 6}, {1, 9, 3, 7}};
		int[] largest = {4, 5, 10, 9};
		String[] str = {"A(4)", "A(3), B(5)", "A(10), B(2), C(6)", "A(1), B(9), C(3), D(7)" };
		for (int i = 0; i < sizes.length; i++) {
			int[] sizeArray = sizes[i].clone();
			Floor f = new Floor(sizeArray);
			assertEquals("Floor", f.toString());
			assertEquals(str[i], f.contentsToString());
			assertEquals(new Item("A", sizes[i][0]), f.peek());
			char c = 'A';
			ArrayList<Item> contents = f.getContents();
			for (int j = 0; j < sizes[i].length; j++, c++) {
				Item expectedItem = new Item(c+"", sizes[i][j]);
				assertEquals(expectedItem, contents.get(j));
				Item diffName = new Item("Z", sizes[i][j]);
				Item diffSize = new Item(c+"", 15);
				assertTrue(f.contains(expectedItem));
				assertFalse(f.contains(diffName));
				assertFalse(f.contains(diffSize));
				if (sizes[i][j] == largest[i]) {
					assertTrue(f.isLargest(expectedItem));
					assertFalse(f.isLargest(diffName));
				} else {
					assertFalse(f.isLargest(expectedItem));
				}
			}
		}
	}
	
	@Test
	public void testFloorWithSeed() {
		for (int seed = 1; seed < 100; seed += 10) {
			for (int n = 1; n < 10; n++) {
				for (int minSize = 5; minSize <= 25; minSize += 5) {
					int maxSize = minSize + 7;
					for (int trial = 0; trial < 10; trial++) {
						Floor f1 = new Floor(minSize, maxSize, n, seed);
						assertEquals("Floor", f1.toString());
						ArrayList<Item> contents1 = f1.getContents();
						Floor f2 = new Floor(minSize, maxSize, n, seed);
						ArrayList<Item> contents2 = f2.getContents();
						assertEquals(n, contents1.size());
						assertEquals(n, contents2.size());
						assertEquals(f1.contentsToString(), f2.contentsToString());
						char c = 'A';
						for (int i = 0; i < n; i++, c++) {
							assertEquals(contents1.get(i), contents2.get(i));
							assertTrue(contents1.get(i).size() >= minSize);
							assertTrue(contents1.get(i).size() <= maxSize);
							assertEquals(c+"", contents1.get(i).name());
						}
					}
				}
			}
		}
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new Floor(1, 1, -1, 42)
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new Floor(2, 1, 0, 42)
		);
	}
	
	@Test
	public void testFloorNoSeed() {
		for (int n = 1; n < 10; n++) {
			for (int minSize = 5; minSize <= 25; minSize += 5) {
				int maxSize = minSize + 7;
				for (int trial = 0; trial < 10; trial++) {
					Floor f = new Floor(minSize, maxSize, n);
					assertEquals("Floor", f.toString());
					ArrayList<Item> contents = f.getContents();
					assertEquals(n, contents.size());
					char c = 'A';
					for (int i = 0; i < n; i++, c++) {
						assertTrue(contents.get(i).size() >= minSize);
						assertTrue(contents.get(i).size() <= maxSize);
						assertEquals(c+"", contents.get(i).name());
					}
				}
			}
		}
		IllegalArgumentException thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new Floor(1, 1, -1)
		);
		thrown = assertThrows( 
			IllegalArgumentException.class,
			() -> new Floor(2, 1, 0)
		);
	}
	
	@Test
	public void testApplicationState() {
		int[] sizes = { 7, 2, 18, 3, 6 };
		Item[] sortedOrder = {new Item("B", 2), new Item("D", 3), new Item("E", 6), new Item("A", 7), new Item("C", 18)};
		int[] sizes2 = { 5, 1, 21, 8};
		int[] sizes3 = { 16, 9, 19, 4, 22};
		class CallBackTestData {
			int setNewInstanceCalled;
			int sortCalled;
			int resetCalled;
		}
		for (int numBins = 1; numBins <= 5; numBins++) {
			Floor f = new Floor(sizes);
			final CallBackTestData callbackData = new CallBackTestData();
			CallBack onSetNew = new CallBack() {
				@Override public void call() {callbackData.setNewInstanceCalled++;}
			};
			CallBack onSort = new CallBack() {
				@Override public void call() {callbackData.sortCalled++;}
			};
			CallBack onReset = new CallBack() {
				@Override public void call() {callbackData.resetCalled++;}
			};
			ApplicationState state = new ApplicationState(numBins, f, onSetNew, onSort, onReset);
			assertEquals(ApplicationState.MODE_PRACTICE, state.getMode());
			assertEquals("Practice Mode", state.getModeString());
			assertEquals("practice", state.getModeName());
			
			state.setMode(ApplicationState.MODE_FIRST_FIT);
			assertEquals("First-Fit Mode", state.getModeString());
			assertEquals("first-fit", state.getModeName());
			assertEquals(ApplicationState.MODE_FIRST_FIT, state.getMode());
			
			state.setMode(ApplicationState.MODE_FIRST_FIT_DECREASING);
			assertEquals("First-Fit Decreasing Mode", state.getModeString());
			assertEquals("first-fit decreasing", state.getModeName());
			assertEquals(ApplicationState.MODE_FIRST_FIT_DECREASING, state.getMode());
			
			state.setMode(ApplicationState.MODE_BEST_FIT);
			assertEquals("Best-Fit Mode", state.getModeString());
			assertEquals("best-fit", state.getModeName());
			assertEquals(ApplicationState.MODE_BEST_FIT, state.getMode());
			
			state.setMode(ApplicationState.MODE_BEST_FIT_DECREASING);
			assertEquals("Best-Fit Decreasing Mode", state.getModeString());
			assertEquals("best-fit decreasing", state.getModeName());
			assertEquals(ApplicationState.MODE_BEST_FIT_DECREASING, state.getMode());
			
			state.setMode(ApplicationState.MODE_PRACTICE);
			assertEquals("Practice Mode", state.getModeString());
			assertEquals("practice", state.getModeName());
			assertEquals(ApplicationState.MODE_PRACTICE, state.getMode());
			
			state.setMode(ApplicationState.MODE_FIRST_FIT);
			assertEquals("First-Fit Mode", state.getModeString());
			assertEquals("first-fit", state.getModeName());
			assertEquals(ApplicationState.MODE_FIRST_FIT, state.getMode());
			assertTrue(f == state.getFloor());
			ArrayList<Item> items = state.getItems();
			ArrayList<Item> fromFloor = f.getContents();
			assertEquals(5, items.size());
			assertEquals(5, fromFloor.size());
			for (int i = 0; i < fromFloor.size(); i++) {
				assertEquals(fromFloor.get(i), items.get(i));
			}
			
			// isBestFitBin
			ArrayList<Bin> bins = state.getBins();
			assertEquals(numBins, bins.size());
			for (int i = 0; i < numBins; i++) {
				assertTrue(bins.get(i).isEmpty());
				assertTrue(state.isBestFitBin(new Item("A",16), bins.get(i)));
				assertFalse(state.isBestFitBin(new Item("A",101), bins.get(i)));
			}
			assertFalse(state.isBestFitBin(new Item("Z",2), state.getFloor()));
			if (numBins == 3) {
				bins.get(1).add(new Item("A",16));
				assertFalse(state.isBestFitBin(new Item("B",1), bins.get(0)));
				assertTrue(state.isBestFitBin(new Item("B",1), bins.get(1)));
				assertFalse(state.isBestFitBin(new Item("B",1), bins.get(2)));
				assertTrue(state.isBestFitBin(new Item("B",85), bins.get(0)));
				assertFalse(state.isBestFitBin(new Item("B",85), bins.get(1)));
				assertTrue(state.isBestFitBin(new Item("B",85), bins.get(2)));
				bins.get(1).removeAll();
			}
			
			// firstFitBin
			assertEquals(bins.get(0), state.firstFitBin(new Item("A",16)));
			assertNull(state.firstFitBin(new Item("A",101)));
			assertEquals(0, callbackData.setNewInstanceCalled);
			assertEquals(0, callbackData.sortCalled);
			assertEquals(0, callbackData.resetCalled);
			state.sortFloor(false);
			assertEquals(0, callbackData.setNewInstanceCalled);
			assertEquals(1, callbackData.sortCalled);
			assertEquals(0, callbackData.resetCalled);
			items = state.getFloor().getContents();
			for (int i = 0; i < sortedOrder.length; i++) {
				assertEquals(sortedOrder[i], items.get(i));
			}
			state.sortFloor(true);
			items = state.getFloor().getContents();
			assertEquals(0, callbackData.setNewInstanceCalled);
			assertEquals(2, callbackData.sortCalled);
			assertEquals(0, callbackData.resetCalled);
			reverse(sortedOrder);
			for (int i = 0; i < sortedOrder.length; i++) {
				assertEquals(sortedOrder[i], items.get(i));
			}
			reverse(sortedOrder);
			state.reset();
			assertEquals(0, callbackData.setNewInstanceCalled);
			assertEquals(2, callbackData.sortCalled);
			assertEquals(1, callbackData.resetCalled);
			assertEquals("First-Fit Mode", state.getModeString());
			assertEquals("first-fit", state.getModeName());
			assertTrue(f == state.getFloor());
			items = state.getItems();
			fromFloor = f.getContents();
			assertEquals(5, items.size());
			assertEquals(5, fromFloor.size());
			for (int i = 0; i < fromFloor.size(); i++) {
				assertEquals(fromFloor.get(i), items.get(i));
			}
			bins = state.getBins();
			assertEquals(numBins, bins.size());
			for (int i = 0; i < numBins; i++) {
				assertTrue(bins.get(i).isEmpty());
			}
			int b = 0;
			for (Item e : items) {
				state.getFloor().remove(e);
				bins.get(b % numBins).add(e);
				b++;
			}
			for (int i = 0; i < numBins; i++) {
				assertFalse(bins.get(i).isEmpty());
			}
			assertTrue(state.getFloor().isEmpty());
			state.reset();
			assertEquals(0, callbackData.setNewInstanceCalled);
			assertEquals(2, callbackData.sortCalled);
			assertEquals(2, callbackData.resetCalled);
			assertEquals("First-Fit Mode", state.getModeString());
			assertEquals("first-fit", state.getModeName());
			assertTrue(f == state.getFloor());
			items = state.getItems();
			fromFloor = f.getContents();
			assertEquals(5, items.size());
			assertEquals(5, fromFloor.size());
			for (int i = 0; i < fromFloor.size(); i++) {
				assertEquals(fromFloor.get(i), items.get(i));
			}
			bins = state.getBins();
			assertEquals(numBins, bins.size());
			for (int i = 0; i < numBins; i++) {
				assertTrue(bins.get(i).isEmpty());
			}
			Floor f2 = new Floor(sizes2);
			state.setNewInstance(f2);
			assertEquals(1, callbackData.setNewInstanceCalled);
			assertEquals(2, callbackData.sortCalled);
			assertEquals(2, callbackData.resetCalled);
			assertEquals("First-Fit Mode", state.getModeString());
			assertEquals("first-fit", state.getModeName());
			assertTrue(f2 == state.getFloor());
			items = state.getItems();
			fromFloor = f2.getContents();
			assertEquals(4, items.size());
			assertEquals(4, fromFloor.size());
			for (int i = 0; i < fromFloor.size(); i++) {
				assertEquals(fromFloor.get(i), items.get(i));
			}
			bins = state.getBins();
			assertEquals(numBins, bins.size());
			for (int i = 0; i < numBins; i++) {
				assertTrue(bins.get(i).isEmpty());
			}
			b = 0;
			for (Item e : items) {
				state.getFloor().remove(e);
				bins.get(b % numBins).add(e);
				b++;
			}
			for (int i = 0; i < numBins; i++) {
				if (i < items.size()) assertFalse(bins.get(i).isEmpty());
				else assertTrue(bins.get(i).isEmpty());
			}
			assertTrue(state.getFloor().isEmpty());
			Floor f3 = new Floor(sizes3);
			state.setNewInstance(f3);
			assertEquals(2, callbackData.setNewInstanceCalled);
			assertEquals(2, callbackData.sortCalled);
			assertEquals(2, callbackData.resetCalled);
			assertEquals("First-Fit Mode", state.getModeString());
			assertEquals("first-fit", state.getModeName());
			assertTrue(f3 == state.getFloor());
			items = state.getItems();
			fromFloor = f3.getContents();
			assertEquals(5, items.size());
			assertEquals(5, fromFloor.size());
			for (int i = 0; i < fromFloor.size(); i++) {
				assertEquals(fromFloor.get(i), items.get(i));
			}
			bins = state.getBins();
			assertEquals(numBins, bins.size());
			for (int i = 0; i < numBins; i++) {
				assertTrue(bins.get(i).isEmpty());
			}
			
			// Will need to change this if any new modes are introduced.
			IllegalArgumentException thrown = assertThrows( 
				IllegalArgumentException.class,
				() -> state.setMode(5)
			);
		}
		
	}
	
	@Test
	public void testApplicationStateLowerBound() {
		CallBack cb = new CallBack() {
			@Override public void call() {}
		};
		for (int numItems = 1; numItems <= 28; numItems++) {
			int[] sizes = new int[numItems];
			for (int i = 0; i < numItems; i++) sizes[i] = 25;
			Floor f = new Floor(sizes);
			ApplicationState state = new ApplicationState(9, f, cb, cb, cb);
			int expected = (numItems - 1) / 4 + 1;
			assertEquals(expected, state.lowerBound());
		}
	}
	
	private void reverse(Item[] array) {
		for (int i = 0, j = array.length-1; i < j; i++, j--) {
			Item temp = array[i];
			array[i] = array[j];
			array[j] = temp;
		}
	}
}