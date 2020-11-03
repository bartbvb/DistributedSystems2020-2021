package session;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ejb.Stateful;
import rental.CarRentalCompany;
import rental.CarType;
import rental.Quote;
import rental.RentalStore;
import rental.Reservation;
import rental.ReservationConstraints;
import rental.ReservationException;

@Stateful
public class ReservationSession implements ReservationSessionRemote {
    
    List<Quote> _quoteList = new ArrayList<Quote>();

    @Override
    public Set<String> getAllRentalCompanies() {
        return new HashSet<String>(RentalStore.getRentals().keySet());
    }

    @Override
    public Set<CarType> getAvailableCarTypes(Date start, Date end) {
        Set<CarType> cars = new HashSet<CarType>();
        for(CarRentalCompany c : RentalStore.getRentals().values())
            cars.addAll(c.getAvailableCarTypes(start, end));
        return cars;
    }

    @Override
    public void createQuote(String clientName, ReservationConstraints resCon) throws ReservationException {
        CarRentalCompany crc = null;
        Quote q = null;
        
        for(CarRentalCompany c : RentalStore.getRentals().values()){
            if(c.hasRegion(resCon.getRegion())){
                crc = c;
                try{
                    q = crc.createQuote(resCon, clientName);
                    break;
                }catch (ReservationException e){
                    System.out.println(e.toString());
                }
            }
        }
        
        if(crc != null && q != null)
            _quoteList.add(q);
        else
            throw new ReservationException("ReservationSession.createQuote(): No suitable companies found in region " + resCon.getRegion());
    }

    @Override
    public List<Quote> getCurrentQuotes() {
        return _quoteList;
    }

    @Override
    public List<Reservation> confirmQuotes() throws ReservationException {
        List<Reservation> reservations = new ArrayList<>();
        try{
            for (Quote q : _quoteList){
                CarRentalCompany crc = RentalStore.getRental(q.getRentalCompany());
                reservations.add(crc.confirmQuote(q));
            }
        }catch (ReservationException e){
            System.out.println(e.toString());
            //Cancel all reservations
            for(Reservation res : reservations){
                CarRentalCompany crc = RentalStore.getRental(res.getRentalCompany());
                crc.cancelReservation(res);
            }

            throw new ReservationException("ReservationSession.confirmQuote(): Reservation failed");
        }
        return reservations;
    }
    
    

    
    
}
