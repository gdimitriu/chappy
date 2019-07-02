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
package chappy.utils.streams;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

/**
 * This contains utils for read resources and string transformation.
 * @author Gabriel Dimitriu
 *
 */
public final class StreamUtils {

	/**
	 * 
	 */
	private StreamUtils() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * get the String from the resource.
	 * @param resourceFile resource file
	 * @return the String from the resource
	 */
	static public String getStringFromResource(final String resourceFile) {
		return  new BufferedReader(new InputStreamReader(
				StreamUtils.class.getClassLoader().getResourceAsStream(resourceFile)))
		  .lines().collect(Collectors.joining("\n"));
	}
	
	/**
	 * get the String from the resource without the spaces.
	 * @param resourceFile resource file
	 * @return the String from the resource
	 */
	static public String getStringFromResourceWithoutSpaces(final String resourceFile) {
		return  new BufferedReader(new InputStreamReader(
				StreamUtils.class.getClassLoader().getResourceAsStream(resourceFile)))
		  .lines().map(s -> s.trim()).collect(Collectors.joining(""));
	}
	
	/**
	 * transform the InputStream into a String.
	 * @param stream
	 * @return String
	 */
	static public String toStringFromStream(final InputStream stream) {
		return new BufferedReader(new InputStreamReader(stream)).lines().collect(Collectors.joining("\n"));
	}
	
	/**
	 * get the InputStream from the resource.
	 * @param resourceFile resource file
	 * @return the InputStream from the resource
	 */
	static public InputStream toStreamFromResource(final String resourceFile) {
		return StreamUtils.class.getClassLoader().getResourceAsStream(resourceFile);
	}
	
	/**
	 * get as byte array the input stream.
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public static byte[] getBytesFromInputStream(InputStream is) throws IOException
	{
	    try (ByteArrayOutputStream os = new ByteArrayOutputStream();)
	    {
	        byte[] buffer = new byte[0xFFFF];

	        for (int len; (len = is.read(buffer)) != -1;)
	            os.write(buffer, 0, len);

	        os.flush();

	        return os.toByteArray();
	    }
	}

	public static InputStream toInputStreamFromString(final String str) {
		return  new ByteArrayInputStream(str.getBytes());
	}
}
