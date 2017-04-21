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
package chappy.providers.exception;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

import chappy.interfaces.exception.IChappyException;

/**
 * provider for mapping exceptions.
 * @author Gabriel Dimitriu
 *
 */
public class ExceptionMappingProvider {

	/** singleton instance */
	private static ExceptionMappingProvider singleton = new ExceptionMappingProvider();
	
	/** map which holds the mapping of one exception to another exception */
	private Map<Class<?>, Class<?>> exceptionMapping = new HashMap<Class<?>, Class<?>>();
	/**
	 *  constructor for singleton.
	 */
	private ExceptionMappingProvider() {
		populateMap();
	}

	/**
	 * get the singleton instance.
	 * @return singleton instance
	 */
	public static ExceptionMappingProvider getInstace() {
		return singleton;
	}
	
	/**
	 * map one exception to the mapped exception.
	 * @param exception to be mapped
	 * @return mapped exception or the original exception in case the exception is not mapping.
	 */
	public Exception mapException(final Throwable inException) {
		if (exceptionMapping.containsKey(inException.getClass())) {
			Class<?> clException = exceptionMapping.get(inException.getClass());
			Exception newException = null;
			try {
				newException = (Exception) clException.newInstance();
			} catch (InstantiationException |IllegalAccessException e) {
				return null;
			}
			return (Exception) newException.initCause(inException);
		}
		return null;
	}
	
	private void populateMap() {
		Reflections reflections = new Reflections("chappy");
		Set<Class<? extends IChappyException>> availableResources = reflections.getSubTypesOf(IChappyException.class);
		
		for (Class<? extends IChappyException> current : availableResources) {
			try {
				List<Class<?>> providers = ((IChappyException) current.newInstance()).isWrapperFor();
				for(Class<?> cl : providers) {
					exceptionMapping.put(cl, current);
				}
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}
}
