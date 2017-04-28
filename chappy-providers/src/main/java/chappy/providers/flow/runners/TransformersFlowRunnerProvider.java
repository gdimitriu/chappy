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
package chappy.providers.flow.runners;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.MultivaluedMap;
import javax.xml.bind.JAXBException;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.reflections.Reflections;
import org.xml.sax.SAXException;
import chappy.interfaces.exception.IChappyException;
import chappy.interfaces.flows.IFlowRunner;
import chappy.providers.exception.ExceptionMappingProvider;

/**
 * @author Gabriel Dimitriu
 *
 */
public class TransformersFlowRunnerProvider {

	/** singleton for provider */
	private static TransformersFlowRunnerProvider singleton = new TransformersFlowRunnerProvider();
	
	/** map of runners and string correspondence */
	Map<String, Class<? extends IFlowRunner>> runners = null;
	
	/**
	 * constructor for singleton.
	 */
	private TransformersFlowRunnerProvider() {
		loadRunners();
	}
	
	/**
	 * get the singleton instance.
	 * @return singleton instance.
	 */
	static public TransformersFlowRunnerProvider getInstance() {
		return singleton;
	}
	
	/**
	 * reload the instances.
	 */
	public void reloadInstance() {
		loadRunners();
	}
	
	private void loadRunners() {
		Reflections reflection = new Reflections("chappy.flows.transformers.runners");
		Set<Class<? extends IFlowRunner>> availableRunners = reflection.getSubTypesOf(IFlowRunner.class);
		if (availableRunners.size() == 0) {
			runners = null;
			return;
		}
		runners = new HashMap<String, Class<? extends IFlowRunner>>();
		for (Class<? extends IFlowRunner> elem : availableRunners) {
			String name = elem.getSimpleName().replace("[", "").replace("]","");
			name = name.substring(0, name.indexOf("Runner"));
			runners.put(name, elem);
		}
	}
	/**
	 * create flow runner.
	 * @param name
	 * @param configurationStream
	 * @param multipart
	 * @param queryParams
	 * @return
	 * @throws JAXBException
	 * @throws SAXException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	public IFlowRunner createFlowRunner(final String name,
			final InputStream configurationStream,
			final FormDataMultiPart multipart,
			final MultivaluedMap<String, String> queryParams) throws Exception {
		if (runners == null || !runners.containsKey(name)) {
			return null;
		}
		IFlowRunner runner = null;
		try {
			runner = runners.get(name).newInstance();
			runner.setConfigurations(configurationStream, multipart, queryParams);
		} catch (Exception e) {
			if (e instanceof IChappyException) {
				throw e;
			}
			throw ExceptionMappingProvider.getInstace().mapException(e);
		}
		return runner;
	}
}
