package session;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.EJBContext;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import static javax.ejb.TransactionAttributeType.REQUIRED;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import rental.CarRentalCompany;
import rental.CarType;
import rental.Quote;
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
        Query quer = entMan.createQuery("SELECT CRC.name FROM CarRentalCompany CRC");
        return new HashSet<String>(quer.getResultList());
    }
    
    @Override
    public List<CarType> getAvailableCarTypes(Date start, Date End) {
        try {
            String q = "SELECT c.type FROM Car c WHERE NOT ((c.reservations.startDate > :start AND c.reservations.startDate < :end) OR (c.reservations.endDate > :start AND c.reservations.endDate < :end))";
            Query quer = entMan.createQuery(q);
            quer.setParameter("start", start);
            quer.setParameter("end", End);
            return quer.getResultList();

        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ReservationSession.class.getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<CarType>();
        }    
    }

    @Override
    public Quote createQuote(String client, ReservationConstraints constraints) throws ReservationException {
        try {
            String q = "SELECT crc FROM CarRentalCompany crc JOIN crc.carTypes ct WHERE ct.name = :carType AND :region MEMBER OF crc.regions";
            Query quer = entMan.createQuery(q,CarRentalCompany.class);
            quer.setParameter("region", constraints.getRegion());
            quer.setParameter("carType", constraints.getCarType());
            List<CarRentalCompany> companyList = quer.getResultList();
            if(companyList.isEmpty()) throw new ReservationException("Region is empty.");
            return selectQuote(client, companyList, constraints);
        } catch(ReservationException e) {
            Logger.getLogger(ReservationSession.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }
    
    private Quote selectQuote(String client, List<CarRentalCompany> companyList, ReservationConstraints constraints) throws ReservationException{
        Quote res = null;
        for(CarRentalCompany crc : companyList){
            try{
                res = crc.createQuote(constraints, client);
                quotes.add(res);
            } catch(ReservationException e){}
        }
        if(res == null) throw new ReservationException("Quote not possible.");
        return res;
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
                CarRentalCompany crc = entMan.find(CarRentalCompany.class, quote.getRentalCompany());
                if(crc != null){
                    Reservation res = crc.confirmQuote(quote);
                    entMan.persist(res);
                    done.add(res);
                }
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