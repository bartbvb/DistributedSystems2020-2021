package ds.gae.entities;

import com.google.cloud.datastore.*;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.google.appengine.api.search.DateUtil.deserializeDate;
import static com.google.appengine.api.search.DateUtil.serializeDate;

public class Quote {

    protected Datastore datastore;
    private Key key;

    private Date startDate;
    private Date endDate;
    private String renter;
    private String rentalCompany;
    private String carType;
    private double rentalPrice;

    /***************
     * CONSTRUCTOR *
     ***************/
    Quote(String renter, Date start, Date end, String rentalCompany, String carType, double rentalPrice) {
        this.renter = renter;
        this.startDate = start;
        this.endDate = end;
        this.rentalCompany = rentalCompany;
        this.carType = carType;
        this.rentalPrice = rentalPrice;
        datastore = DatastoreOptions.getDefaultInstance().getService();
    }
    Quote(Entity ent){
        datastore = DatastoreOptions.getDefaultInstance().getService();
        load(ent);
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public String getRenter() {
        return renter;
    }

    public String getRentalCompany() {
        return rentalCompany;
    }

    public double getRentalPrice() {
        return rentalPrice;
    }

    public String getCarType() {
        return carType;
    }

    public Key getKey(){
        if(key != null) return key;
        KeyFactory keyFactory = datastore.newKeyFactory().setKind("Quote");
        key = datastore.allocateId(keyFactory.newKey());
        return key;
    }

    public void createEntity(){
        Entity entity = Entity.newBuilder(getKey())
                .set("renter", renter)
                .set("startDate", serializeDate(startDate))
                .set("endDate", serializeDate(endDate))
                .set("rentalCompany",rentalCompany)
                .set("carType",carType)
                .set("rentalPrice",rentalPrice)
                .build();
        datastore.put(entity);
    }

    public Entity getEntity(){
        return datastore.get(getKey());
    }

    public void load(Entity ent){
        this.renter = ent.getString("renter");
        this.startDate = deserializeDate(ent.getString("startDate"));
        this.endDate = deserializeDate(ent.getString("endDate"));
        this.rentalCompany = ent.getString("rentalCompany");
        this.carType = ent.getString("carType");
        this.rentalPrice = ent.getDouble("rentalPrice");
    }

    /*************
     * TO STRING *
     *************/

    @Override
    public String toString() {
        return String.format(
                "Quote for %s from %s to %s at %s\nCar type: %s\tTotal price: %.2f",
                getRenter(),
                getStartDate(),
                getEndDate(),
                getRentalCompany(),
                getCarType(),
                getRentalPrice()
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                getRenter(),
                getStartDate(),
                getEndDate(),
                getRentalCompany(),
                getCarType(),
                getRentalPrice()
        );
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Quote other = (Quote) obj;
        if (!Objects.equals(renter, other.renter)) {
            return false;
        }
        if (!Objects.equals(startDate, other.startDate)) {
            return false;
        }
        if (!Objects.equals(endDate, other.endDate)) {
            return false;
        }
        if (!Objects.equals(rentalCompany, other.rentalCompany)) {
            return false;
        }
        if (!Objects.equals(carType, other.carType)) {
            return false;
        }
        if (Double.doubleToLongBits(rentalPrice) != Double.doubleToLongBits(other.rentalPrice)) {
            return false;
        }
        return true;
    }
}
