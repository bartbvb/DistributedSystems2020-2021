package Session;

import rental.Car;
import rental.ICarRentalCompany;
import rental.ICarType;

import java.rmi.RemoteException;
import java.util.ArrayList;

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
        ICarRentalCompany iCRC = null; //TODO: RentalAgency.get()

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
        return 0;
    }

    @Override
    public ICarType getMostPopularCarType(String rentalName, int year) throws RemoteException {
        return null;
    }
}
