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
package chappy.utils.changebytecode;

import java.io.IOException;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.Remapper;

/**
 * @author Gabriel Dimitriu
 *
 */
public class ChangeByteCode {
	
	/**
	 * remap the bytecode of the class.
	 * @param bytecode received by the system
	 * @param remapper transformation that should be done.
	 * @return remapped bytecode.
	 * @throws IOException
	 */
	 public byte[] remapByteCode(final byte[] bytecode, final Remapper remapper) throws IOException {
	        ClassReader classReader = new ClassReader(bytecode);
	        ClassWriter classWriter = new ClassWriter(classReader, 0);
	        classReader.accept(
	                new ClassRemapper(classWriter, remapper),
	                0
	            );

	        return classWriter.toByteArray();
	    }
}


