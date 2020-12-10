package ds.gae.entities;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.cloud.datastore.*;
import ds.gae.ReservationException;

public class CarRentalCompany {

    private static final Logger logger = Logger.getLogger(CarRentalCompany.class.getName());

    private Datastore datastore;
    private Key key;
    private String name;
    private Set<Car> cars;
    private Map<String, CarType> carTypes = new HashMap<>();

    /***************
     * CONSTRUCTOR *
     ***************/

    public CarRentalCompany(String name, Set<Car> cars) {
        setName(name);
        this.cars = cars;
        for(Car car : cars) {
            carTypes.put(car.getType().getName(), car.getType());
            car.setRentalCompany(name);
        }
        datastore = DatastoreOptions.getDefaultInstance().getService();
        createEntity();
    }

    public CarRentalCompany(Entity entity){
        datastore = DatastoreOptions.getDefaultInstance().getService();
        load(entity);
    }

    public Key getKey(){
        if(key != null) return key;
        key = datastore.newKeyFactory().setKind("CarRentalCompany").newKey(name);
        return key;
    }
    private void createEntity(){
        Entity entity = Entity.newBuilder(getKey())
                .set("name", name)
                .build();
        datastore.put(entity);
    }

    public Entity getEntity(){
        return datastore.get(getKey());
    }

    public void load(Entity ent){
        this.name = ent.getString("name");
        Query<Entity> query = Query.newEntityQueryBuilder()
                .setKind("Car")
                .setFilter(StructuredQuery.PropertyFilter.hasAncestor(ent.getKey()))
                .build();
        QueryResults<Entity> results = datastore.run(query);
        Set<Car> cars = new HashSet<>();

        while(results.hasNext()) {
            Car car = new Car(results.next());
            carTypes.put(car.getType().getName(), car.getType());
            car.setRentalCompany(this.name);
            cars.add(car);
        }

        this.cars = cars;
    }

    /********
     * NAME *
     ********/

    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }

    /*************
     * CAR TYPES *
     *************/

    public Collection<CarType> getAllCarTypes() {
        return carTypes.values();
    }

    public CarType getCarType(String carTypeName) {
        return carTypes.get(carTypeName);
    }

    public boolean isAvailable(String carTypeName, Date start, Date end) {
        logger.log(Level.INFO, "<{0}> Checking availability for car type {1}", new Object[] { name, carTypeName });
        return getAvailableCarTypes(start, end).contains(getCarType(carTypeName));
    }

    public Set<CarType> getAvailableCarTypes(Date start, Date end) {
        Set<CarType> availableCarTypes = new HashSet<>();
        for (Car car : getCars()) {
            if (car.isAvailable(start, end)) {
                availableCarTypes.add(car.getType());
            }
        }
        return availableCarTypes;
    }

    /*********
     * CARS *
     *********/

    private Car getCar(int uid) {
        try {
            Key carKey = datastore.newKeyFactory()
                    .addAncestor(PathElement.of("CarRentalCompany", this.getName()))
                    .setKind("Car").newKey(uid);

            return new Car(datastore.get(carKey));
        }catch(IllegalArgumentException e){
            throw new IllegalArgumentException("<" + name + "> No car with uid " + uid);
        }
        /*for (Car car : cars) {
            if (car.getId() == uid) {
                return car;
            }
        }
        throw new IllegalArgumentException("<" + name + "> No car with uid " + uid);*/
    }

    public Set<Car> getCars() {
        Query<Entity> query = Query.newEntityQueryBuilder()
                .setKind("Car")
                .setFilter(StructuredQuery.PropertyFilter.hasAncestor(datastore.newKeyFactory().setKind("CarRentalCompany").newKey(this.getName())))
                .build();
        QueryResults<Entity> results = datastore.run(query);

        while(results.hasNext()) {
            Entity car = results.next();
            cars.add(new Car(car));
        }

        return cars;
    }

    private List<Car> getAvailableCars(String carType, Date start, Date end) {
        List<Car> availableCars = new LinkedList<>();
        cars = getCars();
        for (Car car : cars) {
            if (car.getType().getName().equals(carType) && car.isAvailable(start, end)) {
                availableCars.add(car);
            }
        }
        return availableCars;
    }

    /****************
     * RESERVATIONS *
     ****************/

    public Quote createQuote(ReservationConstraints constraints, String client) throws ReservationException {
        logger.log(Level.INFO, "<{0}> Creating tentative reservation for {1} with constraints {2}",
                new Object[] { name, client, constraints.toString() });

        CarType type = getCarType(constraints.getCarType());

        if (!isAvailable(constraints.getCarType(), constraints.getStartDate(), constraints.getEndDate())) {
            throw new ReservationException("<" + name + "> No cars available to satisfy the given constraints.");
        }

        double price = calculateRentalPrice(
                type.getRentalPricePerDay(),
                constraints.getStartDate(),
                constraints.getEndDate()
        );
        Quote q = new Quote(
                client,
                constraints.getStartDate(),
                constraints.getEndDate(),
                getName(),
                constraints.getCarType(),
                price
        );
        q.createEntity();
        return q;
    }

    // Implementation can be subject to different pricing strategies
    private double calculateRentalPrice(double rentalPricePerDay, Date start, Date end) {
        return rentalPricePerDay * Math.ceil((end.getTime() - start.getTime()) / (1000 * 60 * 60 * 24D));
    }

    public Reservation confirmQuote(Quote quote) throws ReservationException {
        logger.log(Level.INFO, "<{0}> Reservation of {1}", new Object[] { name, quote.toString() });
        List<Car> availableCars = getAvailableCars(quote.getCarType(), quote.getStartDate(), quote.getEndDate());
        if (availableCars.isEmpty()) {
            throw new ReservationException("Reservation failed, all cars of type " + quote.getCarType()
                    + " are unavailable from " + quote.getStartDate() + " to " + quote.getEndDate());
        }
        Car car = availableCars.get((int) (Math.random() * availableCars.size()));
        Reservation res = new Reservation(quote, car.getId());
        car.addReservation(res);
        return res;
        /*
        Transaction tx = datastore.newTransaction();
        try {
            Reservation res = new Reservation(quote, car.getId());
            car.addReservation(res);
            tx.commit();
            return res;
        }finally {
            if(tx.isActive()){
                tx.rollback();
            }
        }*/
    }

    public void cancelReservation(Reservation res) {
        logger.log(Level.INFO, "<{0}> Cancelling reservation {1}", new Object[] { name, res.toString() });
        getCar(res.getCarId()).removeReservation(res);
    }
}
