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
		currentInstance = null;
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
