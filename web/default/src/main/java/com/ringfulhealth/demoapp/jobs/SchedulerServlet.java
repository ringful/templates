package com.ringfulhealth.demoapp.jobs;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;
import javax.servlet.GenericServlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.quartz.CronExpression;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.ee.servlet.QuartzInitializerServlet;

public class SchedulerServlet extends GenericServlet {

    @PersistenceUnit
    private EntityManagerFactory emf;
    
    private static final Logger log = Logger.getLogger(SchedulerServlet.class.getName());

    // private static final String CRON_EXPRESSION = "0 0 9,12,15,18,20 * * ?";
    private static final String CRON_EXPRESSION = "0 0/5 * * * ?"; // Every 5 minutes
    // private static final String CRON_EXPRESSION = "0 0 0/1 * * ?"; // Every hour
    // private static final String CRON_EXPRESSION = "0 0 10 * * ?"; // Once per day at 10am

    public void init(ServletConfig servletConfig) throws ServletException {

        super.init(servletConfig);

        try {
            Properties props = new Properties();
            props.load(getServletContext().getResourceAsStream("/WEB-INF/classes/META-INF/conf.properties"));
            
            // This is for Tomcat
            if (emf == null) {
                emf = Persistence.createEntityManagerFactory(props.getProperty("persistenceUnit"));
                getServletContext().setAttribute("emf", emf);
            }

            SchedulerFactory schedulerFactory = (SchedulerFactory) getServletContext().getAttribute(QuartzInitializerServlet.QUARTZ_FACTORY_KEY);
            Scheduler scheduler = schedulerFactory.getScheduler();

            JobDetail jobDetail = new JobDetail("DemoAppJob", "DemoAppJobGroup", NotificationWorker.class);
            JobDataMap dataMap = new JobDataMap ();
            dataMap.put("emf", emf);
            jobDetail.setJobDataMap (dataMap);

            CronTrigger cronTrigger = new CronTrigger("DemoAppTrigger", "DemoAppTriggerGroup");
            CronExpression cexp = new CronExpression(CRON_EXPRESSION);
            cronTrigger.setCronExpression(cexp);
            
            scheduler.scheduleJob(jobDetail, cronTrigger);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void service(ServletRequest serveletRequest, ServletResponse servletResponse) throws ServletException, IOException {
    }

}
