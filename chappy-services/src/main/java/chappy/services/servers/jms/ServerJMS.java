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
package chappy.services.servers.jms;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.activemq.artemis.core.config.Configuration;
import org.apache.activemq.artemis.core.config.impl.ConfigurationImpl;
import org.apache.activemq.artemis.jms.server.config.ConnectionFactoryConfiguration;
import org.apache.activemq.artemis.jms.server.config.JMSConfiguration;
import org.apache.activemq.artemis.jms.server.config.JMSQueueConfiguration;
import org.apache.activemq.artemis.jms.server.config.impl.ConnectionFactoryConfigurationImpl;
import org.apache.activemq.artemis.jms.server.config.impl.JMSConfigurationImpl;
import org.apache.activemq.artemis.jms.server.config.impl.JMSQueueConfigurationImpl;
import org.apache.activemq.artemis.jms.server.embedded.EmbeddedJMS;

import chappy.configurations.system.PropertyConfiguration;
import chappy.configurations.system.SystemConfiguration;
import chappy.configurations.system.SystemConfigurations;
import chappy.interfaces.jms.resources.IJMSRuntimeResource;
import chappy.interfaces.services.IServiceJMS;
import chappy.interfaces.services.IServiceServer;
import chappy.persistence.providers.PersistenceProvider;
import chappy.policy.provider.JMSRuntimeResourceProvider;
import chappy.providers.jms.resources.JMSQueuesProvider;
import chappy.providers.jms.security.RolesProvider;
import chappy.providers.jms.security.ServerSecurityManager;

/**
 * This is the server for the JMS protocol of CHAPPY Server.
 * @author Gabriel Dimitriu
 *
 */
public class ServerJMS implements IServiceJMS {

	// internal data 
	/** JMS embedded server */
	private EmbeddedJMS jmsServer = null;
	
	/** configuration for jms */
	private JMSConfiguration jmsConfig = null;
	
	/** map of consumers and queues */
	private Map<String, IJMSRuntimeResource> registeredConsumers = null;
	
	/** map of runtime consumers */
	private Map<String, JMSConsumerHolder> runtimeConsumers = null;
	
	//from configuration.xml	
	/** JMS communication port */
	private String serverPort = "61616";
	
	/** name of the server */
	private String serverName = null;

	/** name of the protocol */
	private String protocolType = "tcp";

	/** name of the default connection factory */
	private String connectionDefaultFactoryName = "default";

	/** name of the journal directory */
	private String journalDirectory = "target/data/journal";
	
	/** name of the server host */
	private String serverHost = "localhost";
	
	/** durability of queue */
	private String queueDurable = "true";
	
	/** server security is enabled */
	private String serverSecurityEnabled = "true";
	
	/** server persistence is enabled */
	private String serverPersistenceEnabled = "true";
	
	/** binding directory */
	private String bindindDirectory = "target/data/bindings";
	
	/** large message directory */
	private String largeMessageDirectory = "target/data/largemessage";

	//typical to JMS
		
	/** name of the queue */
	private Set<String> queuesNames = null;

	
	
	/**
	 * @param port of the server
	 */
	public ServerJMS(final int port) {
		this();
		serverPort = Integer.toString(port);
	}
	
	/**
	 * default constructor
	 */
	public ServerJMS() {
		queuesNames = new HashSet<String>();
		registeredConsumers = new HashMap<String, IJMSRuntimeResource>();
		runtimeConsumers = new HashMap<String, JMSConsumerHolder>();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		IServiceServer server = new ServerJMS();
		((IServiceJMS) server).setQueuesNames(JMSQueuesProvider.getInstance().getAllQueues());
		try {
			server.startServer();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.services.IServiceServer#stopServer()
	 */
	@Override
	public void stopServer() throws Exception {
		runtimeConsumers.entrySet().stream().forEach(runtimeConsumer -> runtimeConsumer.getValue().stop());
		jmsServer.stop();
		runtimeConsumers.clear();
	}

	/* (non-Javadoc)
	 * @see chappy.interfaces.services.IServiceServer#startServer()
	 */
	@Override
	public void startServer() throws Exception {
		if (connectionDefaultFactoryName != null) {
			connectionDefaultFactoryName = serverName +"_" + serverHost + "_factory";
		}
		// Step 1. Create ActiveMQ Artemis core configuration, and set the properties accordingly
	    Configuration configuration = new ConfigurationImpl().setPersistenceEnabled(Boolean.parseBoolean(serverPersistenceEnabled))
	    		.setJournalDirectory(journalDirectory)
	    		.setBindingsDirectory(bindindDirectory)
	    		.setLargeMessagesDirectory(largeMessageDirectory)
	    		.setSecurityEnabled(Boolean.parseBoolean(serverSecurityEnabled))
	    		.addAcceptorConfiguration(protocolType, protocolType + "://" + serverHost + ":" + serverPort)
	    		.addConnectorConfiguration(serverName, protocolType + "://" + serverHost + ":" + serverPort);

	    RolesProvider rolesProvider = new RolesProvider(queuesNames);
	    configuration = configuration.setSecurityRoles(rolesProvider.getRoles());
	    
	    PersistenceProvider.getInstance().getSystemPersistence();
		PersistenceProvider.getInstance().getSystemFlowPersistence();
		PersistenceProvider.getInstance().getSystemUpgradePersistence();
	    
	    // Step 2. Create the JMS configuration
	    jmsConfig = new JMSConfigurationImpl();

	    // Step 3. Configure the JMS default ConnectionFactory
	    ConnectionFactoryConfiguration cfConfig = new ConnectionFactoryConfigurationImpl()
	    		.setName(connectionDefaultFactoryName).setConnectorNames(Arrays.asList(serverName))
	    		.setBindings(connectionDefaultFactoryName);
	    jmsConfig.getConnectionFactoryConfigurations().add(cfConfig);

	    registeredConsumers.values().stream().forEach(consumer -> registerSpecificFactory(consumer));
	    
	    // Step 4. Configure the JMS Queue
	    for (String queueName : queuesNames) {
	    	JMSQueueConfiguration queueConfig = new JMSQueueConfigurationImpl()
	    		.setName(queueName).setDurable(Boolean.parseBoolean(queueDurable))
	    		.setBindings("queue/" + queueName);
	    	jmsConfig.getQueueConfigurations().add(queueConfig);
	    }

	    // Step 5. Start the JMS Server using the ActiveMQ Artemis core server and the JMS configuration
	    jmsServer = new EmbeddedJMS().setConfiguration(configuration).setJmsConfiguration(jmsConfig);
	    jmsServer.setSecurityManager(new ServerSecurityManager());
	    jmsServer = jmsServer.start();
	    System.out.println("Started Embedded JMS Server");
	    
	    registeredConsumers.entrySet().stream().forEach(consumer -> runtimeConsumers.put(consumer.getKey(), 
	    		new JMSConsumerHolder(jmsServer, consumer.getValue())));
	    
	    runtimeConsumers.entrySet().stream().forEach(runtimeConsumer -> runtimeConsumer.getValue().start());
	}
	
	/**
	 * @param consumer to be register
	 */
	private void registerSpecificFactory(final IJMSRuntimeResource consumer) {
		if (consumer.getFactoryName() == null || consumer.getFactoryName().equals(connectionDefaultFactoryName)) {
			return;
		}
		ConnectionFactoryConfiguration cfConfig = new ConnectionFactoryConfigurationImpl()
	    		.setName(consumer.getFactoryName()).setConnectorNames(Arrays.asList(serverName))
	    		.setBindings(consumer.getFactoryName());
	    jmsConfig.getConnectionFactoryConfigurations().add(cfConfig);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {
		stopServer();
	}
	
	/* (non-Javadoc)
	 * @see chappy.interfaces.services.IServiceServer#configure(java.lang.Object)
	 */
	@Override
	public void configure(final Object configuration) {
		if (configuration instanceof SystemConfigurations) {
			SystemConfiguration[] configs = ((SystemConfigurations) configuration).getServicesConfigurations();
			for (SystemConfiguration config : configs) {
				if ("jms".equals(config.getName())) {
					setFields(config);				
				}
			}
		} else if (configuration instanceof SystemConfiguration) {
			setFields((SystemConfiguration) configuration);
		}
		//set the queues names
		setQueuesNames(JMSQueuesProvider.getInstance().getAllQueues());
		//get and set the resources
		List<IJMSRuntimeResource> resources = JMSRuntimeResourceProvider.getInstance().getAllResources();
		resources.stream().forEach(res -> registerResourceConsumer(res));
	}

	/**
	 * @param config
	 * @throws SecurityException
	 */
	private void setFields(SystemConfiguration config) throws SecurityException {
		Field[] fields = this.getClass().getDeclaredFields();
		for (PropertyConfiguration property: config.getProperties()) {
			for (Field fld : fields) {
				if (fld.getName().equals(property.getName())) {
					try {
						fld.set(this, property.getValue());
					} catch (IllegalArgumentException | IllegalAccessException e) {
						e.printStackTrace();
					}
					break;
				}
			}
		}
	}

	@Override
	public void setName(final String key) {
		serverName = key;
	}

	@Override
	public String getName() {
		return serverName;
	}

	@Override
	public String[] getQueueNames() {
		return queuesNames.toArray(new String[1]);
	}

	@Override
	public void addQueueName(final String queueName) {
		this.queuesNames.add(queueName);
	}

	@Override
	public String getDefaultConnectionFactoryName() {
		return connectionDefaultFactoryName;
	}

	@Override
	public String getJournalDirectory() {
		return journalDirectory;
	}

	/**
	 * @return the bingindDirectory
	 */
	@Override
	public String getBindindDirectory() {
		return bindindDirectory;
	}

	/**
	 * @return the largeMessageDirectory
	 */
	@Override
	public String getLargeMessageDirectory() {
		return largeMessageDirectory;
	}
	
	@Override
	public boolean isQueueDurable() {
		return Boolean.parseBoolean(queueDurable);
	}

	@Override
	public boolean isServerSecurityEnabled() {
		return Boolean.parseBoolean(serverSecurityEnabled);
	}

	@Override
	public boolean isServerPersistenceEnabled() {
		return Boolean.parseBoolean(serverPersistenceEnabled);
	}

	@Override
	public String getServerHost() {
		return serverHost;
	}

	@Override
	public void setQueuesNames(final List<String> queues) {
		this.queuesNames.addAll(queues);
	}

	@Override
	public String registerResourceConsumer(final IJMSRuntimeResource consumer) {
		if (!queuesNames.contains(consumer.getQueueName())) {
			return null;
		}
		String registeredkey = consumer.getQueueName() + ":" + consumer.getFactoryName();
		registeredConsumers.put(registeredkey, consumer);
		return registeredkey;
	}

	@Override
	public IJMSRuntimeResource getRegisterResourceConsumer(final String registeredkey) {
		if (registeredConsumers.containsKey(registeredkey)) {
			registeredConsumers.get(registeredkey);
		}
		return null;
	}

	@Override
	public void unregisterResourceConsumer(final String registeredkey) {
		if (runtimeConsumers.containsKey(registeredkey)) {
			runtimeConsumers.remove(registeredkey).stop();
		}
		if (registeredConsumers.containsKey(registeredkey)) {
			registeredConsumers.remove(registeredkey);
		}
	}
}
