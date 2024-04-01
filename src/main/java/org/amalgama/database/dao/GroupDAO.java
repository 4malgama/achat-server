package org.amalgama.database.dao;

import org.amalgama.database.HibernateUtil;
import org.amalgama.database.entities.Group;
import org.hibernate.Session;

import java.util.List;

public class GroupDAO {
    public static Group getGroup(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Group.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Group getGroupByLink(String link) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Group WHERE Link = ?1", Group.class).setParameter(1, link).uniqueResult();
        } catch (Exception e) {
            return null;
        }
    }

    public static List<Group> getGroupsByName(String name) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Group WHERE Name = ?1", Group.class).setParameter(1, name).list();
        } catch (Exception e) {
            return null;
        }
    }

    public static void addGroup(Group group) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.persist(group);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateGroup(Group group) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.merge(group);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteGroup(Group group) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.remove(group);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
