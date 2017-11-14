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

import java.io.*;

/**
 * App
 * 
 * @author      Jyri Virtaranta jyri.virtaranta@cs.tamk.fi
 * @version     2017.11.05
 * @since       1.8
 */
public class App extends Application {
    private static CommandLineInterface cli;
    private final int SCENE_WIDTH = 640;
    private static MyLinkedList<ShoppingItem> shoppingList;
    private static TableView<ShoppingItem> table;
    private static Scene scene;
    private static TextField nameField;
    private static TextField amountField;

    /**
     * App
     * 
     * @param  args  Command line parameters.
     */
    public static void main(String[] args) {
        cli = new CommandLineInterface();

        Thread cliThread = new Thread(() -> {
            cli.run();

            if (!cli.isRunning()) {
                Platform.exit();
            }
        });
        cliThread.start();

        launch(args);
    }

    @Override
    public void start(Stage stage) {
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
        cli.stop();
    }

    private void saveToFile() {
        String data = "";

        for (int i = 0; i < shoppingList.size(); i++) {
            data += shoppingList.get(i).getAmount() + " ";
            data += shoppingList.get(i).getName() + ";";
        }

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("shoppingList.txt"), "utf-8"))) {
            writer.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadFromFile() {
        BufferedReader reader = null;
        String data = "";
        String path = "shoppingList.txt";

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
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        cli.inputHandler(data);
        updateTable();
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

        save.setOnAction(event -> saveToFile());
        load.setOnAction(event -> loadFromFile());
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
            cli.addToList(new ShoppingItem(amount, name));
        }

        updateTable();
    }

    private void updateTable() {
        shoppingList = cli.getShoppingList();
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
        shoppingList = cli.getShoppingList();

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
