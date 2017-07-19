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
 * this hold the persistence configuration module. 
 * @author Gabriel Dimitriu
 *
 */

@XmlAccessorType(XmlAccessType.FIELD)
public class PersistenceConfiguration {
	
	@XmlElement( name = "persistenceUnit")
	private String persistenceUnit;
	
	@XmlElement( name = "framework")
	private String framework;
	
	@XmlElement( name = "feature")
	private FeaturePersistenceConfiguration[] features;
	
	/**
	 * dummy constructor
	 */
	public PersistenceConfiguration() {
		// TODO Auto-generated constructor stub
	}
	
	/**get the persistence framework
	 * @return the framework
	 */
	public String getFramework() {
		return framework;
	}
	/**
	 * set the persistence framework
	 * @param framework the framework to set
	 */
	public void setFramework(final String framework) {
		this.framework = framework;
	}

	/**
	 * get the persistence framework plugin feature
	 * @return the features
	 */
	public FeaturePersistenceConfiguration[] getFeatures() {
		return features;
	}

	/**
	 * set the persistence framework plugin feature
	 * @param features the features to set
	 */
	public void setFeatures(final FeaturePersistenceConfiguration[] features) {
		this.features = features;
	}

	/**
	 * @return the persistenceUnit
	 */
	public String getPersistenceUnit() {
		return persistenceUnit;
	}

	/**
	 * @param persistenceUnit the persistenceUnit to set
	 */
	public void setPersistenceUnit(final String persistenceUnit) {
		this.persistenceUnit = persistenceUnit;
	}

}
