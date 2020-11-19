package session;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import rental.Car;
import rental.CarRentalCompany;
import rental.CarType;
import rental.Reservation;

//@DeclareRoles({"manager", "user"})
@Stateless
//@RolesAllowed("manager")
public class ManagerSession implements ManagerSessionRemote {
    
    @PersistenceContext
    EntityManager entMan;
    
    @Override
    public Set<String> getAllRentalCompanies() {     
        try {
            Query quer = entMan.createQuery("SELECT CRC.name FROM CarRentalCompany CRC");
            return new HashSet<>(quer.getResultList());
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
            return new HashSet<>();
        }
    }
    
    @Override
    public Set<CarType> getCarTypes(String company) {
        try {
            //return new HashSet<CarType>(RentalStore.getRental(company).getAllTypes());
            String querry = "SELECT c.carTypes FROM CarRentalCompany c WHERE c.name LIKE :compName";
            List<CarType> typeList = entMan.createQuery(querry).setParameter("compName", company).getResultList();
            Set<CarType> res = new HashSet<CarType>(typeList);
            return res;
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public Set<Integer> getCarIds(String company, String type) {
        try{
            Query quer = entMan.createQuery("SELECT C.id FROM CarRentalCompany as CRC JOIN CRC.Cars C JOIN CRC.carTypes CT WHERE CRC.name = :company AND CT.name = :type");
            quer.setParameter("company", company);
            quer.setParameter("type", type);
            return new HashSet<>(quer.getResultList());
        }catch (IllegalArgumentException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
            return new HashSet<>();
        }
    }

    @Override
    public int getNumberOfReservations(String company, String type, int id) {
        try {
            //return RentalStore.getRental(company).getCar(id).getReservations().size();
            Query quer = entMan.createQuery("SELECT COUNT(res) FROM Reservation res WHERE res.rentalCompany LIKE :compName AND res.carType LIKE :type AND res.carId LIKE :id");
            quer.setParameter("compName", company);
            quer.setParameter("type", type);
            quer.setParameter("id", id);
            return quer.getFirstResult();
            
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
        try{
            Query quer = entMan.createQuery("SELECT count(R) FROM Reservation R WHERE R.rentalCompany = :company AND R.carType = :type");
            quer.setParameter("company", company);
            quer.setParameter("type", type);
            Long res = (Long) quer.getSingleResult();
            return res.intValue();
        }catch (IllegalArgumentException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
    }
    
    @Override
    public int getNumberOfReservations(String client) {
        /*int reservations = 0;
        Map<String, CarRentalCompany> rentals = RentalStore.getRentals();
        for(CarRentalCompany company: rentals.values()){
            reservations += company.getReservationsBy(client).size();
        }*/
        try{
            Query quer = entMan.createQuery("SELECT COUNT(R.carRenter) FROM Reservation R WHERE R.carRenter LIKE :client");
            quer.setParameter("client", client);
            Long res = (Long) quer.getSingleResult();
            return res.intValue();
        }catch (IllegalArgumentException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
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
            Query quer = entMan.createQuery("SELECT C FROM Car C WHERE C.id = :id",Car.class);
            quer.setParameter("id",Long.parseLong(car));
            Car c = (Car) quer.getSingleResult();
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
        System.out.println(car);
        entMan.persist(car); //TODO: SQLIntegrityConstraintViolationException: The statement was aborted because it would have caused a duplicate key value in a unique or primary key constraint or unique index identified by 'SQL201117130156220' defined on 'CAR'.
        return Integer.toString(car.getId());
    }

    @Override
    public CarType getMostPopularCarType(String company, int year) {
            //Query quer = entMan.createQuery("SELECT c.type FROM Car c WHERE c.reservations.rentalCompany = :company AND NOT (c.reservations.startDate > :start OR c.reservations.startDate < :start)");
            Query quer = entMan.createQuery("SELECT res.carType FROM CarRentalCompany comp JOIN comp.cars c JOIN c.reservations res WHERE comp.name = :company AND EXTRACT(YEAR FROM res.startDate) = :start GROUP BY res.carType ORDER BY COUNT(res.id) DESC");
            quer.setParameter("company", company);
            quer.setParameter("start", year);
            quer.setMaxResults(1);
            CarType res = entMan.find(CarType.class,(String)quer.getResultList().get(0));
            
            return res;
    }
    
    /*private List<CarType> sortByFrequency(List<CarType> inputList){
        Map<CarType,Integer> elCountMap = new LinkedHashMap<>();
        
        for(CarType o : inputList){
            int count = elCountMap.getOrDefault(o, 0);
            elCountMap.put(o, count+1);
        }
        
        List<CarType> sorted = new ArrayList<>();
        elCountMap.entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .forEach(entry -> {
                    for(int i = 1; i <= entry.getValue(); i++)
                        sorted.add(entry.getKey());
                    });
        return sorted;
    }*/

    
    public Set<String> getBestClient() {
        try {
            Query quer = entMan.createQuery("SELECT res.carRenter FROM Reservations res GROUP BY res.carRenter ORDER BY COUNT(res.carRenter) DESC");
            List typeList = quer.getResultList();
            Set<String> res = new HashSet<>(typeList);
            return res;

        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
            return new HashSet<String>();
        }    
    }

    


}