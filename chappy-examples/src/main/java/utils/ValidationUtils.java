/**
    Copyright (c) 2018 Gabriel Dimitriu All rights reserved.
	DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.

    This file is part of chappy project.

    Chappy is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Chappy is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Chappy.  If not, see <http://www.gnu.org/licenses/>.
 */
package utils;

import java.io.InputStream;
import java.util.List;

import chappy.utils.streams.StreamUtils;

/**
 * utils for validation.
 * @author Gabriel Dimitriu
 *
 */
public final class ValidationUtils {

	/**
	 * 
	 */
	public ValidationUtils() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * compare with order two list of streams.
	 * @param expected list of streams
	 * @param actual list of streams
	 */
	public static void compareTwoListOfStreams(final List<InputStream> expected, final List<InputStream> actual) throws Exception {
		if (expected.size() != actual.size()) {
			throw new Exception("nr of messages from both lists are not equal");
		}
		for (int i = 0 ;i < expected.size(); i++) {
			assertEquals("at position " + i, StreamUtils.toStringFromStream(expected.get(i)),
					StreamUtils.toStringFromStream(actual.get(i)));
		}
	}
	
	/**
	 * compare with order two list of strings.
	 * @param expected list of strings
	 * @param actual list of strings
	 */
	public static void compareTwoListOfStrings(final List<String> expected, final List<String> actual) throws Exception {
		if (expected.size() != actual.size()) {
			throw new Exception("nr of messages from both lists are not equal");
		}
		for (int i = 0 ;i < expected.size(); i++) {
			assertEquals("at position " + i, expected.get(i),actual.get(i));
		}
	}
	
	/**
	 * compare without order two list of elements
	 * @param expected list
	 * @param actual list
	 */
	public static void compareTwoListWithoutOrder(final List<?> expected, final List<?> actual) throws Exception{
		if (expected.size() != actual.size()) {
			throw new Exception("nr of messages from both lists are not equal");
		}
		boolean found = false;
		for (int i = 0; i < expected.size(); i++) {
			Object o = expected.get(i);
			found = false;
			for (int j = 0 ; j < actual.size(); j++) {
				if (o.equals(actual.get(j))) {
					found = true;
					break;
				}
			}
			if (!found) {
				throw new Exception("the message " + o+ " does not exist");
			}
		}
	}
	
	/**
	 * The fail message.
	 * @param message the message to be printed
	 */
	public static void fail(final String message) {
		System.out.println("The validation of the " + message + "has failed in response from chappy server.");
	}
	
	public static void assertEquals(String message, int expected, int real) {
		// TODO Auto-generated method stub
		
	}

	public static void assertEquals(String message, String expected, String real) {
		// TODO Auto-generated method stub
		
	}
	
	public static void assertEquals(String expected, String real) {
		
	}
	public static void assertEquals(int expected, int real) {
		// TODO Auto-generated method stub
		
	}
	public static void assertFalse(String message, boolean real) {
		// TODO Auto-generated method stub
		
	}

	public static void assertNull(String message, Object real) {
		// TODO Auto-generated method stub
		
	}


}
