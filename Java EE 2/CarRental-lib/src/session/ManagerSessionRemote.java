package session;

import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.ejb.Remote;
import rental.CarType;
import rental.Reservation;

@Remote
public interface ManagerSessionRemote {
    
    public Set<String> getAllRentalCompanies();
    
    public Set<CarType> getCarTypes(String company);
    
    public Set<Integer> getCarIds(String company,String type);
    
    public Set<CarType> getAvailableCarTypes(Date start, Date End);
    
    public CarType getMostPopularCarType(String company, int year);
    
    public int getNumberOfReservations(String company, String type, int carId);
    
    public int getNumberOfReservations(String company, String type);
    
    public int getNumberOfReservations(String client);
      
    public void createCarRentalCompany(String name);
    
    public void addRegionList(String name, List<String> regions);
    
    public void addCarTypeList(String name, List<String> types);
    
    public void addCarList(String name, List<String> cars);
    
    public String createCarType(String id, int nbSeats, float trunkSpace, double rentalPD, boolean smoke);
    
    public String createCar(String type);
}