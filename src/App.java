import javafx.application.Application;

/**
 * App contains main method for MasterShopper9000
 * 
 * @author      Jyri Virtaranta jyri.virtaranta@cs.tamk.fi
 * @version     2017.11.05
 * @since       1.8
 */
public class App {
    /**
     * Launches either CLI or GUI based on command line parameters.
     *
     * <p>If not given parameters tries to launch GUI,
     * if that fails tries to launch CLI.
     * If given parameter "gui" will not try to launch CLI if GUI fails.
     * If given parameter "cli" will not try to launch GUI if CLI fails.</p>
     * 
     * @param  args  Command line parameters.
     */
    public static void main(String[] args) {
        CommandLineInterface cli = new CommandLineInterface();

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
