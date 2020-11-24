package ds.gae.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.List;



import com.google.appengine.repackaged.com.google.datastore.v1.client.DatastoreOptions;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;

public class Car {

    private int id;
    private CarType carType;
    private Set<Reservation> reservations;
    private Datastore datastore;

    /***************
     * CONSTRUCTOR *
     ***************/

    public Car(int uid, CarType carType) {
        this.id = uid;
        this.carType = carType;
        this.reservations = new HashSet<Reservation>();
    	datastore = com.google.cloud.datastore.DatastoreOptions.getDefaultInstance().getService();
    }
    
    public Car(Entity entity) {
    	this.load(entity);
    }

    /******
     * ID *
     ******/

    public int getId() {
        return id;
    }

    /************
     * CAR TYPE *
     ************/

    public CarType getType() {
        return carType;
    }

    /****************
     * RESERVATIONS *
     ****************/

    public Set<Reservation> getReservations() {
        return reservations;
    }

    public boolean isAvailable(Date start, Date end) {
        if (!start.before(end)) {
            throw new IllegalArgumentException("Illegal given period");
        }

        for (Reservation reservation : getReservations()) {
            if (reservation.getEndDate().before(start) || reservation.getStartDate().after(end)) {
                continue;
            }
            return false;
        }
        return true;
    }

    public void addReservation(Reservation res) {
        reservations.add(res);
    }

    public void removeReservation(Reservation reservation) {
        reservations.remove(reservation);
    }
    
    public Entity getGaeEntity(){    	
    	Key carkey = datastore.newKeyFactory().setKind("Car").newKey(id);
    			Entity car = Entity.newBuilder(carkey)
    			.set("carType", this.carType.getName())
    			.build();
		return car;
    }
    
    public void saveCar() {
    	datastore.put(getGaeEntity());
    }
    
    public void load(Entity entity){
    	this.id = Math.toIntExact(entity.getKey().getId());
    	Key carTypeKey = datastore.newKeyFactory().setKind("carType").newKey(entity.getString("carType"));
    	this.carType = new CarType(datastore.get(carTypeKey));
    	Query<Entity> query = Query.newEntityQueryBuilder()
    			.setKind("Reservation")
    			.setFilter(PropertyFilter.hasAncestor(entity.getKey()))
    			.build();
    	QueryResults<Entity> results = datastore.run(query);
    	
    	Set<Reservation> reservations = new HashSet<>();
    	
    	while(results.hasNext()) {
    		Reservation reservation = new Reservation(results.next());
    		reservations.add(reservation);
    	}
    	
    	this.reservations = reservations;
	}
    
}
