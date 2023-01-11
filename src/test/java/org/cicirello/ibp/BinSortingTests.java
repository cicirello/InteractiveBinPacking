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

/** JUnit tests for the Bin class, specific to sorting a Bin. */
public class BinSortingTests {

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
}
