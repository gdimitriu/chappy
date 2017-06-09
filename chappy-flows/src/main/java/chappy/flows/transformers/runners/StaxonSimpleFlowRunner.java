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
import java.util.List;

import javax.ws.rs.core.MultivaluedMap;
import javax.xml.bind.JAXBException;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.xml.sax.SAXException;

import chappy.interfaces.flows.IFlowRunner;
import chappy.interfaces.transformers.ITransformerStep;
import chappy.providers.transformers.TransformerProvider;
import chappy.utils.streams.wrappers.StreamHolder;

/**
 * This hold the staxon runner.
 * @author Gabriel Dimitriu
 *
 */
public class StaxonSimpleFlowRunner implements IFlowRunner {

	private String mode;
	
	private String configuration;

	/**
	 * 
	 */
	public StaxonSimpleFlowRunner() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.flows.IFlowRunner#createSteps()
	 */
	@Override
	public void createSteps() throws ClassNotFoundException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, NoSuchFieldException, SecurityException {
		//nothing to do for simple flow
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.flows.IFlowRunner#executeSteps(chappy.utils.streams.wrappers.StreamHolder)
	 */
	@Override
	public StreamHolder executeSteps(final StreamHolder holder) throws Exception {
		ITransformerStep step = null;
		switch (mode) {
			case "json2xml":
				step = TransformerProvider.getInstance().createStep("Json2XmlStep");
				step.setSerializedConfiguration(configuration);
				step.execute(holder, null, null);
				break;
			case "xml2json":
				step = TransformerProvider.getInstance().createStep("Xml2JsonStep");
				step.setSerializedConfiguration(configuration);
				step.execute(holder, null, null);
				break;
			default:
				return null;
		}
		
		return holder;
	}

	@Override
	public void configure(final String mode, final String configuration) {
		this.mode = mode;
		this.configuration = configuration;
	}

	@Override
	public void setConfigurations(final InputStream configurationStream, final FormDataMultiPart multipart,
			final MultivaluedMap<String, String> queryParams) throws JAXBException, SAXException {
		// TODO Auto-generated method stub
	}

	@Override
	public void createSteps(final String userName) throws Exception {
		//nothing to do for staxon simple flow because this flow could not be overriden
	}

	@Override
	public List<StreamHolder> executeSteps(final List<StreamHolder> holders) throws Exception {
		//nothing to transform yet.
		return null;
	}

}
