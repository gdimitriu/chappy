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
package chappy.utils.loaders;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * a simple ClassLoader used to load classes defined by bytecode. 
 * @author Gabriel Dimitriu
 *
 */
public class JavaClassLoaderSimple extends ClassLoader {
	
	private Map<String,Class<?>> types = new HashMap<String,Class<?>>();
	
	public JavaClassLoaderSimple(final ClassLoader classLoader) {
		super(classLoader);
	}
	
	@Override
	public Class<?> loadClass(final String name) throws ClassNotFoundException {
		if(name==null) {
			return null;
		}
		if(types.containsKey(name)){
			return types.get(name);
		}
		try {
			return findSystemClass(name);
		} catch (Exception e) {
		}
		return null;
	}
	
	public Class<?> loadClass(final String name, final String data) throws ClassNotFoundException {
		
		byte[] buffer = Base64.getDecoder().decode(data);
		Class<?> type = defineClass(null, buffer, 0, buffer.length);
		types.put(name,type);
		return type;
	}
	
	public Class<?> loadClass(final String name, final byte[] buffer) throws ClassNotFoundException {	
		Class<?> type = defineClass(null, buffer, 0, buffer.length);
		types.put(name,type);
		return type;
	}
}
