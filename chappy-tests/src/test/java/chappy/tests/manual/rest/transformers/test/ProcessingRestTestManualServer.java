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
package chappy.tests.manual.rest.transformers.test;

import javax.xml.bind.JAXBException;
import org.xml.sax.SAXException;

import chappy.configurations.system.SystemConfiguration;
import chappy.interfaces.services.IServiceServer;
import chappy.providers.configurations.SystemConfigurationProvider;
import chappy.services.servers.rest.ServerJetty;

/**
 * Server class for manual tests.
 * 
 * @author Gabriel Dimitriu
 *
 */
public class ProcessingRestTestManualServer {

	private int port = 0;

	private IServiceServer server = null;

	/**
	 * @throws JAXBException
	 * @throws SAXException
	 * 
	 */
	public ProcessingRestTestManualServer() throws JAXBException, SAXException {
		SystemConfigurationProvider.getInstance().readSystemConfiguration(
				getClass().getClassLoader().getResourceAsStream("systemTestConfiguration.xml"));
		SystemConfiguration configuration = SystemConfigurationProvider.getInstance().getSystemConfiguration()
				.getFirstConfiguration();
		port = Integer.parseInt(configuration.getProperty());
		server = new ServerJetty(port);
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
		new ProcessingRestTestManualServer();

	}

}
