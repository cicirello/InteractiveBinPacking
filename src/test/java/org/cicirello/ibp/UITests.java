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

/** JUnit tests for the UI class. */
public class UITests {

  @Test
  public void testUIInit() {
    UI ui = new UI(null);
    ApplicationState state = ui.getApplicationState();
    assertEquals(ApplicationState.MODE_PRACTICE, state.getMode());
    assertEquals("Practice Mode", state.getModeString());
    assertEquals("practice", state.getModeName());
    Floor f = state.getFloor();
    ArrayList<Item> initialFloorItems = f.getContents();
    assertEquals(20, initialFloorItems.size());
    ArrayList<Bin> bins = state.getBins();
    assertEquals(9, bins.size());
    for (Bin b : bins) {
      assertEquals(100, b.capacity());
      assertEquals(0, b.used());
      assertTrue(b.isEmpty());
    }
    ArrayList<Item> items = state.getItems();
    assertEquals(20, items.size());
    for (Item i : items) {
      assertTrue(f.contains(i));
    }

    int[] sizes2 = {15, 5, 25, 10, 20};
    int[] sorted = {5, 10, 15, 20, 25};
    Floor f2 = new Floor(sizes2);
    state.setNewInstance(f2, "TestCase");
    f = state.getFloor();
    ArrayList<Item> floorItems = f.getContents();
    assertEquals(sizes2.length, floorItems.size());
    bins = state.getBins();
    assertEquals(9, bins.size());
    for (Bin b : bins) {
      assertEquals(100, b.capacity());
      assertEquals(0, b.used());
      assertTrue(b.isEmpty());
    }
    items = state.getItems();
    assertEquals(sizes2.length, items.size());
    int j = 0;
    for (Item i : items) {
      assertTrue(f.contains(i));
      assertEquals(sizes2[j], i.size());
      j++;
    }

    state.sortFloor(false);
    floorItems = f.getContents();
    for (int i = 0; i < floorItems.size(); i++) {
      assertEquals(sorted[i], floorItems.get(i).size());
    }
    state.sortFloor(true);
    floorItems = f.getContents();
    for (int i = 0; i < floorItems.size(); i++) {
      assertEquals(sorted[sorted.length - i - 1], floorItems.get(i).size());
    }
  }
}
