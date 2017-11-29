import fi.tamk.tiko.MyListPackage.MyLinkedList;

import java.io.*;
import java.util.Properties;

/**
 * ShoppingListApp
 * 
 * @author      Jyri Virtaranta jyri.virtaranta@cs.tamk.fi
 * @version     2017.11.14
 * @since       1.8
 */
public class ShoppingListApp {
    private Properties settings;
    private MyLinkedList<ShoppingItem> shoppingList;
    private SQLmanager sqlManager;
    private File myFolder;
    private File mySettingsFile;
    private boolean online;
    private final String HOST_TAG = "host";
    private final String DATABASE_TAG = "database";
    private final String USERNAME_TAG = "username";
    private final String PASSWORD_TAG = "password";

    /**
     * Instantiates a new ShoppingListApp.
     */
    public ShoppingListApp() {
        setOnline(true);
        localServices();
        runWebServices();
    }

    public ShoppingListApp(boolean online) {
        setOnline(online);
        localServices();

        if (isOnline()) {
            runWebServices();
        }
    }

    private void localServices() {
        String myFolderPath = System.getProperty("user.home")
                                + File.separator
                                + "MasterShopper9000";
        String mySettingsFilePath = myFolderPath
                                    + File.separator
                                    + "settings.cfg";
        myFolder = new File(myFolderPath);
        mySettingsFile = new File(mySettingsFilePath);
        settings = new Properties();
        shoppingList = new MyLinkedList<>();

        if (!myFolder.exists()) {
            myFolder.mkdirs();
        }

        if (!mySettingsFile.exists()) {
            setDefaultSQLSettings();
        }
    }

    public void runWebServices() {
        if (sqlManager != null) {
            sqlManager.close();
            sqlManager = null;
        }

        try (InputStream input = new FileInputStream(mySettingsFile)) {
            settings.load(input);
            sqlManager = new SQLmanager(settings.getProperty(HOST_TAG),
                                        settings.getProperty(DATABASE_TAG),
                                        settings.getProperty(USERNAME_TAG),
                                        settings.getProperty(PASSWORD_TAG));
        } catch (IOException e) {
            e.printStackTrace();
            setOnline(false);
        }
    }

    public void setDefaultSQLSettings() {
        try (OutputStream output = new FileOutputStream(mySettingsFile)) {
            settings.setProperty(HOST_TAG, "mydb.tamk.fi");
            settings.setProperty(DATABASE_TAG, "dbc6jyrvir1");
            settings.setProperty(USERNAME_TAG, "c6jyrvir");
            settings.setProperty(PASSWORD_TAG, "1Salasana");
            settings.store(output, "Settings file for ShoppingListApp");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setCustomSQLSettings(String host, String database,
                                  String userName, String password) {

        try (OutputStream output = new FileOutputStream(mySettingsFile)) {
            settings.setProperty(HOST_TAG, host);
            settings.setProperty(DATABASE_TAG, database);
            settings.setProperty(USERNAME_TAG, userName);
            settings.setProperty(PASSWORD_TAG, password);
            settings.store(output, "Settings file for ShoppingListApp");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Shutdown procedures.
     */
    public void close() {
        if (isOnline()) {
            sqlManager.close();
        }
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
     *
     * @param  path  the file path for the txt file
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
     *
     * @param  path  the file path for the txt file
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

    /**
     * Saves shoppingList contents to SQL database.
     */
    public void saveToSQL() {
        if (isOnline()) {
            sqlManager.deleteAll();
            sqlManager.save(getShoppingList());
        }
    }

    public void addNewSetting(String tag, String value) {
        try (OutputStream out = new FileOutputStream(mySettingsFile)) {
            settings.setProperty(tag, value);
            settings.store(out, "Settings file for ShoppingListApp");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readSetting(String tag) {
        String result = "";

        try (InputStream input = new FileInputStream(mySettingsFile)) {
            settings.load(input);
            result = settings.getProperty(tag);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Loads shoppingList contents from SQL database.
     */
    public void loadFromSQL() {
        if (isOnline()) {
            setShoppingList(sqlManager.load());
        }
    }

    public File getMyFolder() {
        return myFolder;
    }

    public File getMySettingsFile() {
        return mySettingsFile;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }
}
