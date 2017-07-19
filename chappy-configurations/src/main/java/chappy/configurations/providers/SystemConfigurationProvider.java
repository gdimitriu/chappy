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
package chappy.configurations.providers;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import chappy.configurations.system.SystemConfigurations;

/**
 * @author Gabriel Dimitriu
 *
 */
public class SystemConfigurationProvider {

	/** singleton */
	private static SystemConfigurationProvider singleton = new SystemConfigurationProvider();
	
	/** system configurations */
	private SystemConfigurations systemConfigurations;

	/**
	 * 
	 */
	private SystemConfigurationProvider() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * get the singleton.
	 * @return singleton
	 */
	public static SystemConfigurationProvider getInstance() {
		return singleton;
	}
	
	
	/**
	 * read the default system configuration
	 * @throws JAXBException
	 * @throws SAXException
	 */
	public  void readSystemConfiguration() throws JAXBException, SAXException {
		readExternalSystemConfiguration(null);
	}
	
	/**
	 * read the non default system configuration
	 * @param fileName system configuration
	 * @throws JAXBException
	 * @throws SAXException
	 */
	public void readExternalSystemConfiguration(final String fileName) throws JAXBException, SAXException {
		try {
			if (fileName != null) {
				readSystemConfiguration(new BufferedInputStream(new FileInputStream(new File(fileName))));
			} else {
				System.err.println("Chappy goes to the default system configuration.");
			}
		} catch (FileNotFoundException e) {
			System.err.println(e.getLocalizedMessage());
			System.err.println("Chappy goes to the default system configuration.");
		}
	}
	
	/**
	 * read system configuration from a input stream
	 * @param is input stream
	 * @throws JAXBException
	 * @throws SAXException
	 */
	public void readSystemConfiguration(final InputStream is) throws JAXBException, SAXException {
		InputStream realInputStream = is;
		if (realInputStream == null) {
			realInputStream = ClassLoader.getSystemResourceAsStream("DefaultSystemConfiguration.xml");
		}
		JAXBContext context = JAXBContext.newInstance(SystemConfigurations.class);
		SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = sf.newSchema(new StreamSource(
				ClassLoader.getSystemResourceAsStream("SystemConfiguration.xsd")));
		Unmarshaller unmarshaller = context.createUnmarshaller();
		unmarshaller.setSchema(schema);
		systemConfigurations = (SystemConfigurations) unmarshaller.unmarshal(realInputStream);
	}
	
	/**
	 * get the system configurations
	 * @return system configurations
	 */
	public SystemConfigurations getSystemConfiguration() {
		if (systemConfigurations == null) {
			try {
				readSystemConfiguration();
			} catch (JAXBException | SAXException e) {
				e.printStackTrace();
			}
		}
		return systemConfigurations;
	}
}
