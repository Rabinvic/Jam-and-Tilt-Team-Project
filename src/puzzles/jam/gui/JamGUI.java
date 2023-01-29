package puzzles.jam.gui;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import puzzles.common.Observer;
import puzzles.jam.model.Car;
import puzzles.jam.model.CarMask;
import puzzles.jam.model.JamModel;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.File;
import java.io.InputStream;

public class JamGUI extends Application  implements Observer<JamModel, String>  {
    /** The resources directory is located directly underneath the gui package */
    private final static String RESOURCES_DIR = "/puzzles/jam/gui/resources/";

    /**
     * the current loaded model
     */
    private JamModel model;

    /**
     * the buttons currently loaded on the gui
     */
    private Button[][] buttons;

    /**
     * the current javafx stage
     */
    private Stage stage;

    /**
     * initializes the gui
     */
    public void init() {
        String filename = getParameters().getRaw().get(0);
        model = new JamModel();
        model.loadFile(filename);
        model.addObserver(this);
    }

    /**
     * @return creates the clickable tiles on the gui with a given model
     */
    private GridPane getBoard() {
        CarMask mask = model.getMask();
        GridPane grid = new GridPane();
        int rows, cols;
        if(mask == null) {
            rows = 5;
            cols = 5;
        } else {
            rows = mask.getRows();
            cols = mask.getCols();
        }
        buttons = new Button[rows][cols];
        for(int row = 0; row < rows; row++) {
            for(int col = 0; col < cols; col++) {
                Button button = new Button();
                button.setMinSize(75, 75);
                button.setMaxSize(75, 75);
                grid.add(button, col, row);
                buttons[row][col] = button;
                int finalRow = row;
                int finalCol = col;
                button.setOnMouseClicked(event -> model.selectTile(finalRow, finalCol));
            }
        }
        grid.setMinSize(cols * 75, rows * 75);
        grid.setMaxSize(cols * 75, rows * 75);
        return grid;
    }

    /**
     * @return creates a new gui containing the button logic, tiles, and message with a given model
     */
    private BorderPane makeGui() {
        Text text = new Text("Message: ");
        GridPane grid = getBoard();
        updateButtons();
        FlowPane buttons = new FlowPane();
        Button load = new Button("Load");
        load.setOnMouseClicked(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Resource File");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Text Files", "*.txt")
            );
            fileChooser.setInitialDirectory(new File("data/jam"));
            File selectedFile = fileChooser.showOpenDialog(stage);
            if(selectedFile == null) {
                return;
            }
            model.loadFile(selectedFile.getPath());
        });
        Button hint = new Button("Hint");
        hint.setOnMouseClicked(event -> model.hint());
        Button reset = new Button("Reset");
        reset.setOnMouseClicked(event -> model.reset());
        buttons.getChildren().addAll(load, hint, reset);
        BorderPane gui = new BorderPane();
        gui.setTop(text);
        gui.setCenter(grid);
        gui.setBottom(buttons);
        return gui;
    }

    /**
     * Updates all the tiles on the gui. Goes through each car and checks what part of the car image that tiles should get. Otherwise, sets the bg image if not car is in that tiles.
     */
    private void updateButtons() {
        Car[] cars = model.getCars();
        String bg_path = RESOURCES_DIR + "bg.png";
        InputStream bg_resource = getClass().getResourceAsStream(bg_path);
        Image bg;
        if(bg_resource == null) {
            bg = null;
        } else {
            bg = new Image(bg_resource);
        }
        for(Button[] arr : buttons) {
            for(Button button : arr) {
                button.setGraphic(new ImageView(bg));
            }
        }
        if(cars != null) {
            for(Car car : cars) {
                String path = RESOURCES_DIR + car.file();
                InputStream resource = getClass().getResourceAsStream(path);
                if(resource == null) {
                    continue;
                }
                Image image = new Image(resource);
                double width = image.getWidth();
                double height = image.getHeight();
                double width_step = car.getOrientation() == Car.Orientation.HORIZONTAL ? width / car.getLength() : 0;
                double height_step = car.getOrientation() == Car.Orientation.VERTICAL ? height / car.getLength() : 0;
                double width_crop = car.getOrientation() == Car.Orientation.HORIZONTAL ? width / car.getLength() : width;
                double height_crop = car.getOrientation() == Car.Orientation.VERTICAL ? height / car.getLength() : height;
                PixelReader pixels = image.getPixelReader();
                for(int i = 0; i < car.getLength(); i++) {
                    WritableImage newImage = new WritableImage(pixels, (int) width_step * i, (int) height_step * i, (int) width_crop, (int) height_crop);
                    ImageView view = new ImageView(newImage);
                    int row = car.getRow() + (car.getOrientation() == Car.Orientation.HORIZONTAL ? 0 : i);
                    int col = car.getCol() + (car.getOrientation() == Car.Orientation.VERTICAL ? 0 : i);
                    buttons[row][col].setGraphic(view);
                }
            }
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        BorderPane gui = makeGui();
        Scene scene = new Scene(gui);
        stage.setScene(scene);
        stage.show();
        this.stage = stage;
    }

    @Override
    public void update(JamModel jamModel, String message) {
        this.model = jamModel;
        BorderPane pane = (BorderPane) stage.getScene().getRoot();
        Text text = (Text) pane.getTop();
        text.setText("Message: " + message);
        if(message.startsWith("Successfully loaded file")) {
            BorderPane newPane = new BorderPane();
            newPane.setTop(text);
            GridPane grid = getBoard();
            newPane.setCenter(grid);
            newPane.setBottom(pane.getBottom());
            stage.setScene(new Scene(newPane));
        }
        updateButtons();
    }

    /**
     * loads the gui with the file name as cmd argument 1
     * @param args arguments
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java JamGUI filename");
        } else {
            Application.launch(args);
        }
    }
}
