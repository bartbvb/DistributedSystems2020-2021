package session;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

import rental.ICarRentalCompany;

public interface INamingService extends Remote{

	public void register(String name, ICarRentalCompany company) throws RemoteException;
	
	public void unregister(String name) throws RemoteException;
	
	public Map<String, ICarRentalCompany> getRegisteredCompanies() throws RemoteException;
	
	public ReservationSession createReservationSession(String user) throws RemoteException;
	
	public ManagerSession createManagerSession() throws RemoteException;
	
	public void removeRentalSession(ReservationSession session) throws RemoteException;
	
}
