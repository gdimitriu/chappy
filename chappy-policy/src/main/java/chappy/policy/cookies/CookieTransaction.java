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

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

import chappy.configurations.providers.SystemConfigurationProvider;
import chappy.configurations.system.SystemConfiguration;
import chappy.interfaces.cookies.IChappyCookie;
import chappy.policy.authentication.CredentialHolder;

/**
 * Base class for cookie.
 * @author Gabriel Dimitriu
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@JsonAutoDetect(getterVisibility = Visibility.NONE, fieldVisibility = Visibility.ANY)
public abstract class CookieTransaction implements IChappyCookie {

	/**
	 * default but is overridden in the implementation class.
	 */
	private static final long serialVersionUID = 1L;

	@XmlElement(name = "credentials")
	/** name of the user */
	private CredentialHolder credentials;
	
	@XmlElement(name = "transactionId")
	/** transaction Id */
	private String transactionId;
	
	@XmlElement(name = "correlationId")
	/** correlation id for JMS */
	private String correlationId;
	
	@XmlElement(name = "persistence")
	/** persistence required flag */
	private boolean persistence = false;
	
	@XmlElement(name = "servers")
	/** servers available */
	private ServerConnectionInfo[] servers = new ServerConnectionInfo[0];
	
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
		return credentials.getUser();
	}
	
	/* (non-Javadoc)
	 * @see chappy.interfaces.cookies.IChappyCookie#getUserPassword()
	 */
	@Override
	public String getUserPassword() {
		return credentials.getPasswd();
	}
	
	/**
	 * set the user name
	 * @param nameUser - the user name which own the cookie
	 * @param passwd - the user password
	 */
	public void setCredentials(final String nameUser, final String passwd) {
		this.credentials = new CredentialHolder(nameUser, passwd);
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
		return credentials.getUser() + ":" + transactionId;
	}

	/**
	 * @return the restServerName
	 */
	@Override
	public String getRestServerName() {
		for (int i = 0; i < servers.length; i++) {
			if ("rest".equals(servers[i].getType())) {
				return servers[i].getServerName();
			}
		}
		return null;
	}

	/**
	 * @param restServerName the restServerName to set
	 */
	public void setRestServerName(final String restServerName) {
		for (int i = 0; i < servers.length; i++) {
			if ("rest".equals(servers[i].getType())) {
				servers[i].setServerName(restServerName);
				return;
			}
		}
	}

	/**
	 * @return the jmsServerName
	 */
	@Override
	public String getJmsServerName() {
		for (int i = 0; i < servers.length; i++) {
			if ("jms".equals(servers[i].getType())) {
				return servers[i].getServerName();
			}
		}
		return null;

	}

	/**
	 * @param jmsServerName the jmsServerName to set
	 */
	public void setJmsServerName(final String jmsServerName) {
		for (int i = 0; i < servers.length; i++) {
			if ("jms".equals(servers[i].getType())) {
				servers[i].setServerName(jmsServerName);
				return;
			}
		}
	}

	/**
	 * @return the restServerPort
	 */
	@Override
	public int getRestServerPort() {
		for (int i = 0; i < servers.length; i++) {
			if ("rest".equals(servers[i].getType())) {
				return servers[i].getServerPort();
			}
		}
		return -1;

	}

	/**
	 * @param restServerPort the restServerPort to set
	 */
	public void setRestServerPort(final int restServerPort) {
		for (int i = 0; i < servers.length; i++) {
			if ("rest".equals(servers[i].getType())) {
				servers[i].setServerPort(restServerPort);
				return;
			}
		}
	}

	/**
	 * @return the jmsServerPort
	 */
	@Override
	public int getJmsServerPort() {		
		for (int i = 0; i < servers.length; i++) {
			if ("jms".equals(servers[i].getType())) {
				return servers[i].getServerPort();
			}
		}
		return -1;
	}

	/**
	 * @param jmsServerPort the jmsServerPort to set
	 */
	public void setJmsServerPort(final int jmsServerPort) {
		for (int i = 0; i < servers.length; i++) {
			if ("jms".equals(servers[i].getType())) {
				servers[i].setServerPort(jmsServerPort);
			}
		}
	}

	/**
	 * @return the correlationId
	 */
	@Override
	public String getCorrelationId() {
		return correlationId;
	}

	/**
	 * @param correlationId the correlationId to set
	 */
	@Override
	public void setCorrelationId(final String correlationId) {
		this.correlationId = correlationId;
	}
	
	@Override
	public void update() {
		SystemConfiguration[] configurations = SystemConfigurationProvider.getInstance()
				.getSystemConfiguration().getServicesConfigurations();
		servers = new ServerConnectionInfo[configurations.length];
		for (int i = 0; i < servers.length; i++ ) {
			servers[i] = new ServerConnectionInfo();
			servers[i].setType(configurations[i].getName());
			servers[i].setServerPort(Integer.parseInt(configurations[i].getPropertyValue("serverPort")));
			String serverName = configurations[i].getPropertyValue("serverHost");
			if (serverName.isEmpty()) {
				try {
					serverName = InetAddress.getLocalHost().getCanonicalHostName();
				} catch (UnknownHostException e) {
					serverName = "localhost";
				} 
			}
			servers[i].setServerName(serverName);
		}
	}
	
	@Override
	public boolean getPersistence() {
		return this.persistence;
	}
	
	
	/**
	 * @param persistence the persistence flag
	 */
	public void setPersistence(final boolean persistence) {
		this.persistence = persistence;
	}
}
