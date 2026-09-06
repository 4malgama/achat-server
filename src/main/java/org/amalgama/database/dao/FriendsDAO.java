package org.amalgama.database.dao;

import org.amalgama.database.HibernateUtil;
import org.amalgama.database.entities.Friends;
import org.amalgama.database.entities.User;
import org.hibernate.Session;

import java.util.List;

public class FriendsDAO {
    public static Friends getFriends(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Friends.class, id);
        } catch (Exception ignored) {
        }
        return null;
    }


    public static void addFriends(Friends friends) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.persist(friends);
            session.getTransaction().commit();
        } catch (Exception ignored) {
        }
    }


    public static void deleteFriends(Friends friends) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.remove(friends);
            session.getTransaction().commit();
        } catch (Exception ignored) {
        }
    }


    public static void updateFriends(Friends friends) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.merge(friends);
            session.getTransaction().commit();
        } catch (Exception ignored) {
        }
    }


    public static List<Friends> getFriendsByUser(User user) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Friends WHERE User = ?1", Friends.class)
                    .setParameter(1, user)
                    .getResultList();
        } catch (Exception ignored) {
        }
        return null;
    }

    public static Friends getFriendsByUserAndTarget(User user, User target) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Friends WHERE (User = ?1 AND Friend = ?2) OR (User = ?2 AND Friend = ?1)", Friends.class)
                    .setParameter(1, user)
                    .setParameter(2, target)
                    .getSingleResult();
        } catch (Exception ignored) {
        }
        return null;
    }

}
