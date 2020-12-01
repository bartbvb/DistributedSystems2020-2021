package ds.gae;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.cloud.datastore.*;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;

import ds.gae.entities.Car;
import ds.gae.entities.CarRentalCompany;
import ds.gae.entities.CarType;
import ds.gae.entities.Quote;
import ds.gae.entities.Reservation;
import ds.gae.entities.ReservationConstraints;

public class CarRentalModel {

    private static CarRentalModel instance;

    private Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

    public static CarRentalModel get() {
        if (instance == null) {
            instance = new CarRentalModel();
        }
        return instance;
    }

    /**
     * Get the car types available in the given car rental company.
     *
     * @param companyName the car rental company
     * @return The list of car types (i.e. name of car type), available in the given
     * car rental company.
     */
    public Set<String> getCarTypesNames(String companyName) {
        CarRentalCompany company = getCRC(companyName);
        return company.getAllCarTypes().stream().map(e -> e.getName()).collect(Collectors.toSet());
    }

    private CarRentalCompany getCRC(String name){
        Query<Entity> query = Query.newEntityQueryBuilder()
                .setKind("CarRentalCompany")
                .setFilter(StructuredQuery.PropertyFilter.eq("name",name))
                .build();
        QueryResults<Entity> results = datastore.run(query);

        CarRentalCompany crc = new CarRentalCompany(results.next());
        return crc;
    }

    /**
     * Get the names of all registered car rental companies
     *
     * @return the list of car rental companies
     */
    public Collection<String> getAllRentalCompanyNames() {
        Query<Entity> query = Query.newEntityQueryBuilder()
                .setKind("CarRentalCompany")
                .build();
        QueryResults<Entity> results = datastore.run(query);
        ArrayList<String> res = new ArrayList<>();
        for (QueryResults<Entity> it = results; it.hasNext(); ) {
            Entity e = it.next();
            res.add(e.getString("name"));
        }
        return res;
    }

    /**
     * Create a quote according to the given reservation constraints (tentative
     * reservation).
     *
     * @param companyName name of the car renter company
     * @param renterName  name of the car renter
     * @param constraints reservation constraints for the quote
     * @return The newly created quote.
     * @throws ReservationException No car available that fits the given
     *                              constraints.
     */
    public Quote createQuote(String companyName, String renterName, ReservationConstraints constraints)
            throws ReservationException {
        CarRentalCompany crc = getCRC(companyName);
        return crc.createQuote(constraints, renterName);
    }

    /**
     * Confirm the given quote.
     *
     * @param quote Quote to confirm
     * @throws ReservationException Confirmation of given quote failed.
     */
    public Reservation confirmQuote(Quote quote) throws ReservationException {
        CarRentalCompany crc = getCRC(quote.getRentalCompany());
        Reservation res = crc.confirmQuote(quote);
        return res;
    }

    /**
     * Confirm the given list of quotes
     *
     * @param quotes the quotes to confirm
     * @return The list of reservations, resulting from confirming all given quotes.
     * @throws ReservationException One of the quotes cannot be confirmed. Therefore
     *                              none of the given quotes is confirmed.
     */
    @SuppressWarnings("finally")
	public List<Reservation> confirmQuotes(List<Quote> quotes) throws ReservationException {
        Transaction tx = datastore.newTransaction();
        List<Entity> list = new ArrayList<>();
        List<Reservation> result = new ArrayList<>();
        for(Quote quote: quotes) {
        	Reservation res = confirmQuote(quote);
    		list.add(res.getEntity());
    		result.add(res);
    	}
        try {
        	for(Entity ent: list) {
        		tx.add(ent);
        		tx.commit();
        	}
        }
        finally {
        	if(tx.isActive()) {
        		tx.rollback();
        		throw new ReservationException("couldn't confirm all quotes");
        	} else {
        		return result;
        	}
        }
    }

    /**
     * Get all reservations made by the given car renter.
     *
     * @param renter name of the car renter
     * @return the list of reservations of the given car renter
     */
    public List<Reservation> getReservations(String renter) {
        List<Reservation> out = new ArrayList<>();
        Query<Entity> query = Query.newEntityQueryBuilder()
                .setKind("Reservation")
                .setFilter(StructuredQuery.PropertyFilter.eq("renter",renter))
                .build();
        QueryResults<Entity> results = datastore.run(query);
        for (QueryResults<Entity> it = results; it.hasNext(); ) {
            Entity e = it.next();
            out.add(new Reservation(e));
        }
        return out;
    }

    /**
     * Get the car types available in the given car rental company.
     *
     * @param companyName the given car rental company
     * @return The list of car types in the given car rental company.
     */
    public Collection<CarType> getCarTypesOfCarRentalCompany(String companyName) {
        CarRentalCompany crc = getCRC(companyName);
        Collection<CarType> out = new ArrayList<>(crc.getAllCarTypes());
        return out;
    }

    /**
     * Get the list of cars of the given car type in the given car rental company.
     *
     * @param companyName name of the car rental company
     * @param carType     the given car type
     * @return A list of car IDs of cars with the given car type.
     */
    public Collection<Integer> getCarIdsByCarType(String companyName, CarType carType) {
        Collection<Integer> out = new ArrayList<>();
        for (Car c : getCarsByCarType(companyName, carType)) {
            out.add(c.getId());
        }
        return out;
    }

    /**
     * Get the amount of cars of the given car type in the given car rental company.
     *
     * @param companyName name of the car rental company
     * @param carType     the given car type
     * @return A number, representing the amount of cars of the given car type.
     */
    public int getAmountOfCarsByCarType(String companyName, CarType carType) {
        return this.getCarsByCarType(companyName, carType).size();
    }

    /**
     * Get the list of cars of the given car type in the given car rental company.
     *
     * @param companyName name of the car rental company
     * @param carType     the given car type
     * @return List of cars of the given car type
     */
    private List<Car> getCarsByCarType(String companyName, CarType carType) {
        List<Car> out = new ArrayList<>();
        Key compkey = datastore.newKeyFactory().setKind("CarRentalCompany").newKey(companyName);
        Query<Entity> query = Query.newEntityQueryBuilder()
        		.setKind("Car")
        		.setFilter(PropertyFilter.hasAncestor(compkey))
        		.setFilter(PropertyFilter.eq("carType", carType.getName()))
        		.build();
        
        QueryResults<Entity> results = datastore.run(query);
    	    	
    	while(results.hasNext()) {
    		Car car = new Car(results.next());
    		out.add(car);
    	}
        
        return out;

    }

    /**
     * Check whether the given car renter has reservations.
     *
     * @param renter the car renter
     * @return True if the number of reservations of the given car renter is higher
     * than 0. False otherwise.
     */
    public boolean hasReservations(String renter) {
        return this.getReservations(renter).size() > 0;
    }
}
