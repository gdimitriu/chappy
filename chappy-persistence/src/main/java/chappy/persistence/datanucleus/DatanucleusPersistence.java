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

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;
import org.datanucleus.PropertyNames;
import org.datanucleus.enhancer.DataNucleusEnhancer;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.metadata.PersistenceUnitMetaData;
import org.reflections.Reflections;

import chappy.configurations.system.FeaturePersistenceConfiguration;
import chappy.configurations.system.PersistenceConfiguration;
import chappy.configurations.system.PropertyConfiguration;
import chappy.interfaces.persistence.IPersistence;
import chappy.interfaces.transactions.ITransaction;
import chappy.loaders.JavaClassLoaderSimple;
import chappy.loaders.resolver.ClassLoaderSingletonProvider;
import chappy.loaders.resolver.exceptions.*;
import chappy.persistence.discovery.PersistenceCapableProvider;
import chappy.utils.files.DeleteUtils;
import chappy.utils.streams.StreamUtils;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * This is the specialization of the persistence for the framework Datanucleus.
 * @author Gabriel Dimitriu
 *
 */
public class DatanucleusPersistence implements IPersistence {

	private PersistenceUnitMetaData persistenceUnit = null;
	
	private PersistenceManagerFactory persistenceManagerFactory = null;
	
	private JavaClassLoaderSimple runtimeClassLoader = new JavaClassLoaderSimple(ClassLoader.getSystemClassLoader());

	/* (non-Javadoc)
	 * @see chappy.interfaces.persistence.IPersistence#configure(chappy.configurations.system.PersistenceConfiguration)
	 */
	@Override
	public void configure(final PersistenceConfiguration configuration, final String type) {
		persistenceUnit = new PersistenceUnitMetaData(configuration.getPersistenceUnit(), "RESOURCE_LOCAL", null);
		FeaturePersistenceConfiguration[] features = configuration.getFeatures();
		persistenceUnit.setExcludeUnlistedClasses(true);
		for (FeaturePersistenceConfiguration feature : features) {
			for (PropertyConfiguration propery : feature.getAllProperties()) {
				persistenceUnit.addProperty(propery.getName(), propery.getValue());
			}
		}
		//create temporary directory
		Path tempDirectory = null;
		try {
			tempDirectory = Files.createTempDirectory("enhacerChappy");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String tempDirectoryPath = "d:\\tmp";
		if (tempDirectory != null) {
			tempDirectoryPath = tempDirectory.toAbsolutePath().toString();
		}
		//enhance classes
		DataNucleusEnhancer enhancer = new DataNucleusEnhancer("JDO", null);
		enhancer = enhancer.setVerbose(true);
		enhancer = enhancer.setSystemOut(true);
		enhancer = enhancer.setOutputDirectory(tempDirectoryPath);
		enhancer = enhancer.addPersistenceUnit(persistenceUnit);
		

		List<String> classes = null;
		try {
			JavaClassLoaderSimple compileClassLoader = new JavaClassLoaderSimple(ClassLoader.getSystemClassLoader());
			try {
				ClassLoaderSingletonProvider.getInstance().registerClassLoader("compile-" + configuration.getPersistenceUnit(), compileClassLoader);
				ClassLoaderSingletonProvider.getInstance().setDefaultClassLoader("compile-" + configuration.getPersistenceUnit());
			} catch (ChappyClassLoaderAlreadyRegistered | ChappyClassLoaderNotRegistered e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//add discovery classes
			classes = PersistenceCapableProvider.getPersistenceType(type);
			for (String name : classes) {
				Class<?> cl = Class.forName(name);
				compileClassLoader.loadClass(name,
						StreamUtils.getBytesFromInputStream(ClassLoader.getSystemClassLoader().getResourceAsStream(cl.getName().replace('.', '/') + ".class")));
				persistenceUnit.addClassName(name);
				enhancer.addClasses(name);
			}
			enhancer.setClassLoader(compileClassLoader);
			enhancer.enhance();
			for (String name : classes) {
				try {
					runtimeClassLoader.loadClass(name, enhancer.getEnhancedBytes(name));
				} catch (NucleusException e) {
					//just continue it was previously enhanced on disk.
					e.printStackTrace();
				}
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Map<Object, Object> props = new HashMap<Object, Object>();
		props.putAll(persistenceUnit.getProperties());
		props.put(PropertyNames.PROPERTY_CLASSLOADER_PRIMARY, runtimeClassLoader);
		props.put("datanucleus.autoStartClassNames", classes);
		props.put("datanucleus.rdbms.dynamicSchemaUpdates", true);
		props.put(PropertyNames.PROPERTY_MAX_FETCH_DEPTH, "-1");
		props.put(PropertyNames.PROPERTY_CLASSLOADER_RESOLVER_NAME, "chappy.loaders.resolver.ChappyClassLoaderResolver");
		try {
			ClassLoaderSingletonProvider.getInstance().registerClassLoader("runtime-"+configuration.getPersistenceUnit(), runtimeClassLoader);
			ClassLoaderSingletonProvider.getInstance().setDefaultClassLoader("runtime-"+configuration.getPersistenceUnit());
		} catch (ChappyClassLoaderAlreadyRegistered | ChappyClassLoaderNotRegistered e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//create manager factory
		persistenceManagerFactory = JDOHelper.getPersistenceManagerFactory(props);
		//delete recursive the created directory.
		DeleteUtils.recursiveDeleteDir(tempDirectory.toFile());
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
	public PersistenceManagerFactory getFactory() {
		return persistenceManagerFactory;
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.persistence.IPersistence#createTransaction()
	 */
	@Override
	public ITransaction createTransaction() {
		return new DatanucleusTransaction();
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.persistence.IPersistence#getImplementationOf(java.lang.Class)
	 */
	@Override
	public Class<?> getImplementationOf(final Class<?> interfaceof) {
		Reflections reflection = new Reflections(getClass().getPackage().getName());
		Set<?> rezultat = reflection.getSubTypesOf(interfaceof);
		if(!rezultat.isEmpty()) {
			try {
				return ClassLoaderSingletonProvider.getInstance().loadClass(((Class<?>) rezultat.iterator().next()).getName());
			} catch (ClassNotFoundException e) {
				return null;
			}
		}
		return null;
	}

	
	/* (non-Javadoc)
	 * @see chappy.interfaces.persistence.IPersistence#getClassLoader()
	 */
	@Override
	public ClassLoader getClassLoader() {
		return runtimeClassLoader;
	}
}
