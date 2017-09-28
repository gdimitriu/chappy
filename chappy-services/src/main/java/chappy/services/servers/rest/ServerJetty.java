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
package chappy.services.servers.rest;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

import chappy.configurations.system.SystemConfiguration;
import chappy.interfaces.services.IServiceServer;
import chappy.persistence.providers.PersistenceProvider;

/**
 * This is the jetty HTTP server in which run the jersey REST server.
 * @author Gabriel Dimitriu
 *
 */
public class ServerJetty implements IServiceServer {
	/** http jetty server */
	private Server server = null;
	/** rest reponse port */
	private int serverPort = 8099;
	/** context handler for jersey */
	private ServletContextHandler context = null;
	/** servlet handler for jersey */
	private ServletHolder sh = null;
	
	/** name of the server */
	private String name = null;

	public ServerJetty(final int port) {
		serverPort = port;
	}
	
	public ServerJetty() {
	}
	
	/* (non-Javadoc)
	 * @see chappy.services.servers.rest.IServicesServer#stopServer()
	 */
	@Override
	public void stopServer() throws Exception {
		if(server != null) {
			server.stop();
		}
	}
	
	public static void main(String[] args) {
		IServiceServer jetty = new ServerJetty();
		try {
			jetty.startServer();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void finalize() throws Throwable {
		stopServer();
	}
	
	
	/* (non-Javadoc)
	 * @see chappy.services.servers.rest.IServicesServer#startServer()
	 */
	@Override
	public void startServer() throws Exception {

		sh = new ServletHolder(ServletContainer.class);

//		sh.setInitParameter("jersey.config.server.provider.packages", "transformationsEngine.serverRest.resources");
		sh.setInitParameter("com.sun.jersey.config.property.packages", "chappy.services.servers.rest.AppConfig");
		sh.setInitParameter("javax.ws.rs.Application", "chappy.services.servers.rest.AppConfig");
		sh.setInitParameter("com.sun.jersey.config.property.packages", "rest");
		server = new Server(serverPort);

		context = new ServletContextHandler(server, "/",
				ServletContextHandler.SESSIONS);

		context.addServlet(sh, "/*");
		PersistenceProvider.getInstance().getSystemPersistence();
		PersistenceProvider.getInstance().getSystemFlowPersistence();
		PersistenceProvider.getInstance().getSystemUpgradePersistence();
		server.start();
		System.out.println(String.format("Application started.%nHit enter to stop it..."));
		System.in.read();
		server.stop();
	}

	@Override
	public void configure(final Object configuration) {
		serverPort = Integer.parseInt(((SystemConfiguration) configuration).getProperty());
	}

	@Override
	public void setName(final String key) {
		name = key;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	@Override
	public String getServerHost() {
		return "localhost";
	}
}