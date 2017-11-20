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
package chappy.policy.authentication;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Holder for User/Password.
 * @author Gabriel Dimitriu
 *
 */
@XmlRootElement
public class CredentialHolder implements Serializable {

	/**
	 * default serial version id.
	 */
	private static final long serialVersionUID = 1L;

	/** the user */
	@XmlElement(name = "user")
	private String user;
	
	/** the password */
	@XmlElement(name = "passwd")
	private String passwd;
	
	/**
	 * default constructor.
	 */
	public CredentialHolder() {
		this.user = null;
		this.passwd = null;
	}
	/**
	 * 
	 */
	public CredentialHolder(final String user, final String passwd) {
		this.user = user;
		this.passwd = passwd;
	}
	
	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}
	
	/**
	 * @return the passwd
	 */
	public String getPasswd() {
		return passwd;
	}
	/**
	 * @param user the user to set
	 */
	public void setUser(String user) {
		this.user = user;
	}
	/**
	 * @param passwd the passwd to set
	 */
	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

}
