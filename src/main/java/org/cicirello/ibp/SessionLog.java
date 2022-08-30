/*
 * Interactive Bin Packing.
 * Copyright (C) 2021  Vincent A. Cicirello
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
import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;
import java.time.Duration;
import java.util.regex.Pattern;
 
 /**
 * This class is used to maintain a log of the activity of 
 * a student using the application during a session, enabling
 * a student to submit the log to an instructor for verification
 * of their usage of the application, such as which problem
 * instances they interacted with, what modes were used, whether
 * they solved them to completion using heuristic modes specified
 * by the instructor in an assignment, etc.
 *
 * @author <a href=https://www.cicirello.org/ target=_top>Vincent A. Cicirello</a>, 
 * <a href=https://www.cicirello.org/ target=_top>https://www.cicirello.org/</a>
 */
public final class SessionLog implements Serializable {
	 
	private static final long serialVersionUID = 1L;
	
	/** List of log records. */
	private final RecordList records;
	
	/** Counts of successful moves in each mode. */
	private final int[] successfulMoves;
	
	/** Counts of failed mvoes in each mode. */
	private final int[] failedMoves;	
	
	private transient int currentMode;
	private transient String currentInstance;
	private transient ArrayList<Item> currentItemSequence;
	private transient ArrayList<Integer> currentBinSequence;
	 
	 /**
	  * Initialize the SessionLog.
	  */
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
	 * Adds an entry to the session log. Also does any
	 * entry type specific operations.
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
	
	/**
	 * Records that a heuristic mode was completed.
	 */
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
	 * @param item The item that was moved.
	 * @param bin The bin the item was moved to.
	 */
	public void recordMove(Item item, Bin bin) {
		successfulMoves[currentMode]++;
		currentItemSequence.add(item);
		currentBinSequence.add(bin.getBinNumber());
	}
	
	/**
	 * Increments the failed move counter for the current mode.
	 */
	public void recordFailedMove() {
		failedMoves[currentMode]++;
	}
	
	/**
	 * Formats the session log data in html for viewing within
	 * the application.
	 * @return session log formatted in html for in application viewing
	 */
	public String formatSessionLog() {
		ArrayList<String> alertList = new ArrayList<String>();
		String summary = formatSummaryStats();
		String allActions = formatAllLoggedActions(alertList);
		String allCompletions = formatCompletions(alertList);
		String allAlerts = formatAlerts(alertList);
		
		String logString = "<html><body><h1>Session Log</h1><hr><h2>Something went wrong loading session log.</h2></body></html>";
		try {
			InputStream in = InteractiveBinPacking.class.getResourceAsStream("html/sessionLogTemplate.html");
			String template = new String(in.readAllBytes());
			in.close();
			logString = new Formatter().format(
				template,
				InteractiveBinPacking.class.getResource("images/logo.png").toString(),
				summary, 
				allAlerts, 
				allCompletions, 
				allActions
			).toString();		
		} catch(IOException ex) { }
		
		return logString;
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
				if (i+1 < records.size() && records.get(i+1).getType().equals("SOLUTION")) {
					i++;
					log = records.get(i);
					String solutionData = log.getData();
					int m = validateSolution(
						completedData, 
						solutionData, 
						currentModeName, 
						currentInstance, 
						completionTableRows, 
						alertList
					);
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
			if (successfulMoves[i] < 20*modeInstanceCount[i]) {
				alertList.add("Fewer moves were recorded than needed to solve the instances claimed to have been solved.");
			}
		}
		if (completionTableRows.size() == 0) {
			s.append("<span style=\"color:red;font-size:x-large\"><b>NO VERIFIABLE RECORDS OF COMPLETED INSTANCES IN HEURISTIC MODES IN SESSION LOG.</b></span>\n");
		} else {
			s.append("<table border=2 rules=cols frame=box>\n");
			s.append("<caption style=\"text-align:left\"><b>Table: All instances successfully\n");
			s.append("completed in the heuristic modes.</b></caption>\n");
			s.append("<tr>\n<th style=\"text-align:left\">Instance</th>\n<th style=\"text-align:left\">Mode</th>\n<th style=\"text-align:left\">Validation</th>\n</tr>\n");
			for (String row : completionTableRows) {
				s.append(row);
			}
			s.append("</table>");
		}
		return s.toString();
	}
	
	int validateSolution(String completedData, String solutionData, String currentModeName, String currentInstance, ArrayList<String> completionTableRows, ArrayList<String> alertList) {
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
			alertList.add("Application doesn't log solutions in practice mode, but session log contains a practice mode solution.");
			proceed = false;
		}
		if (proceed) {
			int i = solutionData.indexOf("ItemSequence=");
			int j = solutionData.indexOf(",", 13);
			int x = solutionData.indexOf("BinSequence=");
			int k = x+12;
			if (i < 0 || j < 0 || x < 0 || k >= solutionData.length()) {
				alertList.add("Encoded solution is malformed.");
			} else {
				String[] strBins = solutionData.substring(k).split(" ");
				int[] bins = new int[strBins.length];
				try {
					for (int b = 0; b < strBins.length; b++) {
						bins[b] = Integer.parseInt(strBins[b]);
					}
				} catch(NumberFormatException ex) {
					alertList.add("Expected integer bin numbers in solution.");
					proceed = false;
				}
				String[] pairs = solutionData.substring(i+13, j).split(" ");
				if (pairs.length != 40) {
					alertList.add("Wrong number of items in solution");
					proceed = false;
				}
				int[] sizes = new int[20];
				String[] items = new String[20];
				try {
					for (int b = 0, index = 0; b < pairs.length; b+=2, index++) {
						sizes[index] = Integer.parseInt(pairs[b+1]);
						items[index] = pairs[b];
					}
				} catch(NumberFormatException ex) {
					alertList.add("Expected integer item sizes in solution.");
					proceed = false;
				}
				if (proceed) {
					if (
						checkInstance(sizes, items, instance, alertList)
						&& checkItemOrder(sizes, items, modeNum, alertList)
						&& checkBins(sizes, bins, modeNum, alertList)
					) {
						int numBins = binCount(bins);
						completionTableRows.add("<tr>\n<td style=\"text-align:left\">" + instance + "</td>\n<td style=\"text-align:left\">" + modeName + "</td>\n<td style=\"text-align:left\">Valid solution with " + numBins + " bins</td>\n</tr>\n");
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
		if (modeNum==ApplicationState.MODE_FIRST_FIT || modeNum==ApplicationState.MODE_FIRST_FIT_DECREASING) {
			for (int i = 0; i < sizes.length; i++) {
				boolean foundIt = false;
				for (int j = 0; !foundIt; j++) {
					if (sizes[i] <= binCapacities[j]) {
						if (j+1==bins[i]) {
							binCapacities[j] -= sizes[i];
							foundIt = true;
						} else {
							alertList.add("Items put in incorrect bins for chosen mode.");
							return false;
						}
					}
				}
			}
		} else if (modeNum==ApplicationState.MODE_BEST_FIT || modeNum==ApplicationState.MODE_BEST_FIT_DECREASING) {
			for (int i = 0; i < sizes.length; i++) {
				for (int j = 0; j < binCapacities.length; j++) {
					if (j+1!=bins[i] && sizes[i]<=binCapacities[j] && binCapacities[j]<binCapacities[bins[i]-1]) {
						alertList.add("Items put in incorrect bins for chosen mode.");
						return false;
					}						
				}
				binCapacities[bins[i]-1] -= sizes[i];
			}
		} else {
			return false;
		}
		return true;
	}
	
	boolean checkItemOrder(int[] sizes, String[] items, int modeNum, ArrayList<String> alertList) {
		if (modeNum==ApplicationState.MODE_FIRST_FIT || modeNum==ApplicationState.MODE_BEST_FIT) {
			char c = 'A'; 
			for (int i = 0; i < items.length; i++, c=(char)(c+1)) {
				if (!items[i].equals(""+c)) {
					alertList.add("Items used in incorrect order for chosen mode.");
					return false;
				}
			}
		} else if (modeNum==ApplicationState.MODE_FIRST_FIT_DECREASING || modeNum==ApplicationState.MODE_BEST_FIT_DECREASING) {
			for (int i = 1; i < sizes.length; i++) {
				if (sizes[i] > sizes[i-1]) {
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
		for (int i=0; i < sizes.length; i++) {
			if (foundItems.containsKey(items[i])) {
				alertList.add("Duplicate items in solution.");
				goodInstance = false;
			} else {
				foundItems.put(items[i], sizes[i]);
			}
		}
		if (instance.equals("Default")) {
			int[] weights = {36, 33, 39, 43, 7, 19, 37, 8, 29, 28, 37, 23, 29, 
					10, 22, 11, 33, 9, 17, 30};
			char c = 'A';
			for (int i = 0; i < weights.length; i++, c=(char)(c+1)) {
				if (foundItems.containsKey(""+c)) {
					int w = foundItems.get(""+c);
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
			for (int i = 0; i < items.length; i++, c=(char)(c+1)) {
				if (foundItems.containsKey(""+c)) {
					int w = foundItems.get(""+c);
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
				for (int i = 0; i < weights.length; i++, c=(char)(c+1)) {
					if (foundItems.containsKey(""+c)) {
						int w = foundItems.get(""+c);
						if (w != weights[i]) {
							alertList.add("Wrong item size found in solution of instance: "+instance);
							goodInstance = false;
						}
					} else {
						alertList.add("Unknown items found in solution of instance: "+instance);
						goodInstance = false;
					}
				}
			} catch(NumberFormatException ex) {
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
		int num = -1;
		int i = completedData.indexOf("ModeNum=");
		if (i >= 0) {
			i += 8;
			int j = completedData.indexOf(",", i);
			if (j >= 0) {
				String s = completedData.substring(i,j);
				try {
					num = Integer.parseInt(s);
				} catch (NumberFormatException ex) {}
			}
		}
		return num;
	}
	
	
	String extractModeName(String completedData) {
		int i = completedData.indexOf("Mode=");
		if (i >= 0) {
			return completedData.substring(i+5);
		}
		return "";
	}
	
	String extractInstance(String completedData) {
		int i = completedData.indexOf("Instance=");
		if (i >= 0) {
			i += 9;
			int j = completedData.indexOf(",", i);
			if (j >= 0) {
				return completedData.substring(i,j);
			}
		}
		return "";
	}
	
	/**
	 * Formats all of the logged actions, including with timestamps.
	 * @param alertList A list of any alerts.
	 * @return An html table with all logged actions.
	 */
	String formatAllLoggedActions(ArrayList<String> alertList) {
		StringBuilder s = new StringBuilder();
		s.append("<table border=2 rules=cols frame=box>\n");
		s.append("<caption style=\"text-align:left\"><b>Table: All actions during session\n");
		s.append("in the order taken, excluding moves.</b></caption>\n");
		s.append("<tr>\n<th style=\"text-align:left\">Action</th>\n<th style=\"text-align:left\">Details</th>\n<th style=\"text-align:left\">Timestamp</th>\n</tr>\n");
		
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
				
				s.append("<tr>\n<td style=\"text-align:left\">" + type + "</td>\n<td style=\"text-align:left\">" + data + "</td>\n<td style=\"text-align:left\">" + timestamp + "</td>\n</tr>\n");
			} 
		}
		s.append("</table>\n");
		return s.toString();
	}
	
	String formatAlerts(ArrayList<String> alertList) {
		StringBuilder s = new StringBuilder();
		if (alertList.size()==0) {
			s.append("<p style=\"color:green;font-size:x-large\"><b>NO ALERTS.</b></p>");
		} else {
			s.append("<p><span style=\"color:red;font-size:x-large\"><b>NUMBER OF ALERTS: "+ alertList.size() + "</b></span>\n");
			s.append("The following alerts were found:</p>\n");
			s.append("<ul>\n");
			for (String alert : alertList) {
				s.append("<li>"+alert+"</li>");
			}
			s.append("</ul>\n");
			s.append("<p><b>More information may be available in the list of <a href=\"#logs\">All Logged Actions</a>.</b></p>\n");
		}
		return s.toString();
	}
	
	String formatTimestamp(long time, boolean consistentTime) {
		String t = new Date(time).toString();
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
		return "Instance="+instance+", Mode="+mode;
	}
	
	String malformed(ArrayList<String> alertList) {
		alertList.add("A completed record is malformed.");
		return "<span style=\"color:red\"><b>MALFORMED</b></span>";
	}
	
	/**
	 * Formats the summary statistics section of session log.
	 * @return The summary statistics section of the session log.
	 */
	String formatSummaryStats() {
		StringBuilder s = new StringBuilder();
		long startTime = records.get(0).getTimestamp();
		long endTime = records.get(records.size()-1).getTimestamp();
		String start = new Date(startTime).toString();
		String end = new Date(endTime).toString();
		Duration d = Duration.ofMillis(endTime-startTime);
		
		s.append("<b>Session Start:</b> " + start + "<br>\n");
		s.append("<b>Session End:</b> " + end + "<br>\n");
		s.append("<b>Session Duration:</b> " + d.toString() + "<br>\n");
		
		s.append("<br><table border=2 rules=cols frame=box>\n");
		s.append("<caption style=\"text-align:left\"><b>Table: Counts of number of successful and unsuccessful moves\n");
		s.append("during the session for each of the modes.</b></caption>\n");
		s.append("<tr>\n<th style=\"text-align:left\">Mode</th>\n<th style=\"text-align:right\">Successful</th>\n<th style=\"text-align:right\">Unsuccessful</th>\n</tr>\n");
		
		for (int i = 0; i < successfulMoves.length; i++) {
			String modeName = ApplicationState.modeIntToModeName(i);
			int countS = successfulMoves[i];
			int countU = failedMoves[i];
			s.append("<tr>\n<td style=\"text-align:left\">" + modeName + "</td>\n<td style=\"text-align:right\">" + countS + "</td>\n<td style=\"text-align:right\">" + countU + "</td>\n</tr>\n");
		}
		s.append("</table>\n");
		return s.toString();
	}
	
	@Override
	public boolean equals(Object other) {
		if (other == null || !(other instanceof SessionLog)) {
			return false;
		}
		SessionLog log = (SessionLog)other;
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
		String template = successful ?
			"<successful>%s</successful>\n" :
			"<failed>%s</failed>\n";
		String strCounts = "";
		String oneCount = "%d ";
		for (int c : counts) {
			strCounts += String.format(oneCount, c);
		}
		return String.format(template, strCounts.strip());
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
			s = s.substring(12, s.length()-13);
			
			if (!scan.hasNextLine()) {
				return null;
			}
			String f = scan.nextLine();
			if (!Pattern.matches("<failed>\\d+\\s\\d+\\s\\d+\\s\\d+\\s\\d+</failed>", f)) {
				return null;
			}
			f = f.substring(8,f.length()-9);
			
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
			type = type.substring(6, type.length()-7);
			if (!scan.hasNextLine()) {
				return false;
			}
			String data = scan.nextLine();
			if (!Pattern.matches("<data>.*</data>", data)) {
				return false;
			}
			data = data.substring(6, data.length()-7);
			if (!scan.hasNextLine()) {
				return false;
			}
			String timestamp = scan.nextLine();
			if (!Pattern.matches("<timestamp>\\d+</timestamp>", timestamp)) {
				return false;
			}
			timestamp = timestamp.substring(11, timestamp.length()-12);
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
	
	private static final class LogRecord implements Serializable {
		private static final long serialVersionUID = 1L;
		
		private String type;
		private String data;
		private long timestamp;
		
		private static final String logFileTemplate 
			= "<action>\n<type>%s</type>\n<data>%s</data>\n<timestamp>%d</timestamp>\n</action>\n";
		
		private LogRecord(String type, String data) {
			this.type = type;
			this.data = data;
			timestamp = System.currentTimeMillis();
		}
		
		private LogRecord(String type, String data, String strTimeStamp) {
			this.type = type;
			this.data = data;
			timestamp = Long.parseLong(strTimeStamp);
		}
		
		String getType() { return type; }
		
		String getData() { return data; }
		
		long getTimestamp() { return timestamp; }
		
		@Override
		public boolean equals(Object other) {
			/* // UNNECESSARY CHECK... access control prevents these cases
			if (other == null || !(other instanceof LogRecord)) {
				return false;
			}
			*/
			LogRecord o = (LogRecord)other;
			return type.equals(o.type) 
				&& data.equals(o.data) 
				&& timestamp == o.timestamp;
		}
		
		@Override
		public int hashCode() {
			int h = Long.hashCode(timestamp);
			h = 31 * h + type.hashCode();
			h = 31 * h + data.hashCode();
			return h;
		}
		
		@Override
		public String toString() {
			// Used in generating session log file contents.
			return String.format(
				logFileTemplate,
				type,
				data,
				timestamp
			);
		}
	}
	
	private static final class RecordList extends ArrayList<LogRecord> {
		private static final long serialVersionUID = 1L;
		
		@Override
		public boolean equals(Object other) {
			/* // UNNECESSARY CHECK... access control prevents these cases
			if (other == null || !(other instanceof RecordList)) {
				return false;
			}
			*/
			RecordList r = (RecordList)other;
			if (size() != r.size()) {
				return false;
			}
			for (int i = 0; i < size(); i++) {
				if (!get(i).equals(r.get(i))) {
					return false;
				}
			}
			return true;
		}
		
		@Override
		public int hashCode() {
			int h = 0;
			for (LogRecord r : this) {
				h = 31*h + r.hashCode();
			}
			return h;
		}
		
		@Override
		public String toString() {
			// Used in generating session log file contents.
			StringBuilder s = new StringBuilder();
			s.append("<actions>\n");
			for (LogRecord r : this) {
				s.append(r.toString());
			}
			s.append("</actions>\n");
			return s.toString();
		}
	}
 }
