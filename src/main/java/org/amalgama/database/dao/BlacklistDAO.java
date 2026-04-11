package org.amalgama.database.dao;

import org.amalgama.database.HibernateUtil;
import org.amalgama.database.entities.Blacklist;
import org.amalgama.database.entities.User;
import org.hibernate.Session;

import java.util.List;

public class BlacklistDAO {
    public static Blacklist getBlacklist(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Blacklist.class, id);
        } catch (Exception ignored) {
        }
        return null;
    }

    public static void addBlacklist(Blacklist blacklist) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.persist(blacklist);
            session.getTransaction().commit();
        } catch (Exception ignored) {

        }
    }

    public static void deleteBlacklist(Blacklist blacklist) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.remove(blacklist);
            session.getTransaction().commit();
        } catch (Exception ignored) {

        }
    }

    public static void updateBlacklist(Blacklist blacklist) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.merge(blacklist);
            session.getTransaction().commit();
        } catch (Exception ignored) {

        }
    }

    public static List<Blacklist> getBlacklistByUser(User user) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Blacklist WHERE User = ?1", Blacklist.class)
                    .setParameter(1, user)
                    .getResultList();
        } catch (Exception ignored) {

        }
        return null;
    }

    public static Blacklist getBlacklistByUserAndTarget(User user, User target) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Blacklist WHERE (User = ?1 AND Blocked = ?2) OR (User = ?2 AND Blocked = ?1)", Blacklist.class)
                    .setParameter(1, user)
                    .setParameter(2, target)
                    .getSingleResult();
        } catch (Exception ignored) {

        }
        return null;
    }
}
