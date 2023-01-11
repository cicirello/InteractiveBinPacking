/*
 * Interactive Bin Packing.
 * Copyright (C) 2008, 2010, 2020-2023 Vincent A. Cicirello
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

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import org.junit.jupiter.api.*;

/** JUnit tests for the Bin class. */
public class BinTests {

  @Test
  public void testBinDefaultConstructor() {
    Bin b = new Bin("Bin Name", -1);
    assertNull(b.peek());
    assertFalse(b.contains(new Item("A", 5)));
    assertFalse(b.isLargest(new Item("A", 5)));
    assertTrue(b.fits(new Item("A", 100)));
    assertTrue(b.fits(new Item("A", 99)));
    assertFalse(b.fits(new Item("A", 101)));
    assertFalse(b.isFull());
    assertTrue(b.isEmpty());
    assertEquals(100, b.space());
    assertEquals(100, b.capacity());
    assertEquals(0, b.used());
    ArrayList<Item> contents = b.getContents();
    assertEquals(0, contents.size());
    assertEquals("Bin Name", b.toString());
    assertEquals(-1, b.getBinNumber());
    assertEquals("empty", b.contentsToString());
  }

  @Test
  public void testBinConstructor2() {
    Bin b = new Bin("Bin Name", 3, 50);
    assertNull(b.peek());
    assertFalse(b.contains(new Item("A", 5)));
    assertFalse(b.isLargest(new Item("A", 5)));
    assertTrue(b.fits(new Item("A", 50)));
    assertTrue(b.fits(new Item("A", 49)));
    assertFalse(b.fits(new Item("A", 51)));
    assertFalse(b.isFull());
    assertTrue(b.isEmpty());
    assertEquals(50, b.space());
    assertEquals(50, b.capacity());
    assertEquals(0, b.used());
    ArrayList<Item> contents = b.getContents();
    assertEquals(0, contents.size());
    assertEquals("Bin Name", b.toString());
    assertEquals(3, b.getBinNumber());
    assertEquals("empty", b.contentsToString());

    IllegalArgumentException thrown =
        assertThrows(IllegalArgumentException.class, () -> new Bin("Bin Name", 1, 0));
  }

  @Test
  public void testBinAdd() {
    Item[] fillsExactlyToCapacityExceptLast = {
      new Item("A", 5), new Item("B", 80), new Item("C", 15), new Item("D", 1)
    };
    Item[] fillsAlmostToCapacityLastExceeds = {
      new Item("A", 80), new Item("B", 15), new Item("C", 6)
    };

    Item[] items = fillsExactlyToCapacityExceptLast;
    Bin b = new Bin("Bin Name", -2);
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
      assertEquals(100 - expectedUsed[i], b.space());
      Item largest = i == 0 ? items[0] : items[1];
      assertTrue(b.isLargest(largest));
      assertFalse(b.isLargest(new Item("Z", 4)));
      assertFalse(b.isLargest(new Item("Z", 80)));
      for (int j = 0; j <= i; j++) {
        assertTrue(b.contains(items[j]));
      }
      for (int j = i + 1; j < items.length; j++) {
        assertFalse(b.contains(items[j]));
      }
      assertEquals("Bin Name", b.toString());
      assertEquals(-2, b.getBinNumber());
    }
    assertFalse(b.fits(items[items.length - 1]));
    assertFalse(b.add(items[items.length - 1]));
    assertEquals(100, b.capacity());
    assertEquals(100, b.used());
    assertEquals(0, b.space());

    items = fillsAlmostToCapacityLastExceeds;
    b = new Bin("Bin Name", -3);
    expectedUsed = new int[] {80, 95};
    for (int i = 0; i < items.length - 1; i++) {
      assertTrue(b.fits(items[i]));
      assertTrue(b.add(items[i]));
      assertFalse(b.isEmpty());
      assertFalse(b.isFull());
      assertEquals(items[0], b.peek());
      assertEquals(100, b.capacity());
      assertEquals(expectedUsed[i], b.used());
      assertEquals(100 - expectedUsed[i], b.space());
      Item largest = items[0];
      assertTrue(b.isLargest(largest));
      assertFalse(b.isLargest(new Item("Z", 4)));
      assertFalse(b.isLargest(new Item("Z", 80)));
      for (int j = 0; j <= i; j++) {
        assertTrue(b.contains(items[j]));
      }
      for (int j = i + 1; j < items.length; j++) {
        assertFalse(b.contains(items[j]));
      }
      assertEquals("Bin Name", b.toString());
      assertEquals(-3, b.getBinNumber());
    }
    assertFalse(b.fits(items[items.length - 1]));
    assertFalse(b.add(items[items.length - 1]));
    assertEquals(100, b.capacity());
    assertEquals(95, b.used());
    assertEquals(5, b.space());
  }

  @Test
  public void testBinAddList() {
    Item[] fitsExactly = {new Item("A", 5), new Item("B", 80), new Item("C", 15)};
    Item[] allButLastFitsFull = {
      new Item("A", 5), new Item("B", 80), new Item("C", 15), new Item("D", 1)
    };
    Item[] allButLastFitsNotFull = {new Item("A", 80), new Item("B", 15), new Item("C", 6)};

    Item[] items = fitsExactly;
    Bin b = new Bin("Bin Name", -1);
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
    b = new Bin("Bin Name", -1);
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
    b = new Bin("Bin Name", -1);
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
    Item[][] alreadySorted = {
      {new Item("A", 5)},
      {new Item("A", 5), new Item("B", 10)},
      {new Item("A", 5), new Item("B", 10), new Item("C", 15)},
      {new Item("A", 5), new Item("B", 10), new Item("C", 15), new Item("D", 20)},
      {new Item("A", 5), new Item("B", 10), new Item("C", 15), new Item("D", 20), new Item("E", 25)}
    };
    Item[][] reversed = {
      {new Item("A", 5)},
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
      Bin b = new Bin("Bin Name", -1);
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
      Bin b = new Bin("Bin Name", -1);
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
      Bin b = new Bin("Bin Name", -1);
      for (int j = 0; j < jumbled[i].length; j++) {
        assertTrue(b.add(jumbled[i][j]));
      }
      ArrayList<Item> contents = b.getContents();
      for (int j = 0; j < jumbled[i].length; j++) {
        assertEquals(jumbled[i][j], contents.get(j));
      }
      b.sort(false);
      contents = b.getContents();
      for (int j = 0; j < alreadySorted[i + 3].length; j++) {
        assertEquals(alreadySorted[i + 3][j], contents.get(j));
      }
    }
  }

  @Test
  public void testBinSortDecreasing() {
    Item[][] reversed = {
      {new Item("A", 5)},
      {new Item("A", 5), new Item("B", 10)},
      {new Item("A", 5), new Item("B", 10), new Item("C", 15)},
      {new Item("A", 5), new Item("B", 10), new Item("C", 15), new Item("D", 20)},
      {new Item("A", 5), new Item("B", 10), new Item("C", 15), new Item("D", 20), new Item("E", 25)}
    };
    Item[][] alreadySorted = {
      {new Item("A", 5)},
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
      Bin b = new Bin("Bin Name", -1);
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
      Bin b = new Bin("Bin Name", -1);
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
      Bin b = new Bin("Bin Name", -1);
      for (int j = 0; j < jumbled[i].length; j++) {
        assertTrue(b.add(jumbled[i][j]));
      }
      ArrayList<Item> contents = b.getContents();
      for (int j = 0; j < jumbled[i].length; j++) {
        assertEquals(jumbled[i][j], contents.get(j));
      }
      b.sort(true);
      contents = b.getContents();
      for (int j = 0; j < alreadySorted[i + 3].length; j++) {
        assertEquals(alreadySorted[i + 3][j], contents.get(j));
      }
    }
  }

  @Test
  public void testBinRemoveAll() {
    Item[][] items = {
      {new Item("A", 5)},
      {new Item("A", 5), new Item("B", 10)},
      {new Item("A", 5), new Item("B", 10), new Item("C", 15)},
      {new Item("A", 5), new Item("B", 10), new Item("C", 15), new Item("D", 20)},
      {new Item("A", 5), new Item("B", 10), new Item("C", 15), new Item("D", 20), new Item("E", 25)}
    };
    for (int i = 0; i < items.length; i++) {
      Bin b = new Bin("Bin Name", -1);
      for (int j = 0; j < items[i].length; j++) {
        assertTrue(b.add(items[i][j]));
      }
      b.removeAll();
      assertNull(b.peek());
      assertFalse(b.contains(new Item("A", 5)));
      assertFalse(b.isLargest(new Item("A", 5)));
      assertTrue(b.fits(new Item("A", 100)));
      assertTrue(b.fits(new Item("A", 99)));
      assertFalse(b.fits(new Item("A", 101)));
      assertFalse(b.isFull());
      assertTrue(b.isEmpty());
      assertEquals(100, b.space());
      assertEquals(100, b.capacity());
      assertEquals(0, b.used());
      ArrayList<Item> contents = b.getContents();
      assertEquals(0, contents.size());
      assertEquals("Bin Name", b.toString());
      assertEquals(-1, b.getBinNumber());
      assertEquals("empty", b.contentsToString());
    }
  }

  @Test
  public void testBinRemove() {
    Item[] items = {new Item("A", 5), new Item("B", 10), new Item("C", 15), new Item("D", 20)};
    for (int i = 0; i < items.length; i++) {
      Bin b = new Bin("Bin Name", -1);
      for (int j = 0; j < items.length; j++) {
        assertTrue(b.add(items[j]));
      }
      assertTrue(b.remove(items[i]));
      for (int j = 0; j < items.length; j++) {
        if (i != j) assertTrue(b.contains(items[j]));
        else assertFalse(b.contains(items[j]));
      }
      assertFalse(b.remove(items[i]));
      for (int j = 0; j < items.length; j++) {
        if (i != j) assertTrue(b.contains(items[j]));
        else assertFalse(b.contains(items[j]));
      }
    }
  }

  @Test
  public void testBinContentsToString() {
    Item[] items = {new Item("A", 5), new Item("B", 10), new Item("C", 15), new Item("D", 20)};
    String[] expected = {"A(5)", "A(5), B(10)", "A(5), B(10), C(15)", "A(5), B(10), C(15), D(20)"};
    Bin b = new Bin("Bin Name", -1);
    for (int i = 0; i < items.length; i++) {
      assertTrue(b.add(items[i]));
      assertEquals(expected[i], b.contentsToString());
    }
  }
}
