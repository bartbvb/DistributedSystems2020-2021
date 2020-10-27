package session;

import rental.*;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.*;
import java.util.stream.Collectors;

public class ManagerSession implements IManagerSession{

    public ManagerSession() throws RemoteException{}

    /**
     * @param name The name of the company.
     * @param company The company interface.
     * @throws RemoteException
     */
    @Override
    public void registerCompany(String name, ICarRentalCompany company) throws RemoteException {
        getNamingService().register(name,company);
    }

    /**
     * @param name The name of the company.
     * @throws RemoteException
     */
    @Override
    public void unregisterCompany(String name) throws RemoteException {
        getNamingService().unregister(name);
    }

    /**
     * @param rentalName The name of the rentalcompany.
     * @param carType The type of car.
     * @return How many reservations the given type of car has at the given company.
     * @throws RemoteException
     */
    @Override
    public int getNrOfReservationsForCarType(String rentalName, String carType) throws RemoteException {
        ICarRentalCompany iCRC = getNamingService().getRegisteredCompanies().get(rentalName);

        int count = 0;
        ArrayList<Car> cars = iCRC.getCarsOfType(carType);
        for (Car c : cars){
            count += c.getNrOfReservations();
        }
        return count;
    }

    /**
     * @return All renters who have the highest number of final reservations.
     * @throws RemoteException
     */
    @Override
    public ArrayList<String> getBestCustomers() throws RemoteException {
        int maxReservations = 0;
        //Give scores
        Map<String, Integer> scores = new HashMap<>();
        for (ICarRentalCompany iCRC : getNamingService().getRegisteredCompanies().values()){
            for (Car c : iCRC.getAllCars()){
                for (Reservation r : c.getReservations()){
                    int score = 1;
                    if(scores.containsKey(r.getCarRenter()))
                        score = scores.get(r.getCarRenter()) + 1;
                    scores.put(r.getCarRenter(),score);
                    if(score > maxReservations) maxReservations = score;
                }
            }
        }
        //Return all max customers
        int finalMaxReservations = maxReservations;
        return new ArrayList<>(scores.entrySet().stream().filter(entry -> entry.getValue() == finalMaxReservations).map(Map.Entry::getKey).collect(Collectors.toList()));
    }

    /**
     * @param name The users name.
     * @return The number of reservations the given user has.
     * @throws RemoteException
     */
    @Override
    public int getNrOfReservationsByUser(String name) throws RemoteException {
        List<ICarRentalCompany> rentalCompanies = new ArrayList<>(getNamingService().getRegisteredCompanies().values());
        int count = 0;
        for (ICarRentalCompany iCRC : rentalCompanies){
            count += iCRC.getReservationsByUser(name).size();
        }
        return count;
    }

    /**
     * @param rentalName The name of the rental company.
     * @param year The most popular car types year.
     * @return The car type that was most popular for the given year.
     * @throws RemoteException
     */
    @Override
    public ICarType getMostPopularCarType(String rentalName, int year) throws RemoteException {

        ICarRentalCompany iCRC = getNamingService().getRegisteredCompanies().get(rentalName);
        ArrayList<CarType> cartypes = new ArrayList<>(iCRC.getAllCarTypes());
        CarType mostPopular = null;
        int timesRented, mostRented = 0;
        for (CarType type : cartypes){
            timesRented = iCRC.getNbOfReservationsForCarTypeAndYear(type.getName(),year);
            if (timesRented > mostRented){
                mostRented = timesRented;
                mostPopular = type;
            }
        }
        return mostPopular;
    }

    private NamingService ns = null;

    /**
     * Searches the registry for the NamingService if we don't have one yet.
     * @return The NamingService.
     * @throws RemoteException
     */
    private NamingService getNamingService() throws RemoteException{
        if(ns == null){
            try {
                ns = (NamingService) LocateRegistry.getRegistry().lookup("naming Service");
            } catch (NotBoundException e) {
                e.printStackTrace();
            }
        }
        return ns;
    }
}
