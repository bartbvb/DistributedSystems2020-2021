package ds.gae.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.List;


import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.EmbeddedEntity;
import com.google.appengine.repackaged.com.google.datastore.v1.client.DatastoreOptions;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;

public class Car {

    private int id;
    private CarType carType;
    private Set<Reservation> reservations;

    /***************
     * CONSTRUCTOR *
     ***************/

    public Car(int uid, CarType carType) {
        this.id = uid;
        this.carType = carType;
        this.reservations = new HashSet<Reservation>();
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
    	Datastore store = com.google.cloud.datastore.DatastoreOptions.getDefaultInstance().getService();
    	
    	Key carkey = store.newKeyFactory().setKind("car").newKey(id);
    			Entity car = Entity.newBuilder(carkey)
    			.set("carType", this.carType.getName())
    			.build();
		return car;
    }
    
    public void saveCar() {
    	Datastore store = com.google.cloud.datastore.DatastoreOptions.getDefaultInstance().getService();
    	store.put(getGaeEntity());
    }
    
    //TODO add a load function and add the set to the gaeEntity
}
