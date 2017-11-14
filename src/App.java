import javafx.application.Application;

/**
 * App
 * 
 * @author      Jyri Virtaranta jyri.virtaranta@cs.tamk.fi
 * @version     2017.11.05
 * @since       1.8
 */
public class App {
    private static CommandLineInterface cli;

    /**
     * App
     * 
     * @param  args  Command line parameters.
     */
    public static void main(String[] args) {
        cli = new CommandLineInterface();

        if (args.length > 0) {
            if (args[0].equals("gui")) {
                try {
                    Application.launch(GraphicalUserInterface.class);
                } catch (UnsupportedOperationException e) {
                    e.printStackTrace();
                }
            } else if (args[0].equals("cli")) {
                cli.run();
            }
        } else {
            try {
                Application.launch(GraphicalUserInterface.class);
            } catch (UnsupportedOperationException e) {
                e.printStackTrace();
                cli.run();
            }
        }
    }
}
