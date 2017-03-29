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
package chappy.utils.streams.wrappers;

import java.io.ByteArrayOutputStream;

/**
 * This is wrapper over ByteArrayOutputStream to have access to the buffer.
 * This is used by StreamHolder to not duplicate the buffer in case of transformation chain.
 * @author Gabriel Dimitriu
 *
 */
public class ByteArrayOutputStreamWrapper extends ByteArrayOutputStream {

	/**
	 * 
	 */
	public ByteArrayOutputStreamWrapper() {
		super();
	}
	
	/**
	 * @param size
	 */
	public ByteArrayOutputStreamWrapper(int size) {
		super(size);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * get the buffer without copying it.
	 * @return the buffer
	 */
	public synchronized byte[] getBuffer() {
		return this.buf;
	}
}
