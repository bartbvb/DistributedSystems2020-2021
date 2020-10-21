package Session;

import rental.*;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;

public class ReservationSession implements IReservationSession{
    @Override
    public void createQuote(String clientName, ReservationConstraints resCon) throws ReservationException, RemoteException {

    }

    @Override
    public List<Quote> getCurrentQuotes() throws RemoteException {
        return null;
    }

    @Override
    public List<Reservation> confirmQuotes() throws ReservationException, RemoteException {
        return null;
    }

    @Override
    public List<ICarType> getAvailableCarTypes(Date start, Date end) throws RemoteException {
        return null;
    }

    @Override
    public ICarType getCheapestCarType(Date start, Date end, String region) throws RemoteException {
        return null;
    }

    @Override
    public void cleanSession() throws RemoteException {

    }
}
