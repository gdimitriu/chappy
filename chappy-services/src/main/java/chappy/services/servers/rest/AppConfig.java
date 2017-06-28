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
package chappy.services.servers.rest;

import java.util.ArrayList;
import java.util.List;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

import chappy.interfaces.rest.ObjectMapperContextResolver;
import chappy.interfaces.rest.resources.IRestResourceProvider;
import chappy.providers.rest.resources.RestResourcesProvider;

/**
 * This is the class which register the resources for jersey REST server.
 * @author Gabriel Dimitriu
 *
 */
public class AppConfig extends ResourceConfig {
	private static List<Class<?>> registeredClasses = new ArrayList<Class<?>>();
	static {
		IRestResourceProvider[] providers = RestResourcesProvider.getInstance().getResourceProviders();
		if (providers != null) {
			for( IRestResourceProvider provider : providers) {
				registeredClasses.addAll(provider.getRegisteredResources());
			}
		}
		registerBasicRest();
	}
	
	/**
	 * this will register the basic needs for jersey.
	 */
	private static void registerBasicRest() {
		registeredClasses.add(MultiPartFeature.class);
		registeredClasses.add(ExceptionMapper.class);
		registeredClasses.add(JacksonJaxbJsonProvider.class);
		registeredClasses.add(ObjectMapperContextResolver.class);
	}
	
	public AppConfig() {
		super(registeredClasses.toArray(new Class<?>[registeredClasses.size()]));
	}
	
	/**
	 * initialize the repository of classes to the basic need of jersey.
	 */
	public static void initRegisteredClasses() {
		registeredClasses.clear();
		registerBasicRest();
	}
}
