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
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MultivaluedMap;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.xml.sax.SAXException;

import chappy.flows.transformers.staticflows.FlowConfiguration;
import chappy.flows.transformers.staticflows.StepConfiguration;
import chappy.interfaces.flows.IFlowRunner;
import chappy.interfaces.transformers.ITransformerStep;
import chappy.providers.transformers.TransformerProvider;
import chappy.utils.streams.wrappers.StreamHolder;

/**
 * This hold the configuration of the flow.
 * This also create internally the step classes from the flow.
 * This will call the execute from the steps.
 * @author Gabriel Dimitriu
 *
 */
public class StaticFlowRunner implements IFlowRunner{

	/** flow configuration steps */
	private FlowConfiguration configuration = null;
	/** multi-part request from rest which contains mapping. */
	private FormDataMultiPart multipart;
	/** query parameters which are parameters to be send to the steps */
	private MultivaluedMap<String, String> queryParams;
	/** list of steps to be executed */
	private List<ITransformerStep> stepList = new ArrayList<ITransformerStep>();

	/**
	 * constructor need for reflection.
	 */
	public StaticFlowRunner() {
		
	}
	
	@Override
	public void setConfigurations(final InputStream configurationStream,
			final FormDataMultiPart multipart,
			final MultivaluedMap<String, String> queryParams) throws JAXBException, SAXException {
		JAXBContext context = JAXBContext.newInstance(FlowConfiguration.class);
		SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = sf.newSchema(new StreamSource(
				getClass().getClassLoader().getResourceAsStream("flow.xsd")));
		Unmarshaller unmarshaller = context.createUnmarshaller();
		unmarshaller.setSchema(schema);
		this.configuration = (FlowConfiguration) unmarshaller.unmarshal(configurationStream); 
		this.multipart = multipart;
		this.queryParams = queryParams;
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.flows.IFlowRunner#createSteps()
	 */
	@Override
	public void createSteps() throws Exception {
		StepConfiguration[] steps = configuration.getSteps();
		for (StepConfiguration conf : steps) {
			ITransformerStep step = TransformerProvider.getInstance().createStep(conf.getName());
			step.setDisabled(String.valueOf(conf.isDisabled()));
			conf.setStageParameters(step);
			stepList.add(step);
		}
		
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.flows.IFlowRunner#executeSteps(chappy.utils.streams.wrappers.StreamHolder)
	 */
    @Override
	public StreamHolder executeSteps(final StreamHolder holder) throws Exception {
		for (ITransformerStep step : stepList) {
			step.execute(holder, multipart, queryParams);
		}
		return holder;
	}

	@Override
	public void configure(final String mode, final String configuration) {
		// TODO Auto-generated method stub
		
	}

}
