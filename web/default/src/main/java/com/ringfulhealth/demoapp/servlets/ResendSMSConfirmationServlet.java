package com.ringfulhealth.demoapp.servlets;

import com.ringfulhealth.demoapp.entity.User;
import com.ringfulhealth.demoapp.services.Util;
import java.io.IOException;
import java.util.ArrayList;
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


public class ResendSMSConfirmationServlet extends HttpServlet {
    
    private static final Logger log = Logger.getLogger(ResendSMSConfirmationServlet.class.getName());

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {

        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");
        String referrer = req.getHeader("referer");
        if (referrer.indexOf("?") != -1) {
            referrer.substring(0, referrer.indexOf("?"));
        }
        
        if (user.getPhoneConfirmed() == 1 || "".equals(user.getPhoneConfirmCode())) {
            resp.sendRedirect(referrer + "?error=Your phone number is already confirmed");
        }
        
        try {
            String mesg = "Pls click on the link to confirm mobile phone: " + Util.prefix + "/ap?a=" + user.getPhoneConfirmCode();
            Util.sendSms(Util.systemPhone, user.getPhone(), mesg);
            resp.sendRedirect(referrer + "?msg=Confirmation SMS is re-sent. Please check your phone");
        } catch (Exception e) {
            e.printStackTrace ();
            resp.sendRedirect(referrer + "?error=There is a problem sending SMS. Please check and make sure that your phone number is correct!");
        }
    }

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        doPost (req, resp);
    }

}
