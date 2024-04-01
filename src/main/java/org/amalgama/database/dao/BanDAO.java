package org.amalgama.database.dao;

import org.amalgama.database.HibernateUtil;
import org.amalgama.database.entities.Ban;
import org.amalgama.database.entities.User;
import org.hibernate.Session;

public class BanDAO {
    public static Ban getBan(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Ban.class, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Ban getBanByIp(String ip) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Ban WHERE IP = ?1", Ban.class)
                    .setParameter(1, ip)
                    .getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Ban getBanByUser(User user) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Ban WHERE User = ?1", Ban.class)
                    .setParameter(1, user)
                    .getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void addBan(Ban ban) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.persist(ban);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteBan(Ban ban) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.remove(ban);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateBan(Ban ban) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.merge(ban);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
