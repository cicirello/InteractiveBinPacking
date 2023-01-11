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

import java.awt.Component;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.junit.jupiter.api.*;

/** JUnit tests for the west panel. */
public class WestPanelTests {

  @Test
  public void testWestPanel() {
    int[] sizes = {7, 2};
    int[] sizes2 = {5, 8};
    CallBack cb =
        new CallBack() {
          @Override
          public void call() {}
        };
    for (int numBins = 1; numBins <= 3; numBins++) {
      Floor floor = new Floor(sizes);
      ApplicationState state = new ApplicationState(numBins, floor, cb, cb, cb);
      WestPanel west = new WestPanel(numBins, state);
      Component[] c = west.getComponents();
      assertEquals(numBins, c.length);
      for (int i = 0; i < c.length; i++) {
        JPanel row = (JPanel) c[i];
        assertEquals("Capacity: 100", ((JLabel) row.getComponent(0)).getText().trim());
        assertEquals("Used: 0", ((JLabel) row.getComponent(1)).getText().trim());
      }
      state.getBins().get(0).add(new Item("C", 10));
      west.refresh();
      for (int i = 0; i < c.length; i++) {
        JPanel row = (JPanel) c[i];
        assertEquals("Capacity: 100", ((JLabel) row.getComponent(0)).getText().trim());
        if (i != 0) {
          assertEquals("Used: 0", ((JLabel) row.getComponent(1)).getText().trim());
        } else {
          assertEquals("Used: 10", ((JLabel) row.getComponent(1)).getText().trim());
        }
      }
      state.getBins().get(0).add(new Item("D", 90));
      west.refresh();
      for (int i = 0; i < c.length; i++) {
        JPanel row = (JPanel) c[i];
        assertEquals("Capacity: 100", ((JLabel) row.getComponent(0)).getText().trim());
        if (i != 0) {
          assertEquals("Used: 0", ((JLabel) row.getComponent(1)).getText().trim());
        } else {
          assertEquals("Used: 100", ((JLabel) row.getComponent(1)).getText().trim());
        }
      }
    }
    Floor floor = new Floor(sizes);
    int numBins = 4;
    ApplicationState state = new ApplicationState(numBins, floor, cb, cb, cb);
    ArrayList<Bin> bins = state.getBins();
    bins.get(0).add(new Item("C", 5));
    bins.get(1).add(new Item("D", 10));
    bins.get(2).add(new Item("E", 100));
    WestPanel west = new WestPanel(numBins, state);
    Component[] c = west.getComponents();
    assertEquals(numBins, c.length);
    for (int i = 0; i < c.length; i++) {
      JPanel row = (JPanel) c[i];
      assertEquals("Capacity: 100", ((JLabel) row.getComponent(0)).getText().trim());
      switch (i) {
        case 0:
          assertEquals("Used: 5", ((JLabel) row.getComponent(1)).getText().trim());
          break;
        case 1:
          assertEquals("Used: 10", ((JLabel) row.getComponent(1)).getText().trim());
          break;
        case 2:
          assertEquals("Used: 100", ((JLabel) row.getComponent(1)).getText().trim());
          break;
        case 3:
          assertEquals("Used: 0", ((JLabel) row.getComponent(1)).getText().trim());
          break;
      }
    }
  }
}
