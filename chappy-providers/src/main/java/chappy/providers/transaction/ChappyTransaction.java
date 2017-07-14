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
package chappy.providers.transaction;

import java.io.IOException;

import chappy.interfaces.transactions.AbstractTransaction;

/**
 * @author Gabriel Dimitriu
 *
 */
public class ChappyTransaction extends AbstractTransaction {

	/**
	 * 
	 */
	public ChappyTransaction() {
		
	}

	

	/* (non-Javadoc)
	 * @see chappy.transaction.base.ITransaction#addTransformer(java.lang.String, java.lang.String, byte[])
	 */
	@Override
	public void addTransformer(final String userName, final String fullName, final byte[] originalByteCode) throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException{
		super.addTransformer(userName, fullName, originalByteCode);
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.transactions.ITransaction#start()
	 */
	@Override
	public void start() {
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.transactions.ITransaction#commit()
	 */
	@Override
	public void commit() {
		// TODO Auto-generated method stub		
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.transactions.ITransaction#rollback()
	 */
	@Override
	public void rollback() {
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.transactions.ITransaction#makePersistent(java.lang.Object)
	 */
	@Override
	public void makePersistent(final Object obj) {
		// TODO Auto-generated method stub		
	}
}