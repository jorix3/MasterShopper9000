import fi.tamk.tiko.MyListPackage.MyLinkedList;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;

/**
 * GraphicalUserInterface
 * 
 * @author      Jyri Virtaranta jyri.virtaranta@cs.tamk.fi
 * @version     2017.11.14
 * @since       1.8
 */
public class GraphicalUserInterface extends Application {
    private final String ACCES_TOKEN = "ATeMJijlPUQAAAAAAAAIRNvyM_2hWgF-Yz" +
            "InniaPO_4dyad0JWEr8rocnqBE65ml";
    private ShoppingListApp shoppingListApp;
    private MyLinkedList<ShoppingItem> shoppingList;
    private TableView<ShoppingItem> table;
    private Scene scene;
    private TextField nameField;
    private TextField amountField;
    private FileChooser fileChooser;

    /**
     * @see Application#start(Stage) start
     */
    @Override
    public void start(Stage stage) {
        shoppingListApp = new ShoppingListApp();
        Label label = new Label("Label");
        Button addButton = new Button("Add");
        Button removeButton = new Button("Remove");
        BorderPane topPane = new BorderPane();
        BorderPane contentPane = new BorderPane();
        HBox bottomBar = new HBox();
        nameField = new TextField("name");
        amountField = new TextField("amount");
        table = createTable();
        fileChooser = new FileChooser();
        ExtensionFilter filter;
        filter = new ExtensionFilter("TXT files (*.txt)", "*.txt");
        String myFolder = "MasterShopper9000";
        File myAppFolder = new File(System.getProperty("user.home"), myFolder);

        if (!myAppFolder.exists()) {
            myAppFolder.mkdirs();
        }

        fileChooser.getExtensionFilters().add(filter);
        fileChooser.setInitialFileName("shoppingList.txt");
        fileChooser.setInitialDirectory(myAppFolder);

//        DbxRequestConfig config = new DbxRequestConfig("MasterShopper9000");
//        DbxClientV2 client = new DbxClientV2(config, ACCES_TOKEN);
//
//        FullAccount account = null;
//
//        try {
//            account = client.users().getCurrentAccount();
//        } catch (DbxException e) {
//            e.printStackTrace();
//        }
//
//        System.out.println(account.getName().getDisplayName());

        MenuBar menuBar = menuBarBuilder();

        stage.setTitle("MasterShopper9000");
        stage.centerOnScreen();
        stage.initStyle(StageStyle.DECORATED);
        stage.setMinWidth(375);
        stage.setMinHeight(300);

        label.setPadding(new Insets(10, 0, 0, 0));

        table.setEditable(true);
        updateTable();

        addButton.setMinWidth(75);
        removeButton.setMinWidth(75);

        addButton.setOnAction(event -> modifyShoppingList(false));
        removeButton.setOnAction(event -> modifyShoppingList(true));

        topPane.setTop(menuBar);
        topPane.setBottom(label);
        topPane.setAlignment(label, Pos.CENTER);

        bottomBar.getChildren().addAll(amountField,
                nameField,
                addButton,
                removeButton);

        bottomBar.setPadding(new Insets(10,10,0,10));
        bottomBar.setSpacing(10);
        bottomBar.setAlignment(Pos.CENTER);

        contentPane.setTop(topPane);
        contentPane.setBottom(bottomBar);
        contentPane.setCenter(table);
        contentPane.setPadding(new Insets(0, 0, 10, 0));
        contentPane.setAlignment(addButton, Pos.CENTER);

        scene = new Scene(contentPane, 640, 600);
        scene.getStylesheets().add("Style.css");

        stage.setScene(scene);
        stage.show();
    }

    /**
     * @see Application#stop() stop
     */
    @Override
    public void stop() {
        shoppingListApp.close();
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

        RadioMenuItem music = new RadioMenuItem("Background Music");
        MenuItem clear = new MenuItem("Clear List");
        MenuItem saveLocal = new MenuItem("Save");
        MenuItem saveJDB = new MenuItem("Save to JavaDB");
        MenuItem loadLocal = new MenuItem("Load");
        MenuItem loadJDB = new MenuItem("Load from JavaDB");
        MenuItem exit = new MenuItem("Exit");

        MenuItem cut = new MenuItem("Cut (ctrl+x) - disabled");
        MenuItem copy = new MenuItem("Copy (ctrl+c) - disabled");
        MenuItem paste = new MenuItem("Paste (ctrl+v) - disabled");

        MenuItem about = new MenuItem("About MasterShopper9000 - disabled");

        saveLocal.setOnAction(event -> saveFile());
        loadLocal.setOnAction(event -> loadFile());
        saveJDB.setOnAction(event -> shoppingListApp.saveToSQL());
        clear.setOnAction(event -> {
            shoppingList.clear();
            updateTable();
        });

        loadJDB.setOnAction(event -> {
            shoppingListApp.loadFromSQL();
            updateTable();
        });

        exit.setOnAction(event -> Platform.exit());

        music.setSelected(true);

        menuFile.getItems().addAll(
                clear,
                saveLocal,
                loadLocal,
                new SeparatorMenuItem(),
                saveJDB,
                loadJDB,
                new SeparatorMenuItem(),
                exit
        );

        menuSettings.getItems().addAll(music, cut, copy, paste);
        menuAbout.getItems().addAll(about);
        menuBar.getMenus().addAll(menuFile, menuSettings, menuAbout);

        return menuBar;
    }

    /**
     * Modify shoppingList based on text fields.
     *
     * <p>If text field 'name' is not on the list adds new items.
     * If it is on the list and given boolean is true it increases existing items amount.
     * If it is on the list and given boolean is false it decreases existing items amount.</p>
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

        amountCol.prefWidthProperty().bind(table.widthProperty().multiply(0.3));
        nameCol.prefWidthProperty().bind(table.widthProperty().multiply(0.7));

        amountCol.setResizable(false);
        nameCol.setResizable(false);

        table.getColumns().add(amountCol);
        table.getColumns().add(nameCol);

        return table;
    }
}
