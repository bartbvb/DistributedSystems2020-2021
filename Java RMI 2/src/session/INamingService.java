package session;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import rental.CarType;
import rental.ICarRentalCompany;

public interface INamingService extends Remote{
	
	//core Naming service functionality

	public void register(String name, ICarRentalCompany company) throws RemoteException;
	
	public void unregister(String name) throws RemoteException;
	
	public Map<String, ICarRentalCompany> getRegisteredCompanies() throws RemoteException;
	
	public ReservationSession createReservationSession() throws RemoteException;
	
	public ManagerSession createManagerSession() throws RemoteException;
	
	public void removeRentalSession(ReservationSession session) throws RemoteException;
	
	//querry resolution methods
	
	Set<CarType> getAvailableCarTypes(Date start, Date end) throws RemoteException;	
	
	
}
