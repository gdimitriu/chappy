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
package chappy.interfaces.jms.resources;

import javax.jms.Connection;

/**
 * Abstract class for producer/consumer resource.
 * @author Gabriel Dimitriu
 *
 */
public abstract class JMSAbstractProducerConsumer implements IJMSRuntimeResource {
	
	/** current connection is the connection on which this resource will receive messages */
	private Connection currentConnection = null;
	
	/* (non-Javadoc)
	 * @see servers.jms.IResourceProducerConsumer#getFactoryName()
	 */
	@Override
	public String getFactoryName() {
		return this.getClass().getSimpleName();
	}
	
	/* (non-Javadoc)
	 * @see chappy.interfaces.jms.resources.IJMSRuntimeResource#getCurrentConnection()
	 */
	public Connection getCurrentConnection() {
		return currentConnection;
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.jms.resources.IJMSRuntimeResource#setCurrentConnection(javax.jms.Connection)
	 */
	public void setCurrentConnection(Connection currentConnection) {
		this.currentConnection = currentConnection;
	}

}
