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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;

import chappy.interfaces.jms.resources.IJMSRuntimeResource;
import javassist.Modifier;

/**
 * Singleton provider for the registered resources. 
 * @author Gabriel Dimitriu
 *
 */
public class JMSRuntimeResourceProvider {

	/** singleton holder */
	public static JMSRuntimeResourceProvider singleton = new JMSRuntimeResourceProvider();
	
	/** list of resources available at startup */
	private List<IJMSRuntimeResource> systemResources = null;
	
	/** list of resources register by system/user */
	private List<IJMSRuntimeResource> registeredResources= null;
	
	/**
	 * 
	 */
	private JMSRuntimeResourceProvider() {
		systemResources = new ArrayList<>();
		registeredResources = new ArrayList<>();
		loadServerRuntimeResources();
	}

	/**
	 * @return singleton instance.
	 */
	public static JMSRuntimeResourceProvider getInstance() {
		return singleton;
	}
	
	/**
	 * reload all default resources keep registered resources.
	 */
	public void reload() {
		systemResources = new ArrayList<>();
		loadServerRuntimeResources();
	}
	
	/**
	 * @return list of resources.
	 */
	public List<IJMSRuntimeResource> getAllResources() {
		List<IJMSRuntimeResource> all = new ArrayList<>();
		all.addAll(systemResources);
		all.addAll(registeredResources);
		return all;
	}
	
	
	/**
	 * register a resource.
	 * @param resource to be register.
	 */
	public void registerSystemRuntimeResource(final IJMSRuntimeResource resource) {
		registeredResources.add(resource);
	}

	
	/**
	 * return if the resource is a system resource.
	 * @param resource to be checked
	 * @return true if is a system resource false otherwise.
	 */
	public boolean isSystemRuntimeResource(final Object resource) {
		if (resource instanceof IJMSRuntimeResource) {
			if (systemResources.contains(resource)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * scan and load server JMS resources from all chappy packages.
	 */
	private void loadServerRuntimeResources() {
		Reflections reflection = new Reflections("chappy");
		Set<Class<? extends IJMSRuntimeResource>> resources = reflection.getSubTypesOf(IJMSRuntimeResource.class);
		for (Class<? extends IJMSRuntimeResource> resource : resources) {
			if (!Modifier.isAbstract(resource.getModifiers()) && !resource.isInterface()) {
				try {
					systemResources.add(resource.newInstance());
				} catch (InstantiationException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
