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
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Date;
 import java.time.Duration;
 
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
	 
	private final RecordList records;
	private final int[] successfulMoves;
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
		
		String allAlerts = formatAlerts(alertList);
		
		StringBuilder html = new StringBuilder();
		html.append("<html>\n<body>\n<h1><a name=\"TOP\"></a>Session Log</h1>\n<hr>\n");
		
		html.append("<h2><a name=\"ToC\"></a>Table of Contents</h2>\n"); 
		html.append("The session log is organized as follows:\n");
		html.append("<ul>\n");
		html.append("<li><a href=\"#summary\">Summary Statistics</a></li>\n");
		html.append("<li><a href=\"#alerts\">Alerts</a></li>\n");
		html.append("<li><a href=\"#success\">Heuristic Mode Successful Completions</a></li>\n");
		html.append("<li><a href=\"#logs\">All Logged Actions</a></li>\n");
		html.append("</ul>\n");
		
		html.append("<hr>\n");
		html.append("<h2><a name=\"summary\">Summary Statistics</h2>\n");
		
		html.append(summary);
		
		html.append("<p>Return to <a href=\"#TOP\">Top</a> or <a href=\"#ToC\">Table of Contents</a>.</p>\n");
		
		html.append("<hr>\n");
		html.append("<h2><a name=\"alerts\">Alerts</h2>\n");
		html.append("This section is meant for instructors viewing session logs\n");
		html.append("submitted by students for assignments. Although we believe\n");
		html.append("that the amount of effort necessary to falsify a session\n");
		html.append("log is greater than the effort necessary to complete the\n");
		html.append("tutorial and any exercises assigned by the instructor,\n");
		html.append("the application performs some rudimentary analysis to detect\n");
		html.append("questionable records. This is where you will\n");
		html.append("find alerts for things that may imply suspicious activity,\n");
		html.append("such as inconsistent sequence of timestamps, problem instances\n");
		html.append("solved before they were started, entries claiming completion of\n");
		html.append("problem instances but with data for a different problem instance,\n");
		html.append("among others.\n");
		html.append("<p>Instructors should also inspect the summary statistics\n");
		html.append("section for time related issues. For example, at the present\n");
		html.append("time the application does not attempt to determine if the\n");
		html.append("total time in session is consistent with amount of activity\n");
		html.append("claimed by the session log.</p>\n");
		
		// HERE ALERTS MUST BE GENERATED
		html.append(allAlerts);
		
		html.append("<p>Return to <a href=\"#TOP\">Top</a> or <a href=\"#ToC\">Table of Contents</a>.</p>\n");
		
		html.append("<hr>\n");
		html.append("<h2><a name=\"success\">Heuristic Mode Successful Completions</h2>\n");
		
		// HERE SUCCESSFUL COMPLETIONS MUST BE GENERATED
		
		html.append("<p>Return to <a href=\"#TOP\">Top</a> or <a href=\"#ToC\">Table of Contents</a>.</p>\n");
		
		html.append("<hr>\n");
		html.append("<h2><a name=\"logs\">All Logged Actions</h2>\n");
		
		html.append(allActions);
		
		html.append("<p>Return to <a href=\"#TOP\">Top</a> or <a href=\"#ToC\">Table of Contents</a>.</p>\n");
		
		html.append("<hr>\n</body>\n</html>");
		return html.toString();
	}
	
	/**
	 * Formats all of the logged actions, including with timestamps.
	 * @param alertList A list of any alerts.
	 * @return An html table with all logged actions.
	 */
	public String formatAllLoggedActions(ArrayList<String> alertList) {
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
			s.append("<p style=\"color:red;font-size:x-large\"><b>NO ALERTS.</b></p>");
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
		int i = data.indexOf("Instance=");
		if (i < 0) {
			return malformed(alertList);
		} else {
			i += 9;
			int j = data.indexOf(",", i);
			if (j < 0) {
				return malformed(alertList);
			} else {
				String instance = data.substring(i,j);
				i = data.indexOf("Mode=", j);
				if (i < 0) {
					return malformed(alertList);
				} else {
					String mode = data.substring(i+5);
					return "Instance="+instance+", Mode="+mode;
				}
			}
		}
	}
	
	String malformed(ArrayList<String> alertList) {
		alertList.add("A completed record is malformed.");
		return "<span style=\"color:red\"><b>MALFORMED</b></span>";
	}
	
	/**
	 * Formats the summary statistics section of session log.
	 * @return The summary statistics section of the session log.
	 */
	public String formatSummaryStats() {
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
	
	private static final class LogRecord implements Serializable {
		private static final long serialVersionUID = 1L;
		
		private String type;
		private String data;
		private long timestamp;
		
		LogRecord(String type, String data) {
			this.type = type;
			this.data = data;
			timestamp = System.currentTimeMillis();
		}
		
		String getType() { return type; }
		
		String getData() { return data; }
		
		long getTimestamp() { return timestamp; }
		
		@Override
		public boolean equals(Object other) {
			if (other == null || !(other instanceof LogRecord)) {
				return false;
			}
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
	}
	
	private static final class RecordList extends ArrayList<LogRecord> {
		private static final long serialVersionUID = 1L;
		
		@Override
		public boolean equals(Object other) {
			if (other == null || !(other instanceof RecordList)) {
				return false;
			}
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
	}
 }
