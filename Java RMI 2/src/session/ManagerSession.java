package session;

import rental.*;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class ManagerSession implements IManagerSession{

    public ManagerSession() throws RemoteException{}
    @Override
    public void registerCompany(String name, ICarRentalCompany company) throws RemoteException {
        //TODO: RentalAgency.register()
    }

    @Override
    public void unregisterCompany(String name) throws RemoteException {
        //TODO: RentalAgency.unregister()
    }

    @Override
    public int getNrOfReservationsForCarType(String rentalName, String carType) throws RemoteException {
        ICarRentalCompany iCRC = null; //TODO: get(rentalName)

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
        List<ICarRentalCompany> rentalCompanies = new ArrayList<>(); //TODO: get
        int count = 0;
        for (ICarRentalCompany iCRC : rentalCompanies){
            count += iCRC.getReservationsByUser(name).size();
        }
        return count;
    }

    @Override
    public ICarType getMostPopularCarType(String rentalName, int year) throws RemoteException {
        ICarRentalCompany iCRC = null; //TODO: get(rentalName)
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
}
