package session;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import java.util.List;

import rental.ICarRentalCompany;
import rental.ReservationConstraints;

public class NamingService implements Serializable, INamingService{

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
	public synchronized void register(String name, ICarRentalCompany company) throws RemoteException {
		registeredCompanies.put(name, company);
	}

	
	/**
	 *@param name
	 *	the name of the company to unregister
	 *@effect
	 *	the company will no longer be bound in a map in this naming service
	 */
	public synchronized void unregister(String name) throws RemoteException {
		registeredCompanies.remove(name);
	}

	public Map<String, ICarRentalCompany> getRegisteredCompanies() throws RemoteException {
		return registeredCompanies;
	}

	//creates a reservationSession
	public synchronized ReservationSession createReservationSession(String user) throws RemoteException {
		ReservationSession session = new ReservationSession(user);
		rentalSessions.put(user, session);
		return session;
	}

	//creates a manager Session
	public synchronized ManagerSession createManagerSession() throws RemoteException {
		ManagerSession session = new ManagerSession();
		return session;
	}

	
	/**
	 *@param session
	 *	the session to remove from the active sessions mapping
	 *@effect
	 *	the session will be removed from the active sessions and will no longer have any references to it
	 */
	public synchronized void removeRentalSession(ReservationSession session) throws RemoteException {
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


	public ReservationConstraints createConstraints(Date start, Date end, String carType, String region)
			throws RemoteException {
		ReservationConstraints constraints = new ReservationConstraints(start, end, carType, region);
		return constraints;
	}

}
