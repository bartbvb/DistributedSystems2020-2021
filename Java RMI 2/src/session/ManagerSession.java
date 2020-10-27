package session;

import rental.*;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class ManagerSession implements IManagerSession{

    public ManagerSession() throws RemoteException{}
    @Override
    public void registerCompany(String name, ICarRentalCompany company) throws RemoteException {
        getNamingService().register(name,company);
    }

    @Override
    public void unregisterCompany(String name) throws RemoteException {
        getNamingService().unregister(name);
    }

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

    @Override
    public ArrayList<String> getBestCustomers() throws RemoteException {
        return null;
    }

    @Override
    public int getNrOfReservationsByUser(String name) throws RemoteException {
        List<ICarRentalCompany> rentalCompanies = new ArrayList<>(getNamingService().getRegisteredCompanies().values());
        int count = 0;
        for (ICarRentalCompany iCRC : rentalCompanies){
            count += iCRC.getReservationsByUser(name).size();
        }
        return count;
    }

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
