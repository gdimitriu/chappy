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
package chappy.clients.common.transaction;

/**
 * @author Gabriel Dimitriu
 *
 */
public class ChappyClientTransactionHolder {
	
	/** holder for the jms transaction */
	private JMSTransactionHolder jmsTransaction = null;
	
	/** holder for the rest transaction */
	private RESTTransactionHolder restTransaction = null;

	/**
	 * 
	 */
	public ChappyClientTransactionHolder() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the jmsTransaction
	 */
	public JMSTransactionHolder getJmsTransaction() {
		return jmsTransaction;
	}

	/**
	 * @param jmsTransaction the jmsTransaction to set
	 */
	public void setJmsTransaction(JMSTransactionHolder jmsTransaction) {
		this.jmsTransaction = jmsTransaction;
	}

	/**
	 * @return the restTransaction
	 */
	public RESTTransactionHolder getRestTransaction() {
		return restTransaction;
	}

	/**
	 * @param restTransaction the restTransaction to set
	 */
	public void setRestTransaction(RESTTransactionHolder restTransaction) {
		this.restTransaction = restTransaction;
	}

}
