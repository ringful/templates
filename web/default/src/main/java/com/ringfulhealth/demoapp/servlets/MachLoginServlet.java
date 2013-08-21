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
import org.json.JSONObject;


public class MachLoginServlet extends HttpServlet {
    
    @PersistenceUnit
    private EntityManagerFactory emf;

    private static final Logger log = Logger.getLogger(MachLoginServlet.class.getName());

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

        if (emf == null) {
            // This is for Tomcat
            emf = (EntityManagerFactory) getServletContext().getAttribute("emf");
        }
        UserManager um = new UserManager (emf);

        try {
            User user = um.getUserByUsername(username);
            if (user == null || !password.equals(user.getPassword())) {
                JSONObject obj = new JSONObject();
                obj.put("result", "error");
                obj.put("message", "Authentication failed");
                
                log.info("JSON: " + obj.toString());
                resp.getOutputStream().println(obj.toString());
                return;
            } else {
                JSONObject obj = new JSONObject();
                obj.put("result", "ok");
                obj.put("message", "Success");
                obj.put("firstname", user.getFirstname());
                obj.put("lastname", user.getLastname());
                
                log.info("JSON: " + obj.toString());
                resp.getOutputStream().println(obj.toString());
                return;
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
