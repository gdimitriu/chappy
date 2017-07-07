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
package chappy.providers.persistence;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

import chappy.configurations.system.PersistenceConfiguration;
import chappy.interfaces.cookies.CookieTransaction;
import chappy.interfaces.persistence.IPersistence;
import chappy.policy.provider.SystemPolicyProvider;
import chappy.providers.configurations.SystemConfigurationProvider;

/**
 * This is the provider for the persistence.
 * @author Gabriel Dimitriu
 *
 */
public class PersistenceProvider {

	/** singleton instance */
	static private PersistenceProvider singleton = new PersistenceProvider();

	/** system persistence keyword */
	private static final String SYSTEM_PERSISTENCE = "system";

	/** persistence package */
	private static String PERSISTENCE_PACKAGE = "chappy.persistence";
	
	/** cache map for persistenceUnit names */
	private Map<String, IPersistence> persistenceCache = new HashMap<String, IPersistence>();
	
	/**
	 * cache for system persistence
	 * there is only one system persistence unit.
	 */
	private static IPersistence systemPersistence = null;
	
	/**
	 * private for singleton.
	 * @throws Exception 
	 */
	private PersistenceProvider() {
		//empty
	}

	/**
	 * get the persistence instance.
	 * @return persistence Provider instance.
	 */
	static public PersistenceProvider getInstance() {
		return singleton;
	}

	/**
	 * get the system
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public IPersistence getSystemPersistence() throws InstantiationException, IllegalAccessException {
		if (systemPersistence == null) {
			synchronized (this) {
				if (systemPersistence == null) {
					PersistenceConfiguration[] configurations = SystemConfigurationProvider.getInstance()
							.getSystemConfiguration().getPersistenceConfigurations();
					for (PersistenceConfiguration conf : configurations) {
						if (conf.getPersistenceUnit().equals(SYSTEM_PERSISTENCE)) {
							Reflections reflection = new Reflections(PERSISTENCE_PACKAGE + "." + conf.getFramework());
							Set<Class<? extends IPersistence>> frameworks = reflection
									.getSubTypesOf(IPersistence.class);
							for (Class<? extends IPersistence> framework : frameworks) {
								IPersistence persistence = framework.newInstance();
								if (conf.getFramework().equals(persistence.getFramework())) {
									persistence.configure(conf);
									return systemPersistence;
								}
							}
						}
					}
				}
			}
		}
		return systemPersistence;
	}
	
	/**
	 * get the persistence instance for the user authenticated by cookie.
	 * @param cookie
	 * @return persistence implementation instance
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	synchronized public IPersistence getPersistenceInstance(final CookieTransaction cookie) throws InstantiationException, IllegalAccessException {
		
		String type = SystemPolicyProvider.getInstance().getAuthenticationHandler().persistenceType(cookie.getUserName());
		String cacheUnitPersistence = createCacheUnit(type, cookie.getUserName());
		IPersistence persistence = null;
		//first check in cache
		if (persistenceCache.containsKey(cacheUnitPersistence)) {
			return persistenceCache.get(cacheUnitPersistence);
		}
		
		int featureIndex = type.indexOf("/");
		String frameworkName = type.substring(featureIndex + 1, type.lastIndexOf("/"));
		String persistenceUnit = type.substring(0, featureIndex);
		if (persistenceUnit.equals(SYSTEM_PERSISTENCE)) {
			return null;
		}
		String feature = type.substring(type.lastIndexOf("/") + 1, type.length());
		Reflections reflection = new Reflections(PERSISTENCE_PACKAGE + "." + frameworkName);
		Set<Class<? extends IPersistence>> frameworks = reflection.getSubTypesOf(IPersistence.class);
		for (Class<? extends IPersistence> framework : frameworks) {
			persistence = framework.newInstance();
			if (frameworkName.equals(persistence.getFramework())) {
				 for (String str : persistence.getFeatures()) {
					 if (feature.equals(str)) {
						 persistenceCache.put(cacheUnitPersistence, persistence);
						 persistence.configure(
								 SystemConfigurationProvider.getInstance()
								 .getSystemConfiguration().getPersistenceConfiguration(persistenceUnit, frameworkName, feature));
						 return persistence;
					 }
				 }
			}
		}
		return null;
	}
	
	/**
	 * create the cache name for the persistence implementation.
	 * @param type
	 * @param userName
	 * @return cache key
	 */
	private String createCacheUnit(final String type, final String userName) {		
		return userName + ":" + type;
	}
}
