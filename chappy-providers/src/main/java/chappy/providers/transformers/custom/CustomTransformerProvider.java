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
package chappy.providers.transformers.custom;

import chappy.interfaces.transformers.ITransformerStep;

/**
 * @author Gabriel Dimitriu
 *
 */
public class CustomTransformerProvider {

	/** singleton instance */
	private static CustomTransformerProvider singleton = new CustomTransformerProvider();
	
	/**
	 * constructor for the singleton.
	 */
	private CustomTransformerProvider() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * get the singleton instance.
	 * @return singleton instance
	 */
	public static CustomTransformerProvider getInstance() {
		return singleton;
	}
	
	/**
	 * This will create the concrete step. 
	 * This is equivalent with the StepsFactory from digester.
	 * @param name of the step (class)
	 * @return created step
	 * @throws Exception 
	 */
	public ITransformerStep createStep(final String name) throws Exception {
		return CustomTransformerStorageProvider.getInstance().getNewInstance(name);
	}
}
