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

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

/** Formats SessionLogs. */
final class SessionLogFormatter {

  private final String formattedSessionLog;
  private final RecordList records;
  private final int[] successfulMoves;
  private final int[] failedMoves;

  SessionLogFormatter(RecordList records, int[] successfulMoves, int[] failedMoves) {
    this.records = records;
    this.successfulMoves = successfulMoves;
    this.failedMoves = failedMoves;
    ArrayList<String> alertList = new ArrayList<String>();
    String summary = formatSummaryStats();
    String allActions = formatAllLoggedActions(alertList);
    String allCompletions = formatCompletions(alertList);
    String allAlerts = formatAlerts(alertList);

    String logString =
        "<html><body><h1>Session Log</h1><hr><h2>Something went wrong loading session log.</h2></body></html>";
    try {
      InputStream in =
          InteractiveBinPacking.class.getResourceAsStream("html/sessionLogTemplate.html");
      String template = new String(in.readAllBytes(), Charset.forName("UTF-8"));
      in.close();
      logString =
          new Formatter()
              .format(
                  template,
                  InteractiveBinPacking.class.getResource("images/logo.png").toString(),
                  summary,
                  allAlerts,
                  allCompletions,
                  allActions)
              .toString();
    } catch (IOException ex) {
      // Should be impossible to get here, unless jar file is corrupt in some way (e.g.,
      // only way this exception is thrown is it error occurs reading resources stored
      // in the jar).
    }

    formattedSessionLog = logString;
  }

  @Override
  public String toString() {
    return formattedSessionLog;
  }

  String formatCompletions(ArrayList<String> alertList) {
    StringBuilder s = new StringBuilder();
    String currentModeName = "";
    String currentInstance = "Default";
    ArrayList<String> completionTableRows = new ArrayList<String>();
    int[] modeInstanceCount = new int[successfulMoves.length];
    for (int i = 0; i < records.size(); i++) {
      LogRecord log = records.get(i);
      String type = log.getType();
      if (type.equals("SET_MODE")) {
        currentModeName = log.getData();
      } else if (type.equals("SELECT_INSTANCE")) {
        currentInstance = log.getData();
      } else if (type.equals("COMPLETED")) {
        String completedData = log.getData();
        if (i + 1 < records.size() && records.get(i + 1).getType().equals("SOLUTION")) {
          i++;
          log = records.get(i);
          String solutionData = log.getData();
          int m =
              validateSolution(
                  completedData,
                  solutionData,
                  currentModeName,
                  currentInstance,
                  completionTableRows,
                  alertList);
          if (m >= 0) {
            modeInstanceCount[m]++;
          }
        } else {
          alertList.add("Completed record is missing required solution.");
        }
      } else if (type.equals("SOLUTION")) {
        alertList.add("Solution found without corresponding completion record.");
      }
    }
    for (int i = 1; i < successfulMoves.length; i++) {
      if (successfulMoves[i] < 20 * modeInstanceCount[i]) {
        alertList.add(
            "Fewer moves were recorded than needed to solve the instances claimed to have been solved.");
      }
    }
    if (completionTableRows.size() == 0) {
      s.append(
          "<span style=\"color:red;font-size:x-large\"><b>NO VERIFIABLE RECORDS OF COMPLETED INSTANCES IN HEURISTIC MODES IN SESSION LOG.</b></span>\n");
    } else {
      s.append("<table border=2 rules=cols frame=box>\n");
      s.append("<caption style=\"text-align:left\"><b>Table: All instances successfully\n");
      s.append("completed in the heuristic modes.</b></caption>\n");
      s.append(
          "<tr>\n<th style=\"text-align:left\">Instance</th>\n<th style=\"text-align:left\">Mode</th>\n<th style=\"text-align:left\">Validation</th>\n</tr>\n");
      for (String row : completionTableRows) {
        s.append(row);
      }
      s.append("</table>");
    }
    return s.toString();
  }

  int validateSolution(
      String completedData,
      String solutionData,
      String currentModeName,
      String currentInstance,
      ArrayList<String> completionTableRows,
      ArrayList<String> alertList) {
    int modeNum = extractModeNum(completedData);
    String instance = extractInstance(completedData);
    boolean proceed = true;
    if (!instance.equals(currentInstance)) {
      alertList.add("Solution encoded doesn't correspond to instance.");
      proceed = false;
    }
    String modeName = extractModeName(completedData);
    if (!modeName.equals(currentModeName)) {
      alertList.add("Solution encoded doesn't correspond to mode.");
      proceed = false;
    }
    if (!modeName.equals(ApplicationState.modeIntToModeName(modeNum))) {
      alertList.add("Mode name and internal mode number are inconsistent.");
      proceed = false;
    }
    if (modeNum == 0) {
      alertList.add(
          "Application doesn't log solutions in practice mode, but session log contains a practice mode solution.");
      proceed = false;
    }
    if (proceed) {
      int i = solutionData.indexOf("ItemSequence=");
      int j = solutionData.indexOf(",", 13);
      int x = solutionData.indexOf("BinSequence=");
      int k = x + 12;
      if (i < 0 || j < 0 || x < 0 || k >= solutionData.length()) {
        alertList.add("Encoded solution is malformed.");
      } else {
        String[] strBins = solutionData.substring(k).split(" ");
        int[] bins = new int[strBins.length];
        try {
          for (int b = 0; b < strBins.length; b++) {
            bins[b] = Integer.parseInt(strBins[b]);
          }
        } catch (NumberFormatException ex) {
          alertList.add("Expected integer bin numbers in solution.");
          proceed = false;
        }
        String[] pairs = solutionData.substring(i + 13, j).split(" ");
        if (pairs.length != 40) {
          alertList.add("Wrong number of items in solution");
          proceed = false;
        }
        int[] sizes = new int[20];
        String[] items = new String[20];
        try {
          for (int b = 0, index = 0; b < pairs.length; b += 2, index++) {
            sizes[index] = Integer.parseInt(pairs[b + 1]);
            items[index] = pairs[b];
          }
        } catch (NumberFormatException ex) {
          alertList.add("Expected integer item sizes in solution.");
          proceed = false;
        }
        if (proceed) {
          if (checkInstance(sizes, items, instance, alertList)
              && checkItemOrder(sizes, items, modeNum, alertList)
              && checkBins(sizes, bins, modeNum, alertList)) {
            int numBins = binCount(bins);
            completionTableRows.add(
                "<tr>\n<td style=\"text-align:left\">"
                    + instance
                    + "</td>\n<td style=\"text-align:left\">"
                    + modeName
                    + "</td>\n<td style=\"text-align:left\">Valid solution with "
                    + numBins
                    + " bins</td>\n</tr>\n");
            return modeNum;
          }
        }
      }
    }
    return -1;
  }

  int binCount(int[] bins) {
    HashSet<Integer> unique = new HashSet<Integer>();
    for (int b : bins) {
      unique.add(b);
    }
    return unique.size();
  }

  boolean checkBins(int[] sizes, int[] bins, int modeNum, ArrayList<String> alertList) {
    int[] binCapacities = new int[sizes.length];
    for (int i = 0; i < binCapacities.length; i++) {
      binCapacities[i] = 100;
    }
    if (modeNum == ApplicationState.MODE_FIRST_FIT
        || modeNum == ApplicationState.MODE_FIRST_FIT_DECREASING) {
      for (int i = 0; i < sizes.length; i++) {
        boolean foundIt = false;
        for (int j = 0; !foundIt; j++) {
          if (sizes[i] <= binCapacities[j]) {
            if (j + 1 == bins[i]) {
              binCapacities[j] -= sizes[i];
              foundIt = true;
            } else {
              alertList.add("Items put in incorrect bins for chosen mode.");
              return false;
            }
          }
        }
      }
    } else if (modeNum == ApplicationState.MODE_BEST_FIT
        || modeNum == ApplicationState.MODE_BEST_FIT_DECREASING) {
      for (int i = 0; i < sizes.length; i++) {
        for (int j = 0; j < binCapacities.length; j++) {
          if (j + 1 != bins[i]
              && sizes[i] <= binCapacities[j]
              && binCapacities[j] < binCapacities[bins[i] - 1]) {
            alertList.add("Items put in incorrect bins for chosen mode.");
            return false;
          }
        }
        binCapacities[bins[i] - 1] -= sizes[i];
      }
    } else {
      return false;
    }
    return true;
  }

  boolean checkItemOrder(int[] sizes, String[] items, int modeNum, ArrayList<String> alertList) {
    if (modeNum == ApplicationState.MODE_FIRST_FIT || modeNum == ApplicationState.MODE_BEST_FIT) {
      char c = 'A';
      for (int i = 0; i < items.length; i++, c = (char) (c + 1)) {
        if (!items[i].equals("" + c)) {
          alertList.add("Items used in incorrect order for chosen mode.");
          return false;
        }
      }
    } else if (modeNum == ApplicationState.MODE_FIRST_FIT_DECREASING
        || modeNum == ApplicationState.MODE_BEST_FIT_DECREASING) {
      for (int i = 1; i < sizes.length; i++) {
        if (sizes[i] > sizes[i - 1]) {
          alertList.add("Items used in incorrect order for chosen mode.");
          return false;
        }
      }
    } else {
      return false;
    }
    return true;
  }

  boolean checkInstance(int[] sizes, String[] items, String instance, ArrayList<String> alertList) {
    boolean goodInstance = true;
    HashMap<String, Integer> foundItems = new HashMap<String, Integer>();
    for (int i = 0; i < sizes.length; i++) {
      if (foundItems.containsKey(items[i])) {
        alertList.add("Duplicate items in solution.");
        goodInstance = false;
      } else {
        foundItems.put(items[i], sizes[i]);
      }
    }
    if (instance.equals("Default")) {
      int[] weights = {36, 33, 39, 43, 7, 19, 37, 8, 29, 28, 37, 23, 29, 10, 22, 11, 33, 9, 17, 30};
      char c = 'A';
      for (int i = 0; i < weights.length; i++, c = (char) (c + 1)) {
        if (foundItems.containsKey("" + c)) {
          int w = foundItems.get("" + c);
          if (w != weights[i]) {
            alertList.add("Wrong item size found in solution of default instance.");
            goodInstance = false;
          }
        } else {
          alertList.add("Unknown items found in solution of default instance.");
          goodInstance = false;
        }
      }
    } else if (instance.equals("Random")) {
      char c = 'A';
      for (int i = 0; i < items.length; i++, c = (char) (c + 1)) {
        if (foundItems.containsKey("" + c)) {
          int w = foundItems.get("" + c);
          if (w < 20 || w > 50) {
            alertList.add("Wrong item size found in solution of random instance.");
            goodInstance = false;
          }
        } else {
          alertList.add("Unknown items found in solution of random instance.");
          goodInstance = false;
        }
      }
    } else if (instance.startsWith("#")) {
      try {
        long seed = Long.parseLong(instance.substring(1));
        int[] weights = ApplicationState.createRandomItemSizes(20, 50, 20, new Random(seed));
        char c = 'A';
        for (int i = 0; i < weights.length; i++, c = (char) (c + 1)) {
          if (foundItems.containsKey("" + c)) {
            int w = foundItems.get("" + c);
            if (w != weights[i]) {
              alertList.add("Wrong item size found in solution of instance: " + instance);
              goodInstance = false;
            }
          } else {
            alertList.add("Unknown items found in solution of instance: " + instance);
            goodInstance = false;
          }
        }
      } catch (NumberFormatException ex) {
        alertList.add("Malformed instance number in solution.");
        goodInstance = false;
      }
    } else {
      alertList.add("Unknown instance type found in solution.");
      goodInstance = false;
    }
    return goodInstance;
  }

  int extractModeNum(String completedData) {
    int i = completedData.indexOf("ModeNum=");
    if (i >= 0) {
      i += 8;
      int j = completedData.indexOf(",", i);
      if (j >= 0) {
        String s = completedData.substring(i, j);
        try {
          int num = Integer.parseInt(s);
          return num;
        } catch (NumberFormatException ex) {
          return -1;
        }
      }
    }
    return -1;
  }

  String extractModeName(String completedData) {
    int i = completedData.indexOf("Mode=");
    if (i >= 0) {
      return completedData.substring(i + 5);
    }
    return "";
  }

  String extractInstance(String completedData) {
    int i = completedData.indexOf("Instance=");
    if (i >= 0) {
      i += 9;
      int j = completedData.indexOf(",", i);
      if (j >= 0) {
        return completedData.substring(i, j);
      }
    }
    return "";
  }

  /**
   * Formats all of the logged actions, including with timestamps.
   *
   * @param alertList A list of any alerts.
   * @return An html table with all logged actions.
   */
  String formatAllLoggedActions(ArrayList<String> alertList) {
    StringBuilder s = new StringBuilder();
    s.append("<table border=2 rules=cols frame=box>\n");
    s.append("<caption style=\"text-align:left\"><b>Table: All actions during session\n");
    s.append("in the order taken, excluding moves.</b></caption>\n");
    s.append(
        "<tr>\n<th style=\"text-align:left\">Action</th>\n<th style=\"text-align:left\">Details</th>\n<th style=\"text-align:left\">Timestamp</th>\n</tr>\n");

    long previousTime = 0;
    for (LogRecord log : records) {
      String type = log.getType();
      long time = log.getTimestamp();
      boolean consistentTime = checkTimeDifference(previousTime, time, alertList);
      previousTime = time;
      if (!type.equals("SOLUTION")) {
        String data = log.getData();
        if (type.equals("COMPLETED")) {
          data = formatCompletedData(data, alertList);
        }
        String timestamp = formatTimestamp(time, consistentTime);

        s.append(
            "<tr>\n<td style=\"text-align:left\">"
                + type
                + "</td>\n<td style=\"text-align:left\">"
                + data
                + "</td>\n<td style=\"text-align:left\">"
                + timestamp
                + "</td>\n</tr>\n");
      }
    }
    s.append("</table>\n");
    return s.toString();
  }

  String formatAlerts(ArrayList<String> alertList) {
    StringBuilder s = new StringBuilder();
    if (alertList.size() == 0) {
      s.append("<p style=\"color:green;font-size:x-large\"><b>NO ALERTS.</b></p>");
    } else {
      s.append(
          "<p><span style=\"color:red;font-size:x-large\"><b>NUMBER OF ALERTS: "
              + alertList.size()
              + "</b></span>\n");
      s.append("The following alerts were found:</p>\n");
      s.append("<ul>\n");
      for (String alert : alertList) {
        s.append("<li>" + alert + "</li>");
      }
      s.append("</ul>\n");
      s.append(
          "<p><b>More information may be available in the list of <a href=\"#logs\">All Logged Actions</a>.</b></p>\n");
    }
    return s.toString();
  }

  String formatTimestamp(long time, boolean consistentTime) {
    String t = Instant.ofEpochMilli(time).toString();
    if (consistentTime) {
      return t;
    } else {
      return "<span style=\"color:red\"><b>INCONSISTENT: " + t + "</b></span>";
    }
  }

  /*
   * returns true if OK, and false if inconsistent
   */
  boolean checkTimeDifference(long previous, long next, ArrayList<String> alertList) {
    if (next < previous) {
      alertList.add("Inconsistency in time sequence.");
      return false;
    }
    return true;
  }

  String formatCompletedData(String data, ArrayList<String> alertList) {
    int modeNum = extractModeNum(data);
    if (modeNum < 0) {
      return malformed(alertList);
    }
    String instance = extractInstance(data);
    if (instance.length() == 0) {
      return malformed(alertList);
    }
    String mode = extractModeName(data);
    if (mode.length() == 0) {
      return malformed(alertList);
    }
    return "Instance=" + instance + ", Mode=" + mode;
  }

  String malformed(ArrayList<String> alertList) {
    alertList.add("A completed record is malformed.");
    return "<span style=\"color:red\"><b>MALFORMED</b></span>";
  }

  /**
   * Formats the summary statistics section of session log.
   *
   * @return The summary statistics section of the session log.
   */
  String formatSummaryStats() {
    StringBuilder s = new StringBuilder();
    long startTime = records.get(0).getTimestamp();
    long endTime = records.get(records.size() - 1).getTimestamp();
    String start = Instant.ofEpochMilli(startTime).toString();
    String end = Instant.ofEpochMilli(endTime).toString();
    Duration d = Duration.ofMillis(endTime - startTime);

    s.append("<b>Session Start:</b> " + start + "<br>\n");
    s.append("<b>Session End:</b> " + end + "<br>\n");
    s.append("<b>Session Duration:</b> " + d.toString() + "<br>\n");

    s.append("<br><table border=2 rules=cols frame=box>\n");
    s.append(
        "<caption style=\"text-align:left\"><b>Table: Counts of number of successful and unsuccessful moves\n");
    s.append("during the session for each of the modes.</b></caption>\n");
    s.append(
        "<tr>\n<th style=\"text-align:left\">Mode</th>\n<th style=\"text-align:right\">Successful</th>\n<th style=\"text-align:right\">Unsuccessful</th>\n</tr>\n");

    for (int i = 0; i < successfulMoves.length; i++) {
      String modeName = ApplicationState.modeIntToModeName(i);
      int countS = successfulMoves[i];
      int countU = failedMoves[i];
      s.append(
          "<tr>\n<td style=\"text-align:left\">"
              + modeName
              + "</td>\n<td style=\"text-align:right\">"
              + countS
              + "</td>\n<td style=\"text-align:right\">"
              + countU
              + "</td>\n</tr>\n");
    }
    s.append("</table>\n");
    return s.toString();
  }
}
