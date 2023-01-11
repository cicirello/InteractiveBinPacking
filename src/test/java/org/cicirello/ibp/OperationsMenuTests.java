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
import javax.swing.JMenu;
import org.junit.jupiter.api.*;

/** JUnit tests for the Operations Menu. */
public class OperationsMenuTests {

  @Test
  public void testOperationsMenu() {
    int[] sizes = {7, 2, 18, 3, 6};
    Item[] sorted = {
      new Item("B", 2), new Item("D", 3), new Item("E", 6), new Item("A", 7), new Item("C", 18)
    };
    Item[] sorted2 = {new Item("B", 2), new Item("D", 3), new Item("E", 6), new Item("C", 18)};
    CallBack cb =
        new CallBack() {
          @Override
          public void call() {}
        };
    Floor floor = new Floor(sizes);
    ApplicationState state = new ApplicationState(1, floor, cb, cb, cb);
    MenuBar menus = new MenuBar(null, state);
    JMenu opsMenu = menus.getMenu(2);
    assertTrue(opsMenu.getItem(0).isEnabled());
    assertTrue(opsMenu.getItem(1).isEnabled());
    assertTrue(opsMenu.getItem(2).isEnabled());
    opsMenu.getItem(1).doClick();
    ArrayList<Item> shouldBeSorted = state.getFloor().getContents();
    for (int i = 0; i < sorted.length; i++) {
      assertEquals(sorted[i], shouldBeSorted.get(i));
    }

    floor = new Floor(sizes);
    state = new ApplicationState(1, floor, cb, cb, cb);
    menus = new MenuBar(null, state);
    opsMenu = menus.getMenu(2);
    assertTrue(opsMenu.getItem(0).isEnabled());
    assertTrue(opsMenu.getItem(1).isEnabled());
    assertTrue(opsMenu.getItem(2).isEnabled());
    opsMenu.getItem(0).doClick();
    shouldBeSorted = state.getFloor().getContents();
    reverse(sorted);
    for (int i = 0; i < sorted.length; i++) {
      assertEquals(sorted[i], shouldBeSorted.get(i));
    }
    reverse(sorted);

    floor = new Floor(sizes);
    state = new ApplicationState(1, floor, cb, cb, cb);
    menus = new MenuBar(null, state);
    opsMenu = menus.getMenu(2);
    assertTrue(opsMenu.getItem(0).isEnabled());
    assertTrue(opsMenu.getItem(1).isEnabled());
    assertTrue(opsMenu.getItem(2).isEnabled());
    state.getFloor().remove(new Item("A", 7));
    state.getBins().get(0).add(new Item("A", 7));
    opsMenu.getItem(1).doClick();
    shouldBeSorted = state.getFloor().getContents();
    for (int i = 0; i < sorted2.length; i++) {
      assertEquals(sorted2[i], shouldBeSorted.get(i));
    }
    assertTrue(state.getBins().get(0).contains(new Item("A", 7)));

    floor = new Floor(sizes);
    state = new ApplicationState(1, floor, cb, cb, cb);
    menus = new MenuBar(null, state);
    opsMenu = menus.getMenu(2);
    assertTrue(opsMenu.getItem(0).isEnabled());
    assertTrue(opsMenu.getItem(1).isEnabled());
    assertTrue(opsMenu.getItem(2).isEnabled());
    state.getFloor().remove(new Item("A", 7));
    state.getBins().get(0).add(new Item("A", 7));
    opsMenu.getItem(0).doClick();
    shouldBeSorted = state.getFloor().getContents();
    reverse(sorted2);
    for (int i = 0; i < sorted2.length; i++) {
      assertEquals(sorted2[i], shouldBeSorted.get(i));
    }
    assertTrue(state.getBins().get(0).contains(new Item("A", 7)));
    reverse(sorted2);

    class MenuBarTester extends MenuBar {
      boolean lowerBoundButtonClicked;

      MenuBarTester(InteractiveBinPacking f, ApplicationState state) {
        super(f, state);
        lowerBoundButtonClicked = false;
      }

      @Override
      void displayLowerBoundMessage(String message) {
        lowerBoundButtonClicked = true;
      }
    }
    floor = new Floor(sizes);
    state = new ApplicationState(1, floor, cb, cb, cb);
    menus = new MenuBarTester(null, state);
    opsMenu = menus.getMenu(2);
    assertTrue(opsMenu.getItem(0).isEnabled());
    assertTrue(opsMenu.getItem(1).isEnabled());
    assertTrue(opsMenu.getItem(2).isEnabled());
    assertFalse(((MenuBarTester) menus).lowerBoundButtonClicked);
    opsMenu.getItem(2).doClick();
    assertTrue(((MenuBarTester) menus).lowerBoundButtonClicked);
  }

  private void reverse(Item[] array) {
    for (int i = 0, j = array.length - 1; i < j; i++, j--) {
      Item temp = array[i];
      array[i] = array[j];
      array[j] = temp;
    }
  }
}
