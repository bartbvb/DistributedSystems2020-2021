package ds.gae.entities;

import java.util.Objects;

import com.google.cloud.datastore.*;

public class CarType {

    private Datastore datastore;
    private Key key;

    private String name;
    private int nbOfSeats;
    private boolean smokingAllowed;
    private double rentalPricePerDay;
    // trunk space in liters
    private float trunkSpace;

    /***************
     * CONSTRUCTOR *
     ***************/

    public CarType(
            String name,
            int nbOfSeats,
            float trunkSpace,
            double rentalPricePerDay,
            boolean smokingAllowed) {
        this.name = name;
        this.nbOfSeats = nbOfSeats;
        this.trunkSpace = trunkSpace;
        this.rentalPricePerDay = rentalPricePerDay;
        this.smokingAllowed = smokingAllowed;
        datastore = DatastoreOptions.getDefaultInstance().getService();
        createEntity();
    }
    
    public CarType(Entity entity) {
    	this.load(entity);
    }

    public Key getKey(){
        if(key != null) return key;
        key = datastore.newKeyFactory().setKind("CarType").newKey(name);
        return key;
    }

    private void createEntity(){
        Entity entity = Entity.newBuilder(getKey())
                .set("name", name)
                .set("nbOfSeats",nbOfSeats)
                .set("trunkSpace",trunkSpace)
                .set("rentalPricePerDay",rentalPricePerDay)
                .set("smokingAllowed",smokingAllowed)
                .build();
        datastore.put(entity);
    }

    public Entity getEntity(){
        return datastore.get(key);
    }

    public void load(Entity ent){
        this.name = ent.getString("name");
        this.nbOfSeats = (int)ent.getLong("nbOfSeats");
        this.trunkSpace = (float)ent.getDouble("trunkSpace");
        this.rentalPricePerDay = ent.getDouble("rentalPricePerDay");
        this.smokingAllowed = ent.getBoolean("smokingAllowed");
    }

    public String getName() {
        return name;
    }

    public int getNbOfSeats() {
        return nbOfSeats;
    }

    public boolean isSmokingAllowed() {
        return smokingAllowed;
    }

    public double getRentalPricePerDay() {
        return rentalPricePerDay;
    }

    public float getTrunkSpace() {
        return trunkSpace;
    }

    /*************
     * TO STRING *
     *************/

    @Override
    public String toString() {
        return String.format(
                "Car type: %s \t[seats: %d, price: %.2f, smoking: %b, trunk: %.0fl]",
                getName(),
                getNbOfSeats(),
                getRentalPricePerDay(),
                isSmokingAllowed(),
                getTrunkSpace()
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
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
        CarType other = (CarType) obj;
        if (!Objects.equals(name, other.name)) {
            return false;
        }
        return true;
    }
}
