package rental;

import static javax.persistence.CascadeType.REMOVE;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "reservations")
public class Reservation extends Quote {
    //@ManyToOne(cascade=REMOVE)
    private int carId;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    /***************
     * CONSTRUCTOR *
     ***************/

    public Reservation(){
    super(null, null,null,null,null,0);
    }
    
    public Reservation(Quote quote, int carId) {
    	super(quote.getCarRenter(), quote.getStartDate(), quote.getEndDate(), 
    		quote.getRentalCompany(), quote.getCarType(), quote.getRentalPrice());
        this.carId = carId;
    }
    
    /******
     * ID *
     ******/
    public int getCarId() {
    	return carId;
    }
    
    public void setCarId(int carId){
    this.carId = carId;
    }
    
    /*************
     * TO STRING *
     *************/
    
    @Override
    public String toString() {
        return String.format("Reservation for %s from %s to %s at %s\nCar type: %s\tCar: %s\nTotal price: %.2f", 
                getCarRenter(), getStartDate(), getEndDate(), getRentalCompany(), getCarType(), getCarId(), getRentalPrice());
    }	

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}