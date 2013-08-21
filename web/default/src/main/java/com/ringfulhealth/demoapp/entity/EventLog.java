package com.ringfulhealth.demoapp.entity;

import com.ringfulhealth.demoapp.services.Util;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@Entity
@Table(name="events")
public class EventLog implements Serializable {

    private long id;
    private Date saveDate;
    
    private String appName;
    private long userId;
    private String deviceId;
    
    private Date eventDate;
    private String eventCat;
    private String eventName;
    
    private int episodeId;
    private int actionId;
    private int answerId;
    
    private String mediaFilename;
    private String note;
    
    public EventLog () {
        saveDate = new Date ();
        
        appName = "";
        userId = -1;
        deviceId = "";
        
        eventCat = "";
        eventName = "";
        eventDate = new Date ();
        
        episodeId = -1;
        actionId = -1;
        answerId = -1;
        
        mediaFilename = "";
        note = "";
    }
    
    @Id @GeneratedValue
    public long getId() { return id;}
    public void setId(long id) { this.id = id; }
    
    @Temporal(TemporalType.TIMESTAMP)
    public Date getSaveDate() {
        return saveDate;
    }
    public void setSaveDate(Date saveDate) {
        this.saveDate = saveDate;
    }
    
    @Temporal(TemporalType.TIMESTAMP)
    public Date getEventDate() {
        return eventDate;
    }
    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public String getAppName() {
        return appName;
    }
    public void setAppName(String appName) {
        this.appName = appName;
    }

    public long getUserId() {
        return userId;
    }
    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getDeviceId() {
        return deviceId;
    }
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getEventCat() {
        return eventCat;
    }
    public void setEventCat(String eventCat) {
        this.eventCat = eventCat;
    }
    
    public String getEventName() {
        return eventName;
    }
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public int getEpisodeId() {
        return episodeId;
    }
    public void setEpisodeId(int episodeId) {
        this.episodeId = episodeId;
    }

    public int getActionId() {
        return actionId;
    }
    public void setActionId(int actionId) {
        this.actionId = actionId;
    }

    public int getAnswerId() {
        return answerId;
    }
    public void setAnswerId(int answerId) {
        this.answerId = answerId;
    }

    public String getMediaFilename() {
        return mediaFilename;
    }
    public void setMediaFilename(String mediaFilename) {
        this.mediaFilename = mediaFilename;
    }

    public String getNote() {
        return note;
    }
    public void setNote(String note) {
        this.note = note;
    }
    
    @Transient
    public String getEventDateStr() {
        return Util.formatDateTime(eventDate);
    }
    
    @Transient
    public String getSaveDateStr() {
        return Util.formatDateTime(saveDate);
    }
}