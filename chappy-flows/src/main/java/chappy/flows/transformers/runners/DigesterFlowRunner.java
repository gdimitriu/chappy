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
package chappy.flows.transformers.runners;

import java.io.InputStream;

import javax.ws.rs.core.MultivaluedMap;
import org.apache.commons.digester3.Digester;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import chappy.configurations.transformers.StaxonConfiguration;
import chappy.flows.transformers.dynamicflows.DigesterStepsFactory;
import chappy.flows.transformers.dynamicflows.MapOfStepsParametersFactory;
import chappy.interfaces.flows.IFlowRunner;
import chappy.utils.streams.wrappers.StreamHolder;

/**
 * This create the digester flow runner.
 * @author Gabriel Dimitriu
 *
 */
public class DigesterFlowRunner implements IFlowRunner {
	
	/** configuration stream */
	private InputStream configurationStream = null;
	/** data from multipart */
	private FormDataMultiPart multipart = null;
	/** query from rest request */
	private MultivaluedMap<String, String> queryParams = null;
	
	private Digester digester = new Digester();
	/**
	 * constructor need for reflection
	 */
	public DigesterFlowRunner() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.flows.IFlowRunner#createSteps()
	 */
	@Override
	public void createSteps() throws ClassNotFoundException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, NoSuchFieldException, SecurityException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.flows.IFlowRunner#executeSteps(chappy.utils.streams.wrappers.StreamHolder)
	 */
	@Override
	public StreamHolder executeSteps(final StreamHolder holder) throws Exception {
		digester.push(holder);
		digester.push(multipart);
		digester.push(queryParams);
		return digester.parse(configurationStream);
	}

	@Override
	public void setConfigurations(final InputStream configurationStream,
			final FormDataMultiPart multipart,
			final MultivaluedMap<String, String> queryParams) {
		
		this.configurationStream = configurationStream;
		this.multipart = multipart;
		this.queryParams = queryParams;
		
		digester.addFactoryCreate("*/step", new DigesterStepsFactory());

		String[] paramTypes = new String[] { "java.lang.String" };
		digester.addCallMethod("*/step/disabled", "setDisabled", 1, paramTypes);
		digester.addCallParam("*/step/disabled", 0);
		String[] parameters = MapOfStepsParametersFactory.getSingletion().getParameters();
		String[] fields = MapOfStepsParametersFactory.getSingletion().getMapFields();
		for (int i = 0 ;i < parameters.length; i++) {
			digester.addCallMethod("*/step/parameters/"+ parameters[i], fields[i], 0);
		}
		digester.addObjectCreate("*/step/configuration",
				StaxonConfiguration.class);

		// this do not work so is not equivalent
		// digester.addSetProperties("*/step/configuration/*", new String[]
		// {"autoArray", "autoPrimitive", "virtualNode"},
		// new String[] {"autoArray", "autoPrimitive", "virtualNode"});
		digester.addCallMethod("*/step/configuration/autoArray", "setAutoArray", 0);
		digester.addCallMethod("*/step/configuration/virtualNode", "setVirtualNode", 0);
		digester.addCallMethod("*/step/configuration/autoPrimitive", "setAutoPrimitive", 0);
		digester.addSetNext("*/step/configuration", "setConfiguration");
		
//		digester.addSetProperties("*/step/parameters", MapOfStepsParametersFactory.getSingletion().getParameters(),
//				MapOfStepsParametersFactory.getSingletion().getMapFields());

		paramTypes = new String[] { "chappy.utils.streams.wrappers.StreamHolder",
				"org.glassfish.jersey.media.multipart.FormDataMultiPart", "javax.ws.rs.core.MultivaluedMap" };
		digester.addCallMethod("*/step", "execute", 3, paramTypes);
		digester.addCallParam("*/step", 0, 3);
		digester.addCallParam("*/step", 1, 2);
		digester.addCallParam("*/step", 2, 1);
	}

	@Override
	public void configure(final String mode, final String configuration) {
		//nothing to to for digester
	}

	@Override
	public void createSteps(String userName) throws Exception {
		//nothing to do the the digester flow because this flow could not be overriden
	}
}
