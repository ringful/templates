package com.ringfulhealth.demoapp.services;

import com.ringfulhealth.demoapp.entity.EventLog;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Query;

public class EventLogManager {

    protected final EntityManagerFactory emf;

    public EventLogManager (EntityManagerFactory emf) {
        this.emf = emf;
    }

    public void saveEventLog (EventLog elog) {
        EntityManager manager = emf.createEntityManager();
        try {
            manager.getTransaction().begin();
            if (elog.getId() == 0) {
                manager.persist(elog);
            } else {
                manager.merge(elog);
            }
            manager.getTransaction().commit();
        } finally {
            manager.close();
        }
    }
    
    public List <EventLog> eventLogsForEventCat (long userId, String eventCat, long limit) {
        Date limitDate = new Date (0);
        if (limit > 0) {
            limitDate = new Date ((new Date()).getTime() - limit);
        }
        
        EntityManager manager = emf.createEntityManager();
        Query query = manager.createQuery("select e from EventLog e where userId=:userId and eventCat=:eventCat and eventDate>:limitDate order by eventDate DESC");
        query.setParameter("userId", userId);
        query.setParameter("eventCat", eventCat);
        query.setParameter("limitDate", limitDate);
        return query.getResultList();
    }
    
    public EventLog lookupEventLogById (long id) {
        EntityManager manager = emf.createEntityManager();
        try {
            EventLog elog = manager.find(EventLog.class, id);
            return elog;
            
        } catch (Exception e) {
            e.printStackTrace ();
        } finally {
            manager.close();
        }
        return null;
    }
    
}
