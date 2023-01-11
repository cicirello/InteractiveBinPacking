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

/** JUnit tests for the Problem Menu. */
public class ProblemMenuTests {

  @SuppressWarnings("unchecked")
  @Test
  public void testProblemMenu() {
    int[] sizes = {7, 2, 18, 3, 6};
    CallBack cb =
        new CallBack() {
          @Override
          public void call() {}
        };
    Floor floor = new Floor(sizes);
    ApplicationState state = new ApplicationState(1, floor, cb, cb, cb);
    MenuBar menus = new MenuBar(null, state);
    state.getFloor().remove(new Item("A", 7));
    state.getBins().get(0).add(new Item("A", 7));
    assertEquals(sizes.length - 1, state.getFloor().getContents().size());
    assertEquals(1, state.getBins().get(0).getContents().size());
    JMenu problemMenu = menus.getMenu(1);
    problemMenu.getItem(0).doClick();
    ArrayList<Item> onFloor = state.getFloor().getContents();
    assertEquals(20, onFloor.size());
    assertEquals(0, state.getBins().get(0).getContents().size());
    int[] expectedSizes = {
      36, 33, 39, 43, 7, 19, 37, 8, 29, 28, 37, 23, 29, 10, 22, 11, 33, 9, 17, 30
    };
    char c = 'A';
    for (int i = 0; i < expectedSizes.length; i++, c++) {
      Item expected = new Item(c + "", expectedSizes[i]);
      assertEquals(expected, onFloor.get(i));
    }

    floor = new Floor(sizes);
    state = new ApplicationState(1, floor, cb, cb, cb);
    menus = new MenuBar(null, state);
    state.getFloor().remove(new Item("A", 7));
    state.getBins().get(0).add(new Item("A", 7));
    assertEquals(sizes.length - 1, state.getFloor().getContents().size());
    assertEquals(1, state.getBins().get(0).getContents().size());
    problemMenu = menus.getMenu(1);
    problemMenu.getItem(1).doClick();
    onFloor = state.getFloor().getContents();
    assertEquals(20, onFloor.size());
    assertEquals(0, state.getBins().get(0).getContents().size());
    c = 'A';
    boolean different = false;
    for (int i = 0; i < expectedSizes.length && !different; i++, c++) {
      Item old = new Item(c + "", expectedSizes[i]);
      if (!old.equals(onFloor.get(i))) {
        different = true;
        break;
      }
    }
    assertTrue(different);

    class MenuBarTester extends MenuBar {

      String[] values = {"hello", "world", "12", null};
      int next;

      MenuBarTester(InteractiveBinPacking f, ApplicationState state) {
        super(f, state);
      }

      @Override
      String getProblemInstanceNumberFromUser() {
        String v = values[next];
        next++;
        return v;
      }
    }

    ArrayList<Item> oldFloor = (ArrayList<Item>) onFloor.clone();
    floor = new Floor(sizes);
    state = new ApplicationState(1, floor, cb, cb, cb);
    menus = new MenuBarTester(null, state);
    state.getFloor().remove(new Item("A", 7));
    state.getBins().get(0).add(new Item("A", 7));
    assertEquals(sizes.length - 1, state.getFloor().getContents().size());
    assertEquals(1, state.getBins().get(0).getContents().size());
    problemMenu = menus.getMenu(1);
    problemMenu.getItem(2).doClick();
    onFloor = state.getFloor().getContents();
    assertEquals(20, onFloor.size());
    assertEquals(0, state.getBins().get(0).getContents().size());
    c = 'A';
    different = false;
    for (int i = 0; i < expectedSizes.length && !different; i++, c++) {
      Item old = oldFloor.get(i);
      if (!old.equals(onFloor.get(i))) {
        different = true;
        break;
      }
    }
    assertTrue(different);

    oldFloor = (ArrayList<Item>) onFloor.clone();
    problemMenu.getItem(2).doClick();
    onFloor = state.getFloor().getContents();
    assertEquals(20, onFloor.size());
    assertEquals(0, state.getBins().get(0).getContents().size());
    c = 'A';
    for (int i = 0; i < expectedSizes.length; i++, c++) {
      assertEquals(oldFloor.get(i), onFloor.get(i));
    }
  }
}
