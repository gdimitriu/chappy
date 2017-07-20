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

import chappy.interfaces.markers.ISystemUpgradePersistence;

/**
 * Interface for persistence of custom transformers.
 * This is used to persist and retrieve custom transformers from transaction.
 * @author Gabriel Dimitriu
 *
 */
public interface ICustomStepPersistence extends ISystemUpgradePersistence{

	/**
	 * set the step name as is in storage provider.
	 * This is full name.
	 * @param name of the step as in storage
	 */
	void setStepName(final String name);
	
	/**
	 * get the step name as is in storage provider.
	 * @return name of step.
	 */
	String getStepName();
	
	/**
	 * set the bytecode after remaper.
	 * @param bytecode after remapper.
	 */
	void setByteCode(final byte[] bytecode);
	
	/**
	 * get the bytecode after remapper.
	 * @return bytecode after remapper
	 */
	byte[] getByteCode();
}
