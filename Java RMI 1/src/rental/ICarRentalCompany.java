package rental;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

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

	// mark
	boolean isAvailable(String carTypeName, Date start, Date end) throws RemoteException;

	Set<CarType> getAvailableCarTypes(Date start, Date end) throws RemoteException;

	/****************
	 * RESERVATIONS *
	 ****************/

	Quote createQuote(ReservationConstraints constraints, String client) throws ReservationException;

	Reservation confirmQuote(Quote quote) throws ReservationException;

	void cancelReservation(Reservation res) throws RemoteException;

	String toString();

}