package org.amalgama.database.dao;

import org.amalgama.database.HibernateUtil;
import org.amalgama.database.entities.ProfileComment;
import org.amalgama.database.entities.User;
import org.hibernate.Session;

import java.util.List;

public class ProfileCommentDAO {
    public static ProfileComment getProfileComment(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(ProfileComment.class, id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<ProfileComment> getProfileComments(User sender, User receiver) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session
                    .createQuery("from ProfileComment where User = ?1 and Target = ?2", ProfileComment.class)
                    .setParameter(1, sender)
                    .setParameter(2, receiver)
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<ProfileComment> getProfileComments(User user) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session
                    .createQuery("from ProfileComment where User = ?1", ProfileComment.class)
                    .setParameter(1, user)
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void addProfileComment(ProfileComment profileComment) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.persist(profileComment);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateProfileComment(ProfileComment profileComment) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.merge(profileComment);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteProfileComment(ProfileComment profileComment) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.remove(profileComment);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
