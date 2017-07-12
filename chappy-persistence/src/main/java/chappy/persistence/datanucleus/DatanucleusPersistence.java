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
package chappy.persistence.datanucleus;

import java.util.List;

import javax.jdo.PersistenceManagerFactory;

import org.datanucleus.api.jdo.JDOPersistenceManagerFactory;
import org.datanucleus.enhancer.DataNucleusEnhancer;
import org.datanucleus.metadata.PersistenceUnitMetaData;

import chappy.configurations.system.FeaturePersistenceConfiguration;
import chappy.configurations.system.PersistenceConfiguration;
import chappy.configurations.system.PropertyConfiguration;
import chappy.interfaces.persistence.IPersistence;
import chappy.interfaces.transactions.ITransaction;
import chappy.persistence.discovery.PersistenceCapableProvider;

/**
 * This is the specialization of the persistence for the framework Datanucleus.
 * @author Gabriel Dimitriu
 *
 */
public class DatanucleusPersistence implements IPersistence {

	private PersistenceUnitMetaData persistenceUnit = null;
	
	private PersistenceManagerFactory persistenceManagerFactory = null;
	
	
	

	/* (non-Javadoc)
	 * @see chappy.interfaces.persistence.IPersistence#configure(chappy.configurations.system.PersistenceConfiguration)
	 */
	@Override
	public void configure(final PersistenceConfiguration configuration) {
		//add discovery classes
		List<String> classes = PersistenceCapableProvider.getAllPersistenceCapableClasses();
		persistenceUnit = new PersistenceUnitMetaData(configuration.getPersistenceUnit(), "RESOURCE_LOCAL", null);
		classes.stream().forEach(a -> persistenceUnit.addClassName(a));
		FeaturePersistenceConfiguration[] features = configuration.getFeatures();
		persistenceUnit.setExcludeUnlistedClasses();
		for (FeaturePersistenceConfiguration feature : features) {
			for (PropertyConfiguration propery : feature.getAllProperties()) {
				persistenceUnit.addProperty(propery.getName(), propery.getValue());
			}
		}
		//enhance classes
		DataNucleusEnhancer enhancer = new DataNucleusEnhancer("JDO", null);
		enhancer.setVerbose(true);
		enhancer.addPersistenceUnit(persistenceUnit);
//		enhancer.addPersistenceUnit(configuration.getPersistenceUnit());
//		classes.stream().forEach(a -> enhancer.addClasses(a));
		enhancer.addClasses(classes.toArray(new String[1]));
		enhancer.enhance();
		//create manager factory
		persistenceManagerFactory = new JDOPersistenceManagerFactory(persistenceUnit, null);
		
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.persistence.IPersistence#getFramework()
	 */
	@Override
	public String getFramework() {
		return "datanucleus";
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.persistence.IPersistence#getFeatures()
	 */
	@Override
	public String[] getFeatures() {
		String[] ret = new String[1];
		ret[0] = "ODF";
		return ret;
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.persistence.IPersistence#getFactory()
	 */
	@Override
	public Object getFactory() {
		return persistenceManagerFactory;
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.persistence.IPersistence#createTransaction()
	 */
	@Override
	public ITransaction createTransaction() {
		return new DatanucleusTransaction();
	}
}
