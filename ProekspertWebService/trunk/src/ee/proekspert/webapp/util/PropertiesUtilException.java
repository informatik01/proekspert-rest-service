package ee.proekspert.webapp.util;

/**
 * Custom exception class for use within {@link PropertiesUtil} objects.
 * It can be used to abstract from the underlying implementation.
 *
 */
public class PropertiesUtilException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public PropertiesUtilException(String message) {
		super(message);
	}

	public PropertiesUtilException(Throwable cause) {
		super(cause);
	}

	public PropertiesUtilException(String message, Throwable cause) {
		super(message, cause);
	}
	
}