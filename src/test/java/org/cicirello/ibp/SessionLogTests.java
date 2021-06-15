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

/**
 * JUnit tests for the SessionLog class of the
 * Interactive Bin Packing Application.
 */
public class SessionLogTests {
	
		@Test
	public void testSessionLogEqualsHashCodeSerialization() {
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

}