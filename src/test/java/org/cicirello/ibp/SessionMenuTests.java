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

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import javax.swing.JOptionPane;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

/** JUnit tests for the Session Menu. */
public class SessionMenuTests {

  @Test
  public void testSessionMenuSaveSession(@TempDir File sub) {
    class MenuBarTester extends MenuBar {
      int errorCount;
      int confirmCount;
      int confirmSaveResponse;

      MenuBarTester(InteractiveBinPacking f, ApplicationState state) {
        super(f, state);
        errorCount = confirmCount = 0;
      }

      @Override
      void displayErrorMessage(String message) {
        errorCount++;
      }

      @Override
      int confirmSave() {
        confirmCount++;
        return confirmSaveResponse;
      }

      void setResponse(int response) {
        confirmSaveResponse = response;
      }
    }
    int[] sizes = {7, 2, 18, 3, 6};
    CallBack cb =
        new CallBack() {
          @Override
          public void call() {}
        };
    Floor floor = new Floor(sizes);
    ApplicationState state = new ApplicationState(1, floor, cb, cb, cb);
    MenuBarTester menus = new MenuBarTester(null, state);

    /* // OLD JUnit 4... JUnit 5 approach has this as a field annotated.
    File sub = tempFolder.newFolder("sub");
    */
    File logFile = new File(sub, "testlog.ibp");
    menus.saveSessionLog(logFile);
    int index = 0;
    try (FileReader in = new FileReader(logFile, StandardCharsets.UTF_8)) {
      String session = state.loadSessionLog(in);
      index = session.indexOf("SAVE_SESSION_LOG");
      assertTrue(index >= 0);
    } catch (IOException ex) {
      fail();
    }
    assertEquals(0, menus.errorCount);
    assertEquals(0, menus.confirmCount);

    File without = new File(sub, "without.txt");
    File with = new File(sub, "without.txt.ibp");
    menus.saveSessionLog(without);
    try (FileReader in = new FileReader(with, StandardCharsets.UTF_8)) {
      String session = state.loadSessionLog(in);
      index++;
      index = session.indexOf("LOAD_SESSION_LOG", index);
      assertTrue(index >= 0);
      index++;
      index = session.indexOf("SAVE_SESSION_LOG", index);
      assertTrue(index >= 0);
    } catch (IOException ex) {
      fail();
    }
    assertEquals(0, menus.errorCount);
    assertEquals(0, menus.confirmCount);

    menus.setResponse(JOptionPane.YES_OPTION);
    menus.saveSessionLog(logFile);
    try (FileReader in = new FileReader(logFile, StandardCharsets.UTF_8)) {
      String session = state.loadSessionLog(in);
      index++;
      index = session.indexOf("LOAD_SESSION_LOG", index);
      assertTrue(index >= 0);
      index++;
      index = session.indexOf("SAVE_SESSION_LOG", index);
      assertTrue(index >= 0);
    } catch (IOException ex) {
      fail();
    }
    assertEquals(0, menus.errorCount);
    assertEquals(1, menus.confirmCount);

    menus.setResponse(JOptionPane.NO_OPTION);
    menus.saveSessionLog(logFile);
    try (FileReader in = new FileReader(logFile, StandardCharsets.UTF_8)) {
      String session = state.loadSessionLog(in);
      index++;
      int tempIndex = session.indexOf("LOAD_SESSION_LOG", index);
      assertTrue(tempIndex < 0);
      tempIndex = session.indexOf("SAVE_SESSION_LOG", index);
      assertTrue(tempIndex < 0);
    } catch (IOException ex) {
      fail();
    }
    assertEquals(0, menus.errorCount);
    assertEquals(2, menus.confirmCount);

    try {
      menus.setResponse(JOptionPane.YES_OPTION);
      File dir = new File(InteractiveBinPacking.class.getResource("testlogs/dir.ibp").toURI());
      menus.saveSessionLog(dir);
      assertEquals(1, menus.errorCount);
      assertEquals(3, menus.confirmCount);
    } catch (URISyntaxException ex) {
      fail();
    }
  }

  @Test
  public void testSessionMenuLoadSession() {
    class MenuBarTester extends MenuBar {
      int errorCount;

      MenuBarTester(InteractiveBinPacking f, ApplicationState state) {
        super(f, state);
        errorCount = 0;
      }

      @Override
      void displayErrorMessage(String message) {
        errorCount++;
      }
    }
    int[] sizes = {7, 2, 18, 3, 6};
    CallBack cb =
        new CallBack() {
          @Override
          public void call() {}
        };
    Floor floor = new Floor(sizes);
    ApplicationState state = new ApplicationState(1, floor, cb, cb, cb);
    MenuBarTester menus = new MenuBarTester(null, state);
    File nonExistent = new File("ThisFileDoesNotExist.ibp");
    menus.loadSessionLog(nonExistent);
    assertEquals(1, menus.errorCount);
    try {
      File wrongType = new File(InteractiveBinPacking.class.getResource("html/about.html").toURI());
      menus.loadSessionLog(wrongType);
      assertEquals(2, menus.errorCount);
    } catch (URISyntaxException ex) {
      fail();
    }
    try {
      File malformed =
          new File(InteractiveBinPacking.class.getResource("testlogs/malformed.ibp").toURI());
      menus.loadSessionLog(malformed);
      assertEquals(3, menus.errorCount);
    } catch (URISyntaxException ex) {
      fail();
    }
    try {
      File dir = new File(InteractiveBinPacking.class.getResource("testlogs/dir.ibp").toURI());
      menus.loadSessionLog(dir);
      assertEquals(4, menus.errorCount);
    } catch (URISyntaxException ex) {
      fail();
    }
  }
}
