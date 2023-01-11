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
import org.junit.jupiter.api.*;

/** JUnit tests for the ApplicationState class. */
public class ApplicationStateTests {

  @Test
  public void testApplicationState() {
    int[] sizes = {7, 2, 18, 3, 6};
    Item[] sortedOrder = {
      new Item("B", 2), new Item("D", 3), new Item("E", 6), new Item("A", 7), new Item("C", 18)
    };
    int[] sizes2 = {5, 1, 21, 8};
    int[] sizes3 = {16, 9, 19, 4, 22};
    class CallBackTestData {
      int setNewInstanceCalled;
      int sortCalled;
      int resetCalled;
    }
    for (int numBins = 1; numBins <= 5; numBins++) {
      Floor f = new Floor(sizes);
      final CallBackTestData callbackData = new CallBackTestData();
      CallBack onSetNew =
          new CallBack() {
            @Override
            public void call() {
              callbackData.setNewInstanceCalled++;
            }
          };
      CallBack onSort =
          new CallBack() {
            @Override
            public void call() {
              callbackData.sortCalled++;
            }
          };
      CallBack onReset =
          new CallBack() {
            @Override
            public void call() {
              callbackData.resetCalled++;
            }
          };
      ApplicationState state = new ApplicationState(numBins, f, onSetNew, onSort, onReset);
      assertEquals(ApplicationState.MODE_PRACTICE, state.getMode());
      assertEquals("Practice Mode", state.getModeString());
      assertEquals("practice", state.getModeName());

      state.setMode(ApplicationState.MODE_FIRST_FIT);
      assertEquals("First-Fit Mode", state.getModeString());
      assertEquals("first-fit", state.getModeName());
      assertEquals(ApplicationState.MODE_FIRST_FIT, state.getMode());
      assertEquals(
          "First-Fit Mode", ApplicationState.modeIntToModeString(ApplicationState.MODE_FIRST_FIT));
      assertEquals(
          "first-fit", ApplicationState.modeIntToModeName(ApplicationState.MODE_FIRST_FIT));

      state.setMode(ApplicationState.MODE_FIRST_FIT_DECREASING);
      assertEquals("First-Fit Decreasing Mode", state.getModeString());
      assertEquals("first-fit decreasing", state.getModeName());
      assertEquals(ApplicationState.MODE_FIRST_FIT_DECREASING, state.getMode());
      assertEquals(
          "First-Fit Decreasing Mode",
          ApplicationState.modeIntToModeString(ApplicationState.MODE_FIRST_FIT_DECREASING));
      assertEquals(
          "first-fit decreasing",
          ApplicationState.modeIntToModeName(ApplicationState.MODE_FIRST_FIT_DECREASING));

      state.setMode(ApplicationState.MODE_BEST_FIT);
      assertEquals("Best-Fit Mode", state.getModeString());
      assertEquals("best-fit", state.getModeName());
      assertEquals(ApplicationState.MODE_BEST_FIT, state.getMode());
      assertEquals(
          "Best-Fit Mode", ApplicationState.modeIntToModeString(ApplicationState.MODE_BEST_FIT));
      assertEquals("best-fit", ApplicationState.modeIntToModeName(ApplicationState.MODE_BEST_FIT));

      state.setMode(ApplicationState.MODE_BEST_FIT_DECREASING);
      assertEquals("Best-Fit Decreasing Mode", state.getModeString());
      assertEquals("best-fit decreasing", state.getModeName());
      assertEquals(ApplicationState.MODE_BEST_FIT_DECREASING, state.getMode());
      assertEquals(
          "Best-Fit Decreasing Mode",
          ApplicationState.modeIntToModeString(ApplicationState.MODE_BEST_FIT_DECREASING));
      assertEquals(
          "best-fit decreasing",
          ApplicationState.modeIntToModeName(ApplicationState.MODE_BEST_FIT_DECREASING));

      state.setMode(ApplicationState.MODE_PRACTICE);
      assertEquals("Practice Mode", state.getModeString());
      assertEquals("practice", state.getModeName());
      assertEquals(ApplicationState.MODE_PRACTICE, state.getMode());
      assertEquals(
          "Practice Mode", ApplicationState.modeIntToModeString(ApplicationState.MODE_PRACTICE));
      assertEquals("practice", ApplicationState.modeIntToModeName(ApplicationState.MODE_PRACTICE));

      state.setMode(ApplicationState.MODE_FIRST_FIT);
      assertEquals("First-Fit Mode", state.getModeString());
      assertEquals("first-fit", state.getModeName());
      assertEquals(ApplicationState.MODE_FIRST_FIT, state.getMode());
      assertTrue(f == state.getFloor());
      ArrayList<Item> items = state.getItems();
      ArrayList<Item> fromFloor = f.getContents();
      assertEquals(5, items.size());
      assertEquals(5, fromFloor.size());
      for (int i = 0; i < fromFloor.size(); i++) {
        assertEquals(fromFloor.get(i), items.get(i));
      }

      // isBestFitBin
      ArrayList<Bin> bins = state.getBins();
      assertEquals(numBins, bins.size());
      for (int i = 0; i < numBins; i++) {
        assertTrue(bins.get(i).isEmpty());
        assertTrue(state.isBestFitBin(new Item("A", 16), bins.get(i)));
        assertFalse(state.isBestFitBin(new Item("A", 101), bins.get(i)));
      }
      assertFalse(state.isBestFitBin(new Item("Z", 2), state.getFloor()));
      if (numBins == 3) {
        bins.get(1).add(new Item("A", 16));
        assertFalse(state.isBestFitBin(new Item("B", 1), bins.get(0)));
        assertTrue(state.isBestFitBin(new Item("B", 1), bins.get(1)));
        assertFalse(state.isBestFitBin(new Item("B", 1), bins.get(2)));
        assertTrue(state.isBestFitBin(new Item("B", 85), bins.get(0)));
        assertFalse(state.isBestFitBin(new Item("B", 85), bins.get(1)));
        assertTrue(state.isBestFitBin(new Item("B", 85), bins.get(2)));
        bins.get(1).removeAll();
      }

      // firstFitBin
      assertEquals(bins.get(0), state.firstFitBin(new Item("A", 16)));
      assertNull(state.firstFitBin(new Item("A", 101)));
      assertEquals(0, callbackData.setNewInstanceCalled);
      assertEquals(0, callbackData.sortCalled);
      assertEquals(0, callbackData.resetCalled);
      state.sortFloor(false);
      assertEquals(0, callbackData.setNewInstanceCalled);
      assertEquals(1, callbackData.sortCalled);
      assertEquals(0, callbackData.resetCalled);
      items = state.getFloor().getContents();
      for (int i = 0; i < sortedOrder.length; i++) {
        assertEquals(sortedOrder[i], items.get(i));
      }
      state.sortFloor(true);
      items = state.getFloor().getContents();
      assertEquals(0, callbackData.setNewInstanceCalled);
      assertEquals(2, callbackData.sortCalled);
      assertEquals(0, callbackData.resetCalled);
      reverse(sortedOrder);
      for (int i = 0; i < sortedOrder.length; i++) {
        assertEquals(sortedOrder[i], items.get(i));
      }
      reverse(sortedOrder);
      state.reset();
      assertEquals(0, callbackData.setNewInstanceCalled);
      assertEquals(2, callbackData.sortCalled);
      assertEquals(1, callbackData.resetCalled);
      assertEquals("First-Fit Mode", state.getModeString());
      assertEquals("first-fit", state.getModeName());
      assertTrue(f == state.getFloor());
      items = state.getItems();
      fromFloor = f.getContents();
      assertEquals(5, items.size());
      assertEquals(5, fromFloor.size());
      for (int i = 0; i < fromFloor.size(); i++) {
        assertEquals(fromFloor.get(i), items.get(i));
      }
      bins = state.getBins();
      assertEquals(numBins, bins.size());
      for (int i = 0; i < numBins; i++) {
        assertTrue(bins.get(i).isEmpty());
      }
      int b = 0;
      for (Item e : items) {
        state.getFloor().remove(e);
        bins.get(b % numBins).add(e);
        b++;
      }
      for (int i = 0; i < numBins; i++) {
        assertFalse(bins.get(i).isEmpty());
      }
      assertTrue(state.getFloor().isEmpty());
      state.reset();
      assertEquals(0, callbackData.setNewInstanceCalled);
      assertEquals(2, callbackData.sortCalled);
      assertEquals(2, callbackData.resetCalled);
      assertEquals("First-Fit Mode", state.getModeString());
      assertEquals("first-fit", state.getModeName());
      assertTrue(f == state.getFloor());
      items = state.getItems();
      fromFloor = f.getContents();
      assertEquals(5, items.size());
      assertEquals(5, fromFloor.size());
      for (int i = 0; i < fromFloor.size(); i++) {
        assertEquals(fromFloor.get(i), items.get(i));
      }
      bins = state.getBins();
      assertEquals(numBins, bins.size());
      for (int i = 0; i < numBins; i++) {
        assertTrue(bins.get(i).isEmpty());
      }
      Floor f2 = new Floor(sizes2);
      state.setNewInstance(f2, "TestCase");
      assertEquals(1, callbackData.setNewInstanceCalled);
      assertEquals(2, callbackData.sortCalled);
      assertEquals(2, callbackData.resetCalled);
      assertEquals("First-Fit Mode", state.getModeString());
      assertEquals("first-fit", state.getModeName());
      assertTrue(f2 == state.getFloor());
      items = state.getItems();
      fromFloor = f2.getContents();
      assertEquals(4, items.size());
      assertEquals(4, fromFloor.size());
      for (int i = 0; i < fromFloor.size(); i++) {
        assertEquals(fromFloor.get(i), items.get(i));
      }
      bins = state.getBins();
      assertEquals(numBins, bins.size());
      for (int i = 0; i < numBins; i++) {
        assertTrue(bins.get(i).isEmpty());
      }
      b = 0;
      for (Item e : items) {
        state.getFloor().remove(e);
        bins.get(b % numBins).add(e);
        b++;
      }
      for (int i = 0; i < numBins; i++) {
        if (i < items.size()) assertFalse(bins.get(i).isEmpty());
        else assertTrue(bins.get(i).isEmpty());
      }
      assertTrue(state.getFloor().isEmpty());
      Floor f3 = new Floor(sizes3);
      state.setNewInstance(f3, "TestCase");
      assertEquals(2, callbackData.setNewInstanceCalled);
      assertEquals(2, callbackData.sortCalled);
      assertEquals(2, callbackData.resetCalled);
      assertEquals("First-Fit Mode", state.getModeString());
      assertEquals("first-fit", state.getModeName());
      assertTrue(f3 == state.getFloor());
      items = state.getItems();
      fromFloor = f3.getContents();
      assertEquals(5, items.size());
      assertEquals(5, fromFloor.size());
      for (int i = 0; i < fromFloor.size(); i++) {
        assertEquals(fromFloor.get(i), items.get(i));
      }
      bins = state.getBins();
      assertEquals(numBins, bins.size());
      for (int i = 0; i < numBins; i++) {
        assertTrue(bins.get(i).isEmpty());
      }

      // Will need to change this if any new modes are introduced.
      IllegalArgumentException thrown =
          assertThrows(IllegalArgumentException.class, () -> state.setMode(5));
      thrown =
          assertThrows(
              IllegalArgumentException.class, () -> ApplicationState.modeIntToModeString(5));
      thrown =
          assertThrows(IllegalArgumentException.class, () -> ApplicationState.modeIntToModeName(5));
    }
  }

  @Test
  public void testApplicationStateLowerBound() {
    CallBack cb =
        new CallBack() {
          @Override
          public void call() {}
        };
    for (int numItems = 1; numItems <= 28; numItems++) {
      int[] sizes = new int[numItems];
      for (int i = 0; i < numItems; i++) sizes[i] = 25;
      Floor f = new Floor(sizes);
      ApplicationState state = new ApplicationState(9, f, cb, cb, cb);
      int expected = (numItems - 1) / 4 + 1;
      assertEquals(expected, state.lowerBound());
    }
  }

  private void reverse(Item[] array) {
    for (int i = 0, j = array.length - 1; i < j; i++, j--) {
      Item temp = array[i];
      array[i] = array[j];
      array[j] = temp;
    }
  }
}
