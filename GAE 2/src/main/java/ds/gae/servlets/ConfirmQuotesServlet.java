package ds.gae.servlets;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.repackaged.com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ds.gae.CarRentalModel;
import ds.gae.ReservationException;
import ds.gae.entities.Quote;
import ds.gae.view.JSPSite;
import ds.gae.view.Tools;

@SuppressWarnings("serial")
public class ConfirmQuotesServlet extends HttpServlet {
        
    @SuppressWarnings("unchecked")
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        HttpSession session = req.getSession();
        HashMap<String, ArrayList<Quote>> allQuotes = (HashMap<String, ArrayList<Quote>>) session.getAttribute("quotes");

        ArrayList<Quote> qs = new ArrayList<Quote>();

        for (String crcName : allQuotes.keySet()) {
            qs.addAll(allQuotes.get(crcName));
        }
        //CarRentalModel.get().confirmQuotes(qs);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(qs);
        oos.close();

        Queue queue = QueueFactory.getDefaultQueue();
        queue.add(TaskOptions.Builder.withUrl("/worker").payload(baos.toByteArray()));

        session.setAttribute("quotes", new HashMap<String, ArrayList<Quote>>());

        // TODO
        // If you wish confirmQuotesReply.jsp to be shown to the client as
        // a response of calling this servlet, please replace the following line
        // with resp.sendRedirect(JSPSite.CONFIRM_QUOTES_RESPONSE.url());
        // resp.sendRedirect(JSPSite.CREATE_QUOTES.url());
        resp.sendRedirect(JSPSite.CONFIRM_QUOTES_RESPONSE.url());
    }
}
