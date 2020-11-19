package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import rental.CarType;
import rental.Reservation;
import rental.ReservationConstraints;
import session.ManagerSessionRemote;
import session.ReservationSessionRemote;

public class Main extends AbstractTestManagement<ReservationSessionRemote, ManagerSessionRemote> {

    public Main(String scriptFile) {
        super(scriptFile);
    }

    public static void main(String[] args) throws Exception {
        //System.out.println("Create new Main");
        Main main = new Main("trips");
        
        //System.out.println("Create managersession");
        ManagerSessionRemote managerSession = main.getNewManagerSession("main");
        //System.out.println("Load rentals");
        main.loadRental("dockx.csv",managerSession);
        main.loadRental("hertz.csv",managerSession);
        //System.out.println("Run");
        main.run();
    }
    
    public static void loadRental(String datafile, ManagerSessionRemote managersession) {
        try {
            //System.out.println("Load data");
            CrcData data = loadData(datafile, managersession);
            //System.out.println("Create company");
            managersession.createCarRentalCompany(data.name);
            //System.out.println("Adding regions");
            managersession.addRegionList(data.name, data.regions);
            //System.out.println("Adding carTypes");
            managersession.addCarTypeList(data.name, data.carTypes);
            //System.out.println("Adding carIds");
            managersession.addCarList(data.name, data.carIds);
           
            Logger.getLogger(Main.class.getName()).log(Level.INFO, "Loaded {0} from file {1}", new Object[]{data.name, datafile});
        } catch (NumberFormatException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, "bad file", ex);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    public static CrcData loadData(String datafile, ManagerSessionRemote managersession)
            throws NumberFormatException, IOException {

        CrcData out = new CrcData();
        StringTokenizer csvReader;
        int nextuid = 0;
       
        //open file from jar
        BufferedReader in = new BufferedReader(new InputStreamReader(Main.class.getClassLoader().getResourceAsStream(datafile)));
        
        try {
            while (in.ready()) {
                String line = in.readLine();
                
                if (line.startsWith("#")) {
                    // comment -> skip					
                } else if (line.startsWith("-")) {
                    csvReader = new StringTokenizer(line.substring(1), ",");
                    out.name = csvReader.nextToken();
                    //System.out.println("    Company name: " + out.name);
                    out.regions = Arrays.asList(csvReader.nextToken().split(":"));
                    //System.out.println("    Regions active: " + out.regions.size());
                } else {
                    csvReader = new StringTokenizer(line, ",");
                    //create new car type from first 5 fields
                    String type = managersession.createCarType(csvReader.nextToken(),
                            Integer.parseInt(csvReader.nextToken()),
                            Float.parseFloat(csvReader.nextToken()),
                            Double.parseDouble(csvReader.nextToken()),
                            Boolean.parseBoolean(csvReader.nextToken()));
                    out.carTypes.add(type);
                    //System.out.println("    Car type added: " + type);
                    //create N new cars with given type, where N is the 5th field
                    for (int i = Integer.parseInt(csvReader.nextToken()); i > 0; i--) {
                        String c = managersession.createCar(type);
                        out.carIds.add(c);
                        //System.out.println("    Car added: " + c);
                    }        
                }
            } 
        } finally {
            in.close();
        }

        return out;
    }
    
    static class CrcData {
            public String name;
            public List<String> carTypes =  new LinkedList<String>();
            public List<String> carIds =  new LinkedList<String>();
            public List<String> regions =  new LinkedList<String>();
    }

    @Override
    protected Set<String> getBestClients(ManagerSessionRemote ms) throws Exception {
        Set<String> set = ms.getBestClient();
        System.out.println(set.toString());
        return set;
    }

    @Override
    protected String getCheapestCarType(ReservationSessionRemote session, Date start, Date end, String region) throws Exception {
        return session.getCheapestCar(start, end, region);
    }

    @Override
    protected CarType getMostPopularCarTypeIn(ManagerSessionRemote ms, String carRentalCompanyName, int year) throws Exception {
        return ms.getMostPopularCarType(carRentalCompanyName, year);
    }

    @Override
    protected ReservationSessionRemote getNewReservationSession(String name) throws Exception {
        ReservationSessionRemote r = (ReservationSessionRemote) (new InitialContext()).lookup(ReservationSessionRemote.class.getName());
        r.setRenterName(name);
        return r;
    }

    @Override
    protected ManagerSessionRemote getNewManagerSession(String name) throws Exception {
        ManagerSessionRemote m = (ManagerSessionRemote) (new InitialContext()).lookup(ManagerSessionRemote.class.getName());
        return m;
    }

    @Override
    protected void getAvailableCarTypes(ReservationSessionRemote session, Date start, Date end) throws Exception {
        //if(session.getAvailableCarTypes(start, end) == null) System.out.println("availablecartypes failed");
        for(CarType c : session.getAvailableCarTypes(start, end)){
            System.out.println(c.toString());
        }
    }

    @Override
    protected void createQuote(ReservationSessionRemote session, String name, Date start, Date end, String carType, String region) throws Exception {
        session.createQuote(name, new ReservationConstraints(start,end,carType,region));
    }

    @Override
    protected List<Reservation> confirmQuotes(ReservationSessionRemote session, String name) throws Exception {
        return session.confirmQuotes();
    }

    @Override
    protected int getNumberOfReservationsBy(ManagerSessionRemote ms, String clientName) throws Exception {
        return ms.getNumberOfReservations(clientName);
    }

    @Override
    protected int getNumberOfReservationsByCarType(ManagerSessionRemote ms, String carRentalName, String carType) throws Exception {
        return ms.getNumberOfReservations(carRentalName, carType);
    }
}