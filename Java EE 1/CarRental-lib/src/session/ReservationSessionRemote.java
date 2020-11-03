package session;

import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.ejb.Remote;
import rental.CarType;
import rental.Quote;
import rental.Reservation;
import rental.ReservationConstraints;
import rental.ReservationException;

@Remote
public interface ReservationSessionRemote {

    Set<String> getAllRentalCompanies();
    
    Set<CarType> getAvailableCarTypes(Date start, Date end);
    
    void createQuote(String clientName, ReservationConstraints resCon) throws ReservationException;
    
    List<Quote> getCurrentQuotes();
    
    List<Reservation> confirmQuotes() throws ReservationException;
    
}
