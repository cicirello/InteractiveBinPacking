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

/** Formats SessionLogs. */
final class SessionLogFormatter {

  private final String formattedSessionLog;
  private final RecordList records;
  private final int[] successfulMoves;
  private final int[] failedMoves;
  private final SolutionValidator validator;

  SessionLogFormatter(RecordList records, int[] successfulMoves, int[] failedMoves) {
    this.records = records;
    this.successfulMoves = successfulMoves;
    this.failedMoves = failedMoves;
    ArrayList<String> alertList = new ArrayList<String>();
    validator = new SolutionValidator(alertList);
    String summary = formatSummaryStats();
    String allActions = formatAllLoggedActions();
    String allCompletions = formatCompletions();
    String allAlerts = formatAlerts();

    String logString =
        "<html><body><h1>Session Log</h1><hr><h2>Something went wrong loading session log.</h2></body></html>";
    try (InputStream in =
        InteractiveBinPacking.class.getResourceAsStream("html/sessionLogTemplate.html")) {
      String template = new String(in.readAllBytes(), Charset.forName("UTF-8"));
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

  String formatCompletions() {
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
              validator.validateSolution(
                  completedData,
                  solutionData,
                  currentModeName,
                  currentInstance,
                  completionTableRows);
          if (m >= 0) {
            modeInstanceCount[m]++;
          }
        } else {
          validator.addAlert("Completed record is missing required solution.");
        }
      } else if (type.equals("SOLUTION")) {
        validator.addAlert("Solution found without corresponding completion record.");
      }
    }
    for (int i = 1; i < successfulMoves.length; i++) {
      if (successfulMoves[i] < 20 * modeInstanceCount[i]) {
        validator.addAlert(
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

  /**
   * Formats all of the logged actions, including with timestamps.
   *
   * @return An html table with all logged actions.
   */
  String formatAllLoggedActions() {
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
      boolean consistentTime = validator.checkTimeDifference(previousTime, time);
      previousTime = time;
      if (!type.equals("SOLUTION")) {
        String data = log.getData();
        if (type.equals("COMPLETED")) {
          data = formatCompletedData(data);
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

  String formatAlerts() {
    return validator.alertsToString();
  }

  String formatTimestamp(long time, boolean consistentTime) {
    String t = Instant.ofEpochMilli(time).toString();
    if (consistentTime) {
      return t;
    } else {
      return "<span style=\"color:red\"><b>INCONSISTENT: " + t + "</b></span>";
    }
  }

  String formatCompletedData(String data) {
    int modeNum = validator.extractModeNum(data);
    if (modeNum < 0) {
      return validator.malformed();
    }
    String instance = validator.extractInstance(data);
    if (instance.length() == 0) {
      return validator.malformed();
    }
    String mode = validator.extractModeName(data);
    if (mode.length() == 0) {
      return validator.malformed();
    }
    return "Instance=" + instance + ", Mode=" + mode;
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

  SolutionValidator validator() {
    return validator;
  }
}
