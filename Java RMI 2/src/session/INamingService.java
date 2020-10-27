package session;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import rental.CarType;
import rental.ICarRentalCompany;
import rental.ReservationConstraints;

public interface INamingService extends Remote{
	
	//core Naming service functionality

	public void register(String name, ICarRentalCompany company) throws RemoteException;
	
	public void unregister(String name) throws RemoteException;
	
	public Map<String, ICarRentalCompany> getRegisteredCompanies() throws RemoteException;
	
	public ReservationSession createReservationSession(String user) throws RemoteException;
		
	public ManagerSession createManagerSession() throws RemoteException;
	
	public void removeRentalSession(ReservationSession session) throws RemoteException;
	
	//querry resolution methods
		
	public ReservationConstraints createConstraints(Date start, Date end, String carType, String region) throws RemoteException;
	
	
}
