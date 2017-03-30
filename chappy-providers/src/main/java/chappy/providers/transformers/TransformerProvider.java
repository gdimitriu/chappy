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
package chappy.providers.transformers;

import chappy.interfaces.transformers.ITransformerStep;
import chappy.providers.transformers.custom.CustomTransformerProvider;

/**
 * @author Gabriel Dimitriu
 *
 */
public class TransformerProvider {

	/** singleton instance */
	static private TransformerProvider singleton = new TransformerProvider();
	
	/**
	 *  constructor of singleton
	 */
	private TransformerProvider() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * get the singleton instance.
	 * @return singleton instance
	 */
	static public TransformerProvider getInstance() {
		return singleton;
	}
	
	/**
	 * This will create the concrete step. 
	 * This is equivalent with the StepsFactory from digester.
	 * @param name of the step (class)
	 * @return created steps
	 * @throws Exception 
	 */
	public ITransformerStep createStep(final String name) throws Exception {
		Class<?> result = null;
		String className = "chappy.transformers.json." + name;
		try {
			result = Class.forName(className);
		} catch (ClassNotFoundException e) {
			className = "chappy.mappings.xslt." + name;
			try {
				result = Class.forName(className);
			} catch (ClassNotFoundException eCustom) {
				return CustomTransformerProvider.getInstance().createStep(name);
			}
		}
		return (ITransformerStep) result.newInstance();
	}
	
	
	/**
	 * This will create the concrete step for the user. 
	 * This is equivalent with the StepsFactory from digester.
	 * @param fullName of the step (class)
	 * @param userName is the user from cookie the same user that have push the transformer.
	 * @return created steps
	 * @throws Exception 
	 */
	public ITransformerStep createStep(final String fullName, final String userName) throws Exception {
		try {
			return CustomTransformerProvider.getInstance().createStep(fullName, userName);
		} catch (Exception e) {
			return createStep(fullName);
		}
	}
}
