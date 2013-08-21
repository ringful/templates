package com.ringfulhealth.demoapp.jobs;

import com.ringfulhealth.demoapp.entity.User;
import com.ringfulhealth.demoapp.services.UserManager;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManagerFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class NotificationWorker implements Job {

    private static final Logger log = Logger.getLogger(NotificationWorker.class.getName());

    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("Start cron job");

        EntityManagerFactory emf = (EntityManagerFactory) context.getMergedJobDataMap().get("emf");
        UserManager um = new UserManager (emf);
        Date now = new Date ();
        
        List <User> users = um.users();
        if (users == null) {
            return;
        }
        for (User user : users) {
            log.info("Processing user " + user.getFirstname() + " " + user.getLastname());
        }
        
    }

}
