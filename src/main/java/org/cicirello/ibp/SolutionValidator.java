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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

/** Validates solutions to instances of the bin packing problem. */
final class SolutionValidator {

  private final ArrayList<String> alertList;

  SolutionValidator(ArrayList<String> alertList) {
    this.alertList = alertList;
  }

  int validateSolution(
      String completedData,
      String solutionData,
      String currentModeName,
      String currentInstance,
      ArrayList<String> completionTableRows) {
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
          if (checkInstance(sizes, items, instance)
              && checkItemOrder(sizes, items, modeNum)
              && checkBins(sizes, bins, modeNum)) {
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

  boolean checkBins(int[] sizes, int[] bins, int modeNum) {
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

  boolean checkItemOrder(int[] sizes, String[] items, int modeNum) {
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

  boolean checkInstance(int[] sizes, String[] items, String instance) {
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

  /*
   * returns true if OK, and false if inconsistent
   */
  boolean checkTimeDifference(long previous, long next) {
    if (next < previous) {
      alertList.add("Inconsistency in time sequence.");
      return false;
    }
    return true;
  }

  String malformed() {
    alertList.add("A completed record is malformed.");
    return "<span style=\"color:red\"><b>MALFORMED</b></span>";
  }

  String alertsToString() {
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

  void addAlert(String alert) {
    alertList.add(alert);
  }

  ArrayList<String> allAlerts() {
    return alertList;
  }
}
