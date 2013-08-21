package com.ringfulhealth.demoapp.servlets;

import com.ringfulhealth.demoapp.entity.EventLog;
import com.ringfulhealth.demoapp.services.EventLogManager;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class EventLogServlet extends HttpServlet {

    @PersistenceUnit
    private EntityManagerFactory emf;

    private static final Logger log = Logger.getLogger(EventLogServlet.class.getName());

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {

        EventLog elog = new EventLog ();
        
        elog.setAppName(req.getParameter("appName"));
        elog.setUserId(Long.parseLong(req.getParameter("userId")));
        elog.setDeviceId(req.getParameter("deviceId"));
        
        elog.setEventCat(req.getParameter("eventCat"));
        elog.setEventName(req.getParameter("eventName"));
        elog.setEventDate(new Date (Long.parseLong(req.getParameter("eventDate"))));
        
        elog.setEpisodeId(Integer.parseInt(req.getParameter("episodeId")));
        elog.setActionId(Integer.parseInt(req.getParameter("actionId")));
        elog.setAnswerId(Integer.parseInt(req.getParameter("answerId")));
        
        elog.setMediaFilename(req.getParameter("mediaFilename"));
        elog.setNote(req.getParameter("note"));
        
        if (emf == null) {
            // This is for Tomcat
            emf = (EntityManagerFactory) getServletContext().getAttribute("emf");
        }
        EventLogManager um = new EventLogManager (emf);
        
        try {
            um.saveEventLog(elog);  
        } catch (Exception e) {
            e.printStackTrace ();
        }
                
    }
    
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        doPost (req, resp);
    }
    
}