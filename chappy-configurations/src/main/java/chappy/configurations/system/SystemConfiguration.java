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

/**
 * This hold the configuration for the system.
 * @author Gabriel Dimitriu
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class SystemConfiguration {

	@XmlElement(name = "name")
	private String name;
	
	@XmlElement(name = "property")
	private PropertyConfiguration[] property;
	/**
	 * 
	 */
	public SystemConfiguration() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * get the property.
	 * @return property
	 */
	public String getProperty() {
		if (property != null) {
			return property[0].getValue();
		}
		return null;
	}
	
	/**
	 * get the property by nr.
	 * @param i
	 * @return property at i
	 */
	public PropertyConfiguration getProperty(final int i) {
		return property[i];
	}
	
	/**
	 * get the name of the service.
	 * @return name of the service.
	 */
	public String getName() {
		return name;
	}

}
