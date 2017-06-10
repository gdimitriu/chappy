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

	public static void compareTwoListOfStreams(final List<InputStream> expected, final List<InputStream> actual) {
		if (expected.size() != actual.size()) {
			fail("nr of messages from both lists are not equal");
		}
		for (int i = 0 ;i < expected.size(); i++) {
			assertEquals("at position " + i, StreamUtils.toStringFromStream(expected.get(i)),
					StreamUtils.toStringFromStream(actual.get(i)));
		}
	}
}
