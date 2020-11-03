/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import static java.lang.reflect.Array.set;
import java.util.List;
import java.util.Map;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import rental.*;

/**
 *
 * @author osboxes
 */
@Stateless
public class ManagerSession implements ManagerSessionRemote{


    public int getNrOfReservationsByClient(String user) {
        int reservations = 0;
        Map<String, CarRentalCompany> rentals = RentalStore.getRentals();
        for(CarRentalCompany company: rentals.values()){
            reservations += company.getReservationsBy(user).size();
        }
        
        return reservations;
    }

    @Override
    public int getNrOfReservationsForCarType(String carRentalName, String carType) {
        int reservations = 0;
        
        CarRentalCompany comp = RentalStore.getRental(carRentalName);
        
        List<Car> cars = comp.getCars();
            for(Car car: cars){
                if(car.getType().getName().equals(carType)){
                    reservations += car.getAllReservations().size();
                }
            }
            return reservations;
    }
}
