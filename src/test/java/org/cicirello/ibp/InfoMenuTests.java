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

import java.awt.GraphicsEnvironment;
import javax.swing.JMenu;
import org.junit.jupiter.api.*;

/** JUnit tests for the Info Menu. */
public class InfoMenuTests {

  @Test
  public void testInfoMenu() {
    InfoDialog.setTestingMode();

    int[] sizes = {7, 2, 18, 3, 6};
    CallBack cb =
        new CallBack() {
          @Override
          public void call() {}
        };
    Floor floor = new Floor(sizes);
    ApplicationState state = new ApplicationState(1, floor, cb, cb, cb);
    MenuBar menus = new MenuBar(null, state);
    assertNull(menus.getTutorial());
    assertNull(menus.getHelp());
    JMenu infoMenu = menus.getMenu(4);

    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    if (!ge.isHeadless()) {

      infoMenu.getItem(0).doClick();
      assertNotNull(menus.getTutorial());
      assertEquals(1, menus.getTutorial().getActivationCount());

      infoMenu.getItem(1).doClick();
      assertNotNull(menus.getHelp());
      assertEquals(1, menus.getHelp().getActivationCount());

      infoMenu.getItem(0).doClick();
      assertNotNull(menus.getTutorial());
      assertEquals(2, menus.getTutorial().getActivationCount());

      infoMenu.getItem(0).doClick();
      assertNotNull(menus.getTutorial());
      assertEquals(3, menus.getTutorial().getActivationCount());

      infoMenu.getItem(1).doClick();
      assertNotNull(menus.getHelp());
      assertEquals(2, menus.getHelp().getActivationCount());

      menus.getTutorial().dispose();
      menus.getHelp().dispose();
    }
  }
}
