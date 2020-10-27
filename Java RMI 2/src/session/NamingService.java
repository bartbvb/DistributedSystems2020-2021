package session;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import com.sun.tools.javac.util.List;

import rental.CarType;
import rental.ICarRentalCompany;
import rental.ReservationConstraints;

public class NamingService implements INamingService{

	//Data sets
	Map<String, ICarRentalCompany> registeredCompanies;
	Map<String, ReservationSession> rentalSessions;
	List<ManagerSession> managerSessions;
	
	//final fields
	final String name;
	
	//constructor
	public NamingService(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	
	/**
	 *@param name 
	 *	the name of the company
	 *@param company
	 *	The Car Rental Company interface with which to perform remote invocations
	 *@effect
	 *	the interface is bound to its company's name in a map for ease of access.
	 */
	public void register(String name, ICarRentalCompany company) throws RemoteException {
		registeredCompanies.put(name, company);
	}

	
	/**
	 *@param name
	 *	the name of the company to unregister
	 *@effect
	 *	the company will no longer be bound in a map in this naming service
	 */
	public void unregister(String name) throws RemoteException {
		registeredCompanies.remove(name);
	}

	public Map<String, ICarRentalCompany> getRegisteredCompanies() throws RemoteException {
		return registeredCompanies;
	}

	//creates a reservationSession
	public ReservationSession createReservationSession(String user) throws RemoteException {
		ReservationSession session = new ReservationSession(user);
		rentalSessions.put(user, session);
		return session;
	}

	//creates a manager Session
	public ManagerSession createManagerSession() throws RemoteException {
		ManagerSession session = new ManagerSession();
		return session;
	}

	
	/**
	 *@param session
	 *	the session to remove from the active sessions mapping
	 *@effect
	 *	the session will be removed from the active sessions and will no longer have any references to it
	 */
	public void removeRentalSession(ReservationSession session) throws RemoteException {
		if (rentalSessions.containsValue(session)) {
			Set<String> keys = rentalSessions.keySet();
			for(String key: keys) {
				if(rentalSessions.get(key) == session) {
					rentalSessions.remove(key);
					break;
				}
			}
		}
	}
	
	
	public ReservationSession getUserSession(String user) throws RemoteException {
		ReservationSession session;
		try {
			session = rentalSessions.get(user);
			return session;
		}catch (Exception e) {
			session = this.createReservationSession(user);
			return session;
		}
	}


	@Override
	public ReservationConstraints createConstraints(Date start, Date end, String carType, String region)
			throws RemoteException {
		ReservationConstraints constraints = new ReservationConstraints(start, end, carType, region);
		return constraints;
	}

}
