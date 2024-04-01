package org.amalgama.database.dao;

import org.amalgama.database.HibernateUtil;
import org.amalgama.database.entities.Chat;
import org.amalgama.database.entities.Message;
import org.hibernate.Session;

import java.util.List;

public class MessageDAO {
    public static Message getMessage(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Message.class, id);
        } catch (Exception e) {
            return null;
        }
    }

    public static List<Message> getMessages(Chat chat) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Message WHERE Chat = ?1", Message.class)
                    .setParameter(1, chat)
                    .list();
        } catch (Exception e) {
            return null;
        }
    }

    public static void addMessage(Message message) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.persist(message);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateMessage(Message message) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.merge(message);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteMessage(Message message) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.remove(message);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
