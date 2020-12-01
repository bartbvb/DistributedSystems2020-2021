package ds.gae.entities;

import com.google.cloud.datastore.*;

import java.util.Date;
import java.util.Objects;

import static com.google.appengine.api.search.DateUtil.deserializeDate;
import static com.google.appengine.api.search.DateUtil.serializeDate;

public class Reservation extends Quote {

    private Key key;
    private int carId;

    /***************
     * CONSTRUCTOR *
     ***************/

    public Reservation(Quote quote, int carId) {
        this(
                quote.getRenter(),
                quote.getStartDate(),
                quote.getEndDate(),
                quote.getRentalCompany(),
                quote.getCarType(),
                quote.getRentalPrice()
        );
        this.carId = carId;
        createEntity();
    }
    public Reservation(Entity ent){
        super(ent);
        this.load(ent);
    }
    /*
    public Reservation(Entity entity) {
        this(entity.getString("renter"), 
        		deserializeDate(entity.getString("startDate")), 
				deserializeDate(entity.getString("endDate")), 
				entity.getString("rentalCompany"), 
				entity.getString("carType"), 
				entity.getDouble("rentalPrice"));
        this.carId = Math.toIntExact(entity.getLong("carId"));
    }
*/

    private Reservation(
            String renter,
            Date start,
            Date end,
            String rentalCompany,
            String carType,
            double rentalPrice) {
        super(renter, start, end, rentalCompany, carType, rentalPrice);
    }

    /******
     * ID *
     ******/

    public int getCarId() {
        return carId;
    }

    @Override
    public Key getKey(){
        if(key != null) return key;
        KeyFactory keyFactory = datastore.newKeyFactory().addAncestor(PathElement.of("Car", carId)).setKind("Reservation");
        key = datastore.allocateId(keyFactory.newKey());
        return key;
    }
    @Override
    public void createEntity(){
        Entity entity = Entity.newBuilder(getKey())
                .set("renter", super.getRenter())
                .set("startDate", serializeDate(super.getStartDate()))
                .set("endDate", serializeDate(super.getEndDate()))
                .set("rentalCompany",super.getRentalCompany())
                .set("carType",super.getCarType())
                .set("rentalPrice",super.getRentalPrice())
                .set("carId",carId)
                .build();
        datastore.put(entity);
    }
    @Override
    public Entity getEntity(){
        return datastore.get(getKey());
    }
    @Override
    public void load(Entity ent){
        super.load(ent);
        this.carId = (int)ent.getLong("carId");
    }

    /*************
     * TO STRING *
     *************/

    @Override
    public String toString() {
        return String.format(
                "Reservation for %s from %s to %s at %s\nCar type: %s\tCar: %s\nTotal price: %.2f",
                getRenter(),
                getStartDate(),
                getEndDate(),
                getRentalCompany(),
                getCarType(),
                getCarId(),
                getRentalPrice()
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getCarId());
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        Reservation other = (Reservation) obj;
        if (getCarId() != other.getCarId()) {
            return false;
        }
        return true;
    }
}
