package org.amalgama.database;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    private static SessionFactory factory;

    static {
        try {
            factory = new Configuration().configure().buildSessionFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the SessionFactory instance.
     *
     * @return the SessionFactory instance
     */
    public static SessionFactory getSessionFactory() {
        return factory;
    }
}
