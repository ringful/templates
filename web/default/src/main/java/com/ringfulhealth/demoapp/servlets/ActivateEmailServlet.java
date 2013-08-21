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

public class ActivateEmailServlet extends HttpServlet {

    @PersistenceUnit
    private EntityManagerFactory emf;

    private static final Logger log = Logger.getLogger(ActivateEmailServlet.class.getName());

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {

        String code = req.getParameter("a");

        if (emf == null) {
            // This is for Tomcat
            emf = (EntityManagerFactory) getServletContext().getAttribute("emf");
        }
        UserManager um = new UserManager (emf);
        User user = null;
        try {
            user = um.activateEmail(code);

            HttpSession session = req.getSession ();
            User suser = (User) session.getAttribute("user");
            if (suser != null && suser.getId() == user.getId()) {
                // The correct user is already logged in this browser
                session.setAttribute ("user", user);
            }

        } catch (Exception e) {
            e.printStackTrace ();
            resp.sendRedirect ("status.jsp?error=Sorry, there is a problem activating your email address. Please try again later.");
            return;
        }

        resp.sendRedirect ("status.jsp?success=Your email address has been successfully activated! <a href=\"/dashboard.jsp\" class=\"btn btn-primary\">Next</a>");

    }

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        doPost (req, resp);
    }


}
