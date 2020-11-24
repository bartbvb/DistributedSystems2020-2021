/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import javax.ejb.Remote;
import rental.CarType;

@Remote
public interface ManagerSessionRemote {
    
    int getNrOfReservationsForCarType(String carRentalName, String carType);
    int getNrOfReservationsByClient(String user);
    
}
