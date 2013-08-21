package com.ringfulhealth.demoapp.servlets;

import com.ringfulhealth.demoapp.entity.StatusType;
import com.ringfulhealth.demoapp.entity.User;
import com.ringfulhealth.demoapp.services.UserManager;
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


public class LoginServlet extends HttpServlet {
    
    @PersistenceUnit
    private EntityManagerFactory emf;

    private static final Logger log = Logger.getLogger(LoginServlet.class.getName());

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {

        HttpSession session = req.getSession();
        
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        if (username == null) {
            username = "";
        } else {
            username = username.trim().toLowerCase();
        }
        if (password == null) {
            password = "";
        } else {
            password = password.trim();
        }
        
        // SET this if this is explict in the URL
        // We use this feature in the mobile app
        String targetUrl = req.getParameter("targetUrl");
        if (targetUrl == null || targetUrl.trim().isEmpty()) {
            // nothing to do here
        } else {
            session.setAttribute("targetUrl", targetUrl);
        }

        List <String> missing = new ArrayList <String>();
        if ("".equals(username)) {
            missing.add("Username");
        }
        if ("".equals(password)) {
            missing.add("Password");
        }
        if (!missing.isEmpty()) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < missing.size(); i++) {
                sb.append(missing.get(i));
                if (i + 1 != missing.size()) {
                    sb.append(",+");
                }
            }
            
            resp.sendRedirect("login.jsp?error=Please enter value " + sb.toString());
            return;
        }

        if (emf == null) {
            // This is for Tomcat
            emf = (EntityManagerFactory) getServletContext().getAttribute("emf");
        }
        UserManager um = new UserManager (emf);

        try {
            User user = um.getUserByUsername(username);
            if (user == null || !password.equals(user.getPassword())) {
                resp.sendRedirect("login.jsp?error=Wrong credentials");
                return;
            }
            
            if (user.getStatus() == StatusType.ADMIN) {
                session.setAttribute("user", user);
                resp.sendRedirect("admin_index.jsp");
                
            } else {
            
                session.setAttribute("user", user);
                resp.sendRedirect("profile.jsp");
                
            }
            
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
