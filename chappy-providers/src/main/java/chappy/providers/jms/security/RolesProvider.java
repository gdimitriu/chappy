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
package chappy.providers.jms.security;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.activemq.artemis.core.security.Role;

/**
 * Role provider for JMS.
 * @author Gabriel Dimitriu
 *
 */
public class RolesProvider {

	/** map of roles */
	private Map<String, Set<Role>> roles = new HashMap<String, Set<Role>>();

	/**
	 * 
	 */
	public RolesProvider(final Set<String> queues) {
	    Role role = new Role("user", true, true, false, false, false, false,false, false, false, false);
	    Set<Role> userRole = new HashSet<Role>();
	    userRole.add(role);
	    role = new Role("system", true, true, true, true, true, true, true, true, true, true);
	    userRole.add(role);
	    for (String queue : queues) {
	    	roles.put(queue, userRole);
	    }
	}

	/**
	 * get the roles for this server.
	 * @return map or roles
	 */
	public Map<String, Set<Role>> getRoles() {
		return roles;
	}
}
