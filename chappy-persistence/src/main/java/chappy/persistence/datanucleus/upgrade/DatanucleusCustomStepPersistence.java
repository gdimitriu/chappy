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
package chappy.persistence.datanucleus.upgrade;

import javax.jdo.annotations.PersistenceCapable;
import chappy.interfaces.markers.ISystemUpgradePersistence;
import chappy.interfaces.persistence.ICustomStepPersistence;

/**
 * @author Gabriel Dimitriu
 *
 */
@PersistenceCapable
public class DatanucleusCustomStepPersistence implements ICustomStepPersistence, ISystemUpgradePersistence {

	/** full step name from the storage provider */
	private String stepName;
	
	/** byte-code from storage provider after re-mapper */
	private byte[] byteCode;
	/**
	 * 
	 */
	public DatanucleusCustomStepPersistence() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.persistence.ICustomStepPersistence#setStepName(java.lang.String)
	 */
	@Override
	public void setStepName(final String name) {
		this.stepName = name;

	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.persistence.ICustomStepPersistence#getStepName()
	 */
	@Override
	public String getStepName() {
		return this.stepName;
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.persistence.ICustomStepPersistence#setByteCode(java.lang.Byte[])
	 */
	@Override
	public void setByteCode(final byte[] bytecode) {
		this.byteCode = bytecode;
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.persistence.ICustomStepPersistence#getByteCode()
	 */
	@Override
	public byte[] getByteCode() {
		return this.byteCode;
	}

}
