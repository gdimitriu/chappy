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
package chappy.providers.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import chappy.interfaces.services.IServiceServer;

/**
 * Singleton provider for services.
 * @author Gabriel Dimitriu
 *
 */
public class ServicesProvider {

	/** this should be the singleton provider */
	private static ServicesProvider singleton = new ServicesProvider();
	
	private Map<String, String> services = new HashMap<String, String>();
	
	/**
	 * 
	 */
	private ServicesProvider() {
		services.put("rest", "ServerJetty");
		services.put("jms", "ServerJMS");
	}
	
	/**
	 * get the singleton instance.
	 * @return singleton instance
	 */
	public static ServicesProvider getInstance() {
		return singleton;
	}

	/**
	 * return a new instance of the service.
	 * @param name of service.
	 * @return instance of the server.
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	public IServiceServer getNewServiceServer(final String name) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		if (!services.containsKey(name)) {
			return null;
		}
		return (IServiceServer) Class.forName(createLoadName(name, services.get(name))).newInstance();
	}
	
	private String createLoadName(final String name, final String className) {
		return "chappy.services.servers." + name + "." + className;
	}
	public String createLoadNameFromEntry(final Entry<String,String> setName) {
		return "chappy.services.servers." + setName.getKey() + "." + setName.getValue();
	}
	
	

	/**
	 * get all service servers.
	 * @return list of server instances.
	 */
	public List<IServiceServer> getAllServiceServers() {
		return services.entrySet().stream()
		.map((name) -> {
			try {
				IServiceServer server = (IServiceServer) Class.forName(createLoadNameFromEntry(name))
						.newInstance();
				server.setName(name.getKey());
				return server;
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
				e.printStackTrace();
				return null;
			}
		})
		.collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
	}
}
