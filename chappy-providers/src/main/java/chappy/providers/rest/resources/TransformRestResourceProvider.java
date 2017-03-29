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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Path;

import org.reflections.Reflections;

import chappy.interfaces.rest.resources.IRestResourceProvider;

/**
 * @author Gabriel Dimitriu
 *
 */
public class TransformRestResourceProvider implements IRestResourceProvider {

	private static final String REST_TRANSFORM_RESOURCES_PACKAGE = 
			"chappy.services.servers.rest.resources.transform";

	/**
	 * constructor
	 */
	public TransformRestResourceProvider() {
		// TODO Auto-generated constructor stub
	}
	
	/* (non-Javadoc)
	 * @see chappy.interfaces.rest.resources.IRestResourceProvider#getRegisteredResources()
	 */
	@Override
	public List<Class<?>> getRegisteredResources() {
		Reflections reflections = new Reflections(REST_TRANSFORM_RESOURCES_PACKAGE);
		Set<Class<?>> availableResources = reflections.getTypesAnnotatedWith(Path.class);
		List<Class<?>> resources = new ArrayList<Class<?>>();
		resources.addAll(availableResources);
		return resources;
	}

}
