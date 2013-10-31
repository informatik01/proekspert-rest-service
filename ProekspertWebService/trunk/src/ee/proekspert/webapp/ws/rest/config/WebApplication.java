package ee.proekspert.webapp.ws.rest.config;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * <code>WebApplication</code> class is used for configuring the application.
 * @see org.glassfish.jersey.server.ResourceConfig
 */
public class WebApplication extends ResourceConfig {
	
    public WebApplication() {
    	super(JacksonFeature.class);
        packages("ee.proekspert.webapp");
    }
    
}
