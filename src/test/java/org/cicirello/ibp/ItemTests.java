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

import org.junit.jupiter.api.*;

/** JUnit tests for the Item class. */
public class ItemTests {

  @Test
  public void testItem() {
    String[] name = {"A", "B", "C"};
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
      Item diffSize = new Item(name[i], size[i] + 1);
      assertNotEquals(item, diffSize);
      Item diffName = new Item("Z", size[i]);
      assertNotEquals(item, diffName);
    }

    IllegalArgumentException thrown =
        assertThrows(IllegalArgumentException.class, () -> new Item("Item Name", 0));
  }
}
