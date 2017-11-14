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
    private ShoppingListApp shoppingListApp;
    private Scanner scanner;
    private boolean isRunning = true;

    /**
     * Instantiates a new Command line interface.
     */
    public CommandLineInterface() {
        shoppingListApp = new ShoppingListApp();
        scanner = new Scanner(System.in);
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
            command = scanner.nextLine().toLowerCase();

            if (command.equals("exit") ||
                    command.equals("quit")) {
                stop();
            }

            if (command.equals("save")) {
                shoppingListApp.saveToFile();
            }

            if (command.equals("load")) {
                shoppingListApp.loadFromFile();
                showListContent();
            }

            if (shoppingListApp.inputChecker(command)) {
                shoppingListApp.inputHandler(command);
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
     * Prints shoppingList contents to console
     */
    private void showListContent() {
        System.out.println("Your Shopping List now:");

        for (int i = 0; i < shoppingListApp.getShoppingList().size(); i++) {
            ShoppingItem listItem = shoppingListApp.getShoppingList().get(i);
            String output = "  ";

            output += listItem.getAmount();
            output += " ";
            output += listItem.getName();

            System.out.println(output);
        }

        System.out.println();
    }
}
