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
package chappy.tests.manual.jms.transformers.test;

import javax.xml.bind.JAXBException;
import org.xml.sax.SAXException;

import chappy.configurations.providers.SystemConfigurationProvider;
import chappy.configurations.system.SystemConfigurations;
import chappy.interfaces.services.IServiceServer;
import chappy.policy.provider.JMSRuntimeResourceProvider;
import chappy.services.servers.jms.ServerJMS;
import chappy.services.servers.jms.resources.TransactionRouter;
import chappy.services.servers.jms.resources.tranform.AddFlow;
import chappy.services.servers.jms.resources.tranform.AddTransformer;
import chappy.services.servers.jms.resources.tranform.Authentication;
import chappy.services.servers.jms.resources.tranform.ListTransformers;
import chappy.services.servers.jms.resources.tranform.TransformFlow;

/**
 * Server class for manual tests.
 * 
 * @author Gabriel Dimitriu
 *
 */
public class ProcessingJMSTestManualServer {


	private IServiceServer server = null;

	/**
	 * @throws JAXBException
	 * @throws SAXException
	 * 
	 */
	public ProcessingJMSTestManualServer() throws JAXBException, SAXException {
		SystemConfigurationProvider.getInstance().readSystemConfiguration(
				getClass().getClassLoader().getResourceAsStream("systemTestConfiguration.xml"));
		SystemConfigurations configuration = SystemConfigurationProvider.getInstance().getSystemConfiguration();
		server = new ServerJMS();
		JMSRuntimeResourceProvider.getInstance().registerSystemRuntimeResource(new TransactionRouter());
		JMSRuntimeResourceProvider.getInstance().registerSystemRuntimeResource(new Authentication());
		JMSRuntimeResourceProvider.getInstance().registerSystemRuntimeResource(new AddTransformer());
		JMSRuntimeResourceProvider.getInstance().registerSystemRuntimeResource(new TransformFlow());
		JMSRuntimeResourceProvider.getInstance().registerSystemRuntimeResource(new ListTransformers());
		JMSRuntimeResourceProvider.getInstance().registerSystemRuntimeResource(new AddFlow());
		server.configure(configuration);
		Thread thread = new Thread() {
			public void run() {
				try {
					server.startServer();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		thread.start();
	}

	/**
	 * @param args
	 * @throws SAXException
	 * @throws JAXBException
	 */
	public static void main(String[] args) throws JAXBException, SAXException {
		new ProcessingJMSTestManualServer();

	}

}
