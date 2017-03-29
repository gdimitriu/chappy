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
package chappy.utils.streams.rest;
import java.io.IOException;
import java.io.OutputStream;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;
/**
 * Wrapper class to send stream back as HTTP response.
 * @author Gabriel Dimitriu
 *
 */
public class RestStreamingOutput implements StreamingOutput {
	
	private byte[] buffer = null;
	
	private int offset = 0;
	
	private int length = 0;
	
	public RestStreamingOutput(final byte[] buf) {
		this.buffer = buf;
		this.offset = 0;
		this.length = buf.length;
	}

	public RestStreamingOutput(byte[] buf, int offset, int length) {
		this.buffer = buf;
		this.offset = offset;
		this.length = length;
	}

	@Override
	public void write(OutputStream os) throws IOException, WebApplicationException {
		os.write(buffer, offset, length);
		os.flush();
	}
};