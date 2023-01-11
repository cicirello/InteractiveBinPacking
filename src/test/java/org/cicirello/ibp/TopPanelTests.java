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

import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import org.junit.jupiter.api.*;

/** JUnit tests for the top panel. */
public class TopPanelTests {

  @Test
  public void testTopPanel() {
    int[] sizes = {7, 2};
    CallBack cb =
        new CallBack() {
          @Override
          public void call() {}
        };
    Floor floor = new Floor(sizes);
    ApplicationState state = new ApplicationState(1, floor, cb, cb, cb);
    TopPanel top = new TopPanel(state);
    BorderLayout layout = (BorderLayout) top.getLayout();
    JTextArea text = (JTextArea) layout.getLayoutComponent(BorderLayout.CENTER);
    String expected = "A(7), B(2)";
    assertEquals(expected, text.getText());
    expected = "Not yet in any bins: ";
    JLabel label = (JLabel) layout.getLayoutComponent(BorderLayout.NORTH);
    assertEquals(expected, label.getText());
    state.getFloor().add(new Item("C", 15));
    top.refresh();
    expected = "A(7), B(2), C(15)";
    assertEquals(expected, text.getText());
    expected = "Not yet in any bins: ";
    assertEquals(expected, label.getText());
  }
}
