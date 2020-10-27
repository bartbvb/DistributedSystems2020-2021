package session;

import rental.ReservationException;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class NamingServiceServer{
	

	public static void main(String[] args) throws ReservationException,
			NumberFormatException, IOException {

		//set security manage
		if(System.getSecurityManager() != null)
			System.setSecurityManager(null);
		
		//create the Naming service
		NamingService namingService = new NamingService("naming Service");
		
		//locate Registry
		Registry registry = null;
		try {
			registry = LocateRegistry.getRegistry();
		} catch(RemoteException e) {
			System.exit(-1);
		}
		
		//register Naming Service
		try {
			registry.rebind(namingService.getName(), namingService);
		}catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}


}
