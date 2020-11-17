package session;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import rental.Car;
import rental.CarRentalCompany;
import rental.CarType;
import rental.RentalStore;
import rental.Reservation;

@Stateless
public class ManagerSession implements ManagerSessionRemote {
    
    @PersistenceContext
    EntityManager entMan;
    
    @Override
    public Set<String> getAllRentalCompanies() {
        List<String> companies = entMan.createQuery("SELECT CRC.name FROM CarRentalCompany CRC").getResultList();
        return new HashSet<>(companies);
    }
    
    @Override
    public Set<CarType> getCarTypes(String company) {
        try {
            return new HashSet<CarType>(RentalStore.getRental(company).getAllTypes());
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public Set<Integer> getCarIds(String company, String type) {
        List<Integer> carIds = entMan.createQuery("SELECT C.id FROM CarRentalCompany as CRC JOIN CRC.Cars C JOIN CRC.carTypes CT WHERE CRC.name = " + company + " AND CT.name = " + type).getResultList();
        return new HashSet<>(carIds);
    }

    @Override
    public int getNumberOfReservations(String company, String type, int id) {
        try {
            return RentalStore.getRental(company).getCar(id).getReservations().size();
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
    }

    @Override
    public int getNumberOfReservations(String company, String type) {
        /*Set<Reservation> out = new HashSet<Reservation>();
        try {
            for(Car c: RentalStore.getRental(company).getCars(type)){
                out.addAll(c.getReservations());
            }
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }*/
        List results = entMan.createQuery("SELECT count(R) FROM Reservation R WHERE R.rentalCompany = " + company + " AND R.carType = " + type).getResultList();
        if(results.isEmpty()) return 0;
        return (int)results.get(0);
    }
    
    @Override
    public int getNumberOfReservations(String client) {
        int reservations = 0;
        Map<String, CarRentalCompany> rentals = RentalStore.getRentals();
        for(CarRentalCompany company: rentals.values()){
            reservations += company.getReservationsBy(client).size();
        }
        
        return reservations;
    }

    @Override
    public void createCarRentalCompany(String name) {
        entMan.persist(new CarRentalCompany(name));
    }

    @Override
    public void addRegionList(String name, List<String> regions) {
        CarRentalCompany company = entMan.find(CarRentalCompany.class,name);
        company.addRegions(regions);
        entMan.persist(company);
    }

    @Override
    public void addCarTypeList(String name, List<String> types) {
        CarRentalCompany company = entMan.find(CarRentalCompany.class,name);
        for(String type : types){
            company.addCarType(entMan.find(CarType.class, type));
        }
        entMan.persist(company);
    }

    @Override
    public void addCarList(String name, List<String> cars) {
        CarRentalCompany company = entMan.find(CarRentalCompany.class,name);
        for(String car : cars){
            //Car c = entMan.createQuery("ManagerSession.addCar",Car.class).setParameter("id", Long.parseLong(car)).getSingleResult();
            Car c = entMan.createQuery("SELECT C FROM Car C WHERE C.id = " + Long.parseLong(car),Car.class).getSingleResult();
            company.addCar(c);
        }
        entMan.persist(company);
    }

    @Override
    public String createCarType(String id, int nbSeats, float trunkSpace, double rentalPD, boolean smoke) {
        //If one already exists, return it
        if(entMan.find(CarType.class, id) != null){
            return id;
        }
        //Else make it, persist and return
        CarType type = new CarType(id,nbSeats,trunkSpace, rentalPD, smoke);
        entMan.persist(type);
        return type.getName();
    }

    @Override
    public String createCar(String type) {
        Car car = new Car(entMan.find(CarType.class, type));
        entMan.persist(car);
        return Integer.toString(car.getId());
    }

    

    

}