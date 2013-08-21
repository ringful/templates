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
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@Entity
@Table(name="users")
public class User implements Serializable {

    private long id;
    private Date recordDate;
    
    private String username;
    private String password;
    private StatusType status;
    
    private String firstname;
    private String lastname;
    private Date dob;
    
    private String phone;
    private int phoneConfirmed;
    private String phoneConfirmCode;
    
    private String email;
    private int emailConfirmed;
    private String emailConfirmCode;

    public User () {
        recordDate = new Date ();
        
        username = "";
        password = "";
        status = StatusType.UNKNOWN;
        
        firstname = "";
        lastname = "";
        dob = new Date ();
        
        phone = "";
        phoneConfirmed = 0;
        phoneConfirmCode = "";
        
        email = "";
        emailConfirmed = 0;
        emailConfirmCode = "";
    }

    
    @Id @GeneratedValue
    public long getId() { return id;}
    public void setId(long id) { this.id = id; }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    
    @Enumerated(EnumType.STRING)
    public StatusType getStatus() {
        return status;
    }
    public void setStatus(StatusType status) {
        this.status = status;
    }

    public String getFirstname() {
        return firstname;
    }
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
    
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public int getPhoneConfirmed() {
        return phoneConfirmed;
    }

    public void setPhoneConfirmed(int phoneConfirmed) {
        this.phoneConfirmed = phoneConfirmed;
    }

    public String getPhoneConfirmCode() {
        return phoneConfirmCode;
    }

    public void setPhoneConfirmCode(String phoneConfirmCode) {
        this.phoneConfirmCode = phoneConfirmCode;
    }

    public int getEmailConfirmed() {
        return emailConfirmed;
    }

    public void setEmailConfirmed(int emailConfirmed) {
        this.emailConfirmed = emailConfirmed;
    }

    public String getEmailConfirmCode() {
        return emailConfirmCode;
    }

    public void setEmailConfirmCode(String emailConfirmCode) {
        this.emailConfirmCode = emailConfirmCode;
    }

    @Temporal(TemporalType.TIMESTAMP)
    public Date getRecordDate() {
        return recordDate;
    }
    public void setRecordDate(Date recordDate) {
        this.recordDate = recordDate;
    }
    
    @Temporal(TemporalType.TIMESTAMP)
    public Date getDob() {
        return dob;
    }
    public void setDob(Date dob) {
        this.dob = dob;
    }
    
    
    @Transient
    public String getDobStr () {
        return Util.formatDate(dob);
    }
    
    @Transient
    public String getRecordDateStr () {
        return Util.formatDateTime(recordDate);
    }
    
    @Transient
    public String getHtmlFieldsDob () {
        return Util.createHtmlFields(dob);
    }
}
