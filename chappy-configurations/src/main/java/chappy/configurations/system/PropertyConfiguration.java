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
 * @author Gabriel Dimitriu
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class PropertyConfiguration {

	@XmlElement(name = "name")
	private String name;
	@XmlElement(name = "value")
	private String value;

	/**
	 * 
	 */
	public PropertyConfiguration() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * get the name of the property.
	 * @return property name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * get the value of the property.
	 * @return property value.
	 */
	public String getValue() {
		return value;
	}
}
