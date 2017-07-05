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
package chappy.interfaces.persistence;

import chappy.configurations.system.PersistenceConfiguration;

/**
 * @author Gabriel Dimitriu
 *
 */
public interface IPersistence {
	
	/** configure the persistence */
	void configure(final PersistenceConfiguration configuration);
	
	/**
	 * get the implementation type of persistence
	 * @return name of the framework as string
	 */
	String getFramework();
	
	/**
	 * get the framework features which is implemented by this instance.
	 * @return array of string as feature list.
	 */
	String[] getFeatures();
}
