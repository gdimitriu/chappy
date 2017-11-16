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
package chappy.policy.cookies;

import javax.xml.bind.annotation.XmlRootElement;

import chappy.interfaces.cookies.IChappyCookie;

/**
 * Base class for cookie.
 * @author Gabriel Dimitriu
 *
 */
@XmlRootElement
public abstract class CookieTransaction implements IChappyCookie {

	/**
	 * default but is overriden in the implementation class.
	 */
	private static final long serialVersionUID = 1L;

	/** name of the user */
	private String userName;
	
	/** transaction Id */
	private String transactionId;
	
	/** rest server name */
	private String restServerName;
	
	/* jms server name */
	private String jmsServerName;
	
	/** rest server port */
	private int restServerPort;
	
	/** jms server port */
	private int jmsServerPort;
	
	/**
	 * base cookie.
	 */
	public CookieTransaction() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see chappy.policy.cookies.IChappyCookie#getUserName()
	 */
	@Override
	public String getUserName() {
		return userName;
	}
	
	/**
	 * set the user name
	 * @param nameUser - the user name which own the cookie
	 */
	public void setUserName(final String nameUser) {
		this.userName = nameUser;
	}

	/* (non-Javadoc)
	 * @see chappy.policy.cookies.IChappyCookie#getTransactionId()
	 */
	@Override
	public String getTransactionId() {
		return transactionId;
	}

	/* (non-Javadoc)
	 * @see chappy.policy.cookies.IChappyCookie#setTransactionId(java.lang.String)
	 */
	@Override
	public void setTransactionId(final String transactionId) {
		this.transactionId = transactionId;
	}

	/* (non-Javadoc)
	 * @see chappy.policy.cookies.IChappyCookie#generateStorageId()
	 */
	@Override
	public String generateStorageId() {
		return userName + ":" + transactionId;
	}

	/**
	 * @return the restServerName
	 */
	@Override
	public String getRestServerName() {
		return restServerName;
	}

	/**
	 * @param restServerName the restServerName to set
	 */
	public void setRestServerName(final String restServerName) {
		this.restServerName = restServerName;
	}

	/**
	 * @return the jmsServerName
	 */
	@Override
	public String getJmsServerName() {
		return jmsServerName;
	}

	/**
	 * @param jmsServerName the jmsServerName to set
	 */
	public void setJmsServerName(final String jmsServerName) {
		this.jmsServerName = jmsServerName;
	}

	/**
	 * @return the restServerPort
	 */
	@Override
	public int getRestServerPort() {
		return restServerPort;
	}

	/**
	 * @param restServerPort the restServerPort to set
	 */
	public void setRestServerPort(final int restServerPort) {
		this.restServerPort = restServerPort;
	}

	/**
	 * @return the jmsServerPort
	 */
	@Override
	public int getJmsServerPort() {
		return jmsServerPort;
	}

	/**
	 * @param jmsServerPort the jmsServerPort to set
	 */
	public void setJmsServerPort(final int jmsServerPort) {
		this.jmsServerPort = jmsServerPort;
	}
}
