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
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
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
        List<Integer> carIds = entMan.createQuery("SELECT C.id FROM CarRentalCompany as CRC JOIN CRC.Cars C JOIN CRC.carTypes CT WHERE CRC.name = " + company + " AND CT.name = " + type).getResultList();
        return new HashSet<>(carIds);
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
        List results = entMan.createQuery("SELECT count(R) FROM Reservation R WHERE R.rentalCompany = " + company + " AND R.carType = " + type).getResultList();
        if(results.isEmpty()) return 0;
        return (int)results.get(0);
    }
    
    @Override
    public int getNumberOfReservations(String client) {
        /*int reservations = 0;
        Map<String, CarRentalCompany> rentals = RentalStore.getRentals();
        for(CarRentalCompany company: rentals.values()){
            reservations += company.getReservationsBy(client).size();
        }*/
        List results = entMan.createQuery("SELECT count(R) FROM Reservation R WHERE R.carRenter = " + client).getResultList();
        if(results.isEmpty()) return 0;
        return (int)results.get(0);
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

    @Override
    public Set<CarType> getAvailableCarTypes(Date start, Date End) {
        try {
            String q = "SELECT c.type FROM Car c WHERE NOT ((c.reservations.startDate > :start AND c.reservations.startDate < :end) OR (c.reservations.endDate > :start AND c.reservations.endDate < :end))";
            Query quer = entMan.createQuery(q);
            quer.setParameter("start", start);
            quer.setParameter("end", End);
            List<CarType> typeList = quer.getResultList();
            Set<CarType> res = new HashSet<CarType>(typeList);
            return res;

        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }    
    }

    @Override
    public CarType getMostPopularCarType(String company, int year) {
        String q = "SELECT c.type FROM Car c WHERE NOT (c.reservations.startDate > :start AND c.reservations.rentalCompany = company)";
            Query quer = entMan.createQuery("SELECT c.type FROM Car c WHERE NOT ((c.reservations.startDate > :start AND c.reservations.rentalCompany = :company) OR (c.reservations.startDate < :start AND c.reservations.rentalCompany = :company))");
            quer.setParameter("company", company);
            quer.setParameter("start", year);
            List<CarType> res = quer.getResultList();
            
            return sortByFrequency(res).get(0);
    }
    
    private List<CarType> sortByFrequency(List<CarType> inputList){
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
    }

    
    public Set<String> getBestClient() {
        try {
            Query quer = entMan.createQuery("SELECT comp.name, res.carRenter, COUNT(res.carRenter) AS occur FROM CarRentalCompany comp, comp.cars.reservations res ORDER BY occur Desc");
            quer.setMaxResults(1);
            quer.getResultList();
            List typeList = quer.getResultList();
            Set<String> res = new HashSet<>(typeList);
            return res;

        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }    
    }

    


}