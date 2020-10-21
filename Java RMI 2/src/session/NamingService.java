package session;

public class NamingService implements INamingService{

	//final fields
	final String name;
	
	public NamingService(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

}
