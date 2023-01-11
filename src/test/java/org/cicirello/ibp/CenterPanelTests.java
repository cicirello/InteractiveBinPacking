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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.junit.jupiter.api.*;

/** JUnit tests for the center panel. */
public class CenterPanelTests {

  @Test
  public void testCenterPanel() {
    int[] sizes = {7, 2};
    CallBack cb =
        new CallBack() {
          @Override
          public void call() {}
        };
    int[] binCounts = {1, 2, 9, 10};
    for (int b = 0; b < binCounts.length; b++) {
      int numBins = binCounts[b];
      Floor floor = new Floor(sizes);
      ApplicationState state = new ApplicationState(numBins, floor, cb, cb, cb);
      CenterPanel mid = new CenterPanel(numBins, state);
      Component[] c = mid.getComponents();
      assertEquals(numBins, c.length);
      for (int i = 0; i < c.length; i++) {
        JPanel row = (JPanel) c[i];
        assertEquals("Bin " + (i + 1) + ":", ((JLabel) row.getComponent(0)).getText().trim());
        assertEquals("empty", ((JTextField) row.getComponent(1)).getText().trim());
      }
      state.getBins().get(numBins - 1).add(new Item("C", 10));
      mid.refresh();
      for (int i = 0; i < c.length; i++) {
        JPanel row = (JPanel) c[i];
        assertEquals("Bin " + (i + 1) + ":", ((JLabel) row.getComponent(0)).getText().trim());
        if (i != numBins - 1) {
          assertEquals("empty", ((JTextField) row.getComponent(1)).getText().trim());
        } else {
          assertEquals("C(10)", ((JTextField) row.getComponent(1)).getText().trim());
        }
      }
      state.getBins().get(numBins - 1).add(new Item("D", 13));
      mid.refresh();
      for (int i = 0; i < c.length; i++) {
        JPanel row = (JPanel) c[i];
        assertEquals("Bin " + (i + 1) + ":", ((JLabel) row.getComponent(0)).getText().trim());
        if (i != numBins - 1) {
          assertEquals("empty", ((JTextField) row.getComponent(1)).getText().trim());
        } else {
          assertEquals("C(10), D(13)", ((JTextField) row.getComponent(1)).getText().trim());
        }
      }
    }
  }
}
