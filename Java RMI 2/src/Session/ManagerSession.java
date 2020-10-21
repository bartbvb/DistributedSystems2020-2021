package Session;

import rental.ICarRentalCompany;
import rental.ICarType;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class ManagerSession implements IManagerSession{
    @Override
    public void registerCompany(String name, ICarRentalCompany company) throws RemoteException {

    }

    @Override
    public void unregisterCompany(String name) throws RemoteException {

    }

    @Override
    public int getNrOfReservationsForCarType(String rentalName, String carType) throws RemoteException {
        return 0;
    }

    @Override
    public ArrayList<String> getBestCustomers() throws RemoteException {
        return null;
    }

    @Override
    public int getNrOfReservationsByUser(String name) throws RemoteException {
        return 0;
    }

    @Override
    public ICarType getMostPopularCarType(String rentalName, int year) throws RemoteException {
        return null;
    }
}
