/*
 * Interactive Bin Packing.
 * Copyright (C) 2021-2023 Vincent A. Cicirello
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * This class is used to maintain a log of the activity of a student using the application during a
 * session, enabling a student to submit the log to an instructor for verification of their usage of
 * the application, such as which problem instances they interacted with, what modes were used,
 * whether they solved them to completion using heuristic modes specified by the instructor in an
 * assignment, etc.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, <a
 *     href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public final class SessionLog implements Serializable {

  private static final long serialVersionUID = 1L;

  /** List of log records. */
  private final RecordList records;

  /** Counts of successful moves in each mode. */
  private final int[] successfulMoves;

  /** Counts of failed moves in each mode. */
  private final int[] failedMoves;

  private transient int currentMode;
  private transient String currentInstance;
  private transient ArrayList<Item> currentItemSequence;
  private transient ArrayList<Integer> currentBinSequence;

  /** Initialize the SessionLog. */
  public SessionLog() {
    records = new RecordList();
    addEntry("SESSION", "START");
    currentMode = 0;
    currentInstance = "Default";
    successfulMoves = new int[5];
    failedMoves = new int[5];
    currentItemSequence = new ArrayList<Item>();
    currentBinSequence = new ArrayList<Integer>();
  }

  /**
   * Adds an entry to the session log. Also does any entry type specific operations.
   *
   * @param type The type of entry.
   * @param data The data to associate with the entry.
   */
  public void addEntry(String type, String data) {
    if (type.equals("SET_MODE")) {
      currentMode = Integer.parseInt(data);
      data = ApplicationState.modeIntToModeName(currentMode);
      currentItemSequence.clear();
      currentBinSequence.clear();
    } else if (type.equals("SELECT_INSTANCE")) {
      currentInstance = data;
      currentItemSequence.clear();
      currentBinSequence.clear();
    } else if (type.equals("RESET")) {
      currentItemSequence.clear();
      currentBinSequence.clear();
    }
    records.add(new LogRecord(type, data));
  }

  /** Records that a heuristic mode was completed. */
  public void recordHeuristicModeCompletion() {
    String data = "ModeNum=" + currentMode;
    data += ", Instance=" + currentInstance;
    data += ", Mode=" + ApplicationState.modeIntToModeName(currentMode);
    addEntry("COMPLETED", data);

    data = "ItemSequence=";
    for (Item i : currentItemSequence) {
      data += i.name() + " " + i.size() + " ";
    }
    data = data.strip();
    data += ", BinSequence=";
    for (int b : currentBinSequence) {
      data += b + " ";
    }
    data = data.strip();
    addEntry("SOLUTION", data);

    currentItemSequence.clear();
    currentBinSequence.clear();
  }

  /**
   * Records am item move, and increments the move counter for the current mode.
   *
   * @param item The item that was moved.
   * @param bin The bin the item was moved to.
   */
  public void recordMove(Item item, Bin bin) {
    successfulMoves[currentMode]++;
    currentItemSequence.add(item);
    currentBinSequence.add(bin.getBinNumber());
  }

  /** Increments the failed move counter for the current mode. */
  public void recordFailedMove() {
    failedMoves[currentMode]++;
  }

  /**
   * Formats the session log data in html for viewing within the application.
   *
   * @return session log formatted in html for in application viewing
   */
  public String formatSessionLog() {
    return createSessionLogFormatter().toString();
  }

  /*
   * package private to facilitate testing
   */
  final SessionLogFormatter createSessionLogFormatter() {
    return new SessionLogFormatter(records, successfulMoves, failedMoves);
  }

  @Override
  public boolean equals(Object other) {
    if (other == null || !(other instanceof SessionLog)) {
      return false;
    }
    SessionLog log = (SessionLog) other;
    return Arrays.equals(successfulMoves, log.successfulMoves)
        && Arrays.equals(failedMoves, log.failedMoves)
        && records.equals(log.records);
  }

  @Override
  public int hashCode() {
    int h = Arrays.hashCode(successfulMoves);
    h = 31 * h + Arrays.hashCode(failedMoves);
    h = 31 * h + records.hashCode();
    return h;
  }

  @Override
  public String toString() {
    // Used in generating session log file contents.
    StringBuilder s = new StringBuilder();
    s.append("<session>\n");
    s.append("<moveCounts>\n");
    s.append(moveCountToString(true));
    s.append(moveCountToString(false));
    s.append("</moveCounts>\n");
    s.append(records.toString());
    s.append("</session>\n");
    return s.toString();
  }

  private String moveCountToString(boolean successful) {
    int[] counts = successful ? successfulMoves : failedMoves;
    String template = successful ? "<successful>%s</successful>\n" : "<failed>%s</failed>\n";
    StringBuilder strCounts = new StringBuilder();
    String oneCount = "%d ";
    for (int c : counts) {
      strCounts.append(String.format(oneCount, c));
    }
    return String.format(template, strCounts.toString().strip());
  }

  static SessionLog createSessionLogFromFile(Readable file) {
    try (Scanner scan = new Scanner(file)) {
      SessionLog session = new SessionLog();
      session.records.clear();
      if (!scan.hasNextLine() || !scan.nextLine().equals("<session>")) {
        return null;
      }
      if (!scan.hasNextLine() || !scan.nextLine().equals("<moveCounts>")) {
        return null;
      }
      if (!scan.hasNextLine()) {
        return null;
      }

      String s = scan.nextLine();
      if (!Pattern.matches("<successful>\\d+\\s\\d+\\s\\d+\\s\\d+\\s\\d+</successful>", s)) {
        return null;
      }
      s = s.substring(12, s.length() - 13);

      if (!scan.hasNextLine()) {
        return null;
      }
      String f = scan.nextLine();
      if (!Pattern.matches("<failed>\\d+\\s\\d+\\s\\d+\\s\\d+\\s\\d+</failed>", f)) {
        return null;
      }
      f = f.substring(8, f.length() - 9);

      parseMoveCounts(s, f, session);

      if (!scan.hasNextLine() || !scan.nextLine().equals("</moveCounts>")) {
        return null;
      }
      if (!scan.hasNextLine() || !scan.nextLine().equals("<actions>")) {
        return null;
      }

      if (!parseActions(scan, session)) {
        return null;
      }

      if (!scan.hasNextLine() || !scan.nextLine().equals("</actions>")) {
        return null;
      }
      if (!scan.hasNextLine() || !scan.nextLine().equals("</session>")) {
        return null;
      }
      if (scan.hasNext()) {
        return null;
      }
      return session;
    }
  }

  private static boolean parseActions(Scanner scan, SessionLog session) {
    while (scan.hasNext("<action>")) {
      scan.nextLine();
      if (!scan.hasNextLine()) {
        return false;
      }
      String type = scan.nextLine();
      if (!Pattern.matches("<type>.+</type>", type)) {
        return false;
      }
      type = type.substring(6, type.length() - 7);
      if (!scan.hasNextLine()) {
        return false;
      }
      String data = scan.nextLine();
      if (!Pattern.matches("<data>.*</data>", data)) {
        return false;
      }
      data = data.substring(6, data.length() - 7);
      if (!scan.hasNextLine()) {
        return false;
      }
      String timestamp = scan.nextLine();
      if (!Pattern.matches("<timestamp>\\d+</timestamp>", timestamp)) {
        return false;
      }
      timestamp = timestamp.substring(11, timestamp.length() - 12);
      if (!scan.hasNextLine() || !scan.nextLine().equals("</action>")) {
        return false;
      }
      session.records.add(new LogRecord(type, data, timestamp));
    }
    return true;
  }

  private static void parseMoveCounts(String s, String f, SessionLog session) {
    Scanner sc = new Scanner(s);
    for (int i = 0; i < session.successfulMoves.length; i++) {
      session.successfulMoves[i] = sc.nextInt();
    }
    sc.close();
    sc = new Scanner(f);
    for (int i = 0; i < session.failedMoves.length; i++) {
      session.failedMoves[i] = sc.nextInt();
    }
    sc.close();
  }
}
