/*
 * Interactive Bin Packing.
 * Copyright (C) 2008, 2010, 2020-2021  Vincent A. Cicirello
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

import org.junit.*;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.io.ObjectOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.StringReader;
import java.io.PrintWriter;

/**
 * JUnit tests for the SessionLog class of the
 * Interactive Bin Packing Application.
 */
public class SessionLogTests {
	
	@Test
	public void testWriteReadFilesFromApplicationState() {
		ApplicationState state = createApplicationState();
		StringWriter sOut = writeSessionLogToString(state);
		SessionLog savedLog = readSessionLogFromString(sOut);
		assertTrue(state.equalsInternalSessionLog(savedLog));
		
		sOut = writeSessionLogToString(state);
		String asString = readSessionLogAsStringFromString(sOut, state, false); 
		assertTrue(asString.indexOf("SAVE_SESSION_LOG")>=0);
		
		sOut = writeSessionLogToString(state);
		assertNull(readSessionLogAsStringFromString(sOut, state, true));
	}
	
	@Test
	public void testWriteReadFilesFromSessionLog() {
		SessionLog log = new SessionLog();
		for (int m = 0; m < 5; m++) {
			log.addEntry("SET_MODE", ""+m);
			for (int i = 0; i < 20; i++) {
				String n = ""+('A'+i);
				log.recordMove(new Item(n, 20), new Bin("Bin 1", 1));
			}
			for (int i = 0; i < m; i++) {
				log.recordFailedMove();
			}
		}
		StringWriter sOut = writeSessionLogToString(log);
		SessionLog savedLog = readSessionLogFromString(sOut);
		assertEquals(log, savedLog);
		String asString = savedLog.toString();
		
		// break asString in various ways
		assertNull(readSessionLogFromString(asString.replace("<session>","<sesion>")));
		assertNull(readSessionLogFromString(asString.replace("</session>","</sesion>")));
		assertNull(readSessionLogFromString(asString.replace("<moveCounts>","<moves>")));
		assertNull(readSessionLogFromString(asString.replace("</moveCounts>","</moves>")));
		assertNull(readSessionLogFromString(asString.replace("<actions>","<actons>")));
		assertNull(readSessionLogFromString(asString.replace("</actions>","</actons>")));
		assertNull(readSessionLogFromString(asString.replace("<action>","<acton>")));
		assertNull(readSessionLogFromString(asString.replace("</action>","</acton>")));
		assertNull(readSessionLogFromString(asString.replace("<type>","<typ>")));
		assertNull(readSessionLogFromString(asString.replace("</type>","</typ>")));
		assertNull(readSessionLogFromString(asString.replace("<data>","<dat>")));
		assertNull(readSessionLogFromString(asString.replace("</data>","</dat>")));
		assertNull(readSessionLogFromString(asString.replace("<timestamp>","<timestmp>")));
		assertNull(readSessionLogFromString(asString.replace("</timestamp>","</timestmp>")));
		assertNull(readSessionLogFromString(asString.replace("<successful>","<sucessful>")));
		assertNull(readSessionLogFromString(asString.replace("</successful>","</sucessful>")));
		assertNull(readSessionLogFromString(asString.replace("<failed>","<fail>")));
		assertNull(readSessionLogFromString(asString.replace("</failed>","</fail>")));
		
		// break asString with extra stuff
		assertNull(readSessionLogFromString(asString + "extra"));
		
		// break asString by removing required stuff
		asString = asString.substring(0, asString.indexOf("</session>"));
		assertNull(readSessionLogFromString(asString));
		asString = asString.substring(0, asString.indexOf("</actions>"));
		assertNull(readSessionLogFromString(asString));
		asString = asString.substring(0, asString.indexOf("</action>"));
		assertNull(readSessionLogFromString(asString));
		asString = asString.substring(0, asString.indexOf("<timestamp>"));
		assertNull(readSessionLogFromString(asString));
		asString = asString.substring(0, asString.indexOf("<data>"));
		assertNull(readSessionLogFromString(asString));
		asString = asString.substring(0, asString.indexOf("<type>"));
		assertNull(readSessionLogFromString(asString));
		asString = asString.substring(0, asString.indexOf("<actions>"));
		assertNull(readSessionLogFromString(asString));
		asString = asString.substring(0, asString.indexOf("</moveCounts>"));
		assertNull(readSessionLogFromString(asString));
		asString = asString.substring(0, asString.indexOf("<failed>"));
		assertNull(readSessionLogFromString(asString));
		asString = asString.substring(0, asString.indexOf("<successful>"));
		assertNull(readSessionLogFromString(asString));
		asString = asString.substring(0, asString.indexOf("<moveCounts>"));
		assertNull(readSessionLogFromString(asString));
		asString = asString.substring(0, asString.indexOf("<session>"));
		assertNull(readSessionLogFromString(asString));
	}
	
	private StringWriter writeSessionLogToString(ApplicationState state) {
		StringWriter sOut = new StringWriter();
		PrintWriter out = new PrintWriter(sOut);
		state.saveSessionLog(out);
		return sOut;
	}
	
	private StringWriter writeSessionLogToString(SessionLog log) {
		StringWriter sOut = new StringWriter();
		PrintWriter out = new PrintWriter(sOut);
		out.print(log.toString());
		out.flush();
		return sOut;
	}
	
	private SessionLog readSessionLogFromString(StringWriter sOut) {
		StringReader sIn = new StringReader(sOut.toString());
		SessionLog savedLog = SessionLog.createSessionLogFromFile(sIn);
		return savedLog;
	}
	
	private SessionLog readSessionLogFromString(String str) {
		StringReader sIn = new StringReader(str);
		SessionLog savedLog = SessionLog.createSessionLogFromFile(sIn);
		return savedLog;
	}
	
	private String readSessionLogAsStringFromString(StringWriter sOut, ApplicationState state, boolean breakIt) {
		String str = sOut.toString();
		StringReader sIn = breakIt 
			? new StringReader(str.replace("<session>", "<sesion>"))
			: new StringReader(str);
		return state.loadSessionLog(sIn);
	}
	
	@Test
	public void testEqualsHashCodeSerialization() {
		// Different timestamps on otherwise identical logs.
		SessionLog log1 = new SessionLog();
		try {
			Thread.sleep(10);
		} catch (InterruptedException ex) {}
		SessionLog log2 = new SessionLog();
		assertNotEquals(log1, log2);
		
		try {
			// Equal case
			ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(outBytes);
			out.writeObject(log1);
			out.flush();
			out.close();
			byte[] bytes = outBytes.toByteArray();
			ByteArrayInputStream inBytes = new ByteArrayInputStream(bytes);
			ObjectInputStream in = new ObjectInputStream(inBytes);
			SessionLog deserialized = (SessionLog)in.readObject();
			in.close();
			assertEquals(log1, deserialized);
			assertEquals(log1.hashCode(), deserialized.hashCode());
			
			// same type different data
			log1.addEntry("T1", "D1");
			deserialized.addEntry("T1", "D2");
			assertNotEquals(log1, deserialized);
			
			// multiple records equal case
			outBytes = new ByteArrayOutputStream();
			out = new ObjectOutputStream(outBytes);
			out.writeObject(log1);
			out.flush();
			out.close();
			bytes = outBytes.toByteArray();
			inBytes = new ByteArrayInputStream(bytes);
			in = new ObjectInputStream(inBytes);
			deserialized = (SessionLog)in.readObject();
			in.close();
			assertEquals(log1, deserialized);
			assertEquals(log1.hashCode(), deserialized.hashCode());
			
			// different num records case
			log1.addEntry("T1", "D2");
			assertNotEquals(log1, deserialized);
			
			// different types case
			deserialized.addEntry("T2", "D2");
			assertNotEquals(log1, deserialized);
			
			// multiple records equal case
			outBytes = new ByteArrayOutputStream();
			out = new ObjectOutputStream(outBytes);
			out.writeObject(log1);
			out.flush();
			out.close();
			bytes = outBytes.toByteArray();
			inBytes = new ByteArrayInputStream(bytes);
			in = new ObjectInputStream(inBytes);
			deserialized = (SessionLog)in.readObject();
			in.close();
			assertEquals(log1, deserialized);
			assertEquals(log1.hashCode(), deserialized.hashCode());
			
			// same except for failed moves count
			log1.recordFailedMove();
			assertNotEquals(log1, deserialized);
			
			// same except for successful moves count
			deserialized.recordFailedMove();
			assertEquals(log1, deserialized);
			assertEquals(log1.hashCode(), deserialized.hashCode());
			log1.recordMove(new Item("A",5), new Bin("Bin 1", 1));
			assertNotEquals(log1, deserialized);
			
			// different object types
			assertFalse(log1.equals("hello"));
			
			// null case
			assertFalse(log1.equals(null));
			
		} catch (IOException ex) { 
			fail(); 
		} catch (ClassNotFoundException ex) {
			fail();
		}
	}
	
	@Test
	public void testFormatSummaryStats() {
		SessionLog log = new SessionLog();
		log.recordMove(new Item("A",22), new Bin("Bin 1", 1));
		log.recordMove(new Item("B",22), new Bin("Bin 1", 1));
		log.recordMove(new Item("C",22), new Bin("Bin 1", 1));
		log.recordFailedMove();
		log.recordMove(new Item("D",22), new Bin("Bin 1", 1));
		log.recordMove(new Item("E",22), new Bin("Bin 1", 1));
		
		log.addEntry("SET_MODE", "1");
		log.recordMove(new Item("F",22), new Bin("Bin 1", 1));
		log.recordFailedMove();
		log.recordMove(new Item("G",22), new Bin("Bin 1", 1));
		log.recordMove(new Item("H",22), new Bin("Bin 1", 1));
		log.recordFailedMove();
		log.recordMove(new Item("I",22), new Bin("Bin 1", 1));
		
		log.addEntry("SET_MODE", "2");
		log.recordFailedMove();
		log.recordMove(new Item("J",22), new Bin("Bin 1", 1));
		log.recordMove(new Item("K",22), new Bin("Bin 1", 1));
		log.recordFailedMove();
		log.recordMove(new Item("L",22), new Bin("Bin 1", 1));
		log.recordFailedMove();
		
		log.addEntry("SET_MODE", "3");
		log.recordFailedMove();
		log.recordMove(new Item("M",22), new Bin("Bin 1", 1));
		log.recordFailedMove();
		log.recordFailedMove();
		log.recordMove(new Item("N",22), new Bin("Bin 1", 1));
		log.recordFailedMove();
		
		log.addEntry("SET_MODE", "4");
		log.recordFailedMove();
		log.recordFailedMove();
		log.recordFailedMove();
		log.recordMove(new Item("O",22), new Bin("Bin 1", 1));
		log.recordFailedMove();
		log.recordFailedMove();
		
		String summary = log.formatSummaryStats();
		int i = summary.indexOf("practice");
		int j = summary.indexOf("</tr>", i+8);
		int x = summary.indexOf(">5</td>", i+8);
		int y = summary.indexOf(">1</td>", i+8);
		assertTrue(x > 0);
		assertTrue(x < y);
		assertTrue(y > 0);
		
		i = summary.indexOf("first-fit");
		j = summary.indexOf("</tr>", i+9);
		x = summary.indexOf(">4</td>", i+9);
		y = summary.indexOf(">2</td>", i+9);
		
		i = summary.indexOf("first-fit decreasing");
		j = summary.indexOf("</tr>", i+20);
		x = summary.indexOf(">3</td>", i+20);
		y = summary.indexOf(">3</td>", x+7);
		
		i = summary.indexOf("best-fit");
		j = summary.indexOf("</tr>", i+8);
		x = summary.indexOf(">2</td>", i+8);
		y = summary.indexOf(">4</td>", i+8);
		
		i = summary.indexOf("best-fit decreasing");
		j = summary.indexOf("</tr>", i+21);
		x = summary.indexOf(">1</td>", i+8);
		y = summary.indexOf(">5</td>", i+8);
	}
	
	@Test
	public void testAlertDetectors() {
		SessionLog log = new SessionLog();
		ArrayList<String> alerts = new ArrayList<String>();
		
		String s = log.malformed(alerts);
		assertTrue(s.indexOf("MALFORMED")>=0);
		assertEquals(1, alerts.size());
		assertTrue(alerts.get(0).indexOf("malformed")>=0);
		
		assertTrue(log.checkTimeDifference(2, 2, alerts));
		assertEquals(1, alerts.size());
		assertTrue(log.checkTimeDifference(2, 3, alerts));
		assertEquals(1, alerts.size());
		assertFalse(log.checkTimeDifference(2, 1, alerts));
		assertEquals(2, alerts.size());
		assertTrue(alerts.get(1).indexOf("time sequence")>=0);
		
		String good = "ModeNum=1, Instance=Default, Mode=first-fit";
		String expected = "Instance=Default, Mode=first-fit";
		assertEquals(expected, log.formatCompletedData(good, alerts));
		assertEquals(2, alerts.size());
		
		String mal = "ModeNum=1, Instanc=Default, Mode=first-fit";
		s = log.formatCompletedData(mal, alerts);
		assertTrue(s.indexOf("MALFORMED")>=0);
		assertEquals(3, alerts.size());
		assertTrue(alerts.get(2).indexOf("malformed")>=0);
		mal = "ModeNum=1, Instance=Default Mode=first-fit";
		s = log.formatCompletedData(mal, alerts);
		assertTrue(s.indexOf("MALFORMED")>=0);
		assertEquals(4, alerts.size());
		assertTrue(alerts.get(3).indexOf("malformed")>=0);
		
		mal = "ModeNu=1, Instance=Default, Mode=first-fit";
		s = log.formatCompletedData(mal, alerts);
		assertTrue(s.indexOf("MALFORMED")>=0);
		assertEquals(5, alerts.size());
		assertTrue(alerts.get(4).indexOf("malformed")>=0);
		mal = "ModeNum=1 Instance=Default, Mode=first-fit";
		s = log.formatCompletedData(mal, alerts);
		assertTrue(s.indexOf("MALFORMED")>=0);
		assertEquals(6, alerts.size());
		assertTrue(alerts.get(5).indexOf("malformed")>=0);
		
		mal = "ModeNum=1, Instance=Default, Mode first-fit";
		s = log.formatCompletedData(mal, alerts);
		assertTrue(s.indexOf("MALFORMED")>=0);
		assertEquals(7, alerts.size());
		assertTrue(alerts.get(6).indexOf("malformed")>=0);
	}
	
	@Test
	public void testFormatActions() {
		SessionLog log = new SessionLog();
		ArrayList<String> alerts = new ArrayList<String>();
		
		String s = log.formatAllLoggedActions(alerts);
		assertEquals(2, countMatches("<tr>", s));
		assertEquals(0, alerts.size());
		
		log.addEntry("SET_MODE", "4");
		s = log.formatAllLoggedActions(alerts);
		assertEquals(3, countMatches("<tr>", s));
		assertEquals(0, alerts.size());
		
		log.addEntry("COMPLETED", "ModeNum=1, Instance=Default, Mode=first-fit");
		s = log.formatAllLoggedActions(alerts);
		assertEquals(4, countMatches("<tr>", s));
		assertEquals(0, alerts.size());
		
		log.addEntry("SOLUTION", "Doesn't matter as it should be ignored by this method.");
		s = log.formatAllLoggedActions(alerts);
		assertEquals(4, countMatches("<tr>", s));
		assertEquals(0, alerts.size());
	}
	
	private int countMatches(String pattern, String text) {
		int c = 0;
		int index = text.indexOf(pattern);
		while (index >= 0) {
			c++;
			index = text.indexOf(pattern, index + pattern.length());
		}
		return c;
	}
	
	@Test
	public void testSolutionItemOrdering() {
		SessionLog log = new SessionLog();
		ArrayList<String> alerts = new ArrayList<String>();
		
		int[] sizes = {30, 50, 20, 40, 35};
		String[] items = {"A", "B", "C", "D", "E"};
		
		assertFalse(log.checkItemOrder(sizes, items, 0, alerts));
		assertEquals(0, alerts.size());
		
		assertTrue(log.checkItemOrder(sizes, items, 1, alerts));
		assertEquals(0, alerts.size());
		assertTrue(log.checkItemOrder(sizes, items, 3, alerts));
		assertEquals(0, alerts.size());
		assertFalse(log.checkItemOrder(sizes, items, 2, alerts));
		assertEquals(1, alerts.size());
		assertFalse(log.checkItemOrder(sizes, items, 4, alerts));
		assertEquals(2, alerts.size());
		
		int[] sizesSorted =    {50,  40,  35,  30,  20};
		String[] itemsSorted = {"B", "D", "E", "A", "C"};
		
		assertTrue(log.checkItemOrder(sizesSorted, itemsSorted, 2, alerts));
		assertEquals(2, alerts.size());
		assertTrue(log.checkItemOrder(sizesSorted, itemsSorted, 4, alerts));
		assertEquals(2, alerts.size());
		assertFalse(log.checkItemOrder(sizesSorted, itemsSorted, 1, alerts));
		assertEquals(3, alerts.size());
		assertFalse(log.checkItemOrder(sizesSorted, itemsSorted, 3, alerts));
		assertEquals(4, alerts.size());
	}
	
	@Test
	public void testSolutionBinOrdering() {
		int[] sizesSorted = { 50, 35, 35, 30, 25, 10, 8, 8, 5 };
		int[] ffBins =      {  1,  1,  2,  2,  2,  1, 2, 3, 1 };
		int[] bfBins =      {  1,  1,  2,  2,  2,  2, 1, 3, 1 };
		
		SessionLog log = new SessionLog();
		ArrayList<String> alerts = new ArrayList<String>();
		
		assertFalse(log.checkBins(sizesSorted, ffBins, 0, alerts));
		assertEquals(0, alerts.size());
		assertFalse(log.checkBins(sizesSorted, bfBins, 0, alerts));
		assertEquals(0, alerts.size());
		
		assertTrue(log.checkBins(sizesSorted, ffBins, 2, alerts));
		assertEquals(0, alerts.size());
		assertTrue(log.checkBins(sizesSorted, bfBins, 4, alerts));
		assertEquals(0, alerts.size());
		
		assertFalse(log.checkBins(sizesSorted, bfBins, 2, alerts));
		assertEquals(1, alerts.size());
		assertFalse(log.checkBins(sizesSorted, ffBins, 4, alerts));
		assertEquals(2, alerts.size());
		
		int[] sizes =  {20, 50, 50, 35, 10, 8, 5};
		int[] ffBins2 = {1,  1,  2,  2,  1, 1, 1};
		int[] bfBins2 = {1,  1,  2,  2,  2, 1, 2};
		
		assertFalse(log.checkBins(sizes, ffBins2, 0, alerts));
		assertEquals(2, alerts.size());
		assertFalse(log.checkBins(sizes, bfBins2, 0, alerts));
		assertEquals(2, alerts.size());
		
		assertTrue(log.checkBins(sizes, ffBins2, 1, alerts));
		assertEquals(2, alerts.size());
		assertTrue(log.checkBins(sizes, bfBins2, 3, alerts));
		assertEquals(2, alerts.size());
		
		assertFalse(log.checkBins(sizes, bfBins2, 1, alerts));
		assertEquals(3, alerts.size());
		assertFalse(log.checkBins(sizes, ffBins2, 3, alerts));
		assertEquals(4, alerts.size());
	}
	
	@Test
	public void testBinCount() {
		SessionLog log = new SessionLog();
		
		int[][] cases = {
			{},
			{1, 1, 1, 1, 1, 1},
			{1, 2, 2, 1, 2, 1},
			{3, 2, 3, 1, 2, 3},
			{3, 2, 4, 1, 4, 3},
			{5, 2, 4, 1, 4, 3},
			{5, 2, 4, 1, 6, 3}
		};
		for (int i = 0; i < cases.length; i++) {
			assertEquals(i, log.binCount(cases[i]));
		}
	}
	
	@Test
	public void testCheckInstance() {
		SessionLog log = new SessionLog();
		ArrayList<String> alerts = new ArrayList<String>();
		
		int[] defaultSizes = {
			36, 33, 39, 43, 7, 19, 37, 8, 29, 28, 37, 23, 29, 10, 22, 11, 33, 9, 17, 30
		};
		
		int[] defaultWrong = {
			37, 33, 39, 43, 7, 19, 37, 8, 29, 29, 37, 23, 29, 10, 22, 11, 33, 9, 17, 31
		};
		
		String[] items = {
			"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", 
			"K", "L", "M", "N", "O", "P", "Q", "R", "S", "T"
		};
		
		String[] duplicateItems = {
			"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", 
			"K", "L", "M", "N", "O", "P", "Q", "R", "S", "A"
		};
		
		String[] unknownItems = {
			"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", 
			"K", "L", "M", "N", "O", "P", "Q", "R", "S", "U"
		};
		
		assertTrue(log.checkInstance(defaultSizes, items, "Default", alerts));
		assertEquals(0, alerts.size());
		assertFalse(log.checkInstance(defaultSizes, duplicateItems, "Default", alerts));
		assertEquals(2, alerts.size());
		assertFalse(log.checkInstance(defaultSizes, unknownItems, "Default", alerts));
		assertEquals(3, alerts.size());
		assertFalse(log.checkInstance(defaultWrong, items, "Default", alerts));
		assertEquals(6, alerts.size());
		
		int[] selectSizes = {
			27, 48, 27, 35, 47, 34, 26, 46, 35, 23, 25, 38, 44, 25, 32, 20, 50, 36, 21, 25
		};
		
		int[] selectWrong = {
			28, 48, 27, 35, 47, 34, 26, 46, 35, 23, 25, 38, 44, 25, 32, 20, 50, 36, 21, 24
		};
		
		String selectInstance = "#" + Integer.MAX_VALUE;
		assertTrue(log.checkInstance(selectSizes, items, selectInstance, alerts));
		assertEquals(6, alerts.size());
		assertFalse(log.checkInstance(selectSizes, duplicateItems, selectInstance, alerts));
		assertEquals(8, alerts.size());
		assertFalse(log.checkInstance(selectSizes, unknownItems, selectInstance, alerts));
		assertEquals(9, alerts.size());
		assertFalse(log.checkInstance(selectWrong, items, selectInstance, alerts));
		assertEquals(11, alerts.size());
		
		assertFalse(log.checkInstance(selectSizes, items, "#notanumber", alerts));
		assertEquals(12, alerts.size());
		assertFalse(log.checkInstance(selectSizes, items, "Defualt", alerts));
		assertEquals(13, alerts.size());
		
		assertTrue(log.checkInstance(selectSizes, items, "Random", alerts));
		assertEquals(13, alerts.size());
		assertFalse(log.checkInstance(selectSizes, duplicateItems, "Random", alerts));
		assertEquals(15, alerts.size());
		assertFalse(log.checkInstance(selectSizes, unknownItems, "Random", alerts));
		assertEquals(16, alerts.size());
		assertTrue(log.checkInstance(selectWrong, items, "Random", alerts));
		assertEquals(16, alerts.size());
		selectWrong[0] = 19;
		assertFalse(log.checkInstance(selectWrong, items, "Random", alerts));
		assertEquals(17, alerts.size());
		selectWrong[selectWrong.length-1] = 51;
		assertFalse(log.checkInstance(selectWrong, items, "Random", alerts));
		assertEquals(19, alerts.size());
	}
	
	@Test
	public void testValidateSolution() {
		SessionLog log = new SessionLog();
		ArrayList<String> completionTableRows = new ArrayList<String>();
		ArrayList<String> alertList = new ArrayList<String>();
		String selectInstance = "#" + Integer.MAX_VALUE;
		String completedTemplate = "ModeNum=%d, Instance=%s, Mode=%s";
		
		String ffSolution = "ItemSequence=A 27 B 48 C 27 D 35 E 47 F 34 G 26 H 46 I 35 J 23 K 25 L 38 M 44 N 25 O 32 P 20 Q 50 R 36 S 21 T 25, BinSequence=1 1 2 2 3 2 3 4 4 1 3 5 5 6 6 6 7 7 6 8";
		
		String decreasingSolution = "ItemSequence=Q 50 B 48 E 47 H 46 M 44 L 38 R 36 D 35 I 35 F 34 O 32 A 27 C 27 G 26 K 25 N 25 T 25 J 23 S 21 P 20, BinSequence=1 1 2 2 3 3 4 4 5 5 6 4 5 6 6 7 7 7 7 8";
		
		int modeNum = 1;
		String modeName = "first-fit";
		assertEquals(modeNum, log.validateSolution(
			String.format(completedTemplate, modeNum, selectInstance, modeName),
			ffSolution, modeName, selectInstance,
			completionTableRows, alertList
		));
		assertEquals(0, alertList.size());
		assertEquals(1, completionTableRows.size());
		String row = completionTableRows.get(0);
		assertTrue(row.indexOf(modeName+"</td>")>0);
		assertTrue(row.indexOf(selectInstance)>0);
		assertTrue(row.indexOf("with 8 bins")>0);
		
		modeNum = 3;
		modeName = "best-fit";
		assertEquals(modeNum, log.validateSolution(
			String.format(completedTemplate, modeNum, selectInstance, modeName),
			ffSolution, modeName, selectInstance,
			completionTableRows, alertList
		));
		assertEquals(0, alertList.size());
		assertEquals(2, completionTableRows.size());
		row = completionTableRows.get(1);
		assertTrue(row.indexOf(modeName+"</td>")>0);
		assertTrue(row.indexOf(selectInstance)>0);
		assertTrue(row.indexOf("with 8 bins")>0);
		
		modeNum = 2;
		modeName = "first-fit decreasing";
		assertEquals(modeNum, log.validateSolution(
			String.format(completedTemplate, modeNum, selectInstance, modeName),
			decreasingSolution, modeName, selectInstance,
			completionTableRows, alertList
		));
		assertEquals(0, alertList.size());
		assertEquals(3, completionTableRows.size());
		row = completionTableRows.get(2);
		assertTrue(row.indexOf(modeName+"</td>")>0);
		assertTrue(row.indexOf(selectInstance)>0);
		assertTrue(row.indexOf("with 8 bins")>0);
		
		modeNum = 4;
		modeName = "best-fit decreasing";
		assertEquals(modeNum, log.validateSolution(
			String.format(completedTemplate, modeNum, selectInstance, modeName),
			decreasingSolution, modeName, selectInstance,
			completionTableRows, alertList
		));
		assertEquals(0, alertList.size());
		assertEquals(4, completionTableRows.size());
		row = completionTableRows.get(3);
		assertTrue(row.indexOf(modeName+"</td>")>0);
		assertTrue(row.indexOf(selectInstance)>0);
		assertTrue(row.indexOf("with 8 bins")>0);

		String wrongInstance = "#1";
		assertEquals(-1, log.validateSolution(
			String.format(completedTemplate, modeNum, wrongInstance, modeName),
			decreasingSolution, modeName, selectInstance,
			completionTableRows, alertList
		));
		assertEquals(1, alertList.size());
		assertEquals(4, completionTableRows.size());
		
		String wrongModeName = "best-fit";
		assertEquals(-1, log.validateSolution(
			String.format(completedTemplate, modeNum, selectInstance, modeName),
			decreasingSolution, wrongModeName, selectInstance,
			completionTableRows, alertList
		));
		assertEquals(2, alertList.size());
		assertEquals(4, completionTableRows.size());
		
		int wrongModeNum = modeNum - 1;
		assertEquals(-1, log.validateSolution(
			String.format(completedTemplate, wrongModeNum, selectInstance, modeName),
			decreasingSolution, modeName, selectInstance,
			completionTableRows, alertList
		));
		assertEquals(3, alertList.size());
		assertEquals(4, completionTableRows.size());
		
		assertEquals(-1, log.validateSolution(
			String.format(completedTemplate, 0, selectInstance, "practice"),
			decreasingSolution, "practice", selectInstance,
			completionTableRows, alertList
		));
		assertEquals(4, alertList.size());
		assertEquals(4, completionTableRows.size());
		
		String[] badSolutions = {
			"ItemSequence Q 50 B 48 E 47 H 46 M 44 L 38 R 36 D 35 I 35 F 34 O 32 A 27 C 27 G 26 K 25 N 25 T 25 J 23 S 21 P 20, BinSequence=1 1 2 2 3 3 4 4 5 5 6 4 5 6 6 7 7 7 7 8",
			
			"ItemSequence=Q 50 B 48 E 47 H 46 M 44 L 38 R 36 D 35 I 35 F 34 O 32 A 27 C 27 G 26 K 25 N 25 T 25 J 23 S 21 P 20 BinSequence=1 1 2 2 3 3 4 4 5 5 6 4 5 6 6 7 7 7 7 8",
			
			"ItemSequence=Q 50 B 48 E 47 H 46 M 44 L 38 R 36 D 35 I 35 F 34 O 32 A 27 C 27 G 26 K 25 N 25 T 25 J 23 S 21 P 20, BinSequence 1 1 2 2 3 3 4 4 5 5 6 4 5 6 6 7 7 7 7 8",
			
			"ItemSequence=Q 50 B 48 E 47 H 46 M 44 L 38 R 36 D 35 I 35 F 34 O 32 A 27 C 27 G 26 K 25 N 25 T 25 J 23 S 21 P 20, BinSequence=",
			
			"ItemSequence=Q 50 B 48 E 47 H 46 M 44 L 38 R 36 D 35 I 35 F 34 O 32 A 27 C 27 G 26 K 25 N 25 T 25 J 23 S 21 P 20, BinSequence=1 1 2 2 3 3 4 4 5 5 6 4 5 6 6 7 7 7 7 eight",
			
			"ItemSequence=Q 50 B 48 E 47 H 46 M 44 L 38 R 36 D 35 I 35 F 34 O 32 A 27 C 27 G 26 K 25 N 25 T 25 J 23 S 21, BinSequence=1 1 2 2 3 3 4 4 5 5 6 4 5 6 6 7 7 7 7 8",
			
			"ItemSequence=Q 50 B 48 E 47 H 46 M 44 L 38 R 36 D 35 I 35 F 34 O 32 A 27 C 27 G 26 K 25 N 25 T 25 J 23 S 21 P twenty, BinSequence=1 1 2 2 3 3 4 4 5 5 6 4 5 6 6 7 7 7 7 8",
			
			"ItemSequence=Q 50 B 48 E 47 H 46 M 44 L 38 R 36 D 35 I 35 F 34 O 32 A 27 C 27 G 26 K 25 N 25 T 25 J 23 S 21 P 20, BinSequence=1 1 2 2 3 3 4 4 5 5 6 4 5 6 6 7 7 7 8 8",
			
			"ItemSequence=Q 50 B 48 E 47 H 46 M 44 L 38 R 36 D 35 I 35 F 34 O 32 A 27 C 27 G 26 K 25 N 25 T 25 J 23 P 20 S 21, BinSequence=1 1 2 2 3 3 4 4 5 5 6 4 5 6 6 7 7 7 7 8"
		};
		
		for (int i = 0; i < badSolutions.length; i++) {
			assertEquals(-1, log.validateSolution(
				String.format(completedTemplate, modeNum, selectInstance, modeName),
				badSolutions[i], modeName, selectInstance,
				completionTableRows, alertList
			));
			assertEquals(5+i, alertList.size());
			assertEquals(4, completionTableRows.size());
		}
		
		String badRandomSolution = "ItemSequence=Q 50 B 48 E 47 H 46 M 44 L 38 R 36 D 35 I 35 F 34 O 32 A 27 C 27 G 26 K 25 N 25 T 25 J 23 S 21 P 19, BinSequence=1 1 2 2 3 3 4 4 5 5 6 4 5 6 6 7 7 7 7 8";
		
		assertEquals(-1, log.validateSolution(
			String.format(completedTemplate, modeNum, "Random", modeName),
			badRandomSolution, modeName, "Random",
			completionTableRows, alertList
		));
		int numAlerts = 5+badSolutions.length;
		assertEquals(numAlerts, alertList.size());
		assertEquals(4, completionTableRows.size());
	}
	
	@Test
	public void testFormatCompletions() {
		SessionLog log = new SessionLog();
		ArrayList<String> alertList = new ArrayList<String>();
		int modeNum = 1;
		String modeName = "first-fit";
		String selectInstance = "#" + Integer.MAX_VALUE;
		String completedTemplate = "ModeNum=%d, Instance=%s, Mode=%s";
		
		log.addEntry("SET_MODE", ""+modeNum);
		log.addEntry("SELECT_INSTANCE", selectInstance);
		
		Bin[] bins = new Bin[8];
		for (int i = 0; i < bins.length; i++) {
			bins[i] = new Bin("Bin "+(i+1), i+1);
		}
		Item[] items = {
			new Item("A", 27), new Item("B", 48), new Item("C", 27), new Item("D", 35), new Item("E", 47), new Item("F", 34), new Item("G", 26), new Item("H", 46), new Item("I", 35), new Item("J", 23), new Item("K", 25), new Item("L", 38), new Item("M", 44), new Item("N", 25), new Item("O", 32), new Item("P", 20), new Item("Q", 50), new Item("R", 36), new Item("S", 21), new Item("T", 25)
		};
		int[] binSeq = {
			1, 1, 2, 2, 3, 2, 3, 4, 4, 1, 3, 5, 5, 6, 6, 6, 7, 7, 6, 8
		};
		
		// Fake a solution without completion record
		String falseSolution = "ItemSequence=A 27 B 48 C 27 D 35 E 47 F 34 G 26 H 46 I 35 J 23 K 25 L 38 M 44 N 25 O 32 P 20 Q 50 R 36 S 21 T 25, BinSequence=1 1 2 2 3 2 3 4 4 1 3 5 5 6 6 6 7 7 6 8";
		log.addEntry("SOLUTION", falseSolution);
		String s = log.formatCompletions(alertList);
		assertEquals(1, alertList.size());
		assertEquals(0, countMatches("<tr>", s));
		assertTrue(s.indexOf("NO VERIFIABLE RECORDS")>=0);
		
		String falseCompletion = String.format(completedTemplate, modeNum, selectInstance, modeName);
		log.addEntry("COMPLETED", falseCompletion);
		s = log.formatCompletions(alertList);
		assertEquals(3, alertList.size());
		assertEquals(0, countMatches("<tr>", s));
		assertTrue(s.indexOf("NO VERIFIABLE RECORDS")>=0);
		
		// Good cases
		
		for (int i = 0; i < items.length; i++) {
			log.recordMove(items[i], bins[binSeq[i]-1]);
		}
		log.recordHeuristicModeCompletion();
		
		s = log.formatCompletions(alertList);
		assertEquals(5, alertList.size());
		assertEquals(2, countMatches("<tr>", s));
		assertTrue(s.indexOf("NO VERIFIABLE RECORDS")<0);
		
		log.addEntry("RESET", "");
		for (int i = 0; i < items.length; i++) {
			log.recordMove(items[i], bins[binSeq[i]-1]);
		}
		log.recordHeuristicModeCompletion();
		
		s = log.formatCompletions(alertList);
		assertEquals(7, alertList.size());
		assertEquals(3, countMatches("<tr>", s));
		assertTrue(s.indexOf("NO VERIFIABLE RECORDS")<0);
		
		// More bad cases
		
		// Case: Not enough moves to explain solution.
		//       But solution itself is good.
		log.addEntry("COMPLETED", falseCompletion);
		log.addEntry("SOLUTION", falseSolution);
		s = log.formatCompletions(alertList);
		assertEquals(10, alertList.size());
		assertEquals(4, countMatches("<tr>", s));
		assertTrue(s.indexOf("NO VERIFIABLE RECORDS")<0);
		
		// Case: Wrong mode
		falseCompletion = String.format(completedTemplate, modeNum+1, selectInstance, modeName);
		log.addEntry("COMPLETED", falseCompletion);
		log.addEntry("SOLUTION", falseSolution);
		s = log.formatCompletions(alertList);
		assertEquals(14, alertList.size());
		assertEquals(4, countMatches("<tr>", s));
		assertTrue(s.indexOf("NO VERIFIABLE RECORDS")<0);
	}
	
	@Test
	public void testFormatSessionLog() {
		SessionLog log = new SessionLog();
		String s = log.formatSessionLog().strip();
		assertTrue(s.startsWith("<html>"));
		assertTrue(s.endsWith("</html>"));
		assertTrue(s.indexOf("Something went wrong")<0);
		
		CallBack cb = new CallBack() {
			@Override public void call() {}
		};
		int[] sizes = new int[5];
		for (int i = 0; i < 5; i++) sizes[i] = 25;
		Floor f = new Floor(sizes);
		ApplicationState state = new ApplicationState(9, f, cb, cb, cb);
		s = state.getFormattedLogData().strip();
		assertTrue(s.startsWith("<html>"));
		assertTrue(s.endsWith("</html>"));
		assertTrue(s.indexOf("Something went wrong")<0);
	}
	
	@Test
	public void testExtractMethods() {
		SessionLog log = new SessionLog();
		String[] cases = { "ModeNum=0, Instance=Default, Mode=practice",
			"ModeNum=1, Instance=Default, Mode=first-fit",
			"ModeNum=2, Instance=Random, Mode=first-fit decreasing",
			"ModeNum=3, Instance=#1, Mode=best-fit",
			"ModeNum=4, Instance=#987, Mode=best-fit decreasing"
		};
		String[] expectedModes = {"practice", "first-fit", "first-fit decreasing", "best-fit", "best-fit decreasing"};
		String[] expectedInstance = {"Default", "Default", "Random", "#1", "#987"};
		
		for (int i = 0; i < cases.length; i++) {
			assertEquals(i, log.extractModeNum(cases[i]));
			assertEquals(expectedModes[i], log.extractModeName(cases[i]));
			assertEquals(expectedInstance[i], log.extractInstance(cases[i]));
		}
		
		assertEquals("", log.extractModeName("ModeNum=0, Instance=Default, Mode practice"));
		assertEquals("", log.extractInstance("ModeNum=0, Instance Default, Mode=practice"));
		assertEquals("", log.extractInstance("ModeNum=0, Instance=Default Mode=practice"));
		assertEquals(-1, log.extractModeNum("ModeNum 1, Instance=Default, Mode=first-fit"));
		assertEquals(-1, log.extractModeNum("ModeNum=1 Instance=Default, Mode=first-fit"));
		assertEquals(-1, log.extractModeNum("ModeNum=1 Instance=Default Mode=first-fit"));
	}
	
	@Test
	public void testFormatTimestamp() {
		SessionLog log = new SessionLog();
		assertTrue(log.formatTimestamp(0, true).indexOf("INCONSISTENT") < 0);
		assertTrue(log.formatTimestamp(0, false).indexOf("INCONSISTENT") >= 0);
	}
	
	@Test
	public void testFormatAlerts() {
		SessionLog log = new SessionLog();
		assertTrue(log.formatAlerts(new ArrayList<String>()).indexOf("NO ALERTS") >= 0);
		ArrayList<String> alerts = new ArrayList<String>();
		alerts.add("Test Alert 1");
		alerts.add("Test Alert 2");
		String formatted = log.formatAlerts(alerts);
		assertTrue(formatted.indexOf("NO ALERTS") < 0);
		assertTrue(formatted.indexOf("<li>Test Alert 1</li>") >= 0);
		assertTrue(formatted.indexOf("<li>Test Alert 2</li>") >= 0);
		String remove1 = formatted.substring(formatted.indexOf("<li>")+4);
		String remove2 = remove1.substring(remove1.indexOf("<li>")+4);
		assertTrue(remove2.length() > 0);
		assertTrue(remove2.indexOf("<li>") < 0);
	}
	
	private ApplicationState createApplicationState() {
		CallBack cb = new CallBack() {
			@Override public void call() {}
		};
		int[] sizes = new int[5];
		for (int i = 0; i < 5; i++) sizes[i] = 25;
		Floor f = new Floor(sizes);
		ApplicationState state = new ApplicationState(9, f, cb, cb, cb);
		return state;
	}

}