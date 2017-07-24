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
package chappy.persistence.providers;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import chappy.exception.providers.ExceptionMappingProvider;
import chappy.interfaces.exception.IChappyException;
import chappy.interfaces.persistence.ICustomStepPersistence;
import chappy.interfaces.persistence.IPersistence;
import chappy.interfaces.transactions.ITransaction;
import chappy.interfaces.transformers.ITransformerStep;
import chappy.remapper.bytecode.RemapperValue;
import chappy.utils.changebytecode.ChangeByteCode;
import chappy.utils.loaders.JavaClassLoaderSimple;

/**
 * @author Gabriel Dimitriu
 *
 */
public class CustomTransformerStorageProvider {

	/** singleton instance */
	private static CustomTransformerStorageProvider singleton = new CustomTransformerStorageProvider();
	
	/** hash storage for notPersistance transformers*/
	private Map<String, byte[]> transformersStorage = null;
	
	/** hash storage for Persistence transformers */
	private Map<String, byte[]> persistenceTransformersStorage = null;
	
	private Map<String, Class<?>> loadedTransformers = null; 
	/**
	 * constructor for singleton
	 */
	private CustomTransformerStorageProvider() {
		transformersStorage = new HashMap<String, byte[]>();
		persistenceTransformersStorage = new HashMap<String, byte[]>();
		loadedTransformers = new HashMap<String, Class<?>>();
		loadPersistenceCustomTransformers();
	}

	/**
	 * load the persited custom transformers.
	 */
	public void loadPersistenceCustomTransformers() {
		try {
			IPersistence persistence = PersistenceProvider.getInstance().getSystemUpgradePersistence();
			PersistenceManager pm = persistence.getFactory().getPersistenceManager();
			Class<?> customPersistenceImpl = persistence.getImplementationOf(ICustomStepPersistence.class);
			if (customPersistenceImpl != null) {
				Query query = pm.newQuery(customPersistenceImpl);
				@SuppressWarnings("unchecked")
				List<ICustomStepPersistence> customs = (List<ICustomStepPersistence>) query.execute();
				customs.stream().forEach(a -> persistenceTransformersStorage.put(a.getStepName(), a.getByteCode()));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * clean the repository
	 */
	public void cleanRepository() {
		transformersStorage = new HashMap<String, byte[]>();
		persistenceTransformersStorage = new HashMap<String, byte[]>();
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
		pushNewUserTransformer(userName, fullName, originalByteCode, null);
	}
	/**
	 * push new transformer defined in one class.
	 * @param userName
	 * @param fullName
	 * @param remappedBytecode
	 * @param transaction transaction
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws IOException 
	 */
	public void pushNewUserTransformer(final String userName, final String fullName, final byte[] originalByteCode , final ITransaction transaction) 
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException {
		RemapperValue remapper = (RemapperValue) getClass().getClassLoader()
					.loadClass("chappy.transformers.custom.Remapper").newInstance();
		remapper.setUserName(userName);
		byte[] remappedBytecode = new ChangeByteCode().remapByteCode(originalByteCode, remapper);
		if (transaction == null || !transaction.isPersistence()) {
			transformersStorage.put(CustomUtils.generateStorageName(userName, fullName), remappedBytecode);
		} else if (transaction.isPersistence()){
			persistenceTransformersStorage.put(CustomUtils.generateStorageName(userName, fullName), remappedBytecode);
			transaction.persistTransformer(CustomUtils.generateStorageName(userName, fullName), remappedBytecode);
		}
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
				Exception ex = ExceptionMappingProvider.getInstace().mapException(e);
				if (ex instanceof IChappyException) {
					((IChappyException) ex).setLocalizedMessage("For transformer " + fullName + "dependencies are not fullfilled " + e.getLocalizedMessage());
					throw ex;
				} else {
					throw new Exception("For transformer " + fullName + "dependencies are not fullfilled " + e.getLocalizedMessage());
				}
			}
		}
		if (transformersStorage.containsKey(fullName)) {
			byte[] classData = transformersStorage.get(fullName);
			return createTransformerStep(fullName, classData);
		} else if (persistenceTransformersStorage.containsKey(fullName)) {
			byte[] classData = persistenceTransformersStorage.get(fullName);
			return createTransformerStep(fullName, classData);
		}
		Exception ex = ExceptionMappingProvider.getInstace().mapException(new ClassNotFoundException("class does not exist on server " + fullName));
		((IChappyException) ex).setLocalizedMessage("The custom transformer " + fullName + " does not exist on server ");
		throw ex;
	}

	/**
	 * create the transformer step.
	 * @param fullName
	 * @param classData
	 * @return transformer step
	 * @throws Exception
	 */
	private ITransformerStep createTransformerStep(final String fullName, byte[] classData) throws Exception {
		JavaClassLoaderSimple simpleLoader = new JavaClassLoaderSimple(getClass().getClassLoader());
		try {
			Class<?> classDefinition = simpleLoader.loadClass(fullName, classData);
			loadedTransformers.put(fullName, classDefinition);
			return (ITransformerStep) classDefinition.newInstance();
		} catch (NoClassDefFoundError | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			Exception ex = ExceptionMappingProvider.getInstace().mapException(e);
			if (ex instanceof IChappyException) {
				if (e instanceof InstantiationException) {
					((IChappyException) ex).setLocalizedMessage("For transformer " + fullName 
							+ "dependencies are not fullfilled " + e.getLocalizedMessage());
				} else if (e instanceof ClassNotFoundException || e instanceof NoClassDefFoundError) {
					((IChappyException) ex).setLocalizedMessage("The custom transformer " + fullName 
							+ "does not exist on server " + e.getLocalizedMessage());
				}
				throw ex;
			}
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

	/**
	 * remove transformers from storage.
	 * @param userName
	 * @param listOfTransformers
	 */
	public void removeTransformers(final String userName, final List<String> listOfTransformers) {
		for (String fullName : listOfTransformers) {
			String storageId = CustomUtils.generateStorageName(userName, fullName);
			if (transformersStorage.containsKey(storageId)) {
				transformersStorage.remove(storageId);
			}
		}
	}

	/**
	 * remove a global custom transformer from storage.
	 * @param transformerName full name
	 * @return true if it deleted it
	 */
	public boolean removeTransformer(String transformerName) {
		if (transformersStorage.containsKey(transformerName)) {
			transformersStorage.remove(transformerName);
			return true;
		}
		return false;
	}

	/**
	 * remove a custom transformer for storage
	 * @param userName user who push the transformer
	 * @param transformerName full name
	 * @return true if it delete it
	 */
	public boolean removeTransformer(String userName, String transformerName) {
		String storageId = CustomUtils.generateStorageName(userName, transformerName);
		if (transformersStorage.containsKey(storageId)) {
			transformersStorage.remove(storageId);
			return true;
		}
		return false;
	}
	
}
