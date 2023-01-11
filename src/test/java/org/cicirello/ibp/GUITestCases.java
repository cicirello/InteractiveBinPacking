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
import java.awt.Component;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import org.junit.jupiter.api.*;

/**
 * JUnit tests for all of the GUI classes of the Interactive Bin Packing Application. These test
 * cases automates testing nearly all of the GUI, simulating button presses and menu item clicks,
 * etc via Swing's doClick() method. The only GUI elements not tested via JUnit, which will require
 * manual testing are cases where dialog boxes are displayed to inform the user of something, as
 * well as hyperlinks within the Tutorial and Help dialogs.
 */
public class GUITestCases {

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

  @Test
  public void testBottomPanel() {
    int[] sizes = {7, 2, 8, 5};
    int[] sizes2 = {6, 3, 18, 4, 1};
    CallBack cb =
        new CallBack() {
          @Override
          public void call() {}
        };
    for (int numBins = 1; numBins <= 3; numBins++) {
      class CallCount {
        int moveCount;
      }
      final CallCount cc = new CallCount();
      Floor floor = new Floor(sizes);
      ApplicationState state = new ApplicationState(numBins, floor, cb, cb, cb);
      BottomPanel bottom =
          new BottomPanel(
              null,
              state,
              new CallBack() {
                @Override
                public void call() {
                  cc.moveCount++;
                }
              });
      Component[] c = bottom.getComponents();
      JPanel movePanel = (JPanel) c[0];
      assertEquals("Move:", ((JLabel) movePanel.getComponent(0)).getText().trim());
      @SuppressWarnings("unchecked")
      JComboBox<Item> move = (JComboBox<Item>) movePanel.getComponent(1);
      assertEquals(sizes.length, move.getItemCount());
      char x = 'A';
      for (int i = 0; i < sizes.length; i++, x++) {
        assertEquals(new Item(x + "", sizes[i]), move.getItemAt(i));
      }
      JPanel destPanel = (JPanel) c[1];
      assertEquals("To:", ((JLabel) destPanel.getComponent(0)).getText().trim());
      @SuppressWarnings("unchecked")
      JComboBox<Bin> to = (JComboBox<Bin>) destPanel.getComponent(1);
      assertEquals(numBins + 1, to.getItemCount());
      for (int i = 0; i < numBins; i++) {
        assertEquals("Bin " + (i + 1), to.getItemAt(i).toString());
        assertTrue(to.getItemAt(i).isEmpty());
      }
      assertEquals("Floor", to.getItemAt(numBins).toString());
      assertEquals(floor, to.getItemAt(numBins));
      JButton mButton = (JButton) c[2];
      mButton.doClick();
      x = 'A';
      for (int i = 0; i < sizes.length; i++, x++) {
        assertEquals(new Item(x + "", sizes[i]), move.getItemAt(i));
      }
      for (int i = 0; i < numBins; i++) {
        assertEquals("Bin " + (i + 1), to.getItemAt(i).toString());
      }
      assertEquals("Floor", to.getItemAt(numBins).toString());
      assertTrue(to.getItemAt(0).contains(new Item("A", sizes[0])));
      assertFalse(to.getItemAt(numBins).contains(new Item("A", sizes[0])));
      if (numBins > 1) {
        to.setSelectedIndex(1);
        mButton.doClick();
        x = 'A';
        for (int i = 0; i < sizes.length; i++, x++) {
          assertEquals(new Item(x + "", sizes[i]), move.getItemAt(i));
        }
        for (int i = 0; i < numBins; i++) {
          assertEquals("Bin " + (i + 1), to.getItemAt(i).toString());
        }
        assertEquals("Floor", to.getItemAt(numBins).toString());
        assertTrue(to.getItemAt(1).contains(new Item("A", sizes[0])));
        assertFalse(to.getItemAt(0).contains(new Item("A", sizes[0])));
        assertFalse(to.getItemAt(numBins).contains(new Item("A", sizes[0])));
      }
      move.setSelectedIndex(1);
      to.setSelectedIndex(0);
      mButton.doClick();
      x = 'A';
      for (int i = 0; i < sizes.length; i++, x++) {
        assertEquals(new Item(x + "", sizes[i]), move.getItemAt(i));
      }
      for (int i = 0; i < numBins; i++) {
        assertEquals("Bin " + (i + 1), to.getItemAt(i).toString());
      }
      assertEquals("Floor", to.getItemAt(numBins).toString());
      assertTrue(to.getItemAt(0).contains(new Item("B", sizes[1])));
      assertTrue(to.getItemAt(numBins > 1 ? 1 : 0).contains(new Item("A", sizes[0])));
      assertFalse(to.getItemAt(numBins).contains(new Item("A", sizes[0])));
      assertFalse(to.getItemAt(numBins).contains(new Item("B", sizes[1])));
      to.setSelectedIndex(numBins);
      mButton.doClick();
      x = 'A';
      for (int i = 0; i < sizes.length; i++, x++) {
        assertEquals(new Item(x + "", sizes[i]), move.getItemAt(i));
      }
      for (int i = 0; i < numBins; i++) {
        assertEquals("Bin " + (i + 1), to.getItemAt(i).toString());
      }
      assertEquals("Floor", to.getItemAt(numBins).toString());
      assertFalse(to.getItemAt(0).contains(new Item("B", sizes[1])));
      assertTrue(to.getItemAt(numBins > 1 ? 1 : 0).contains(new Item("A", sizes[0])));
      assertFalse(to.getItemAt(numBins).contains(new Item("A", sizes[0])));
      assertTrue(to.getItemAt(numBins).contains(new Item("B", sizes[1])));
      JButton resetButton = (JButton) c[3];
      resetButton.doClick();
      x = 'A';
      for (int i = 0; i < sizes.length; i++, x++) {
        assertEquals(new Item(x + "", sizes[i]), move.getItemAt(i));
      }
      for (int i = 0; i < numBins; i++) {
        assertEquals("Bin " + (i + 1), to.getItemAt(i).toString());
        assertTrue(to.getItemAt(i).isEmpty());
      }
      assertEquals("Floor", to.getItemAt(numBins).toString());
      assertEquals(floor, to.getItemAt(numBins));
      ArrayList<Item> onFloor = floor.getContents();
      assertEquals(sizes.length, onFloor.size());
      x = 'A';
      for (int i = 0; i < sizes.length; i++, x++) {
        assertEquals(new Item(x + "", sizes[i]), onFloor.get(i));
      }
      move.setSelectedIndex(0);
      to.setSelectedIndex(0);
      mButton.doClick();
      Floor floor2 = new Floor(sizes2);
      state.setNewInstance(floor2, "TestCase");
      bottom.refresh();
      assertEquals(sizes2.length, move.getItemCount());
      x = 'A';
      for (int i = 0; i < sizes2.length; i++, x++) {
        assertEquals(new Item(x + "", sizes2[i]), move.getItemAt(i));
      }
      assertEquals(numBins + 1, to.getItemCount());
      for (int i = 0; i < numBins; i++) {
        assertEquals("Bin " + (i + 1), to.getItemAt(i).toString());
        assertTrue(to.getItemAt(i).isEmpty());
      }
      assertEquals("Floor", to.getItemAt(numBins).toString());
      assertEquals(floor2, to.getItemAt(numBins));
    }
  }

  @Test
  public void testBottomPanelFF() {
    int[] sizes = {7, 2, 8, 5, 79};
    // int[] sizes2 = { 6, 3, 18, 4, 1 };
    CallBack cb =
        new CallBack() {
          @Override
          public void call() {}
        };
    int numBins = 3;
    class CallCount {
      int moveCount;
    }
    final CallCount cc = new CallCount();
    Floor floor = new Floor(sizes);
    ApplicationState state = new ApplicationState(numBins, floor, cb, cb, cb);
    BottomPanelTestVersion bottom =
        new BottomPanelTestVersion(
            null,
            state,
            new CallBack() {
              @Override
              public void call() {
                cc.moveCount++;
              }
            });
    Component[] c = bottom.getComponents();
    JPanel movePanel = (JPanel) c[0];
    @SuppressWarnings("unchecked")
    JComboBox<Item> move = (JComboBox<Item>) movePanel.getComponent(1);
    JPanel destPanel = (JPanel) c[1];
    @SuppressWarnings("unchecked")
    JComboBox<Bin> to = (JComboBox<Bin>) destPanel.getComponent(1);
    JButton mButton = (JButton) c[2];

    state.setMode(ApplicationState.MODE_FIRST_FIT);
    bottom.setExpectedModeString("First-Fit Mode");
    move.setSelectedIndex(1);
    to.setSelectedIndex(0);
    mButton.doClick();
    assertTrue(bottom.didErrorMessage());
    bottom.resetError();

    move.setSelectedIndex(0);
    to.setSelectedIndex(0);
    mButton.doClick();
    assertFalse(bottom.didErrorMessage());

    move.setSelectedIndex(1);
    to.setSelectedIndex(1);
    mButton.doClick();
    assertTrue(bottom.didErrorMessage());
    bottom.resetError();

    bottom.setExpectedModeString("Item already at destination");
    move.setSelectedIndex(0);
    to.setSelectedIndex(0);
    mButton.doClick();
    assertTrue(bottom.didErrorMessage());
    bottom.resetError();
    bottom.setExpectedModeString("First-Fit Mode");

    move.setSelectedIndex(1);
    to.setSelectedIndex(0);
    mButton.doClick();
    assertFalse(bottom.didErrorMessage());

    move.setSelectedIndex(2);
    to.setSelectedIndex(0);
    mButton.doClick();
    assertFalse(bottom.didErrorMessage());

    move.setSelectedIndex(3);
    to.setSelectedIndex(0);
    mButton.doClick();
    assertFalse(bottom.didErrorMessage());

    state.setMode(ApplicationState.MODE_PRACTICE);
    bottom.setExpectedModeString("Insufficient capacity");
    move.setSelectedIndex(4);
    to.setSelectedIndex(0);
    mButton.doClick();
    assertTrue(bottom.didErrorMessage());
    bottom.resetError();

    state.setMode(ApplicationState.MODE_FIRST_FIT);
    bottom.setExpectedModeString("First-Fit Mode");
    move.setSelectedIndex(4);
    to.setSelectedIndex(1);
    mButton.doClick();
    assertFalse(bottom.didErrorMessage());
  }

  @Test
  public void testBottomPanelModesFFD() {
    int[] sizes = {7, 2, 8, 5};
    int[] sizes2 = {6, 3, 18, 4, 1};
    CallBack cb =
        new CallBack() {
          @Override
          public void call() {}
        };
    int numBins = 3;
    class CallCount {
      int moveCount;
    }
    final CallCount cc = new CallCount();
    Floor floor = new Floor(sizes);
    ApplicationState state = new ApplicationState(numBins, floor, cb, cb, cb);
    BottomPanelTestVersion bottom =
        new BottomPanelTestVersion(
            null,
            state,
            new CallBack() {
              @Override
              public void call() {
                cc.moveCount++;
              }
            });
    Component[] c = bottom.getComponents();
    JPanel movePanel = (JPanel) c[0];
    @SuppressWarnings("unchecked")
    JComboBox<Item> move = (JComboBox<Item>) movePanel.getComponent(1);
    JPanel destPanel = (JPanel) c[1];
    @SuppressWarnings("unchecked")
    JComboBox<Bin> to = (JComboBox<Bin>) destPanel.getComponent(1);
    JButton mButton = (JButton) c[2];

    state.setMode(ApplicationState.MODE_FIRST_FIT_DECREASING);
    bottom.setExpectedModeString("First-Fit Decreasing Mode");
    move.setSelectedIndex(1);
    to.setSelectedIndex(0);
    mButton.doClick();
    assertTrue(bottom.didErrorMessage());
    bottom.resetError();

    move.setSelectedIndex(0);
    to.setSelectedIndex(0);
    mButton.doClick();
    assertTrue(bottom.didErrorMessage());
    bottom.resetError();

    move.setSelectedIndex(2);
    to.setSelectedIndex(0);
    mButton.doClick();
    assertFalse(bottom.didErrorMessage());

    move.setSelectedIndex(0);
    to.setSelectedIndex(1);
    mButton.doClick();
    assertTrue(bottom.didErrorMessage());
    bottom.resetError();

    move.setSelectedIndex(0);
    to.setSelectedIndex(0);
    mButton.doClick();
    assertFalse(bottom.didErrorMessage());

    move.setSelectedIndex(3);
    to.setSelectedIndex(0);
    mButton.doClick();
    assertFalse(bottom.didErrorMessage());

    move.setSelectedIndex(1);
    to.setSelectedIndex(0);
    mButton.doClick();
    assertFalse(bottom.didErrorMessage());
  }

  @Test
  public void testBottomPanelModesBFD() {
    int[] sizes = {7, 2, 8, 5};
    int[] sizes2 = {6, 3, 18, 4, 1};
    CallBack cb =
        new CallBack() {
          @Override
          public void call() {}
        };
    int numBins = 3;
    class CallCount {
      int moveCount;
    }
    final CallCount cc = new CallCount();
    Floor floor = new Floor(sizes);
    ApplicationState state = new ApplicationState(numBins, floor, cb, cb, cb);
    BottomPanelTestVersion bottom =
        new BottomPanelTestVersion(
            null,
            state,
            new CallBack() {
              @Override
              public void call() {
                cc.moveCount++;
              }
            });
    Component[] c = bottom.getComponents();
    JPanel movePanel = (JPanel) c[0];
    @SuppressWarnings("unchecked")
    JComboBox<Item> move = (JComboBox<Item>) movePanel.getComponent(1);
    JPanel destPanel = (JPanel) c[1];
    @SuppressWarnings("unchecked")
    JComboBox<Bin> to = (JComboBox<Bin>) destPanel.getComponent(1);
    JButton mButton = (JButton) c[2];

    state.setMode(ApplicationState.MODE_BEST_FIT_DECREASING);
    bottom.setExpectedModeString("Best-Fit Decreasing Mode");
    move.setSelectedIndex(1);
    to.setSelectedIndex(0);
    mButton.doClick();
    assertTrue(bottom.didErrorMessage());
    bottom.resetError();

    move.setSelectedIndex(2);
    to.setSelectedIndex(0);
    mButton.doClick();
    assertFalse(bottom.didErrorMessage());

    move.setSelectedIndex(0);
    to.setSelectedIndex(1);
    mButton.doClick();
    assertTrue(bottom.didErrorMessage());
    bottom.resetError();

    move.setSelectedIndex(0);
    to.setSelectedIndex(0);
    mButton.doClick();
    assertFalse(bottom.didErrorMessage());

    move.setSelectedIndex(3);
    to.setSelectedIndex(0);
    mButton.doClick();
    assertFalse(bottom.didErrorMessage());

    move.setSelectedIndex(1);
    to.setSelectedIndex(0);
    mButton.doClick();
    assertFalse(bottom.didErrorMessage());
  }

  @Test
  public void testBottomPanelModesBF() {
    int[] sizes = {7, 2, 8, 5};
    int[] sizes2 = {6, 3, 18, 4, 1};
    CallBack cb =
        new CallBack() {
          @Override
          public void call() {}
        };
    int numBins = 3;
    class CallCount {
      int moveCount;
    }
    final CallCount cc = new CallCount();
    Floor floor = new Floor(sizes);
    ApplicationState state = new ApplicationState(numBins, floor, cb, cb, cb);
    BottomPanelTestVersion bottom =
        new BottomPanelTestVersion(
            null,
            state,
            new CallBack() {
              @Override
              public void call() {
                cc.moveCount++;
              }
            });
    Component[] c = bottom.getComponents();
    JPanel movePanel = (JPanel) c[0];
    @SuppressWarnings("unchecked")
    JComboBox<Item> move = (JComboBox<Item>) movePanel.getComponent(1);
    JPanel destPanel = (JPanel) c[1];
    @SuppressWarnings("unchecked")
    JComboBox<Bin> to = (JComboBox<Bin>) destPanel.getComponent(1);
    JButton mButton = (JButton) c[2];

    state.setMode(ApplicationState.MODE_BEST_FIT);
    bottom.setExpectedModeString("Best-Fit Mode");
    move.setSelectedIndex(0);
    to.setSelectedIndex(0);
    mButton.doClick();
    assertFalse(bottom.didErrorMessage());
    move.setSelectedIndex(1);
    to.setSelectedIndex(1);
    mButton.doClick();
    assertTrue(bottom.didErrorMessage());
    bottom.resetError();
    move.setSelectedIndex(2);
    to.setSelectedIndex(0);
    mButton.doClick();
    assertTrue(bottom.didErrorMessage());
    bottom.resetError();

    move.setSelectedIndex(1);
    to.setSelectedIndex(0);
    mButton.doClick();
    assertFalse(bottom.didErrorMessage());

    move.setSelectedIndex(2);
    to.setSelectedIndex(0);
    mButton.doClick();
    assertFalse(bottom.didErrorMessage());

    move.setSelectedIndex(3);
    to.setSelectedIndex(0);
    mButton.doClick();
    assertFalse(bottom.didErrorMessage());
  }

  private static class BottomPanelTestVersion extends BottomPanel {
    String expectedModeString;
    boolean errorMessage;

    public BottomPanelTestVersion(
        InteractiveBinPacking f, ApplicationState state, CallBack onMove) {
      super(f, state, onMove);
    }

    public void setExpectedModeString(String s) {
      expectedModeString = s;
    }

    @Override
    void displayMessage(String message, String modeString, boolean success) {
      assertEquals(expectedModeString, modeString);
      errorMessage = !success;
    }

    boolean didErrorMessage() {
      return errorMessage;
    }

    void resetError() {
      errorMessage = false;
    }
  }
}
