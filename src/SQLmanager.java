import fi.tamk.tiko.MyListPackage.MyLinkedList;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

/**
 * SQLmanager
 * 
 * @author      Jyri Virtaranta jyri.virtaranta@cs.tamk.fi
 * @version     2017.11.21
 * @since       1.8
 */
public class SQLmanager {
    private Configuration cfg;
    private SessionFactory factory;

    /**
     * Instantiates a new SQLmanager.
     */
    public SQLmanager() {
        cfg = new Configuration();
        cfg.addAnnotatedClass(ShoppingItem.class);
        factory = cfg.configure().buildSessionFactory();
    }

    /**
     * Shutdown procedures.
     */
    public void close() {
        factory.close();
    }

    /**
     * Saves given MyLinkedList to SQL database.
     *
     * @param  list  content to save.
     */
    public void save(MyLinkedList<ShoppingItem> list) {
        Session session = factory.openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            for (int i = 0; i < list.size(); i++) {
                session.persist(list.get(i));
            }

            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }

            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    /**
     * Loads a content from SQL database.
     *
     * @return    MyLinkedList of ShoppingItems.
     */
    public MyLinkedList<ShoppingItem> load() {
        Session session = factory.openSession();
        Transaction transaction = null;
        String hgl = "FROM ShoppingItem";
        MyLinkedList<ShoppingItem> list = new MyLinkedList<>();

        try {
            transaction = session.beginTransaction();

            for (Object o : session.createQuery(hgl).getResultList()) {
                ShoppingItem item = (ShoppingItem) o;
                list.add(item);
            }

            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }

            e.printStackTrace();
            return null;
        } finally {
            session.close();
        }

        return list;
    }

    /**
     * Deletes all content from SQL database without dropping table.
     */
    public void deleteAll() {
        Session session = factory.openSession();
        Transaction transaction = null;
        String hgl = "FROM ShoppingItem";

        try {
            transaction = session.beginTransaction();

            for (Object o : session.createQuery(hgl).getResultList()) {
                ShoppingItem item = (ShoppingItem) o;
                session.delete(item);
            }

            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }

            e.printStackTrace();
        } finally {
            session.close();
        }
    }
}
