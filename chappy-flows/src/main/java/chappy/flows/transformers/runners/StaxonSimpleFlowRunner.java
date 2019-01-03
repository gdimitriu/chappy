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

import javax.jdo.annotations.PersistenceCapable;

import chappy.interfaces.cookies.IChappyCookie;
import chappy.interfaces.flows.IFlowRunner;
import chappy.interfaces.flows.MultiDataQueryHolder;
import chappy.interfaces.transformers.ITransformerStep;
import chappy.providers.transformers.TransformerProvider;
import chappy.utils.streams.wrappers.StreamHolder;

/**
 * This hold the staxon runner.
 * @author Gabriel Dimitriu
 *
 */
@PersistenceCapable(detachable = "true")
public class StaxonSimpleFlowRunner implements IFlowRunner {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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
				step.execute(holder, null);
				break;
			case "xml2json":
				step = TransformerProvider.getInstance().createStep("Xml2JsonStep");
				step.setSerializedConfiguration(configuration);
				step.execute(holder, null);
				break;
			default:
				return null;
		}
		
		return holder;
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.flows.IFlowRunner#configure(java.lang.String, java.lang.String)
	 */
	@Override
	public void configure(final String mode, final String configuration) {
		this.mode = mode;
		this.configuration = configuration;
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.flows.IFlowRunner#createSteps(chappy.interfaces.cookies.IChappyCookie)
	 */
	@Override
	public void createSteps(final IChappyCookie cookie) throws Exception {
		//nothing to do for staxon simple flow because this flow could not be overriden
	}
	
	/* (non-Javadoc)
	 * @see chappy.interfaces.flows.IFlowRunner#createSteps(java.lang.String)
	 */
	@Override
	public void createSteps(final String userName, final String passwd) throws Exception {
		//nothing to do for staxon simple flow because this flow could not be overriden
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.flows.IFlowRunner#executeSteps(java.util.List)
	 */
	@Override
	public List<StreamHolder> executeSteps(final List<StreamHolder> holders) throws Exception {
		//nothing to transform yet.
		return null;
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.flows.IFlowRunner#setConfigurations(java.io.InputStream, chappy.interfaces.flows.MultiDataQueryHolder)
	 */
	@Override
	public void setConfigurations(final InputStream configurationStream, final MultiDataQueryHolder multiPart) throws Exception {
		//nothing to do yet
		
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.flows.IFlowRunner#executeSteps(chappy.utils.streams.wrappers.StreamHolder, chappy.interfaces.flows.MultiDataQueryHolder)
	 */
	@Override
	public StreamHolder executeSteps(StreamHolder holder, MultiDataQueryHolder multiPart) throws Exception {
		//nothing to transform yet.
		return null;
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.flows.IFlowRunner#executeSteps(java.util.List, chappy.interfaces.flows.MultiDataQueryHolder)
	 */
	@Override
	public List<StreamHolder> executeSteps(List<StreamHolder> holders, MultiDataQueryHolder multiPart)
			throws Exception {
		//nothing to transform yet.
		return null;
	}

}
