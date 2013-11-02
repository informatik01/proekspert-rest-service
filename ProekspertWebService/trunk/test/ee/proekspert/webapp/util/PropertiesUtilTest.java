package ee.proekspert.webapp.util;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

public class PropertiesUtilTest {

	private static final String CONFIG_FILE = "config.properties";
	
	private static final String WEB_SERVICE_BASE_URI = "http://192.168.1.73";
    private static final int WEB_SERVICE_PORT = 11555;
    private static final String WEB_SERVICE_APP_CONTEXT = "proekspert";
    
    private static final String WEB_SERVER_ROOT_DIR = System.getProperty("user.home") +
    		"/My Source Code/Java EE/Proekspert/ProekspertWebServiceClient/WebContent/";
    private static final int WEB_SERVER_PORT = 80; 
	
    private static final String[] resources = { "http://people.proekspert.ee/ak/data_0.txt",
										    	"http://people.proekspert.ee/ak/data_1.txt",
										    	"http://people.proekspert.ee/ak/data_2.txt",
										    	"http://people.proekspert.ee/ak/data_3.txt",
										    	"http://people.proekspert.ee/ak/data_4.txt",
										    	"http://people.proekspert.ee/ak/data_5.txt",
										    	"http://people.proekspert.ee/ak/data_6.txt",
										    	"http://people.proekspert.ee/ak/data_7.txt",
										    	"http://people.proekspert.ee/ak/data_8.txt",
										    	"http://people.proekspert.ee/ak/data_9.txt"};
    
	private static PropertiesUtil propertiesUtil;
	
	@BeforeClass
	public static void setUp() throws Exception {
		propertiesUtil = new PropertiesUtil(CONFIG_FILE);
	}

	@Test
	public void testGetProperty() {
		assertEquals(WEB_SERVICE_BASE_URI, propertiesUtil.getProperty("web.service.base.uri"));
		assertEquals(WEB_SERVICE_PORT, Integer.parseInt(propertiesUtil.getProperty("web.service.port")));
		assertEquals(WEB_SERVICE_APP_CONTEXT, propertiesUtil.getProperty("web.service.app.context"));
		assertEquals(WEB_SERVER_ROOT_DIR, propertiesUtil.getProperty("web.server.root.dir"));
		assertEquals(WEB_SERVER_PORT, Integer.parseInt(propertiesUtil.getProperty("web.server.port")));
	}

	@Test
	public void testGetProperties() {
		assertArrayEquals(resources, propertiesUtil.getProperties("legacy.interface.resources"));
	}

}
