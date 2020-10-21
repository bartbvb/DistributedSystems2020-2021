package rental;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.*;

public interface ICarRentalCompany extends Remote{

	/********
	 * NAME *
	 ********/

	String getName() throws RemoteException;

	List<String> getRegions() throws RemoteException;

	boolean operatesInRegion(String region) throws RemoteException;

	/*************
	 * CAR TYPES *
	 *************/

	Collection<CarType> getAllCarTypes() throws RemoteException;

	CarType getCarType(String carTypeName) throws RemoteException;

	ArrayList<Car> getCarsOfType(String type) throws RemoteException;

	// mark
	boolean isAvailable(String carTypeName, Date start, Date end) throws RemoteException;

	Set<CarType> getAvailableCarTypes(Date start, Date end) throws RemoteException;

	/****************
	 * RESERVATIONS *
	 ****************/

	Quote createQuote(ReservationConstraints constraints, String client) throws ReservationException, RemoteException;

	Reservation confirmQuote(Quote quote) throws ReservationException, RemoteException;

	void cancelReservation(Reservation res) throws RemoteException;

	//String toString() throws RemoteException;
	
	List<Reservation> getReservationsByUser(String clientName) throws RemoteException;
	
	int getNbOfReservationsForCarType(String carType) throws RemoteException;

	int getNbOfReservationsForCarTypeAndYear(String carType, int startYear) throws RemoteException;

}