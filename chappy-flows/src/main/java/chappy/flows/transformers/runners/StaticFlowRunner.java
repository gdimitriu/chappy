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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.PersistenceCapable;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import chappy.clients.common.cookie.CookieFactory;
import chappy.exception.providers.ExceptionMappingProvider;
import chappy.flows.transformers.staticflows.FlowConfiguration;
import chappy.flows.transformers.staticflows.StepConfiguration;
import chappy.interfaces.cookies.IChappyCookie;
import chappy.interfaces.flows.IFlowRunner;
import chappy.interfaces.flows.MultiDataQueryHolder;
import chappy.interfaces.statisticslogs.ILogs;
import chappy.interfaces.statisticslogs.IStatistics;
import chappy.interfaces.statisticslogs.StatisticLog;
import chappy.interfaces.transactions.ITransaction;
import chappy.interfaces.transformers.ITransformerStep;
import chappy.providers.transaction.StatisticsLogsProvider;
import chappy.providers.transaction.TransactionProviders;
import chappy.providers.transformers.TransformerProvider;
import chappy.utils.streams.wrappers.StreamHolder;

/**
 * This hold the configuration of the flow.
 * This also create internally the step classes from the flow.
 * This will call the execute from the steps.
 * @author Gabriel Dimitriu
 *
 */
@PersistenceCapable(detachable = "true")
public class StaticFlowRunner implements IFlowRunner{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** flow configuration steps */
	private FlowConfiguration configuration = null;
	/** multi-part request from rest which contains mapping. */
	private MultiDataQueryHolder internalMultipart;
	/** list of steps to be executed */
	private List<ITransformerStep> stepList = new ArrayList<ITransformerStep>();
	
	/** cookie of the  transaction */
	private IChappyCookie transactionCookie = null;
	/**
	 * constructor need for reflection.
	 */
	public StaticFlowRunner() {
		
	}
	
	@Override
	public void setConfigurations(final InputStream configurationStream,
			final MultiDataQueryHolder multipart) throws Exception {
		try {
			JAXBContext context = JAXBContext.newInstance(FlowConfiguration.class);
			SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = sf.newSchema(new StreamSource(
				getClass().getClassLoader().getResourceAsStream("flow.xsd")));
			Unmarshaller unmarshaller = context.createUnmarshaller();
			unmarshaller.setSchema(schema);
			this.configuration = (FlowConfiguration) unmarshaller.unmarshal(configurationStream); 
		} catch (JAXBException | SAXException e) {
			throw ExceptionMappingProvider.getInstace().mapException(e);
		}
		this.internalMultipart = multipart;
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
			step.setOrder(conf.getOrder());
			conf.setStageParameters(step);
			stepList.add(step);
		}
		
	}
	
	/* (non-Javadoc)
	 * @see chappy.interfaces.flows.IFlowRunner#createSteps(final IChappyCookie cookie)
	 */
	@Override
	public void createSteps(final IChappyCookie cookie) throws Exception {
		transactionCookie = cookie;
		if (cookie == null || cookie.getUserName() == null || cookie.getUserName().equals("")) {
			createSteps();
			return;
		}
		StepConfiguration[] steps = configuration.getSteps();
		for (StepConfiguration conf : steps) {
			ITransformerStep step = TransformerProvider.getInstance().createStep(conf.getName(), cookie.getUserName());
			step.setDisabled(String.valueOf(conf.isDisabled()));
			step.setOrder(conf.getOrder());
			conf.setStageParameters(step);
			stepList.add(step);
		}
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.flows.IFlowRunner#createSteps(final String userName)
	 */
	@Override
	public void createSteps(final String userName, final String passwd) throws Exception {
		createSteps(CookieFactory.getFactory().newCookie(null, userName, passwd));		
	}

	
	/* (non-Javadoc)
	 * @see chappy.interfaces.flows.IFlowRunner#executeSteps(chappy.utils.streams.wrappers.StreamHolder)
	 */
    @Override
	public StreamHolder executeSteps(final StreamHolder holder) throws Exception {
    	return executeSteps(holder, this.internalMultipart);
	}
    
    @Override
   	public List<StreamHolder> executeSteps(final List<StreamHolder> holders) throws Exception {
    	return executeSteps(holders, this.internalMultipart);
   	}

	@Override
	public void configure(final String mode, final String configuration) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public StreamHolder executeSteps(final StreamHolder holder, final MultiDataQueryHolder multiPart) throws Exception {
		IStatistics statistics = null; 
    	ILogs logs = null;
    	ITransaction transaction = null;
    	LocalDateTime startTime = null;
    	LocalDateTime finishTime = null;
    	if (transactionCookie.getTransactionId() != null) {
    		statistics = StatisticsLogsProvider.getInstance().getStatistics(transactionCookie);
    		logs = StatisticsLogsProvider.getInstance().getLogs(transactionCookie);
        	transaction = TransactionProviders.getInstance().getTransaction(transactionCookie);
        	transaction.start();
    	}

    	for (ITransformerStep step : stepList) {
			startTime = LocalDateTime.now();
			if (logs != null) {
				StatisticLog log = logs.putLog(step.getClass().getSimpleName(), startTime, "started");
				transaction.makePersistent(log);
			}
			step.execute(holder, multiPart);
			finishTime = LocalDateTime.now();
			if (logs != null) {
				StatisticLog log = logs.putLog(step.getClass().getSimpleName(), finishTime, "executed");
				transaction.makePersistent(log);
			}			
			if (statistics != null) {
				StatisticLog stat = statistics.putStatistic(step.getClass().getSimpleName(), startTime, finishTime);
				transaction.makePersistent(stat);
			}
		}
    	if (transaction != null) {
    		transaction.commit();
    	}
		return holder;
	}

	@Override
	public List<StreamHolder> executeSteps(final List<StreamHolder> holders, final MultiDataQueryHolder multiPart)
			throws Exception {
		IStatistics statistics = StatisticsLogsProvider.getInstance().getStatistics(transactionCookie);
    	ILogs logs = StatisticsLogsProvider.getInstance().getLogs(transactionCookie);
    	LocalDateTime startTime = null;
    	LocalDateTime finishTime = null;
    	ITransaction transaction = TransactionProviders.getInstance().getTransaction(transactionCookie);
    	transaction.start();
   		for (ITransformerStep step : stepList) {
			startTime = LocalDateTime.now();
			if (logs != null) {
				logs.putLog(step.getClass().getSimpleName(), startTime, "started");
			}
   			step.execute(holders, multiPart);
			finishTime = LocalDateTime.now();
			if (logs != null) {
				StatisticLog log = logs.putLog(step.getClass().getSimpleName(), finishTime, "executed");
				transaction.makePersistent(log);
			}
			if (statistics != null) {
				StatisticLog stat = statistics.putStatistic(step.getClass().getSimpleName(), startTime, finishTime);
				transaction.makePersistent(stat);
			}
   		}
   		transaction.commit();
   		return holders;
	}


}
