package org.amalgama.database.dao;

import jakarta.persistence.NoResultException;
import org.amalgama.database.HibernateUtil;
import org.amalgama.database.entities.User;
import org.hibernate.Session;

public class UserDAO {
    public static User getUser(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(User.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static User getUser(String login) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from User where Login = ?1", User.class)
                    .setParameter(1, login)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static User getUser(String login, String password) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from User where LOWER(Login) = LOWER(?1) and Password = ?2", User.class)
                    .setParameter(1, login)
                    .setParameter(2, password)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public static void addUser(User user) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.persist(user);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateUser(User user) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.merge(user);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteUser(User user) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.remove(user);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
