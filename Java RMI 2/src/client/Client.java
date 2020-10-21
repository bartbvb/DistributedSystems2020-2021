package client;

import java.rmi.NotBoundException;
import java.util.Date;
import java.util.List;
import java.util.Set;


//import com.sun.tools.classfile.Opcode.Set;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


import rental.*;

public class Client extends AbstractTestBooking {

	/********
	 * MAIN *
	 ********/

	private final static int LOCAL = 0;
	private final static int REMOTE = 1;
	public static ICarRentalCompany iCRC;

	/**
	 * The `main` method is used to launch the client application and run the test
	 * script.
	 */
	public static void main(String[] args) throws Exception {
		// The first argument passed to the `main` method (if present)
		// indicates whether the application is run on the remote setup or not.
		int localOrRemote = (args.length == 1 && args[0].equals("REMOTE")) ? REMOTE : LOCAL;
		if(localOrRemote == 1) {
			throw new UnsupportedOperationException("Remote set-up not implemented yet");
		}
		
		String carRentalCompanyName = "Hertz";

		System.setSecurityManager(null);
		try{
			Registry registry = LocateRegistry.getRegistry();
			iCRC = (ICarRentalCompany) registry.lookup(carRentalCompanyName);

		} catch (RemoteException | NotBoundException e) {
			e.printStackTrace();
		}

		// An example reservation scenario on car rental company 'Hertz' would be...
		Client client = new Client("simpleTrips", carRentalCompanyName, localOrRemote);
		client.run();
	}

	/***************
	 * CONSTRUCTOR *
	 ***************/

	public Client(String scriptFile, String carRentalCompanyName, int localOrRemote) {
		super(scriptFile);
	}

	/**
	 * Create a new reservation session for the user with the given name.
	 *
	 * @param name name of the client (renter) owning this session
	 * @return the new reservation session
	 *
	 * @throws Exception if things go wrong, throw exception
	 */
	@Override
	protected Object getNewReservationSession(String name) throws Exception {
		return null;
	}

	/**
	 * Create a new manager session for the user with the given name (there is only one manager for all car rental companies).
	 * @param name of the user (i.e. manager) using this session
	 * @return the new manager session
	 *
	 * @throws Exception if things go wrong, throw exception
	 */
	@Override
	protected Object getNewManagerSession(String name) throws Exception {
		return null;
	}

	/**
	 * Check which car types are available in the given period and print them.
	 *
	 * @param o the session to do the request from
	 * @param start start time of the period
	 * @param end end time of the period
	 *
	 * @throws Exception if things go wrong, throw exception
	 */
	@Override
	protected void checkForAvailableCarTypes(Object o, Date start, Date end) throws Exception {
		try {
			Set<CarType> cars = iCRC.getAvailableCarTypes(start, end);
			for(CarType i: cars) {
				System.out.println(i);
			} 
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Retrieve a quote for a given car type (tentative reservation).
	 * 
	 * @param clientName name of the client
	 * @param start      start time for the quote
	 * @param end        end time for the quote
	 * @param carType    type of car to be reserved
	 * @param region     region in which car must be available
	 * @return the newly created quote
	 * 
	 * @throws Exception if things go wrong, throw exception
	 */
	protected Quote createQuote(String clientName, Date start, Date end, String carType, String region)
			throws Exception {
		Quote quote = null;
		//try {
			ReservationConstraints constraints = new ReservationConstraints(start, end, carType, region);
			quote = iCRC.createQuote(constraints, clientName);
			System.out.println(quote);
		/*} catch (Exception e){
			System.out.println(e.getMessage());
		}*/
		return quote;
	}

	/**
	 * Add a quote for a given car type to the session.
	 *
	 * @param o the session to add the reservation to
	 * @param name the name of the client owning the session
	 * @param start start time of the reservation
	 * @param end end time of the reservation
	 * @param carType type of car to be reserved
	 * @param region region for which the car shall be reserved
	 * should be done
	 *
	 * @throws Exception if things go wrong, throw exception
	 */
	@Override
	protected void addQuoteToSession(Object o, String name, Date start, Date end, String carType, String region) throws Exception {

	}

	/**
	 * Confirm the given quote to receive a final reservation of a car.
	 * 
	 * @param quote the quote to be confirmed
	 * @return the final reservation of a car
	 * 
	 * @throws Exception if things go wrong, throw exception
	 */
	protected Reservation confirmQuote(Quote quote) throws Exception {
		Reservation reservation = null;
		try {
			reservation = iCRC.confirmQuote(quote);
			System.out.println(reservation);
		} catch(Exception e){
			System.out.println(e.getMessage());
			iCRC.cancelReservation(reservation);
		}
		return reservation;
	}
	/**
	 * Confirm the quotes in the given session.
	 *
	 * @param o the session to finalize
	 * @param name the name of the client owning the session
	 *
	 * @throws Exception if things go wrong, throw exception
	 */
	@Override
	protected List<Reservation> confirmQuotes(Object o, String name) throws Exception {
		return null;
	}

	/**
	 * Get all reservations made by the given client.
	 *
	 * @param clientName name of the client
	 * @return the list of reservations of the given client
	 * 
	 * @throws Exception if things go wrong, throw exception
	 */
	protected List<Reservation> getReservationsByRenter(Object ms, String clientName) throws Exception {
		List<Reservation>reservations = iCRC.getReservationsByUser(clientName);
		try {
			for (Reservation res : reservations) {
				System.out.println("");
				System.out.print("carType: ");
				System.out.print(res.getCarType());
				System.out.print("   carID: ");
				System.out.print(res.getCarId());
				System.out.print("   period: ");
				System.out.print(res.getStartDate());
				System.out.print("-");
				System.out.print(res.getEndDate());
				System.out.print("   price: ");
				System.out.print(res.getRentalPrice());
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return reservations;
	}

	/**
	 * Get the number of reservations made by the given renter (across whole
	 * rental agency).
	 *
	 * @param	ms manager session
	 * @param clientName name of the renter
	 * @return	the number of reservations of the given client (across whole
	 * rental agency)
	 *
	 * @throws Exception if things go wrong, throw exception
	 */
	@Override
	protected int getNumberOfReservationsByRenter(Object ms, String clientName) throws Exception {
		return 0;
	}

	/**
	 * Get the number of reservations for a particular car type.
	 *
	 * @param ms manager session
	 * @param carRentalName name of the rental company managed by this session
	 * @param carType name of the car type
	 * @return number of reservations for this car type
	 *
	 * @throws Exception if things go wrong, throw exception
	 */
	@Override
	protected int getNumberOfReservationsForCarType(Object ms, String carRentalName, String carType) throws Exception {
		int nrOfReservations = 0;
		try {
			nrOfReservations = iCRC.getNumberOfReservationsForCarType(carType);
		} catch (Exception e){
			e.printStackTrace();
		}
		return nrOfReservations;
	}


}