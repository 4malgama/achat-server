package org.amalgama.database.dao;

import org.amalgama.database.HibernateUtil;
import org.amalgama.database.entities.Attachment;
import org.amalgama.database.entities.Message;
import org.hibernate.Session;

import java.util.List;

public class AttachmentDAO {
    public static Attachment getAttachment(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Attachment.class, id);
        } catch (Exception e) {
            return null;
        }
    }

    public static List<Attachment> getAttachments(Message message) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Attachment where Message = ?1", Attachment.class)
                    .setParameter(1, message)
                    .list();
        } catch (Exception e) {
            return null;
        }
    }

    public static void addAttachment(Attachment attachment) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.persist(attachment);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateAttachment(Attachment attachment) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.merge(attachment);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteAttachment(Attachment attachment) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.remove(attachment);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
