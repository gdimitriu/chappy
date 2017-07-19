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
import chappy.interfaces.transactions.ITransaction;

/**
 * @author Gabriel Dimitriu
 *
 */
public interface IPersistence {
	
	/** configure the persistence
	 * @param configuration the configuration 
	 * @param type the type: user/system/upgrade/etc
	 * */
	void configure(final PersistenceConfiguration configuration, final String type);
	
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
	
	/**
	 * get the factory for the persistence.
	 * @return factory for persistence
	 */
	Object getFactory();
	
	
	/**
	 * create the transaction based on the type of persistence.
	 * @return transaction.
	 */
	ITransaction createTransaction();
}
