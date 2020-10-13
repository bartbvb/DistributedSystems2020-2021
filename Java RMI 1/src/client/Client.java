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

		// An example reservation scenario on car rental company 'Hertz' would be...
		Client client = new Client("simpleTrips", carRentalCompanyName, localOrRemote);
		client.run();
	}

	/***************
	 * CONSTRUCTOR *
	 ***************/

	public Client(String scriptFile, String carRentalCompanyName, int localOrRemote) {
		super(scriptFile);
		System.setSecurityManager(null);
		try{
			Registry registry = LocateRegistry.getRegistry();
			iCRC = (ICarRentalCompany) registry.lookup(carRentalCompanyName);

		} catch (RemoteException | NotBoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Check which car types are available in the given period (across all companies
	 * and regions) and print this list of car types.
	 *
	 * @param start start time of the period
	 * @param end   end time of the period
	 * @throws Exception if things go wrong, throw exception
	 */
	@Override
	protected void checkForAvailableCarTypes(Date start, Date end) throws Exception {
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
	@Override
	protected Quote createQuote(String clientName, Date start, Date end, String carType, String region)
			throws Exception {
		Quote quote = null;
		try {
			ReservationConstraints constraints = new ReservationConstraints(start, end, carType, region);
			quote = iCRC.createQuote(constraints, clientName);
			System.out.println(quote);
		} catch (Exception e){
			e.printStackTrace();
		}
		return quote;
	}

	/**
	 * Confirm the given quote to receive a final reservation of a car.
	 * 
	 * @param quote the quote to be confirmed
	 * @return the final reservation of a car
	 * 
	 * @throws Exception if things go wrong, throw exception
	 */
	@Override
	protected Reservation confirmQuote(Quote quote) throws Exception {
		Reservation reservation = null;
		try {
			reservation = iCRC.confirmQuote(quote);
			System.out.println(reservation);
		} catch(Exception e){
			e.printStackTrace();
		}
		return reservation;
	}

	/**
	 * Get all reservations made by the given client.
	 *
	 * @param clientName name of the client
	 * @return the list of reservations of the given client
	 * 
	 * @throws Exception if things go wrong, throw exception
	 */
	@Override
	protected List<Reservation> getReservationsByRenter(String clientName) throws Exception {
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
	 * Get the number of reservations for a particular car type.
	 * 
	 * @param carType name of the car type
	 * @return number of reservations for the given car type
	 * 
	 * @throws Exception if things go wrong, throw exception
	 */
	@Override
	protected int getNumberOfReservationsForCarType(String carType) throws Exception {
		int nrOfReservations = 0;
		try {
			nrOfReservations = iCRC.getNumberOfReservationsForCarType(carType);
		} catch (Exception e){
			e.printStackTrace();
		}
		return nrOfReservations;
	}
	
}