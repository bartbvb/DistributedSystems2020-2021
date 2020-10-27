package session;

import rental.*;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;

public interface IReservationSession extends Remote {
    String getClientName() throws RemoteException;
    void createQuote(String clientName, ReservationConstraints resCon) throws ReservationException, RemoteException;
    List<Quote> getCurrentQuotes() throws RemoteException;
    List<Reservation> confirmQuotes() throws ReservationException, RemoteException;
    List<ICarType> getAvailableCarTypes(Date start, Date end) throws RemoteException;
    ICarType getCheapestCarType(Date start, Date end, String region) throws RemoteException;
    void cleanSession() throws RemoteException;
}
