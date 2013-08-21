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

public class MachRegisterServlet extends HttpServlet {

    @PersistenceUnit
    private EntityManagerFactory emf;

    private static final Logger log = Logger.getLogger(MachRegisterServlet.class.getName());

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {

        HttpSession session = req.getSession ();
        if (emf == null) {
            // This is for Tomcat
            emf = (EntityManagerFactory) getServletContext().getAttribute("emf");
        }
        UserManager um = new UserManager (emf);

        String username = Util.cleanUpFormData(req.getParameter("username"));
        if (!username.matches("^[a-zA-Z0-9\\-_]+$") || username.length() < 3) {
            // String error = "The username must be at least 3 letters and can only contain letters, numbers, - or _";
            sendError (resp);
            return;
        }
        String password = Util.cleanUpFormData(req.getParameter("password"));
        if (password.isEmpty()) {
            sendError (resp);
            return;
        }
        
        User user = null;
        try {
            User foundUser = um.getUserByPhone(req.getParameter("phone"));
            if (foundUser != null) {
                sendError (resp);
                return;
            }
            foundUser = um.getUserByEmail(req.getParameter("email"));
            if (foundUser != null) {
                sendError (resp);
                return;
            }
            foundUser = um.getUserByUsername(username);
            if (foundUser != null) {
                sendError (resp);
                return;
            }

            user = new User ();
            user.setPhone(req.getParameter("phone"));
            user.setPhoneConfirmed(0);
            user.setPhoneConfirmCode("");
            user.setEmail(req.getParameter("email"));
            user.setEmailConfirmed(0);
            user.setEmailConfirmCode("");

            user.setUsername(username);
            user.setPassword(password);

            user.setFirstname(req.getParameter("firstname"));
            user.setLastname(req.getParameter("lastname"));
            
            try {
                DateFormat formatter = new SimpleDateFormat("MM/dd/yy");
                // Date dob = formatter.parse(req.getParameter("dob"));
                Date dob = formatter.parse(req.getParameter("dobMonth") + "/" + req.getParameter("dobDay") + "/" + req.getParameter("dobYear"));
                user.setDob(dob);
            } catch (Exception ex) {
                user.setDob(null);
            }
            
            // 3 registration code is created in this method
            um.createNewUser(user);
            
            JSONObject obj = new JSONObject();
            obj.put("result", "ok");
            obj.put("message", "Registered");        
            log.info("JSON: " + obj.toString());
            resp.getOutputStream().println(obj.toString());    

        } catch (Exception e) {
            e.printStackTrace ();
            sendError (resp);
        }

        // The following blocks can fail independent of each other

        try {
            if (user.getPhoneConfirmed() == 0) {
                // The phone is not yet confirmed -- this is always the case here
                String mesg = "Pls click on the link to confirm mobile phone: " + Util.prefix + "/ap?a=" + user.getPhoneConfirmCode();
                Util.sendSms(Util.systemPhone, user.getPhone(), mesg);
            }
        } catch (Exception e) {
            e.printStackTrace ();
        }

        // Assume that email is already confirmed since the registerCode is sent via email
        try {
            // String mesg = "Pls click on the link to confirm email address: " + Util.prefix + "/ae?a=" + user.getEmailConfirmCode();
            // Util.sendEmail(Util.systemEmail, user.getEmail(), "Please confirm your email address", mesg);
            
            String template = Util.convertStreamToString(getServletContext().getResourceAsStream("/WEB-INF/email/green/generic.html"));
            Hashtable <String, String> params = new Hashtable <String, String> ();
            params.put("title", "Please confirm your email address");
            params.put("body", "Please click on the link below to confirm email address");
            params.put("link", Util.prefix + "/ap?a=" + user.getEmailConfirmCode());
            Util.sendHtmlEmail(Util.systemEmail, user.getEmail(), "Please confirm your email address", template, params, "demoapp_reset_password");
                    
            
        } catch (Exception e) {
            e.printStackTrace ();
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
