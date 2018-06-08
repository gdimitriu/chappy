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

import org.datanucleus.ClassLoaderResolverImpl;

import chappy.loaders.resolver.exceptions.ChappyClassLoaderNotRegistered;

/**
 * @author gdimitriu
 *
 */
public class ChappyClassLoaderResolver extends ClassLoaderResolverImpl {

	/**
	 * 
	 */
	public ChappyClassLoaderResolver() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param ctxLoader
	 */
	public ChappyClassLoaderResolver(ClassLoader ctxLoader) {
		super(ctxLoader);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Class classForName(final String name) {
		try {
			ClassLoader classLoader = ClassLoaderSingletonProvider.getInstance().getDefaultClassLoader();
			return super.classForName(name, classLoader);
		} catch (ChappyClassLoaderNotRegistered e) {
			return super.classForName(name); 
		}
	}
	
	@Override
	public Class classForName(final String name, final boolean init) {
		try {
			ClassLoader classLoader = ClassLoaderSingletonProvider.getInstance().getDefaultClassLoader();
			return super.classForName(name, classLoader, init);
		} catch (ChappyClassLoaderNotRegistered e) {
			return super.classForName(name, init); 
		}
	}
	
	@Override
	public Class classForName(String name, ClassLoader primary, boolean initialize) {
		try {
			ClassLoader classLoader = ClassLoaderSingletonProvider.getInstance().getDefaultClassLoader();
			return super.classForName(name, classLoader, initialize);
		} catch (ChappyClassLoaderNotRegistered e) {
			return super.classForName(name, primary, initialize); 
		}
	}
	
	@Override
	public Class classForName(String name, ClassLoader primary) {
		try {
			ClassLoader classLoader = ClassLoaderSingletonProvider.getInstance().getDefaultClassLoader();
			return super.classForName(name, classLoader);
		} catch (ChappyClassLoaderNotRegistered e) {
			return super.classForName(name, primary); 
		}
	}
}
