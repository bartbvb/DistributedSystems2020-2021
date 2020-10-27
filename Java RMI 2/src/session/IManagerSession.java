package session;

import rental.ICarType;
import rental.ICarRentalCompany;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;

public interface IManagerSession extends Remote {
    void registerCompany(String name, ICarRentalCompany company) throws RemoteException;
    void unregisterCompany(String name) throws RemoteException;
    int getNrOfReservationsForCarType(String rentalName, String carType) throws RemoteException;
    Set<String> getBestCustomers() throws RemoteException;
    int getNrOfReservationsByUser(String name) throws RemoteException;
    ICarType getMostPopularCarType(String rentalName, int year) throws RemoteException;
}
