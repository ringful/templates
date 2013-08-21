package com.ringfulhealth.demoapp.servlets;

import com.ringfulhealth.demoapp.entity.User;
import com.ringfulhealth.demoapp.services.UserManager;
import com.ringfulhealth.demoapp.services.Util;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.logging.Logger;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.json.JSONObject;

public class MachSignupServlet extends HttpServlet {

    @PersistenceUnit
    private EntityManagerFactory emf;

    private static final Logger log = Logger.getLogger(MachSignupServlet.class.getName());

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {

        HttpSession session = req.getSession ();
        if (emf == null) {
            // This is for Tomcat
            emf = (EntityManagerFactory) getServletContext().getAttribute("emf");
        }
        UserManager um = new UserManager (emf);
        
        User user = null;
        try {
            user = new User ();
            user.setEmail(req.getParameter("email"));
            user.setFirstname(req.getParameter("firstname"));
            user.setLastname(req.getParameter("lastname"));
            
            um.forceCreateUser(user);
            
            JSONObject obj = new JSONObject();
            obj.put("result", "ok");
            obj.put("message", "Signed Up");        
            log.info("JSON: " + obj.toString());
            resp.getOutputStream().println(obj.toString());    

        } catch (Exception e) {
            e.printStackTrace ();
            sendError (resp);
        }
    }

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        doPost (req, resp);
    }
    
    private void sendError (HttpServletResponse resp) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("result", "error");
            obj.put("message", "Registration failed");
                
            log.info("JSON: " + obj.toString());
            resp.getOutputStream().println(obj.toString());
            
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }

}
