package session;

import rental.*;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReservationSession implements IReservationSession{

    private List<Quote> _quoteList = new ArrayList<>();

    private String _clientName = "";

    public ReservationSession(String name) throws RemoteException{
        _clientName = name;
    }

    @Override
    public String getClientName() throws RemoteException{
        return _clientName;
    }

    @Override
    public synchronized void createQuote(String clientName, ReservationConstraints resCon) throws ReservationException, RemoteException {
        ICarRentalCompany iCRC = null;
        Quote q = null;

        for (ICarRentalCompany c : /*getRentals().values()*/){
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

    @Override
    public synchronized List<Quote> getCurrentQuotes() throws RemoteException {
        return _quoteList;
    }

    @Override
    public synchronized List<Reservation> confirmQuotes() throws ReservationException, RemoteException {
        List<Reservation> reservations = new ArrayList<>();
        try{
            for (Quote q : _quoteList){
                ICarRentalCompany iCRC = /*getRental(q.getRentalCompany())*/
                reservations.add(iCRC.confirmQuote(q));
            }
        }catch (ReservationException e){
            System.out.println(e.toString());
            //Cancel all reservations
            for(Reservation res : reservations){
                ICarRentalCompany iCRC = /*getRental(r.getRentalCompany())*/
                iCRC.cancelReservation(res);
            }

            throw new ReservationException("ReservationSession.confirmQuote(): Reservation failed");
        }
        return reservations;
    }

    @Override
    public synchronized List<ICarType> getAvailableCarTypes(Date start, Date end) throws RemoteException {
        List<ICarType> cars = new ArrayList<>();
        for (ICarRentalCompany iCRC : /*getRentals().values()*/){
            cars.addAll(iCRC.getAvailableCarTypes(start, end));
        }
        return cars;
    }

    @Override
    public ICarType getCheapestCarType(Date start, Date end, String region) throws RemoteException {
        List<ICarType> cars = new ArrayList<>();
        for (ICarRentalCompany iCRC : /*getRentals().values()*/){
            if(iCRC.inRegion(region))
                cars.addAll(iCRC.getAvailableCarTypes(start, end));
        }

        ICarType ret = new CarType("",0,0.0,Double.MAX_VALUE,false); //Overwrite me
        for (ICarType c : cars){
            if(c.getRentalPricePerDay() < ret.getRentalPricePerDay()){
                ret = c;
            }
        }
        if(ret.getName().equals("")) System.out.println("No available cars found in region " + region);
        return ret;
    }

    @Override
    public void cleanSession() throws RemoteException {
        _quoteList.clear();
    }
}
