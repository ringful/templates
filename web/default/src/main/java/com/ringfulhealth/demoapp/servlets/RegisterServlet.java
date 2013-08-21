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

public class RegisterServlet extends HttpServlet {

    @PersistenceUnit
    private EntityManagerFactory emf;

    private static final Logger log = Logger.getLogger(RegisterServlet.class.getName());

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {

        HttpSession session = req.getSession ();
        if (emf == null) {
            // This is for Tomcat
            emf = (EntityManagerFactory) getServletContext().getAttribute("emf");
        }
        UserManager um = new UserManager (emf);

        String username = Util.cleanUpFormData(req.getParameter("username"));
        if (!username.matches("^[a-zA-Z0-9\\-_]+$") || username.length() < 3) {
            String error = "The username must be at least 3 letters and can only contain letters, numbers, - or _";
            if (username.isEmpty()) {
                error = "You must enter an username";
            }
            
            String redirect = "register.jsp?error=" + error;
            resp.sendRedirect(redirect);
            return;
        }
        String password = Util.cleanUpFormData(req.getParameter("password"));
        if (password.isEmpty()) {
            String redirect = "register.jsp?error=You must enter a password";
            resp.sendRedirect(redirect);
            return;
        }
        
        User user = null;
        try {
            User foundUser = um.getUserByPhone(req.getParameter("phone"));
            if (foundUser != null) {
                String redirect = "register.jsp?error=Phone number already confirmed by someone else";
                resp.sendRedirect(redirect);
                return;
            }
            foundUser = um.getUserByEmail(req.getParameter("email"));
            if (foundUser != null) {
                String redirect = "register.jsp?error=Email already confirmed by someone else";
                resp.sendRedirect(redirect);
                return;
            }
            foundUser = um.getUserByUsername(username);
            if (foundUser != null) {
                String redirect = "register.jsp?error=Username already registered by someone else";
                resp.sendRedirect(redirect);
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
            
            // GRACE: login the user automatically the first time
            session.setAttribute ("user", user);

        } catch (Exception e) {
            e.printStackTrace ();
            resp.sendRedirect("register.jsp?error=An error has occurred");
        }
        
        resp.sendRedirect("profile.jsp");

        // The following blocks can fail independent of each other

        try {
            if (user.getPhoneConfirmed() == 0) {
                String mesg = "Pls click on the link to confirm mobile phone: " + Util.prefix + "/ap?a=" + user.getPhoneConfirmCode();
                Util.sendSms(Util.systemPhone, user.getPhone(), mesg);
            }
        } catch (Exception e) {
            e.printStackTrace ();
        }
        try {
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

}
