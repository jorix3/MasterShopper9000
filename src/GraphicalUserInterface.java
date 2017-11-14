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
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * GraphicalUserInterface
 * 
 * @author      Jyri Virtaranta jyri.virtaranta@cs.tamk.fi
 * @version     2017.11.14
 * @since       1.8
 */
public class GraphicalUserInterface extends Application {
    private ShoppingListApp shoppingListApp;
    private final int SCENE_WIDTH = 640;
    private static MyLinkedList<ShoppingItem> shoppingList;
    private static TableView<ShoppingItem> table;
    private static Scene scene;
    private static TextField nameField;
    private static TextField amountField;

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

        MenuBar menuBar = menuBarBuilder();

        stage.setTitle("MasterShopper9000");
        stage.centerOnScreen();
        stage.initStyle(StageStyle.DECORATED);

        label.setPadding(new Insets(10, 0, 0, 0));

        table.setEditable(true);
        updateTable();

        addButton.setOnAction(event -> modifyShoppingItem(false));
        removeButton.setOnAction(event -> modifyShoppingItem(true));

        topPane.setTop(menuBar);
        topPane.setBottom(label);
        topPane.setAlignment(label, Pos.CENTER);

        bottomBar.getChildren().addAll(amountField,
                nameField,
                addButton,
                removeButton);

        bottomBar.setPadding(new Insets(10,0,0,0));
        bottomBar.setSpacing(10);
        bottomBar.setAlignment(Pos.CENTER);

        contentPane.setTop(topPane);
//        contentPane.setRight(slider);
        contentPane.setBottom(bottomBar);
        contentPane.setCenter(table);
        contentPane.setPadding(new Insets(0, 0, 10, 0));
        contentPane.setAlignment(addButton, Pos.CENTER);

        scene = new Scene(contentPane, SCENE_WIDTH, 600);
        scene.getStylesheets().add("Style.css");
        stage.setScene(scene);

        stage.show();
    }

    @Override
    public void stop() {

    }


    private MenuBar menuBarBuilder() {
        MenuBar menuBar = new MenuBar();

        Menu menuFile = new Menu("File");
        Menu menuEdit = new Menu("Edit");
        Menu menuAbout = new Menu("About");

        RadioMenuItem music = new RadioMenuItem("Background Music");
        MenuItem save = new MenuItem("Save");
        MenuItem load = new MenuItem("Load");
        SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();
        MenuItem exit = new MenuItem("Exit");

        MenuItem cut = new MenuItem("Cut (ctrl+x) - disabled");
        MenuItem copy = new MenuItem("Copy (ctrl+c) - disabled");
        MenuItem paste = new MenuItem("Paste (ctrl+v) - disabled");

        MenuItem about = new MenuItem("About Lotto App  - disabled");

        save.setOnAction(event -> shoppingListApp.saveToFile());
        load.setOnAction(event -> {
            shoppingListApp.loadFromFile();
            updateTable();
        });
        exit.setOnAction(event -> Platform.exit());

        music.setSelected(true);

        menuFile.getItems().addAll(music,
                save,
                load,
                separatorMenuItem,
                exit);

        menuEdit.getItems().addAll(cut, copy, paste);
        menuAbout.getItems().addAll(about);
        menuBar.getMenus().addAll(menuFile, menuEdit, menuAbout);

        return menuBar;
    }

    private void modifyShoppingItem(boolean isNegative) {
//        cli.addToList(new ShoppingItem(5, "test-beer"));
//        cli.addToList(new ShoppingItem(2, "test-milk"));

        if (!amountField.getText().isEmpty()
                && !nameField.getText().isEmpty()) {
            int amount;

            try {
                amount = Integer.parseInt(amountField.getText().trim());
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return;
            }

            if (isNegative) {
                amount = 0 - amount;
            }

            String name = nameField.getText().trim();
            shoppingListApp.addToList(new ShoppingItem(amount, name));
        }

        updateTable();
    }

    private void updateTable() {
        shoppingList = shoppingListApp.getShoppingList();
        table.getItems().clear();

        for (int i = 0; i < shoppingList.size(); i++) {
            ShoppingItem item = shoppingList.get(i);
            table.getItems().add(item);
        }
    }

    private TableView<ShoppingItem> createTable() {
        TableView<ShoppingItem> table = new TableView<>();
        TableColumn<ShoppingItem, Number> amountCol;
        TableColumn<ShoppingItem, String> nameCol;
        shoppingList = shoppingListApp.getShoppingList();

        amountCol = new TableColumn<>("Amount");
        nameCol = new TableColumn<>("Name");

        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        amountCol.prefWidthProperty().bind(table.widthProperty().multiply(0.3));
        nameCol.prefWidthProperty().bind(table.widthProperty().multiply(0.7));

        for (int i = 0; i < shoppingList.size(); i++) {
            table.getItems().add(shoppingList.get(i));
        }

        table.getColumns().add(amountCol);
        table.getColumns().add(nameCol);

        return table;
    }
}
