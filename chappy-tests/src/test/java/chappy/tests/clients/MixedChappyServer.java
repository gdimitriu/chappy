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
package chappy.tests.clients;

import static org.junit.Assert.fail;

import javax.ws.rs.core.UriBuilder;
import chappy.configurations.providers.SystemConfigurationProvider;
import chappy.configurations.system.SystemConfiguration;
import chappy.configurations.system.SystemConfigurations;
import chappy.interfaces.services.IServiceServer;
import chappy.persistence.providers.CustomTransformerStorageProvider;
import chappy.policy.provider.JMSRuntimeResourceProvider;
import chappy.services.servers.jms.ServerJMS;
import chappy.services.servers.jms.resources.TransactionRouter;
import chappy.services.servers.jms.resources.tranform.AddTransformer;
import chappy.services.servers.jms.resources.tranform.Authentication;
import chappy.services.servers.jms.resources.tranform.ListTransformers;
import chappy.services.servers.jms.resources.tranform.TransformFlow;
import chappy.services.servers.rest.ServerJetty;

/**
 * @author Gabriel Dimitriu
 *
 */
public class MixedChappyServer {

	private IServiceServer serverREST = null;
	
	private IServiceServer serverJMS = null;
	
	private int restPort = 0;
	/**
	 * 
	 */
	public MixedChappyServer() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @return port on which the rest servers accept requests.
	 */
	public int getRestPort() {
		return restPort;
	}

	/**
	 * start all servers and initialize the custom transformer storage.
	 * @throws Exception
	 */
	public void startAll() throws Exception {
		startRESTServer(false);
		startJMSServer(false);
		CustomTransformerStorageProvider.getInstance().cleanRepository();
	}
	
	/**
	 * stop all servers and clean the custom transformer storage
	 * @throws Exception
	 */
	public void stopAll() throws Exception {
		stopRESTServer();
		stopJMSServer();
		CustomTransformerStorageProvider.getInstance().cleanRepository();
	}
	
	/**
	 * start the Chappy REST server.
	 * @throws Exception of type JAXBException and SAXException
	 */
	public void startRESTServer() throws Exception {
		startRESTServer(true);
	}
	
	/**
	 * start the Chappy REST server.
	 * @param clean the custom storage provider.
	 * @throws Exception of type JAXBException and SAXException
	 */
	public void startRESTServer(final boolean clean) throws Exception {
		SystemConfigurationProvider.getInstance().readSystemConfiguration(
				getClass().getClassLoader().getResourceAsStream("systemTestConfiguration.xml"));
		SystemConfiguration configuration = SystemConfigurationProvider.getInstance().getSystemConfiguration()
				.getFirstConfiguration();
		restPort = Integer.parseInt(configuration.getProperty());
		UriBuilder.fromUri("{arg}").build(new String[] { "http://localhost:" + restPort + "/" }, false);
		serverREST = new ServerJetty(restPort);
		Thread thread = new Thread() {
			public void run() {
				try {
					serverREST.startServer();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					fail("could not start server " + e.getLocalizedMessage());
				}
			}
		};
		thread.start();
		if (clean) {
			CustomTransformerStorageProvider.getInstance().cleanRepository();
		}
	}
	
	/**
	 * start the Chappy JMS server.
	 * @throws Exception of type JAXBException and SAXException
	 */
	public void startJMSServer() throws Exception {
		startJMSServer(true);
	}
	
	/**
	 * start the Chappy JMS server.
	 * @param clean the custom storage provider.
	 * @throws Exception of type JAXBException and SAXException
	 */
	public void startJMSServer(final boolean clean) throws Exception {
		
		SystemConfigurationProvider.getInstance().readSystemConfiguration(
				getClass().getClassLoader().getResourceAsStream("systemTestConfiguration.xml"));
		SystemConfigurations configuration = SystemConfigurationProvider.getInstance().getSystemConfiguration();
		serverJMS = new ServerJMS();
		JMSRuntimeResourceProvider.getInstance().registerSystemRuntimeResource(new TransactionRouter());
		JMSRuntimeResourceProvider.getInstance().registerSystemRuntimeResource(new Authentication());
		JMSRuntimeResourceProvider.getInstance().registerSystemRuntimeResource(new AddTransformer());
		JMSRuntimeResourceProvider.getInstance().registerSystemRuntimeResource(new TransformFlow());
		JMSRuntimeResourceProvider.getInstance().registerSystemRuntimeResource(new ListTransformers());
		serverJMS.configure(configuration);
		Thread thread = new Thread() {
			public void run() {
				try {
					serverJMS.startServer();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		thread.start();
		if (clean) {
			CustomTransformerStorageProvider.getInstance().cleanRepository();
		}
	}
	
	/**
	 * stop the JMS server.
	 * @throws Exception
	 */
	public void stopJMSServer() throws Exception {
		this.serverJMS.stopServer();
		this.serverJMS = null;
	}
	
	/**
	 * stop the REST server.
	 * @throws Exception
	 */
	public void stopRESTServer() throws Exception {
		this.serverREST.stopServer();
		this.serverREST = null;
	}
}
