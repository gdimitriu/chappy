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
package chappy.loaders.resolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import chappy.loaders.resolver.exceptions.ChappyClassLoaderAlreadyRegistered;
import chappy.loaders.resolver.exceptions.ChappyClassLoaderNotRegistered;

/**
 * @author Gabriel Dimitriu
 *
 */
public class ClassLoaderSingletonProvider {

	private static final String DEFAULT_SYSTEM_CLASSLOADER = "defaultSystemClassLoader";

	private static ClassLoaderSingletonProvider singleton = new ClassLoaderSingletonProvider();
	
	private Map<String, ClassLoader> classLoaders = new ConcurrentHashMap<>();
	
	private String defaultClassLoader = DEFAULT_SYSTEM_CLASSLOADER;
	
	/**
	 * 
	 */
	private ClassLoaderSingletonProvider() {
		// TODO Auto-generated constructor stub
		classLoaders.put(DEFAULT_SYSTEM_CLASSLOADER, this.getClass().getClassLoader());
	}
	
	public static ClassLoaderSingletonProvider getInstance() {
		return singleton;
	}

	public void registerClassLoader(final String name, final ClassLoader loader) throws ChappyClassLoaderAlreadyRegistered {
		classLoaders.put(name, loader);		
	}
	
	public ClassLoader getRegisteredClassLoader(final String name) {
		if (classLoaders.containsKey(name)) {
			return classLoaders.get(name);
		}
		return null;
	}
	
	public void setDefaultClassLoader(final String name) throws ChappyClassLoaderNotRegistered {
		if (classLoaders.containsKey(name)) {
			defaultClassLoader = name;
			return;
		} 
		throw new ChappyClassLoaderNotRegistered(name);
	}
	
	public String getDefaultClassLoaderName() {
		return defaultClassLoader;
	}
	
	public ClassLoader getDefaultClassLoader() throws ChappyClassLoaderNotRegistered {
		if (classLoaders.containsKey(defaultClassLoader)) {
			return classLoaders.get(defaultClassLoader);
		}
		throw new ChappyClassLoaderNotRegistered(defaultClassLoader);
	}
	
	public List<ClassLoader> getAllRuntimeClassLoaders() {
		List<ClassLoader> allRuntime = new ArrayList<>();
		for (String key :classLoaders.keySet()) {
			if (key.startsWith("runtime-")) {
				allRuntime.add(classLoaders.get(key));
			}
		}
		return allRuntime;
	}
	
	public Class<?> loadClass(final String name) throws ClassNotFoundException {
		List<ClassLoader> classLoaders = getAllRuntimeClassLoaders();
		for (ClassLoader classLoader : classLoaders) {
			try {
				return classLoader.loadClass(name);
			} catch (Throwable e) {

			}
		}
		return ClassLoader.getSystemClassLoader().loadClass(name); 
	}
}
