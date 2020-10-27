package session;

import rental.*;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReservationSession implements IReservationSession{

    private List<Quote> _quoteList = new ArrayList<>();

    private String _clientName = "";

    public ReservationSession(String name) throws RemoteException{
        _clientName = name;
    }

    /**
     * @return The name of the client for this session.
     * @throws RemoteException
     */
    @Override
    public String getClientName() throws RemoteException{
        return _clientName;
    }

    /**
     * @param clientName The name of the client.
     * @param resCon The reservation constraints for the quote.
     * @throws ReservationException
     * @throws RemoteException
     */
    @Override
    public synchronized void createQuote(String clientName, ReservationConstraints resCon) throws ReservationException, RemoteException {
        ICarRentalCompany iCRC = null;
        Quote q = null;

        for (ICarRentalCompany c : getNamingService().getRegisteredCompanies().values()){
            if(c.inRegion(resCon.getRegion())){
                iCRC = c;
                try{
                    q = iCRC.createQuote(resCon,clientName);
                    break;
                }catch (ReservationException e){
                    System.out.println(e.toString());
                }
            }
        }

        //if we found a company in the region that was able to create a quote, add it to the list
        if(iCRC != null && q != null){
            _quoteList.add(q);
        }
        else {
            throw new ReservationException("ReservationSession.createQuote(): No suitable companies found in region " + resCon.getRegion());
        }
    }

    /**
     * @return The list of quotes.
     * @throws RemoteException
     */
    @Override
    public synchronized List<Quote> getCurrentQuotes() throws RemoteException {
        return _quoteList;
    }

    /**
     * @return The list of confirmed quotes as reservations.
     * @throws ReservationException
     * @throws RemoteException
     */
    @Override
    public synchronized List<Reservation> confirmQuotes() throws ReservationException, RemoteException {
        List<Reservation> reservations = new ArrayList<>();
        try{
            for (Quote q : _quoteList){
                ICarRentalCompany iCRC = getNamingService().getRegisteredCompanies().get(q.getRentalCompany());
                reservations.add(iCRC.confirmQuote(q));
            }
        }catch (ReservationException e){
            System.out.println(e.toString());
            //Cancel all reservations
            for(Reservation res : reservations){
                ICarRentalCompany iCRC = getNamingService().getRegisteredCompanies().get(res.getRentalCompany());
                iCRC.cancelReservation(res);
            }

            throw new ReservationException("ReservationSession.confirmQuote(): Reservation failed");
        }
        return reservations;
    }

    /**
     * @param start The start date.
     * @param end The end date
     * @return All car types available in the given period.
     * @throws RemoteException
     */
    @Override
    public synchronized List<ICarType> getAvailableCarTypes(Date start, Date end) throws RemoteException {
        List<ICarType> cars = new ArrayList<>();
        for (ICarRentalCompany iCRC : getNamingService().getRegisteredCompanies().values()){
            cars.addAll(iCRC.getAvailableCarTypes(start, end));
        }
        return cars;
    }

    /**
     * @param start The start date
     * @param end The end date
     * @param region The region to search in
     * @return The cheapest car type in the given region during the given period.
     * @throws RemoteException
     */
    @Override
    public ICarType getCheapestCarType(Date start, Date end, String region) throws RemoteException {
        List<ICarType> cars = new ArrayList<>();
        for (ICarRentalCompany iCRC : getNamingService().getRegisteredCompanies().values()){
            if(iCRC.inRegion(region))
                cars.addAll(iCRC.getAvailableCarTypes(start, end));
        }

        ICarType ret = new CarType("",0,0.0f,Double.MAX_VALUE,false); //Overwrite me
        for (ICarType c : cars){
            if(c.getRentalPricePerDay() < ret.getRentalPricePerDay()){
                ret = c;
            }
        }
        if(ret.getName().equals("")) System.out.println("No available cars found in region " + region);
        return ret;
    }

    /**
     * Cleans the list of quotes.
     * @throws RemoteException
     */
    @Override
    public void cleanSession() throws RemoteException {
        _quoteList.clear();
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
