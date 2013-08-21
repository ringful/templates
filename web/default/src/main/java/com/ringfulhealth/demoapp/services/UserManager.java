package com.ringfulhealth.demoapp.services;

import com.ringfulhealth.demoapp.entity.User;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Query;

public class UserManager {

    protected final EntityManagerFactory emf;

    public UserManager (EntityManagerFactory emf) {
        this.emf = emf;
    }
    
    public void createNewUser (User user) throws Exception {
        EntityManager manager = emf.createEntityManager();
        try {
            User foundUser = null;
            if (user.getPhoneConfirmed() == 0) {
                do {
                    String rand = Util.getRandomId(5);
                    user.setPhoneConfirmCode(rand);
                    foundUser = getUserByPhoneConfirmCode (user.getPhoneConfirmCode());
                } while (foundUser != null);
            }
            foundUser = null;
            if (user.getEmailConfirmed() == 0) {
                do {
                    String rand = Util.getRandomId(5);
                    user.setEmailConfirmCode(rand);
                    foundUser = getUserByEmailConfirmCode (user.getEmailConfirmCode());
                } while (foundUser != null);
            }

            user.setRecordDate(new Date());
            
            manager.getTransaction().begin();
            manager.persist(user);
            manager.getTransaction().commit();
        } finally {
            manager.close();
        }
    }

    public void updateUser (User user) throws Exception {
        EntityManager manager = emf.createEntityManager();
        try {
            User existing = getUserById (user.getId());
            if (!existing.getUsername().equals(user.getUsername())) {
                throw new Exception ("Cannot change username!");
            }

            User foundUser;
            if (!existing.getPhone().equals(user.getPhone())) {
                // phone changed.
                user.setPhoneConfirmed(0);
                do {
                    String rand = Util.getRandomId(5);
                    user.setPhoneConfirmCode(rand);
                    foundUser = getUserByPhoneConfirmCode (user.getPhoneConfirmCode());
                } while (foundUser != null);
            }
            if (!existing.getEmail().equals(user.getEmail())) {
                // email changed.
                do {
                    String rand = Util.getRandomId(5);
                    user.setEmailConfirmCode(rand);
                    foundUser = getUserByEmailConfirmCode (user.getEmailConfirmCode());
                } while (foundUser != null);
            }

            manager.getTransaction().begin();
            manager.merge(user);
            manager.getTransaction().commit();
        } finally {
            manager.close();
        }
    }
    
    public void forceCreateUser (User user) {
        EntityManager manager = emf.createEntityManager();
        try {
            user.setRecordDate(new Date());
            
            manager.getTransaction().begin();
            manager.persist(user);
            manager.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace ();
        } finally {
            manager.close();
        }
    }
    
    public void forceUpdateUser (User user) {
        EntityManager manager = emf.createEntityManager();
        try {
            manager.getTransaction().begin();
            manager.merge(user);
            manager.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace ();
        } finally {
            manager.close();
        }
    }

    public void deleteUser (User user) throws Exception {
        EntityManager manager = emf.createEntityManager();
        try {
            manager.getTransaction().begin();
            
            Query query = manager.createQuery("delete from User u where u.id = :uid");
            query.setParameter("uid", user.getId());
            query.executeUpdate();
            manager.getTransaction().commit();

        } catch (Exception e) {
            e.printStackTrace ();
        } finally {
            manager.close ();
        }
    }

    // Activate phone and remove all un-activated duplicates
    // mode: 1 -- SMS, 2 -- Voice
    public User activatePhone (String code, int mode) throws Exception {
        if (code == null || code.isEmpty()) {
            throw new Exception ("Phone confirmation code is empty");
        }

        EntityManager manager = emf.createEntityManager();
        try {
            User toactivate = getUserByPhoneConfirmCode (code);
            if (toactivate == null) {
                throw new Exception ("Phone confirmation code not found");
            }
            toactivate.setPhoneConfirmed(mode);
            if (mode == 1) {
                toactivate.setPhoneConfirmCode("");
            } else if (mode == 2) {
                // We keep the confirmation code in case we want to activate sms later!
            }

            Query query = manager.createQuery("select u from User u where u.phone=:phone");
            query.setParameter("phone", toactivate.getPhone());
            List <User> users = query.getResultList();
            if (users != null) {
                for (User u : users) {
                    if (u.getId() != toactivate.getId()) {
                        u.setPhone("");
                        u.setPhoneConfirmed (0);
                        u.setPhoneConfirmCode("");
                    }
                }
            }

            manager.getTransaction().begin();
            manager.merge(toactivate);
            if (users != null) {
                for (User u : users) {
                    manager.merge (u);
                }
            }
            manager.getTransaction().commit();

            return toactivate;

        } finally {
            manager.close ();
        }
    }

    // Activate email and remove all un-activated duplicates
    public User activateEmail (String code) throws Exception {
        if (code == null || code.isEmpty()) {
            throw new Exception ("Email confirmation code is empty");
        }

        EntityManager manager = emf.createEntityManager();
        try {
            User toactivate = getUserByEmailConfirmCode (code);
            if (toactivate == null) {
                throw new Exception ("Email confirmation code not found");
            }
            toactivate.setEmailConfirmed(1);
            toactivate.setEmailConfirmCode("");
            
            Query query = manager.createQuery("select u from User u where u.email=:email");
            query.setParameter("email", toactivate.getEmail());
            List <User> users = query.getResultList();
            if (users != null) {
                for (User u : users) {
                    if (u.getId() != toactivate.getId()) {
                        u.setEmail("");
                        u.setEmailConfirmed (0);
                        u.setEmailConfirmCode("");
                    }
                }
            }

            manager.getTransaction().begin();
            manager.merge(toactivate);
            if (users != null) {
                for (User u : users) {
                    manager.merge (u);
                }
            }
            manager.getTransaction().commit();

            return toactivate;

        } finally {
            manager.close ();
        }
    }

    // All users excluding admin users
    public List <User> users () {
        EntityManager manager = emf.createEntityManager();
        Query query = manager.createQuery("select u from User u where u.status <> 'ADMIN'");
        return query.getResultList();
    }
    
    public User getUserByPhoneConfirmCode (String code) {
        EntityManager manager = emf.createEntityManager();
        Query query = manager.createQuery("select u from User u where u.phoneConfirmCode=:code");
        query.setParameter("code", code);
        try {
            User user = (User) query.getSingleResult();
            return user;
        } catch (NoResultException noResult) {
            return null;
        }
    }

    public User getUserByEmailConfirmCode (String code) {
        EntityManager manager = emf.createEntityManager();
        Query query = manager.createQuery("select u from User u where u.emailConfirmCode=:code");
        query.setParameter("code", code);
        try {
            User user = (User) query.getSingleResult();
            return user;
        } catch (NoResultException noResult) {
            return null;
        }
    }

    public User getUserByUsername (String username) {
        EntityManager manager = emf.createEntityManager();
        Query query = manager.createQuery("select u from User u where u.username=:username");
        query.setParameter("username", username);
        try {
            User user = (User) query.getSingleResult();
            return user;
        } catch (NoResultException noResult) {
            return null;
        }
    }

    public User getUserByPhone (String phone) {
        EntityManager manager = emf.createEntityManager();
        Query query = manager.createQuery("select u from User u where u.phone=:phone and u.phoneConfirmed=:confirmed");
        query.setParameter("phone", phone);
        query.setParameter("confirmed", 1);
        try {
            User user = (User) query.getSingleResult();
            return user;
        } catch (NoResultException noResult) {
            return null;
        }
    }

    public User getUserByEmail (String email) {
        EntityManager manager = emf.createEntityManager();
        Query query = manager.createQuery("select u from User u where u.email=:email and u.emailConfirmed=:confirmed");
        query.setParameter("email", email);
        query.setParameter("confirmed", 1);
        try {
            User user = (User) query.getSingleResult();
            return user;
        } catch (NoResultException noResult) {
            return null;
        }
    }
    
    public List<User> getUsersByEmail (String email) {
        EntityManager manager = emf.createEntityManager();
        Query query = manager.createQuery("select u from User u where u.email=:email");
        query.setParameter("email", email);
        try {
            return query.getResultList();
        } catch (NoResultException noResult) {
            return null;
        }
    }

    public User getUserById (long id) {
        EntityManager manager = emf.createEntityManager();
        User user = manager.find(User.class, id);
        return user;
    }
    
    public String phoneConfirmationCode (String phone) {
        EntityManager manager = emf.createEntityManager();
        Query query = manager.createQuery("select u from User u where u.phone=:phone");
        query.setParameter("phone", phone);
        
        List <User> users = query.getResultList();
        if (users == null || users.isEmpty()) {
            return "";
        } else if (users.size() == 1) {
            User u = users.get (0);
            if (u.getPhoneConfirmed() == 0) {
                return u.getPhoneConfirmCode();
            } else {
                return "";
            }
        } else {
            return "";
        }
    }
    
}
