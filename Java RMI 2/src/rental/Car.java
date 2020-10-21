package rental;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class Car {

    private int id;
    private CarType type;
    private List<Reservation> reservations;

    /***************
     * CONSTRUCTOR *
     ***************/
    
    public Car(int uid, CarType type) {
    	this.id = uid;
        this.type = type;
        this.reservations = new ArrayList<Reservation>();
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
        return type;
    }

    /****************
     * RESERVATIONS *
     ****************/

    public boolean isAvailable(Date start, Date end) {
        if(!start.before(end))
            throw new IllegalArgumentException("Illegal given period");

        for(Reservation reservation : reservations) {
            if(reservation.getEndDate().before(start) || reservation.getStartDate().after(end))
                continue;
            return false;
        }
        return true;
    }
    
    public void addReservation(Reservation res) {
        reservations.add(res);
    }
    
    public void removeReservation(Reservation reservation) {
        // equals-method for Reservation is required!
        reservations.remove(reservation);
    }
    
    public List<Reservation> getReservationsByRenter(String clientName){
		List<Reservation> reserv = new ArrayList<Reservation>();

    	for(Reservation res: reservations) {
    		if(res.getCarRenter().equals(clientName))
    		    reserv.add(res);
    	}
    	return reserv;
    }

    public int getNrOfReservationsByYear(int year){
        List<Reservation> reservationsInYear = reservations.stream().filter(c -> c.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear() == year).collect(Collectors.toList());
        return reservationsInYear.size();
    }
    
    public int getNrOfReservations(){
    	return reservations.size();
    }
}