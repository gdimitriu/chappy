# chappy

Chappy has been design as a test-bed for long running servers.

The transformation server was choose as type of long running server. The transformation server will take request using REST.

Chappy wish to implement the following characteristics of the long running server:
- REST server using Jersey and Jetty.
- Predefined service for transformation using staxon and saxon.
- Hot-plugin of the transformation steps.
- Hot-plugin of the transformations step by user with returning a cookie with will be used to run the flow. (delete or update is not implemented yet)
- Hot-plugin of the transformations step with dependencies. (not implemented yet)
- Persistence of the upgrade/hot-plugin.(not implemented yet)
- HTTP upgrade service. (not implemented yet)

Chappy uses the following libraries and technologies:
- Jetty for the embedded http server.
- Jersey for the REST server.
- Staxon for json2xml and xml2json transformations.
- Saxon for xml2xml mapping.
- Apache Digester for running a flow in one step.
- JAXB for data-binding.Maven for building.
- ASM for bytecode modification
- reflections library for resource discovery.

Run the tests from the package chappy-tests using maven:
- the port could be modified from systemTestConfiguration.xml from the test resources.
- mvn build, test
- to run the suite all modules should be added into the classpath.
- the chappy.tests.manual.rest.transformers.test contains the manual tests











