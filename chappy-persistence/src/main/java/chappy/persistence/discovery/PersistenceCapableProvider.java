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
package chappy.persistence.discovery;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.jdo.annotations.PersistenceCapable;

import org.reflections.Reflections;

import chappy.interfaces.constants.IChappyPackagesConstants;
import chappy.utils.classes.RTIClassUtils;

/**
 * @author Gabriel Dimitriu
 *
 */
public class PersistenceCapableProvider {

	/**
	 * 
	 */
	public PersistenceCapableProvider() {
		// TODO Auto-generated constructor stub
	}
	
	
	/**
	 * get all classes from chappy which has persistence capability.
	 * @return list of classes which are persistence capable
	 */
	static public List<String> getAllPersistenceCapableClasses() {
		Reflections reflection = new Reflections("chappy");
		Set<Class<?>> classes = reflection.getTypesAnnotatedWith(PersistenceCapable.class);
		List<String> all = new ArrayList<String>();
		for (Class<?> cPersistence : classes) {
			all.add(cPersistence.getName());
		}
		return all;
	}
	
	/**
	 * get the classes from chappy which has persistence capability and is
	 * marked with the type interface maker.
	 * @param type of persistence
	 * @return list of classes name.
	 * @throws ClassNotFoundException
	 */
	static public List<String> getPersistenceType(final String type) throws ClassNotFoundException {
		List<String> classes = new ArrayList<String>();
		String marker = "I" + type + "PersistenceMarker";
		Reflections reflections = new Reflections("chappy");
		Class<?> markerClass = Class.forName(IChappyPackagesConstants.CHAPPY_INTERFACES_MARKERS + marker);
		Set<Class<?>> allPresistedclasses = reflections.getTypesAnnotatedWith(PersistenceCapable.class);
		for (Class<?> cl : allPresistedclasses) {
			List<Class<?>> interfaces = RTIClassUtils.getAllInterfaces(cl);
			for (Class<?> implInterface : interfaces) {
				if (implInterface.equals(markerClass)) {
					classes.add(cl.getName());
					break;
				}
			}
		}
		return classes;
	}

}
