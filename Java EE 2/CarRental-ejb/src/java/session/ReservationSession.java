package session;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.annotation.Resource;
import javax.ejb.EJBContext;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import static javax.ejb.TransactionAttributeType.REQUIRED;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import rental.CarType;
import rental.Quote;
import rental.RentalStore;
import rental.Reservation;
import rental.ReservationConstraints;
import rental.ReservationException;

@Stateful
public class ReservationSession implements ReservationSessionRemote {

    @Resource 
    private EJBContext context;
    
    private String renter;
    private List<Quote> quotes = new LinkedList<Quote>();
    
    @PersistenceContext
    EntityManager entMan;

    @Override
    public Set<String> getAllRentalCompanies() {
        return new HashSet<String>(RentalStore.getRentals().keySet());
    }
    
    @Override
    public List<CarType> getAvailableCarTypes(Date start, Date end) {
        List<CarType> availableCarTypes = new LinkedList<CarType>();
        for(String crc : getAllRentalCompanies()) {
            for(CarType ct : RentalStore.getRentals().get(crc).getAvailableCarTypes(start, end)) {
                if(!availableCarTypes.contains(ct))
                    availableCarTypes.add(ct);
            }
        }
        return availableCarTypes;
    }

    @Override
    public Quote createQuote(String company, ReservationConstraints constraints) throws ReservationException {
        try {
            Quote out = RentalStore.getRental(company).createQuote(constraints, renter);
            quotes.add(out);
            return out;
        } catch(Exception e) {
            throw new ReservationException(e);
        }
    }

    @Override
    public List<Quote> getCurrentQuotes() {
        return quotes;
    }

    @Override
    @TransactionAttribute(REQUIRED)
    public List<Reservation> confirmQuotes() throws ReservationException {
        List<Reservation> done = new LinkedList<Reservation>();
        try {
            for (Quote quote : quotes) {
                done.add(RentalStore.getRental(quote.getRentalCompany()).confirmQuote(quote));
            }
            quotes.clear();
            
        } catch (Exception e) {
            context.setRollbackOnly();
            throw new ReservationException(e);
        }
        return done;
    }

    @Override
    public void setRenterName(String name) {
        if (renter != null) {
            throw new IllegalStateException("name already set");
        }
        renter = name;
    }

    @Override
    public String getRenterName() {
        return renter;
    }
    
    @Override
    public String getCheapestCar(Date start, Date end, String region) {
        Query quer = entMan.createQuery("SELECT c.id, t.rentalPricePerDay AS price FROM CarRentalCompany comp JOIN comp.cars c JOIN c.type t WHERE NOT ((c.reservations.startDate > :start AND c.reservations.startDate < :end) OR (c.reservations.endDate > :start AND c.reservations.endDate < :end)) AND comp.regions LIKE :reg ORDER BY price DESC");
        quer.setParameter("start", start);
        quer.setParameter("end", end);
        quer.setParameter("reg", region);
        quer.setMaxResults(1);
        quer.getResultList();
        List list = quer.getResultList();
        String res = list.toString();
        return res;
    }
}