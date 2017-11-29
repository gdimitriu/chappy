# chappy

Chappy has been design as a test-bed for long running servers and application server.

The transformation server was choose as type of long running server.
The transformation server will take request using REST and JMS.
The user role configuration and upgrade of the is done on HTTPS using custom made protocol (not implemented yet).

Chappy wish to implement the following functional characteristics of the long time running application server:
- Predefined service for transformation using staxon and saxon for one time transformation without transactions.
- Internal transaction (a user could logon the system using JMS/REST and it will receive a cookie which will be used in the rest of the process). Transaction is persisted until the user logout.
- Auto-discovery of new packages for connectors. (only REST auto-discovery is implemented) 
- Hot-plugin of the transformation steps.
- Persistence of the logs in configured storage.
- Persistence of running steps and flows for the fail-over situation.
- HTTPS upgrade and maintenance service. (not implemented yet)
- Hot-plugin for upgrade of the chappy jars (maven module or submodules). (not implemented yet)
- Hot-plugin of different type of flow. (not implemented yet)
- Hot-plugin of the transformations step with dependencies. (not implemented yet)
- Persistence of the upgrade/hot-plugin.(not implemented yet)
- Datastore for messages. (not implemented yet)

Transaction functional behavioral:
- Hot-plugin of the user custom transformer step is kept inside transaction.
- Custom transformer are moved from customer package to a special package using byte-code modification. 
- On logout the added custom transformers are deleted from system.
- Transaction holds references to the statistics of each transformation operation.
- Transaction is persisted until the user log-out.
- Caching of the flow is done in transaction which is persisted (the request is now only for REST because the clients are not implemented yet). 

Transformer functional behavioral:
- Predefined steps are :
	- xslt (saxson step for xml2xml transformation)
	- json2xml (staxon step for json2xml transformation)
	- xml2json (staxon step for xml2json transformation)
	- configurableSplitter (configurable splitter for plain messages) (not implemented yet)
	- configurableEnveloper (configurable enveloper for plain messages) (not implemented yet)
	- configurablePutData   (configurable put files to datastore) (not implemented yet)
	- configurableFetchData (configurable fetch files from datastore) (not implemented yet)	
- Custom transformer could be derived from:
	- AbstractStep which could do anything
	- AbstractSplitterStep which splits messages
	- AbstractEnveloperStep which envelopes messages in one step (there is no waiting for number of messages implemented yet)
	- AbstractReorderStep which put in a custom order the receiving messages. (not implemented yet)
- Transformer has multiple input and output messages.
- Transformer flow could be cached inside transaction to speedup the run of the flow. 
- Transformer has sequence of the messages, the sequence order could be used later on for the splitter, enveloper and reorder steps. (not implemented yet)
- Transformation in the flow could have priority and only some inputs are routed to the corresponding steps. (not implemented yet)

Chappy uses the following libraries and technologies:
- Jetty for the embedded http server.
- Jersey for the REST server.
- ActiveMQ for the JMS server.
- ASM for byte-code modification
- reflections library for resource discovery.
- Datanucleus for persistence.
- Staxon for json2xml and xml2json transformations.
- Saxon for xml2xml mapping.
- Apache Digester for running a flow in one step.
- JAXB for data-binding.
- JUnit for unitests.
- Maven for building.
- maven-surefire-plugin to run the unitests in maven.

Run the tests from the package chappy-tests using maven (for JMS is not working yet, in eclipse they are all green):
- the JAVA_HOME is needed to be set into the main pom due to cassandra needs.
- the port could be modified from systemTestConfiguration.xml from the test resources.
- mvn build, test
- to run the suite all modules should be added into the classpath.
- the chappy.tests.manual.rest.transformers.test contains the manual tests

NOTE:
- Only runs in dev environment is working in this moment (eclipse).
- because I do have some problems with classloaders for datanucleus enhancement the system works (with persistence and transaction) only in eclipse environment and at second start of server (first run the datanucleus will enhance the classes which could be loaded at second run by the system class-loader).
- Standalone distribution is not yet created.
