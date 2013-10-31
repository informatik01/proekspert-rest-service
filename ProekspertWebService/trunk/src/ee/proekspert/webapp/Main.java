package ee.proekspert.webapp;

import java.io.IOException;
import java.net.URI;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.UriBuilder;

import org.apache.log4j.Logger;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.StaticHttpHandler;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;

import ee.proekspert.webapp.service.DataService;
import ee.proekspert.webapp.util.PropertiesUtil;
import ee.proekspert.webapp.ws.rest.config.WebApplication;

/**
 * Utility class for running the application from the command line.
 *
 */
public class Main {
	
	private static final Logger logger = Logger.getLogger(Main.class);
	
	private static final String CONFIG_FILE = "config.properties";

    private static final String WEB_SERVICE_BASE_URI;
    private static final int WEB_SERVICE_PORT;
    private static final String WEB_SERVICE_APP_CONTEXT;
    
    private static final String WEB_SERVER_ROOT_DIR;
    private static final int WEB_SERVER_PORT;
    
    static {
    	PropertiesUtil propertiesUtil = new PropertiesUtil(CONFIG_FILE);
    	
    	WEB_SERVICE_BASE_URI = propertiesUtil.getProperty("web.service.base.uri");
    	WEB_SERVICE_PORT = Integer.parseInt(propertiesUtil.getProperty("web.service.port"));
    	WEB_SERVICE_APP_CONTEXT = propertiesUtil.getProperty("web.service.app.context");
    	
    	WEB_SERVER_ROOT_DIR = propertiesUtil.getProperty("web.server.root.dir");
    	WEB_SERVER_PORT = Integer.parseInt(propertiesUtil.getProperty("web.server.port"));
    }
    
    // This is the full URL where the web service is available
    private static final String WEB_SERVICE_URL =
    		buildServiceURI(WEB_SERVICE_PORT).toString() + "/service/data";
    
    private static URI buildServiceURI(int port) {
    	return UriBuilder.fromUri(WEB_SERVICE_BASE_URI)
    			.path(WEB_SERVICE_APP_CONTEXT)
    			.port(port).build();
    }

    private static final DataService dataService = new DataService(true);
    
    /**
     * The main method that starts the application. It configures the server on which the
     * {@link ee.proekspert.webapp.ws.rest.ProekspertWebService} is running.
     * <p>Optionally it can also start a simple web server for static content, if any parameter
     * was provided on the command line. This functionality is provided as a quick and dirty way
     * of testing functionality of the web service. DO NOT USE IT FOR PRODUCTION.
     * @param args	parameters for the application. Currently, if ANY arbitrary parameter was specified
     * 				on the command line, this will cause the creation and start of the local web server
     * 				that can serve static content.
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        	HttpServer webService = GrizzlyHttpServerFactory.createHttpServer(
        			buildServiceURI(WEB_SERVICE_PORT), new WebApplication());
			logger.info("The web service is available at " + WEB_SERVICE_URL);
        	
        	HttpServer webServer = null;
        	try {
	        	if (args.length > 0) {
	        		webServer = GrizzlyHttpServerFactory.createHttpServer(buildServiceURI(WEB_SERVER_PORT));
	        		StaticHttpHandler staticHttpHandler = new StaticHttpHandler(WEB_SERVER_ROOT_DIR);
	        		webServer.getServerConfiguration().addHttpHandler(staticHttpHandler, "/");
	        	}
	        	logger.info("The web server root directory: " + WEB_SERVER_ROOT_DIR);
        	} catch (ProcessingException e) {
        		logger.error("Error starting local HTTP server.", e);
        		logger.info("Local HTTP server was not started. See logfile for the details.");
        	}
        	
        	System.out.println("\nPress ENTER to shutdown the web service.");
            System.in.read();
            webService.shutdownNow();
            if (webServer != null) {
            	webServer.shutdownNow();
            }
            dataService.shutdown();
    }

}
