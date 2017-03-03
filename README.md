# chappy

Chappy has been design as a testbed for long running servers.

The transformation server was choose as type of long running server. The transformation server will take request using REST.

Chappy wish to implement the following characteristics of the long running server:
- REST server using Jersey and Jetty.
- Predefined service for transformation using staxon and saxon.
- Auto-upgrade of the transformation flows/step. (not implemented yet)
- Persistence of the upgrade.(not implemented yet)
- HTTP upgrade service. (not implemented yet)

Chappy uses the following libraries and technologies:
- Jetty for the embedded http server.
- Jersey for the REST server.
- Staxon for json2xml and xml2json transformations.
- Saxon for xml2xml mapping.
- Apache Digester for running a flow in one step.
- JAXB for data-binding.Maven for building.













