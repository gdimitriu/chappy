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
package chappy.configurations.system;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Gabriel Dimitriu
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "configurations")
public class SystemConfigurations {
	
	@XmlElement(name = "system")
	private SystemConfiguration systemConfiguration;
	@XmlElement(name = "configuration")
	private SystemConfiguration[] servicesConfigurations;
	@XmlElement(name = "persistence")
	private PersistenceConfiguration[] persistenceConfigurations;

	/**
	 * 
	 */
	public SystemConfigurations() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * get the first not system configuration.
	 * @return first services configuration
	 */
	public SystemConfiguration getFirstConfiguration() {
		if (servicesConfigurations.length > 0) {
			return servicesConfigurations[0];
		}
		return null;
	}


	/**
	 * get the services configurations.
	 * @return services configurations
	 */
	public SystemConfiguration[] getServicesConfigurations() {
		return servicesConfigurations;
	}

	/**
	 * get the system configuration.
	 * @return system configuration
	 */
	public SystemConfiguration getSystemConfiguration() {
		return systemConfiguration;
	}

	/** get the persistence configurations
	 * @return the persistenceConfigurations
	 */
	public PersistenceConfiguration[] getPersistenceConfigurations() {
		return persistenceConfigurations;
	}

	/**
	 * set the persistence configurations
	 * @param persistenceConfigurations the persistenceConfigurations to set
	 */
	public void setPersistenceConfigurations(PersistenceConfiguration[] persistenceConfigurations) {
		this.persistenceConfigurations = persistenceConfigurations;
	}

}
