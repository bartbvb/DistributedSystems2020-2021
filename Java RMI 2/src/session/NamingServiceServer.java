package session;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.invoke.MethodHandles;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import rental.ReservationException;

public class NamingServiceServer implements INamingService{
	

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
