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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import chappy.absract.bytecode.RemapperValue;
import chappy.interfaces.transformers.ITransformerStep;
import chappy.utils.changebytecode.ChangeByteCode;
import chappy.utils.loaders.JavaClassLoaderSimple;

/**
 * @author Gabriel Dimitriu
 *
 */
public class CustomTransformerStorageProvider {

	/** singleton instance */
	private static CustomTransformerStorageProvider singleton = new CustomTransformerStorageProvider();
	
	/** hash storage */
	private Map<String, byte[]> transformersStorage = null;
	
	private Map<String, Class<?>> loadedTransformers = null; 
	/**
	 * constructor for singleton
	 */
	private CustomTransformerStorageProvider() {
		transformersStorage = new HashMap<String, byte[]>();
		loadedTransformers = new HashMap<String, Class<?>>();
	}
	
	/**
	 * clean the repository
	 */
	public void cleanRepository() {
		transformersStorage = new HashMap<String, byte[]>();
		loadedTransformers = new HashMap<String, Class<?>>();
	}
	
	/**
	 * get the singleton instance.
	 * @return singleton instance.
	 */
	public static CustomTransformerStorageProvider getInstance() {
		return singleton;
	}

	/**
	 * push new transformer defined in one class.
	 * @param fullName
	 * @param remappedBytecode
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws IOException 
	 */
	public void pushNewTransformer(final String fullName, final byte[] originalByteCode) throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException {
		RemapperValue remapper = (RemapperValue) getClass().getClassLoader()
					.loadClass("chappy.transformers.custom.Remapper").newInstance();
		byte[] remappedBytecode = new ChangeByteCode().remapByteCode(originalByteCode, remapper);
		transformersStorage.put(fullName, remappedBytecode);
	}
	
	/**
	 * push new transformer defined in one class.
	 * @param userName
	 * @param fullName
	 * @param remappedBytecode
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws IOException 
	 */
	public void pushNewUserTransformer(final String userName, final String fullName, final byte[] originalByteCode) 
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException {
		RemapperValue remapper = (RemapperValue) getClass().getClassLoader()
					.loadClass("chappy.transformers.custom.Remapper").newInstance();
		remapper.setUserName(userName);
		byte[] remappedBytecode = new ChangeByteCode().remapByteCode(originalByteCode, remapper);
		transformersStorage.put(CustomUtils.generateStorageName(userName, fullName), remappedBytecode);
	}
	
	/**
	 * get the new instance of the transformer.
	 * @param fullName of the transformer
	 * @return transformer instance
	 * @throws Exception
	 */
	public ITransformerStep getNewInstance(final String fullName) throws Exception {
		if (loadedTransformers.containsKey(fullName)) {
			try {
				return (ITransformerStep) loadedTransformers.get(fullName).newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				throw new Exception("For transformer " + fullName + "dependencies are not fullfilled " + e.getLocalizedMessage());
			}
		}
		if (transformersStorage.containsKey(fullName)) {
			byte[] classData = transformersStorage.get(fullName);
			JavaClassLoaderSimple simpleLoader = new JavaClassLoaderSimple(getClass().getClassLoader());
			Class<?> classDefinition = simpleLoader.loadClass(fullName, classData);
			loadedTransformers.put(fullName, classDefinition);
			return (ITransformerStep) classDefinition.newInstance();
		}
		return null;
	}
	
	/**
	 * get the new instance of the transformer.
	 * @param fullName of the transformer
	 * @return transformer instance
	 * @throws Exception
	 */
	public ITransformerStep getNewInstance(final String fullName, final String userName) throws Exception {
		return getNewInstance(CustomUtils.generateStorageName(userName, fullName));
	}
	
}
