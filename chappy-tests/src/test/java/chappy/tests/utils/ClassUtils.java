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

import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Set;

import org.reflections.Reflections;

import chappy.interfaces.transformers.AbstractStep;

/**
 * @author Gabriel Dimitriu
 *
 */
public class ClassUtils {

	/**
	 * 
	 */
	public ClassUtils() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * getClassAsString
	 * @param className
	 * @param packageName
	 * @return class definition as string
	 * @throws FileNotFoundException
	 */
	public String getClassAsString(final String className, final String packageName) throws FileNotFoundException {
		String classPath = null;
		Reflections ref = new Reflections(packageName);
		Set<Class<? extends AbstractStep>> trasnformers = ref.getSubTypesOf(AbstractStep.class);
		for (Class<? extends AbstractStep> cl : trasnformers) {
			if (cl.getSimpleName().equals(className)){
				classPath = cl.getCanonicalName();
			}
		}
		if (classPath == null) {
			fail("class was not found on system");
		}
		classPath = classPath.replace(".", "/");
		classPath = getClass().getProtectionDomain().getCodeSource().getLocation().getPath() + classPath  + ".class";
		InputStream is = new FileInputStream(new File(classPath));
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int count =0;
		try {
			while((count = is.read(buffer,0,buffer.length))>0 ) {
				outputStream.write(buffer,0,count);
			}
			is.close();
			outputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return Base64.getEncoder().encodeToString(outputStream.toByteArray());
	}
}
