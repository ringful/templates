package com.ringfulhealth.demoapp.servlets;

import com.ringfulhealth.demoapp.entity.User;
import com.ringfulhealth.demoapp.services.UserManager;
import com.ringfulhealth.demoapp.services.Util;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class ResetPasswordServlet extends HttpServlet {
    
    @PersistenceUnit
    private EntityManagerFactory emf;

    private static final Logger log = Logger.getLogger(ResetPasswordServlet.class.getName());

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {

        String username = req.getParameter("username");
        if (username == null) {
            username = "";
        } else {
            username = username.trim().toLowerCase();
        }
        
        String phone = req.getParameter("phone");
        phone = Util.formatPhoneNumber(phone);
        
        String email = req.getParameter("email");
        if (email == null) {
            email = "";
        } else {
            email = email.trim().toLowerCase();
        }
        
        if (emf == null) {
            // This is for Tomcat
            emf = (EntityManagerFactory) getServletContext().getAttribute("emf");
        }
        UserManager um = new UserManager (emf);

        try {
            String newpass = Util.randomNumericString(5);
            String msg = "";
            String error = "";
            
            User user = um.getUserByUsername(username);
            if (user != null && email != null && email.equals(user.getEmail())) {
                try {
                    // Util.sendEmail(Util.systemEmail, email, "Vamos: Password Reset", "Upon your request, we have reset your Vamos password to " + newpass + ". Please log into the web site and change your password as soon as you can at the Profile menu. If you did not request this change, please email us at info@ringful.com immediately. Thank you.");
                    String template = Util.convertStreamToString(getServletContext().getResourceAsStream("/WEB-INF/email/green/reset_password.html"));
                    Hashtable <String, String> params = new Hashtable <String, String> ();
                    params.put("name", user.getFirstname() + " " + user.getLastname());
                    params.put("newpass", newpass);
                    Util.sendHtmlEmail(Util.systemEmail, email, "Password Reset", template, params, "demoapp_reset_password");
                    
                    msg = msg + "New password is emailed to you. ";
                    
                } catch (Exception ee) {
                    error = error + "We cannot email you. ";
                    ee.printStackTrace();
                }                
            }
            if (user != null && phone != null && phone.equals(user.getPhone())) {
                try {
                    Util.sendSms(Util.systemPhone, phone, "Upon your request, we reset your Vamos password to " + newpass + ". Pls login and change it asap.");
                    msg = msg + "New password is texted to you. ";
                    
                } catch (Exception ee) {
                    error = error + "We cannot text you. ";
                    ee.printStackTrace();
                }                
            }
            
            if (msg.isEmpty()) {
                error = error + "Cannot perform password reset. ";
            } else {
                user.setPassword(newpass);
                um.forceUpdateUser(user);
            }
            
            resp.sendRedirect("login.jsp?msg=" + msg + "&error=" + error);
            
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            resp.sendError(500);
            return;
        }
    }

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        doPost (req, resp);
    }

}
