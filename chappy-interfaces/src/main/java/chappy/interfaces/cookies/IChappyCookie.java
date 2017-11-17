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
package chappy.interfaces.cookies;

import java.io.Serializable;

/**
 * Interface for cookies.
 * @author Gabriel Dimitriu
 *
 */
public interface IChappyCookie extends Serializable {

	/**
	 * get the user name
	 * @return the user name
	 */
	String getUserName();

	/**
	 * get the transaction id.
	 * @return transaction id
	 */
	String getTransactionId();

	/**
	 * set the transaction id.
	 * @param transactionId transaction id
	 */
	void setTransactionId(String transactionId);

	/**
	 * generate the storage id.
	 * @return storage id for the hash.
	 */
	String generateStorageId();
	
	/**
	 * get the rest server name
	 * @return rest server name
	 */
	String getRestServerName();
	
	/**
	 * get the rest server port
	 * @return rest server port
	 */
	int getRestServerPort();
	
	
	/**
	 * get the jms server name
	 * @return jms server name
	 */
	String getJmsServerName();
	
	/**
	 * get the jms server port
	 * @return jms server port
	 */
	int getJmsServerPort();
	

	/**
	 * @return correlationId used by JMS
	 */
	String getCorrelationId();
	
	
	/**
	 * @param correlationId correlationId used by JMS
	 */
	void setCorrelationId(final String correlationId);
	
	/**
	 * update the server information for the cookie.
	 */
	void update();
}