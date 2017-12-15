import fi.tamk.tiko.MyListPackage.MyLinkedList;

import java.io.File;
import java.sql.*;

/**
 * DerbyManager
 * 
 * @author      Jyri Virtaranta jyri.virtaranta@cs.tamk.fi
 * @version     2017.12.15
 * @since       1.8
 */
public class DerbyManager {
    private String dbURL;
    private Connection connection = null;
    private Statement statement = null;
    private ResultSet resultSet = null;

    /**
     * Instantiates a new Derby manager.
     */
    public DerbyManager() {
        String path = System.getProperty("user.home")
                        + File.separator
                        + "DerbyManager";
        dbURL = "jdbc:derby:directory:" + path + "/.derby;create=true";
        start();
    }

    /**
     * Instantiates a new Derby manager.
     *
     * @param  path  filepath for derby database.
     */
    public DerbyManager(String path) {
        dbURL = "jdbc:derby:directory:" + path + "/.derby;create=true";
        start();
    }

    /**
     * Establish new connection.
     */
    private void start() {
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
            connection = DriverManager.getConnection(dbURL);
            statement = connection.createStatement();
        } catch (SQLException | ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Save given ShoppingItem list to database.
     *
     * @param  list  the list to save
     */
    public void save(MyLinkedList<ShoppingItem> list) {
        try {
            statement.execute("DROP TABLE App.ShoppingItem");
            statement.execute("CREATE TABLE App.ShoppingItem("
                                + "Name VARCHAR(50) PRIMARY KEY,"
                                + "Amount INT)");

            for (int i = 0; i < list.size(); i++) {
                ShoppingItem item = list.get(i);

                PreparedStatement ps = connection.prepareStatement(
                        "INSERT INTO App.ShoppingItem VALUES(?, ?)"
                );

                ps.setString(1, item.getName());
                ps.setInt(2, item.getAmount());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load list from database.
     *
     * @return   the loaded list
     */
    public MyLinkedList<ShoppingItem> load() {
        MyLinkedList<ShoppingItem> list = new MyLinkedList<>();

        try {
            resultSet = statement.executeQuery("SELECT Name, Amount FROM App.ShoppingItem ORDER BY Name");

            while (resultSet.next()) {
                String name = resultSet.getString(1);
                int amount = resultSet.getInt(2);

                list.add(new ShoppingItem(amount, name));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return list;
    }

    /**
     * Closing arguments. Close connection etc.
     */
    public void close() {
        try {
            DriverManager.getConnection("jdbc:derby:;shutdown=true");
        } catch (SQLException e) {
            e.printStackTrace();

            if (((e.getErrorCode() == 50000)
                    && ("XJ015".equals(e.getSQLState())))) {

                System.out.println("Derby shut down normally");
            } else {
                System.out.println("Derby did not shut down normally");
            }
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
