import fi.tamk.tiko.MyListPackage.MyLinkedList;
import java.util.Scanner;

/**
 * CommandLineInterface
 *
 * <p>Command line interface for MasterShopper9000 application</p>
 *
 * @author      Jyri Virtaranta jyri.virtaranta@cs.tamk.fi
 * @version     2017.11.05
 * @since       1.8
 */
public class CommandLineInterface {
    private Scanner scanner;
    private MyLinkedList<ShoppingItem> shoppingList;
    private boolean isRunning = true;

    /**
     * Instantiates a new Command line interface.
     */
    public CommandLineInterface() {
        scanner = new Scanner(System.in);
        shoppingList = new MyLinkedList<>();
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
     * Returns isRunning boolean.
     *
     * @return  boolean  true if command line interface is running
     */
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * Runs the command line interface.
     */
    public void run() {
        String command;

        System.out.println("SHOPPING LIST");
        System.out.println("Tampere University of Applied Sciences");

        while (isRunning) {
            System.out.println("Give shopping list " +
                    "(example: 1 milk;2 tomato;3 carrot;)");
            command = scanner.nextLine();

            if (command.toLowerCase().equals("exit") ||
                    command.toLowerCase().equals("quit")) {
                stop();
            }

            if (inputChecker(command)) {
                inputHandler(command.toLowerCase());
                showListContent();
            }
        }
    }

    /**
     * Stops the command line interface
     */
    public void stop() {
        System.exit(0);
    }

    /**
     * Checks if given user input starts with a number followed by a space.
     *
     * @param   command  given user input as String
     * @return  boolean  true if input starts with number followed by space
     */
    private boolean inputChecker(String command) {
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
     * Prints shoppingList contents to console
     */
    private void showListContent() {
        System.out.println("Your Shopping List now:");

        for (int i = 0; i < shoppingList.size(); i++) {
            ShoppingItem listItem = shoppingList.get(i);
            String output = "  ";

            output += listItem.getAmount();
            output += " ";
            output += listItem.getName();

            System.out.println(output);
        }

        System.out.println();
    }
}
