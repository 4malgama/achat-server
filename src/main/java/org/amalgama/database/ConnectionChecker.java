package org.amalgama.database;

import org.hibernate.Session;

public class ConnectionChecker {
    public static void checkConnection() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            System.out.println("[PostgreSQL]: Connection " + (session.isConnected() && session.isOpen() ? "OK" : "FAILED"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
