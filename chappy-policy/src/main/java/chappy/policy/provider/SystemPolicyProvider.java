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
package chappy.policy.provider;

import chappy.interfaces.policy.IUserPolicy;
import chappy.policy.authentication.CredentialHolder;

/**
 * provider for the authentication system.
 * @author Gabriel Dimitriu
 *
 */
public class SystemPolicyProvider {

	/** singleton */
	private static SystemPolicyProvider singleton = new SystemPolicyProvider();
	
	/**
	 * constructor for singleton.
	 */
	private SystemPolicyProvider() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * get the singleton instance.
	 * @return singleton.
	 */
	public static SystemPolicyProvider getInstance() {
		return singleton;
	}
	
	/**
	 * get the authentication handler 
	 * @return authentication handler
	 */
	public IUserPolicy getAuthenticationHandler() {
		return new IUserPolicy() {
		};
	}
	
	public CredentialHolder getCredentialForSystemResource(final Object resource) {
		if (JMSRuntimeResourceProvider.getInstance().isSystemRuntimeResource(resource)) {
			return new CredentialHolder("system", "system");
		}
		return new CredentialHolder("guest","");
	}
}
