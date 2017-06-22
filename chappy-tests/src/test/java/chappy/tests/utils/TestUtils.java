/**
    Copyright (c) 2017 Gabriel Dimitriu All rights reserved.
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
package chappy.tests.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.util.List;

import chappy.utils.streams.StreamUtils;

/**
 * utils for unitests.
 * @author Gabriel Dimitriu
 *
 */
public final class TestUtils {

	/**
	 * 
	 */
	public TestUtils() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * compare with order two list of streams.
	 * @param expected list of streams
	 * @param actual list of streams
	 */
	public static void compareTwoListOfStreams(final List<InputStream> expected, final List<InputStream> actual) {
		if (expected.size() != actual.size()) {
			fail("nr of messages from both lists are not equal");
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
	public static void compareTwoListOfStrings(final List<String> expected, final List<String> actual) {
		if (expected.size() != actual.size()) {
			fail("nr of messages from both lists are not equal");
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
	public static void compareTwoListWithoutOrder(final List<?> expected, final List<?> actual) {
		if (expected.size() != actual.size()) {
			fail("nr of messages from both lists are not equal");
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
				fail("the message " + o+ " does not exist");
			}
		}
	}
}
