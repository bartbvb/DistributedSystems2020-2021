package ds.gae;

import ds.gae.entities.Quote;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class Worker extends HttpServlet {
	
	//worker is bound to URL: "http://localhost:8080/worker"

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try{
            ServletInputStream in = req.getInputStream();
            ArrayList<Quote> qs = (ArrayList<Quote>)new ObjectInputStream(in).readObject();
            CarRentalModel.get().confirmQuotes(qs);
            CarRentalModel.get().sendEmail(qs.get(0).getRenter());
        } catch (ClassNotFoundException | ReservationException e) {
            e.printStackTrace();
        }

    }
}
