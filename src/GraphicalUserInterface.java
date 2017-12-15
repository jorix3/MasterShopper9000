import fi.tamk.tiko.MyListPackage.MyLinkedList;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.*;
import java.util.Optional;
import java.util.Properties;

/**
 * GraphicalUserInterface
 * 
 * @author      Jyri Virtaranta jyri.virtaranta@cs.tamk.fi
 * @version     2017.11.14
 * @since       1.8
 */
public class GraphicalUserInterface extends Application {
    private ShoppingListApp shoppingListApp;
    private MyLinkedList<ShoppingItem> shoppingList;
    private TableView<ShoppingItem> table;
    private Scene scene;
    private TextField nameField;
    private TextField amountField;
    private FileChooser fileChooser;
    private Properties settings;
    private double windowWidth;
    private double windowHeight;
    private double windowXpos;
    private double windowYpos;
    private boolean localDatabase;

    /**
     * @see Application#start(Stage) start
     */
    @Override
    public void start(Stage stage) {
        // Read settings from shoppingListApp settings file //
        shoppingListApp = new ShoppingListApp();
        settings = new Properties();
        readSettings();

        // Create main window content //
        MenuBar menuBar = menuBarBuilder();
        Button addButton = new Button("Add");
        Button removeButton = new Button("Remove");
        BorderPane contentPane = new BorderPane();
        HBox bottomBar = new HBox();
        nameField = new TextField();
        amountField = new TextField();
        table = createTable();

        // Set file chooser filters and settings //
        fileChooser = new FileChooser();
        ExtensionFilter filter = new ExtensionFilter("TXT files (*.txt)",
                                                    "*.txt");

        fileChooser.getExtensionFilters().add(filter);
        fileChooser.setInitialFileName("shoppingList.txt");
        fileChooser.setInitialDirectory(shoppingListApp.getMyFolder());

        // Set stage settings //
        stage.setTitle("MasterShopper9000");
        stage.initStyle(StageStyle.DECORATED);
        stage.setMinWidth(375);
        stage.setMinHeight(300);

        if (windowXpos == -1 && windowYpos == -1) {
            stage.centerOnScreen();
        } else {
            stage.setX(windowXpos);
            stage.setY(windowYpos);
        }

        // Set main window content settings //
        nameField.setPromptText("name");
        amountField.setPromptText("amount");
        table.setEditable(true);
        updateTable();
        addButton.setMinWidth(75);
        removeButton.setMinWidth(75);
        addButton.setOnAction(event -> modifyShoppingList(false));
        removeButton.setOnAction(event -> modifyShoppingList(true));

        // Populate bottomBar and adjust visuals //
        bottomBar.getChildren().addAll(amountField,
                                        nameField,
                                        addButton,
                                        removeButton);

        bottomBar.setPadding(new Insets(10,10,0,10));
        bottomBar.setSpacing(10);
        bottomBar.setAlignment(Pos.CENTER);

        // Populate main content and set visuals //
        contentPane.setTop(menuBar);
        contentPane.setBottom(bottomBar);
        contentPane.setCenter(table);
        contentPane.setPadding(new Insets(0, 0, 10, 0));

        // Finish stage and scene //
        scene = new Scene(contentPane, windowWidth, windowHeight);
        scene.getStylesheets().add("Style.css");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @see Application#stop() close
     *
     * <p>Saves window position and size to settings file.</p>
     */
    @Override
    public void stop() {
        File settingsFile = shoppingListApp.getMySettingsFile();

        try (OutputStream output = new FileOutputStream(settingsFile)) {
            String width = String.valueOf(scene.getWidth());
            String height = String.valueOf(scene.getHeight());
            String xPos = String.valueOf(scene.getWindow().getX());
            String yPos = String.valueOf(scene.getWindow().getY());
            String isOnline = String.valueOf(localDatabase);

            settings.setProperty("windowWidth", width);
            settings.setProperty("windowHeight", height);
            settings.setProperty("windowXpos", xPos);
            settings.setProperty("windowYpos", yPos);
            settings.setProperty("localDatabase", isOnline);
            settings.store(output, "Settings file for ShoppingListApp");
        } catch (IOException e) {
            e.printStackTrace();
        }

        shoppingListApp.close();
    }

    /**
     * Read settings such as window position and size.
     */
    private void readSettings() {
        File settingsFile = shoppingListApp.getMySettingsFile();

        try (InputStream input = new FileInputStream(settingsFile)) {
            settings.load(input);
            String width = settings.getProperty("windowWidth", "640");
            String height = settings.getProperty("windowHeight", "600");
            String xPos = settings.getProperty("windowXpos", "-1");
            String yPos = settings.getProperty("windowYpos", "-1");
            String isOnline = settings.getProperty("localDatabase", "false");

            windowXpos = Double.parseDouble(xPos);
            windowYpos = Double.parseDouble(yPos);
            windowWidth = Double.parseDouble(width);
            windowHeight = Double.parseDouble(height);
            localDatabase = Boolean.valueOf(isOnline);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Open save to file dialog.
     */
    private void saveFile(){
        File file = fileChooser.showSaveDialog(scene.getWindow());

        if (file != null) {
            String path = file.getPath();
            shoppingListApp.saveToFile(path);
        }
    }

    /**
     * Open load from file dialog.
     */
    private void loadFile(){
        File file = fileChooser.showOpenDialog(scene.getWindow());

        if (file != null) {
            String path = file.getPath();
            shoppingListApp.loadFromFile(path);
            updateTable();
        }
    }

    /**
     * Creates a menubar.
     *
     * @return    the created menu bar
     */
    private MenuBar menuBarBuilder() {
        MenuBar menuBar = new MenuBar();

        Menu menuFile = new Menu("File");
        Menu menuSettings = new Menu("Settings");
        Menu menuAbout = new Menu("About");

        MenuItem clear = new MenuItem("Clear List");
        MenuItem saveLocal = new MenuItem("Save to file");
        MenuItem saveDatabase = new MenuItem("Save to database");
        MenuItem loadLocal = new MenuItem("Load from file");
        MenuItem loadDatabase = new MenuItem("Load from database");
        MenuItem saveDropBox = new MenuItem("Upload file to DropBox");
        MenuItem exit = new MenuItem("Exit");

        Menu database = new Menu("Choose database");
        RadioMenuItem sql = new RadioMenuItem("Use MySQL");
        RadioMenuItem derby = new RadioMenuItem("Use Derby");

        MenuItem sqlSettings = new MenuItem("Set MySQL settings");
        MenuItem sqlDefault = new MenuItem("Reset MySQL settings");
        MenuItem dropBoxSettings = new MenuItem("Set DropBox settings");
        MenuItem dropBoxDefault = new MenuItem("Reset DropBox settings");

        MenuItem about = new MenuItem("About MasterShopper9000 - disabled");

        ToggleGroup tg = new ToggleGroup();
        sql.setToggleGroup(tg);
        derby.setToggleGroup(tg);

        if (localDatabase) {
            derby.setSelected(true);
        } else {
            sql.setSelected(true);
        }

        clear.setOnAction(event -> {
            shoppingList.clear();
            updateTable();
        });

        saveLocal.setOnAction(event -> saveFile());
        loadLocal.setOnAction(event -> loadFile());

        saveDatabase.setOnAction(event -> {
            if (localDatabase) {
                shoppingListApp.saveToDerby();
            } else {
                shoppingListApp.saveToSQL();
            }
        });

        loadDatabase.setOnAction(event -> {
            if (localDatabase) {
                shoppingListApp.loadFromDerby();
            } else {
                shoppingListApp.loadFromSQL();
            }

            updateTable();
        });

        saveDropBox.setOnAction(event -> {
            File file = fileChooser.showOpenDialog(scene.getWindow());

            if (file != null) {
                shoppingListApp.saveToDropBox(file);
            }
        });

        exit.setOnAction(event -> Platform.exit());

        sqlSettings.setOnAction(event -> sqlSettingsWindow());
        sqlDefault.setOnAction(event -> {
            shoppingListApp.setDefaultSQLSettings();
            shoppingListApp.reloadSQLmanager();
        });

        dropBoxSettings.setOnAction(event -> dropBoxSettingsWindow());
        dropBoxDefault.setOnAction(event -> {
            shoppingListApp.setDefaultDropBoxSettings();
            shoppingListApp.reloadDropBoxManager();
        });

        menuFile.getItems().addAll(clear,
                                    saveLocal,
                                    loadLocal,
                                    new SeparatorMenuItem(),
                                    saveDatabase,
                                    loadDatabase,
                                    new SeparatorMenuItem(),
                                    saveDropBox,
                                    new SeparatorMenuItem(),
                                    exit);

        sql.setOnAction(event -> localDatabase = false);
        derby.setOnAction(event -> localDatabase = true);

        database.getItems().addAll(sql, derby);

        menuSettings.getItems().addAll(database,
                                        sqlSettings,
                                        sqlDefault,
                                        new SeparatorMenuItem(),
                                        dropBoxSettings,
                                        dropBoxDefault);

        menuAbout.getItems().addAll(about);
        menuBar.getMenus().addAll(menuFile, menuSettings, menuAbout);

        return menuBar;
    }

    /**
     * Open set SQL settings window.
     */
    private void sqlSettingsWindow() {
        Dialog<String[]> dialog = new Dialog<>();
        dialog.setTitle("SQL settings");

        ButtonType saveButton = new ButtonType("Save",
                                                ButtonBar.ButtonData.OK_DONE);

        dialog.getDialogPane()
                .getButtonTypes()
                .addAll(saveButton, ButtonType.CANCEL);

        dialog.getDialogPane()
                .lookupButton(saveButton)
                .setStyle("-fx-base: #ebebeb");
        dialog.getDialogPane()
                .lookupButton(ButtonType.CANCEL)
                .setStyle("-fx-base: #ebebeb");

        GridPane gp = new GridPane();
        gp.setHgap(10);
        gp.setVgap(10);
        gp.setPadding(new Insets(10));

        TextField host = new TextField();
        host.setPromptText("host");
        TextField database = new TextField();
        database.setPromptText("database");
        TextField userName = new TextField();
        userName.setPromptText("username");
        PasswordField password = new PasswordField();
        password.setPromptText("password");

        gp.add(new Label("Host name:"), 0,0);
        gp.add(host, 1, 0);
        gp.add(new Label("Database name:"), 0, 1);
        gp.add(database, 1, 1);
        gp.add(new Label("User name:"), 0, 2);
        gp.add(userName, 1, 2);
        gp.add(new Label("Password:"), 0, 3);
        gp.add(password, 1, 3);

        dialog.getDialogPane().setContent(gp);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButton
                    && !host.getText().isEmpty()
                    && !database.getText().isEmpty()
                    && !userName.getText().isEmpty()
                    && !password.getText().isEmpty()) {

                return new String[] {host.getText(), database.getText(),
                                    userName.getText(), password.getText()};
            }
            return null;
        });

        Optional<String[]> result = dialog.showAndWait();

        result.ifPresent(array -> {
            shoppingListApp.setSQLSettings(array[0],
                                            array[1],
                                            array[2],
                                            array[3]);
            shoppingListApp.reloadSQLmanager();
        });
    }

    /**
     * Open set DropBox settings window.
     */
    private void dropBoxSettingsWindow() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("DropBox settings");
        dialog.setHeaderText("You can generate an access token through: \n"
                            + "https://www.dropbox.com/developers/apps");

        ButtonType saveButton = new ButtonType("Save",
                ButtonBar.ButtonData.OK_DONE);

        dialog.getDialogPane()
                .getButtonTypes()
                .addAll(saveButton, ButtonType.CANCEL);

        dialog.getDialogPane()
                .lookupButton(saveButton)
                .setStyle("-fx-base: #ebebeb");
        dialog.getDialogPane()
                .lookupButton(ButtonType.CANCEL)
                .setStyle("-fx-base: #ebebeb");

        GridPane gp = new GridPane();
        gp.setHgap(10);
        gp.setVgap(10);
        gp.setPadding(new Insets(10));

        TextField accessToken = new TextField();
        accessToken.setPromptText("access token");
        accessToken.setPrefWidth(600.0);

        gp.add(new Label("Access Token:"), 0,0);
        gp.add(accessToken, 1, 0);

        dialog.getDialogPane().setContent(gp);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButton
                    && !accessToken.getText().isEmpty()) {

                return accessToken.getText();
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(content -> {
            shoppingListApp.setDropBoxSettings(content);
            shoppingListApp.reloadDropBoxManager();
        });
    }

    /**
     * Modify shoppingList based on text fields.
     *
     * <p>If text field 'name' is not on the list adds new items.
     * If it is on the list and given boolean is true,
     * it increases existing items amount.
     * If it is on the list and given boolean is false,
     * it decreases existing items amount.</p>
     *
     * @param  isSubtract  boolean determines if adding or subtracting
     */
    private void modifyShoppingList(boolean isSubtract) {
        if (!amountField.getText().isEmpty()
                && !nameField.getText().isEmpty()) {
            int amount;

            try {
                amount = Integer.parseInt(amountField.getText().trim());
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return;
            }

            if (isSubtract) {
                amount = 0 - amount;
            }

            String name = nameField.getText().trim();
            shoppingListApp.addToList(new ShoppingItem(amount, name));
        }

        updateTable();
    }

    /**
     * Update tableView contents to reflect shoppingList.
     */
    private void updateTable() {
        shoppingList = shoppingListApp.getShoppingList();
        table.getItems().clear();

        for (int i = 0; i < shoppingList.size(); i++) {
            table.getItems().add(shoppingList.get(i));
        }
    }

    /**
     * Create tableView for shoppingItems.
     *
     * @return  the created tableView
     */
    private TableView<ShoppingItem> createTable() {
        TableView<ShoppingItem> table = new TableView<>();
        TableColumn<ShoppingItem, Number> amountCol;
        TableColumn<ShoppingItem, String> nameCol;

        amountCol = new TableColumn<>("Amount");
        nameCol = new TableColumn<>("Name");

        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        amountCol.prefWidthProperty()
                .bind(table.widthProperty().multiply(0.3));
        nameCol.prefWidthProperty()
                .bind(table.widthProperty().multiply(0.7));

        amountCol.setResizable(false);
        nameCol.setResizable(false);

        table.getColumns().add(amountCol);
        table.getColumns().add(nameCol);

        return table;
    }
}
