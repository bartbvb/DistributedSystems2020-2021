package client;

import java.rmi.NotBoundException;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

//import com.sun.tools.classfile.Opcode.Set;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


import rental.*;
import session.INamingService;
import session.IReservationSession;
import session.IManagerSession;

public class Client extends AbstractTestManagement<IReservationSession, IManagerSession> {

	/********
	 * MAIN *
	 ********/

	private final static int LOCAL = 0;
	private final static int REMOTE = 1;
	public static INamingService iNS;

	/**
	 * The `main` method is used to launch the client application and run the test
	 * script.
	 */
	public static void main(String[] args) throws Exception {
		// The first argument passed to the `main` method (if present)
		// indicates whether the application is run on the remote setup or not.
		//int localOrRemote = (args.length == 1 && args[0].equals("REMOTE")) ? REMOTE : LOCAL;
		
		String namingService = "naming Service";

		System.setSecurityManager(null);
		try{
			Registry registry = LocateRegistry.getRegistry();
			iNS = (INamingService) registry.lookup(namingService);
		} catch (RemoteException | NotBoundException e) {
			e.printStackTrace();
		}

		// An example reservation scenario on car rental company 'Hertz' would be...
		//Client client = new Client("simpleTrips", namingService, localOrRemote);
		//client.run();
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
	protected IReservationSession getNewReservationSession(String name) throws Exception {
		return iNS.createReservationSession(name);
	}

	/**
	 * Create a new manager session for the user with the given name (there is only one manager for all car rental companies).
	 * @param name of the user (i.e. manager) using this session
	 * @return the new manager session
	 *
	 * @throws Exception if things go wrong, throw exception
	 */
	@Override
	protected IManagerSession getNewManagerSession(String name) throws Exception {
		return iNS.createManagerSession();
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
	protected void checkForAvailableCarTypes(IReservationSession o, Date start, Date end) throws Exception {
		try {
			List<ICarType> types = o.getAvailableCarTypes(start, end);
			for (ICarType type: types) {
				System.out.println(type);
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}

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
	protected void addQuoteToSession(IReservationSession o, String name, Date start, Date end, String carType, String region) throws Exception {
		o.createQuote(name, iNS.createConstraints(start, end, carType, region));
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
	protected List<Reservation> confirmQuotes(IReservationSession o, String name) throws Exception {
		return o.confirmQuotes();
	}

	@Override
	protected Set<String> getBestClients(IManagerSession ms) throws Exception {
		return ms.getBestCustomers();
	}

	@Override
	protected String getCheapestCarType(IReservationSession session, Date start, Date end, String region)
			throws Exception {
		return session.getCheapestCarType(start, end, region).toString();
	}

	@Override
	protected CarType getMostPopularCarTypeInCRC(IManagerSession ms, String carRentalCompanyName, int year)
			throws Exception {
		return (CarType) ms.getMostPopularCarType(carRentalCompanyName, year);
	}

	@Override
	protected int getNumberOfReservationsByRenter(IManagerSession ms, String clientName) throws Exception {
		return ms.getNrOfReservationsByUser(clientName);
	}

	@Override
	protected int getNumberOfReservationsForCarType(IManagerSession ms, String carRentalName, String carType)
			throws Exception {
		return ms.getNrOfReservationsForCarType(carRentalName, carType);
	}

	


}