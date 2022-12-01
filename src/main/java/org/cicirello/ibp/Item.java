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

/**
 * This class represents a single object for the bin packing problem (i.e., an object that must be
 * packed into one of the bins).
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public class Item {

  private String name;
  private int size;

  /**
   * Construct an item.
   *
   * @param name A name for the item.
   * @param size The size of the item (i.e., number of units of bin capacity needed by the item).
   * @throws IllegalArgumentException if size is not positive
   */
  public Item(String name, int size) {
    if (size <= 0) throw new IllegalArgumentException("size must be positive");
    this.name = name;
    this.size = size;
  }

  /**
   * Gets the size of the item (i.e., number of units of bin capacity needed by the item).
   *
   * @return the size of the item
   */
  public int size() {
    return size;
  }

  /**
   * Gets the name of the item.
   *
   * @return the name of the item
   */
  public String name() {
    return name;
  }

  @Override
  public boolean equals(Object other) {
    if (!(other instanceof Item)) return false;
    Item i = (Item) other;
    return i.name.equals(name) && i.size == size;
  }

  @Override
  public int hashCode() {
    return name.hashCode() * 31 + size;
  }

  @Override
  public String toString() {
    return name + "(" + size + ")";
  }
}
