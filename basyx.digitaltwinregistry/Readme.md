# Eclipse BaSyx - DigitalTwin Registry
Eclipse BaSyx provides the BaSyx - DigitalTwin Registry as off-the-shelf component:

    docker run --name=aas-env -p:8081:8081 -v C:/tmp/application.properties:/application/application.properties eclipsebasyx/basyx-dt-registry:2.0.0-SNAPSHOT 

> *Disclaimer*: In this example, configuration files are located in `C:/tmp`

> *Disclaimer*: The binding of volume `C:/tmp/application.properties` to `/application/application.properties` is tested using Windows Powershell. Other terminals might run into an error.

It aggregates the AAS Discovery and AAS Registry interface into a single component. For its features and configuration, see the documentation of the respective components.

The Aggregated API endpoint documentation is available at:

	http://{host}:{port}/v3/api-docs
	
The Aggregated Swagger UI for the endpoint is available at:

	http://{host}:{port}/swagger-ui/index.html

For a configuration example, see [application.properties](./basyx.digitaltwinregistry.component/src/main/resources/application.properties)
The Health Endpoint and CORS Documentation can be found [here](../docs/Readme.md). 

## To run the tests
The tests are present in the [src/test/java](./basyx.digitaltwinregistry.component/src/test/java).

To execute the tests please do the following:

**From Eclipse IDE**: Right click on [basyx.digitaltwinregistry.component](./basyx.digitaltwinregistry.component) -> Run As -> Maven test

**From terminal**: Go to the [basyx.digitaltwinregistry.component](./basyx.digitaltwinregistry.component) directory and use command -> mvn test

## Configure Favicon
To configure the favicon, add the favicon.ico to [basyx-java-server-sdk\basyx.common\basyx.http\src\main\resources\static](../basyx.common/basyx.http/src/main/resources/static/).
