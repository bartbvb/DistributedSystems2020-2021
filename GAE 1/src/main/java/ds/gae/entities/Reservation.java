package ds.gae.entities;

import com.google.cloud.datastore.*;

import java.util.Date;
import java.util.Objects;

import static com.google.appengine.api.search.DateUtil.serializeDate;

public class Reservation extends Quote {

    private Datastore datastore;
    private Key key;
    private Entity entity;
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
        datastore = DatastoreOptions.getDefaultInstance().getService();
    }

    private Reservation(
            String renter,
            Date start,
            Date end,
            String rentalCompany,
            String carType,
            double rentalPrice) {
        super(renter, start, end, rentalCompany, carType, rentalPrice);
        datastore = DatastoreOptions.getDefaultInstance().getService();
    }

    /******
     * ID *
     ******/

    public int getCarId() {
        return carId;
    }

    public Key getKey(){
        if(key != null) return key;
        KeyFactory keyFactory = datastore.newKeyFactory().setKind("Reservation");
        key = datastore.allocateId(keyFactory.newKey());
        return key;
    }

    public Entity getEntity(){
        if(entity != null) return entity;
        entity = Entity.newBuilder(getKey())
                .set("renter", super.getRenter())
                .set("startDate", serializeDate(super.getStartDate()))
                .set("endDate", serializeDate(super.getEndDate()))
                .set("rentalCompany",super.getRentalCompany())
                .set("carType",super.getCarType())
                .set("rentalPrice",super.getRentalPrice())
                .set("carId",carId)
                .build();
        return entity;
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
