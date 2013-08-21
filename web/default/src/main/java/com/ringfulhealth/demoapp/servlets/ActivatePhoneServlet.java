package com.ringfulhealth.demoapp.servlets;

import com.ringfulhealth.demoapp.entity.User;
import com.ringfulhealth.demoapp.services.UserManager;
import java.io.IOException;
import java.util.logging.Logger;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class ActivatePhoneServlet extends HttpServlet {

    @PersistenceUnit
    private EntityManagerFactory emf;

    private static final Logger log = Logger.getLogger(ActivatePhoneServlet.class.getName());

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {

        String code = req.getParameter("a");
        // It is SMS activation by default
        int mode = 1;
        // Has Digits -- voice confirmation
        if (req.getParameter("Digits") != null) {
            /*
            if ("1".equals(req.getParameter("Digits"))) {
                mode = 2;
            } else {
                resp.setContentType("text/plain");
                resp.getWriter().println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                // resp.getWriter().println("<Response><Say>Sorry. You must press one to confirm. You number is not confirmed. Please log into the web site and initiate the phone confirmation again.</Say></Response>");
                resp.getWriter().println("<Response><Play>http://followup.ringfulhealth.com/doc/sorry_confirm.mp3</Play></Response>");
                return;
            }
            */
            
            // We decide to allow any key press!
            mode = 2;
        }

        if (emf == null) {
            // This is for Tomcat
            emf = (EntityManagerFactory) getServletContext().getAttribute("emf");
        }
        UserManager um = new UserManager (emf);
        User user = null;
        try {
            user = um.activatePhone(code, mode);
            
            HttpSession session = req.getSession ();
            User suser = (User) session.getAttribute("user");
            if (suser != null && suser.getId() == user.getId()) {
                // The correct user is already logged in this browser
                session.setAttribute ("user", user);
            }

            if (mode == 2) {
                // return voice response and be done
                resp.setContentType("text/xml");
                resp.getWriter().println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                resp.getWriter().println("<Response><Say>Thank you. Your phone number is confirmed.</Say></Response>");
                // resp.getWriter().println("<Response><Play>http://fightcancer.me/voice/thank_you.mp3</Play></Response>");
                return;
            }
        } catch (Exception e) {
            e.printStackTrace ();
            if (mode == 2) {
                // return voice response and be done
                resp.setContentType("text/xml");
                resp.getWriter().println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                resp.getWriter().println("<Response><Say>Sorry. There is an error. Please try again.</Say></Response>");
                // resp.getWriter().println("<Response><Play>http://fightcancer.me/voice/error.mp3</Play></Response>");
                return;
            } else {
                resp.sendRedirect ("status.jsp?error=Sorry, there is a problem activating your phone number. Please try again later.");
                return;
            }
        }

        resp.sendRedirect ("status.jsp?success=Your phone number has been successfully activated! <a href=\"/dashboard.jsp\" class=\"btn btn-primary\">Next</a>");
    }

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        doPost (req, resp);
    }


}
