package org.amalgama.database.dao;

import org.amalgama.database.HibernateUtil;
import org.amalgama.database.entities.Chat;
import org.amalgama.database.entities.Group;
import org.amalgama.database.entities.User;
import org.hibernate.Session;

import java.util.List;

public class ChatDAO {
    public static Chat getChat(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Chat.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Chat getChat(User user) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Chat WHERE User = ?1", Chat.class)
                    .setParameter(1, user)
                    .getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Chat getChat(Group group) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Chat WHERE Group = ?1", Chat.class)
                    .setParameter(1, group)
                    .getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void addChat(Chat chat) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.persist(chat);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateChat(Chat chat) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.merge(chat);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteChat(Chat chat) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.remove(chat);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<Chat> getChats(User user) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Chat WHERE User = ?1 OR Second = ?1", Chat.class)
                    .setParameter(1, user)
                    .list();
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
