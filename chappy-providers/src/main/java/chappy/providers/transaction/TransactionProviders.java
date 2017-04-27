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

import java.util.List;
import java.util.Map;

import chappy.interfaces.cookies.CookieTransaction;

/**
 * providers for transactions.
 * @author Gabriel Dimitriu
 *
 */
public class TransactionProviders {

	/** singleton providers */
	static private TransactionProviders singleton = new TransactionProviders();
	
	private Map<String, List<String>> listOfTransactionData = null;
	
	/**
	 * private because is singleton.
	 */
	private TransactionProviders() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * get the instance of the singleton.
	 * @return singleton of the transactions.
	 */
	public TransactionProviders getInstance() {
		return singleton;
	}

	public List<String> getListOfTransformers(final CookieTransaction cookie) {
		
		return null;
	}
}
