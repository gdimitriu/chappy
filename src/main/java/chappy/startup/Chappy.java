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
package chappy.startup;

import java.util.List;

import javax.xml.bind.JAXBException;
import org.xml.sax.SAXException;

import chappy.configurations.providers.SystemConfigurationProvider;
import chappy.configurations.system.SystemConfiguration;
import chappy.interfaces.services.IServiceServer;
import chappy.providers.services.ServicesProvider;

/**
 * This is the main entry point of the server.
 * @author Gabriel Dimitriu
 *
 */
public class Chappy {
	
	/** list of services */
	private List<IServiceServer> listOfInstanceOfServers = null;

	/**
	 * 
	 */
	public Chappy() {
		try {
			@SuppressWarnings("unused")
			IServiceServer service = ServicesProvider.getInstance().getNewServiceServer("rest");
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		listOfInstanceOfServers = ServicesProvider.getInstance().getAllServiceServers();
	}

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		Chappy system = new Chappy();
		try {
			if (args.length > 0) {
				SystemConfigurationProvider.getInstance().readExternalSystemConfiguration(args[0]);
			} else {
				SystemConfigurationProvider.getInstance().readSystemConfiguration();
			}
			system.configurureAllServices();
			system.startAllServices();
		} catch (JAXBException | SAXException e) {
			e.printStackTrace();
			System.err.println("Chappy has a wrong installation.");
		}

	}
	
	private void configurureAllServices() {
		SystemConfiguration[] configurations = SystemConfigurationProvider.getInstance()
				.getSystemConfiguration().getServicesConfigurations();
		for (int i = 0; i < configurations.length; i++)
			for (IServiceServer server : listOfInstanceOfServers) {
				if (server.getName().equals(configurations[i].getName())) {
					server.configure(configurations[i]);
			}
		}
	}
	private void startAllServices() throws Exception {
		for (IServiceServer server : listOfInstanceOfServers) {
			Thread thread = new Thread() {
				public void run() {
					try {
						server.startServer();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			thread.start();
		}
	}
}
