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
package chappy.providers.rest.resources;

import java.util.Set;

import org.reflections.Reflections;

import chappy.interfaces.rest.resources.IRestResourceProvider;

/**
 * This hold the singleton for the REST resource provider.
 * This hold the classes description for all registered REST resources.
 * @author Gabriel Dimitriu
 *
 */
public class RestResourcesProvider {

	private static final RestResourcesProvider singleton = new RestResourcesProvider();
	
	private RestResourcesProvider() {
		
	}
	/**
	 * get the singleton instance.
	 * @return singleton instance
	 */
	public static RestResourcesProvider getInstance() {
		return singleton;
	}
	
	/**
	 * get the resources providers.
	 * @return array of rest resource providers
	 */
	public IRestResourceProvider[] getResourceProviders() {
		Reflections reflections = new Reflections("chappy.providers.rest.resources");
		Set<Class<? extends IRestResourceProvider>> availableResources = reflections.getSubTypesOf(IRestResourceProvider.class);
		IRestResourceProvider[] providers = new IRestResourceProvider[availableResources.size()];
		int index = 0;
		for (Class<? extends IRestResourceProvider> current : availableResources) {
			IRestResourceProvider provider = null;
			try {
				 provider = current.newInstance();
				 providers[index] = provider;
				 index++;
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return providers;
	}
}
