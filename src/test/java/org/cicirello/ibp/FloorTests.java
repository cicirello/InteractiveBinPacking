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

/** JUnit tests for the Floor class. */
public class FloorTests {

  @Test
  public void testFloorFromArrayOFSizes() {
    int[][] sizes = {{4}, {3, 5}, {10, 2, 6}, {1, 9, 3, 7}};
    int[] largest = {4, 5, 10, 9};
    String[] str = {"A(4)", "A(3), B(5)", "A(10), B(2), C(6)", "A(1), B(9), C(3), D(7)"};
    for (int i = 0; i < sizes.length; i++) {
      int[] sizeArray = sizes[i].clone();
      Floor f = new Floor(sizeArray);
      assertEquals("Floor", f.toString());
      assertEquals(0, f.getBinNumber());
      assertEquals(str[i], f.contentsToString());
      assertEquals(new Item("A", sizes[i][0]), f.peek());
      char c = 'A';
      ArrayList<Item> contents = f.getContents();
      for (int j = 0; j < sizes[i].length; j++, c++) {
        Item expectedItem = new Item(c + "", sizes[i][j]);
        assertEquals(expectedItem, contents.get(j));
        Item diffName = new Item("Z", sizes[i][j]);
        Item diffSize = new Item(c + "", 15);
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
            assertEquals(0, f1.getBinNumber());
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
              assertEquals(c + "", contents1.get(i).name());
            }
          }
        }
      }
    }
    IllegalArgumentException thrown =
        assertThrows(IllegalArgumentException.class, () -> new Floor(1, 1, -1, 42));
    thrown = assertThrows(IllegalArgumentException.class, () -> new Floor(2, 1, 0, 42));
  }

  @Test
  public void testFloorNoSeed() {
    for (int n = 1; n < 10; n++) {
      for (int minSize = 5; minSize <= 25; minSize += 5) {
        int maxSize = minSize + 7;
        for (int trial = 0; trial < 10; trial++) {
          Floor f = new Floor(minSize, maxSize, n);
          assertEquals("Floor", f.toString());
          assertEquals(0, f.getBinNumber());
          ArrayList<Item> contents = f.getContents();
          assertEquals(n, contents.size());
          char c = 'A';
          for (int i = 0; i < n; i++, c++) {
            assertTrue(contents.get(i).size() >= minSize);
            assertTrue(contents.get(i).size() <= maxSize);
            assertEquals(c + "", contents.get(i).name());
          }
        }
      }
    }
    IllegalArgumentException thrown =
        assertThrows(IllegalArgumentException.class, () -> new Floor(1, 1, -1));
    thrown = assertThrows(IllegalArgumentException.class, () -> new Floor(2, 1, 0));
  }
}
