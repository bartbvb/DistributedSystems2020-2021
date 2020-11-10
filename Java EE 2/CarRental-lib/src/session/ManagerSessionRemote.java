package session;

import java.util.List;
import java.util.Set;
import javax.ejb.Remote;
import rental.CarType;
import rental.Reservation;

@Remote
public interface ManagerSessionRemote {
    
    public Set<CarType> getCarTypes(String company);
    
    public Set<Integer> getCarIds(String company,String type);
    
    public int getNumberOfReservations(String company, String type, int carId);
    
    public int getNumberOfReservations(String company, String type);
      
    public void createCarRentalCompany(String name, List<String> regions, List<String> car);
    
    public void addRegionList(String name, List<String> regions);
    
    public void addCarTypeList(String name, List<String> types);
    
    public void addCarList(String name, List<String> car);
}