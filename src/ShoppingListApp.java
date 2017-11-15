import org.hibernate.*;
import org.hibernate.cfg.*;
import fi.tamk.tiko.MyListPackage.MyLinkedList;

import java.io.*;

/**
 * ShoppingListApp
 * 
 * @author      Jyri Virtaranta jyri.virtaranta@cs.tamk.fi
 * @version     2017.11.14
 * @since       1.8
 */
public class ShoppingListApp {
    private MyLinkedList<ShoppingItem> shoppingList;
    private Configuration cfg;

    /**
     * Instantiates a new ShoppingListApp.
     */
    public ShoppingListApp() {
        shoppingList = new MyLinkedList<>();
        cfg = new Configuration();
        cfg.configure();
        cfg.addAnnotatedClass(ShoppingItem.class);
    }

    /**
     * Returns shoppingList
     *
     * @return  MyLinkedList  of type ShoppingItem
     */
    public MyLinkedList<ShoppingItem> getShoppingList() {
        return shoppingList;
    }

    /**
     * Sets shoppingList
     *
     * @param  shoppingList  new MyLinkedList of type ShoppingItem
     */
    public void setShoppingList(MyLinkedList<ShoppingItem> shoppingList) {
        this.shoppingList = shoppingList;
    }

    /**
     * Checks if given user input starts with a number followed by a space.
     *
     * @param   command  given user input as String
     * @return  boolean  true if input starts with number followed by space
     */
    public boolean inputChecker(String command) {
        boolean result = false;
        String amount = "";

        if (!command.contains(" ")) {
            return result;
        }

        for (int i = 0; i < command.length(); i++) {
            if (command.charAt(i) != ' ') {
                amount += command.charAt(i);
            } else {
                break;
            }
        }

        if (amount.matches("^[0-9]+$")) {
            result = true;
        }

        return result;
    }

    /**
     * Creates a new ShoppingItem(s) based on user input
     *
     * @param  command  given user input as String
     */
    public void inputHandler(String command) {
        String name = "";
        String amount = "";
        String errorMsg = "Something was wrong with the amount of items.";
        int index = 0;
        int amountValue = 0;

        if (command.charAt(command.length() - 1) != ';') {
            command += ';';
        }

        for (int i = 0; command.length() > i; i++) {
            if (command.charAt(i) != ';') {
                name += command.charAt(i);
            } else {
                while (name.charAt(0) == ' ') {
                    name = name.substring(1);
                }

                for (int j = 0; j < name.length(); j++) {
                    if (name.charAt(j) != ' ') {
                        amount += name.charAt(j);
                    } else {
                        index = j;
                        break;
                    }
                }

                name = name.substring(index + 1);

                try {
                    amountValue = Integer.parseInt(amount);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    System.out.println(errorMsg);
                }

                addToList(new ShoppingItem(amountValue, name));
                name = "";
                amount = "";
            }
        }
    }

    /**
     * Adds given ShoppingItem to shoppingList
     *
     * <p>if item already exist in shoppingList the amount is added together.
     * if amount is 0 or less item will be removed from shoppingList</p>
     *
     * @param  item  ShoppingItem to add to shoppingList
     */
    public void addToList(ShoppingItem item) {
        for (int i = 0; i < shoppingList.size(); i++) {
            ShoppingItem listItem = shoppingList.get(i);

            if (listItem.getName().equals(item.getName())) {
                listItem.setAmount(listItem.getAmount() + item.getAmount());

                if (listItem.getAmount() <= 0) {
                    shoppingList.remove(i);
                }

                return;
            }
        }

        if (item.getAmount() > 0) {
            shoppingList.add(item);
        }
    }

    /**
     * Saves shoppingList contents to a txt file.
     */
    public void saveToFile(String path) {
        String data = "";

        for (int i = 0; i < shoppingList.size(); i++) {
            data += shoppingList.get(i).getAmount() + " ";
            data += shoppingList.get(i).getName() + ";";
        }

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(path), "utf-8"))) {
            writer.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load shoppingList contents from txt file.
     */
    public void loadFromFile(String path) {
        BufferedReader reader = null;
        String data = "";

        try {
            reader = new BufferedReader(new FileReader(new File(path)));
            String line;

            while ((line = reader.readLine()) != null) {
                data += line;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (NullPointerException | IOException e) {
                e.printStackTrace();
            }
        }

        if (inputChecker(data)) {
            shoppingList = new MyLinkedList<>();
            inputHandler(data);
        }
    }

    public void saveToSQL() {
        SessionFactory factory = cfg.buildSessionFactory();
        Session session = factory.openSession();
        String hgl = "DROP TABLE ShoppingItem";
        Transaction transaction = null;
        MyLinkedList<ShoppingItem> tmp = new MyLinkedList<>();

        try {
            transaction = session.beginTransaction();

            session.createQuery(hgl).executeUpdate();

            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }

            e.printStackTrace();
            return;
        } finally {
            session.close();
            factory.close();
        }

//        try {
//            transaction = session.beginTransaction();
//
//            for (int i = 0; i < shoppingList.size(); i++) {
//                session.persist(shoppingList.get(i));
//            }
//
//            transaction.commit();
//        } catch (HibernateException e) {
//            if (transaction != null) {
//                transaction.rollback();
//            }
//
//            e.printStackTrace();
//
//            try {
//                transaction = session.beginTransaction();
//
//                for (int i = 0; i < tmp.size(); i++) {
//                    session.persist(tmp.get(i));
//                }
//
//                transaction.commit();
//            } catch (HibernateException e) {
//                if (transaction != null) {
//                    transaction.rollback();
//                }
//
//                e.printStackTrace();
//            }
//        } finally {
//            session.close();
//            factory.close();
//        }
    }

    public void loadFromSQL() {
        SessionFactory factory = cfg.buildSessionFactory();
        Session session = factory.openSession();
        String hgl = "FROM ShoppingItem";
        MyLinkedList<ShoppingItem> tmp = new MyLinkedList<>();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            for (Object o : session.createQuery(hgl).getResultList()) {
                ShoppingItem item = (ShoppingItem) o;
                tmp.add(item);
            }

            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }

            e.printStackTrace();
            return;
        } finally {
            session.close();
            factory.close();
        }

        shoppingList = tmp;
    }
}
