package ee.proekspert.webapp.ws.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import org.glassfish.grizzly.http.server.Response;

import ee.proekspert.webapp.model.ServiceMessage;
import ee.proekspert.webapp.service.DataService;

/**
 * <code>ProekspertWebService</code> handles REST requests.
 *
 */
@Path("service")
public class ProekspertWebService {

	@GET
	@Path("data")
	@Produces("application/json")
	public ServiceMessage getJSONPData(@Context Response response) {
		// Enabling Cross-Origin Resource Sharing (CORS) support
		response.addHeader("Access-Control-Allow-Origin", "*");
		// Addressing IE caching issues
		response.addHeader("Cache-Control", "max-age=0,no-cache,no-store,post-check=0,pre-check=0");
		
		return DataService.getSeviceMessage();
	}
	
}
