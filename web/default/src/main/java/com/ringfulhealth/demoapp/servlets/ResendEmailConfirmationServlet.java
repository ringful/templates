package com.ringfulhealth.demoapp.servlets;

import com.ringfulhealth.demoapp.entity.User;
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

// THIS IS NOT USED SINCE ALL REGISTRATION COMES VIA EMAIL INVITE
// THE EMAIL ADDRESSES ARE ALREADY CONFIRMED FOR EACH
public class ResendEmailConfirmationServlet extends HttpServlet {
    
    private static final Logger log = Logger.getLogger(ResendEmailConfirmationServlet.class.getName());

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {

        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");
        String referrer = req.getHeader("referer");
        if (referrer.indexOf("?") != -1) {
            referrer.substring(0, referrer.indexOf("?"));
        }
        
        if (user.getEmailConfirmed() == 1 || "".equals(user.getEmailConfirmCode())) {
            resp.sendRedirect(referrer + "?error=Your email is already confirmed");
        }
        
        try {
            // String mesg = "Pls click on the link to confirm email address: " + Util.prefix + "/ae?a=" + user.getEmailConfirmCode();
            // Util.sendEmail(Util.systemEmail, user.getEmail(), "FightCancer.me: Please confirm your email address", mesg);
            
            String template = Util.convertStreamToString(getServletContext().getResourceAsStream("/WEB-INF/email/green/generic.html"));
            Hashtable <String, String> params = new Hashtable <String, String> ();
            params.put("title", "Please confirm your email address");
            params.put("body", "Please click on the link below to confirm email address");
            params.put("link", Util.prefix + "/ap?a=" + user.getEmailConfirmCode());
            Util.sendHtmlEmail(Util.systemEmail, user.getEmail(), "Please confirm your email address", template, params, "demoapp_reset_password");
            
            resp.sendRedirect(referrer + "?msg=Confirmation email is re-sent. Please check your email");
        } catch (Exception e) {
            e.printStackTrace ();
            resp.sendRedirect(referrer + "?error=There is a problem sending email. Please check and make sure that your email address is correct!");
        }
    }

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        doPost (req, resp);
    }

}
