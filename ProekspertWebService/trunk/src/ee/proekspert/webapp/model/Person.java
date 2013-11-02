package ee.proekspert.webapp.model;

/**
 * <code>Person</code> is a simple JavaBean class for storing
 * information related to a person.
 *
 */
public class Person {
	
	private String name;
	
	private String phoneNumber;
	
	public Person() {}
		
	public Person(String name, String phoneNumber) {
		this.name = name;
		this.phoneNumber = phoneNumber;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getPhoneNumber() {
		return phoneNumber;
	}
	
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
}
