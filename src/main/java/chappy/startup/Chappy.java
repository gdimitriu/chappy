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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import chappy.configurations.system.SystemConfiguration;
import chappy.configurations.system.SystemConfigurations;
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
	

	private SystemConfigurations systemConfigurations;

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
				system.readSystemConfiguration(args[0]);
			} else {
				system.readSystemConfiguration();
			}
			system.configurureAllServices();
			system.startAllServices();
		} catch (JAXBException | SAXException e) {
			e.printStackTrace();
			System.err.println("Chappy has a wrong installation.");
		}

	}
	
	private void configurureAllServices() {
		SystemConfiguration[] configurations = systemConfigurations.getServicesConfigurations();
		for (int i = 0; i < configurations.length; i++)
			for (IServiceServer server : listOfInstanceOfServers) {
				if (server.getName().equals(configurations[i].getName())) {
					server.configure(configurations[i]);
			}
		}
	}
	private void startAllServices() throws Exception {
		for (IServiceServer server : listOfInstanceOfServers) {
			server.startServer();
		}
	}
	
	private void readSystemConfiguration() throws JAXBException, SAXException {
		readSystemConfiguration(null);
	}
	private void readSystemConfiguration(final String fileName) throws JAXBException, SAXException {
		JAXBContext context = JAXBContext.newInstance(SystemConfigurations.class);
		SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = sf.newSchema(new StreamSource(
				ClassLoader.getSystemResourceAsStream("SystemConfiguration.xsd")));
		Unmarshaller unmarshaller = context.createUnmarshaller();
		unmarshaller.setSchema(schema);
		InputStream is = null;
		try {
			if (fileName != null) {
				is = new BufferedInputStream(new FileInputStream(new File(fileName)));
			} else {
				System.err.println("Chappy goes to the default system configuration.");
			}
		} catch (FileNotFoundException e) {
			System.err.println(e.getLocalizedMessage());
			System.err.println("Chappy goes to the default system configuration.");
		}
		if (is == null) {
			is = ClassLoader.getSystemResourceAsStream("DefaultSystemConfiguration.xml");
		}
		systemConfigurations = (SystemConfigurations) unmarshaller.unmarshal(is);
	}

}
