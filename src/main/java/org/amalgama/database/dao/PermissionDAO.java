package org.amalgama.database.dao;

import org.amalgama.database.HibernateUtil;
import org.amalgama.database.entities.Permission;
import org.hibernate.Session;

public class PermissionDAO {
    public static Permission getPermission(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Permission.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Permission getPermission(String rank) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Permission WHERE Rank = ?1", Permission.class).setParameter(1, rank).getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void addPermission(Permission permission) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.persist(permission);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updatePermission(Permission permission) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.merge(permission);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deletePermission(Permission permission) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.remove(permission);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
