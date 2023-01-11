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

import javax.swing.JMenu;
import org.junit.jupiter.api.*;

/** JUnit tests for the Mode Menu. */
public class ModeMenuTests {

  @Test
  public void testModeMenu() {
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
    JMenu modeMenu = menus.getMenu(0);
    assertTrue(modeMenu.getItem(0).isSelected());
    for (int i = 1; i <= 4; i++) {
      assertFalse(modeMenu.getItem(i).isSelected());
    }
    JMenu opsMenu = menus.getMenu(2);
    assertTrue(opsMenu.getItem(0).isEnabled());
    assertTrue(opsMenu.getItem(1).isEnabled());
    modeMenu.getItem(1).doClick();
    for (int i = 0; i <= 4; i++) {
      if (i != 1) assertFalse(modeMenu.getItem(i).isSelected());
      else assertTrue(modeMenu.getItem(i).isSelected());
    }
    assertFalse(opsMenu.getItem(0).isEnabled());
    assertFalse(opsMenu.getItem(1).isEnabled());
    assertEquals(sizes.length, state.getFloor().getContents().size());
    assertEquals(0, state.getBins().get(0).getContents().size());
    state.getFloor().remove(new Item("A", 7));
    state.getBins().get(0).add(new Item("A", 7));
    assertEquals(sizes.length - 1, state.getFloor().getContents().size());
    assertEquals(1, state.getBins().get(0).getContents().size());

    modeMenu.getItem(2).doClick();
    for (int i = 0; i <= 4; i++) {
      if (i != 2) assertFalse(modeMenu.getItem(i).isSelected());
      else assertTrue(modeMenu.getItem(i).isSelected());
    }
    assertTrue(opsMenu.getItem(0).isEnabled());
    assertTrue(opsMenu.getItem(1).isEnabled());
    assertEquals(sizes.length, state.getFloor().getContents().size());
    assertEquals(0, state.getBins().get(0).getContents().size());
    state.getFloor().remove(new Item("A", 7));
    state.getBins().get(0).add(new Item("A", 7));
    assertEquals(sizes.length - 1, state.getFloor().getContents().size());
    assertEquals(1, state.getBins().get(0).getContents().size());

    modeMenu.getItem(3).doClick();
    for (int i = 0; i <= 4; i++) {
      if (i != 3) assertFalse(modeMenu.getItem(i).isSelected());
      else assertTrue(modeMenu.getItem(i).isSelected());
    }
    assertFalse(opsMenu.getItem(0).isEnabled());
    assertFalse(opsMenu.getItem(1).isEnabled());
    assertEquals(sizes.length, state.getFloor().getContents().size());
    assertEquals(0, state.getBins().get(0).getContents().size());
    state.getFloor().remove(new Item("A", 7));
    state.getBins().get(0).add(new Item("A", 7));
    assertEquals(sizes.length - 1, state.getFloor().getContents().size());
    assertEquals(1, state.getBins().get(0).getContents().size());

    modeMenu.getItem(4).doClick();
    for (int i = 0; i <= 4; i++) {
      if (i != 4) assertFalse(modeMenu.getItem(i).isSelected());
      else assertTrue(modeMenu.getItem(i).isSelected());
    }
    assertTrue(opsMenu.getItem(0).isEnabled());
    assertTrue(opsMenu.getItem(1).isEnabled());
    assertEquals(sizes.length, state.getFloor().getContents().size());
    assertEquals(0, state.getBins().get(0).getContents().size());
    state.getFloor().remove(new Item("A", 7));
    state.getBins().get(0).add(new Item("A", 7));
    assertEquals(sizes.length - 1, state.getFloor().getContents().size());
    assertEquals(1, state.getBins().get(0).getContents().size());

    modeMenu.getItem(3).doClick();
    assertFalse(opsMenu.getItem(0).isEnabled());
    assertFalse(opsMenu.getItem(1).isEnabled());
    assertEquals(sizes.length, state.getFloor().getContents().size());
    assertEquals(0, state.getBins().get(0).getContents().size());
    state.getFloor().remove(new Item("A", 7));
    state.getBins().get(0).add(new Item("A", 7));
    assertEquals(sizes.length - 1, state.getFloor().getContents().size());
    assertEquals(1, state.getBins().get(0).getContents().size());

    modeMenu.getItem(0).doClick();
    assertTrue(opsMenu.getItem(0).isEnabled());
    assertTrue(opsMenu.getItem(1).isEnabled());
    assertEquals(sizes.length - 1, state.getFloor().getContents().size());
    assertEquals(1, state.getBins().get(0).getContents().size());
  }
}
