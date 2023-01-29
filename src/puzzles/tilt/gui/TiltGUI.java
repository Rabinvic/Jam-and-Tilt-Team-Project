package puzzles.tilt.gui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import puzzles.common.Observer;
import puzzles.tilt.model.TiltModel;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;

import java.io.File;

/**
 * A class to create a GUI for the Tilt puzzle
 *
 * @author Victor Rabinovich
 */
public class TiltGUI extends Application implements Observer<TiltModel, String> {
    /** The resources directory is located directly underneath the gui package */
    private final static String RESOURCES_DIR = "resources/";
    private TiltModel model;
    private GridPane board;//game board containing sliders
    private Label messageBox;//top text box
    private BorderPane gameBoard;//border pane containing game board and arrow buttons
    private BorderPane mainWindow;//main border pane
    private Image greenDisk = new Image(getClass().getResourceAsStream(RESOURCES_DIR+"green.png"));
    private Image blueDisk = new Image(getClass().getResourceAsStream(RESOURCES_DIR+"blue.png"));
    private Image blocker = new Image(getClass().getResourceAsStream(RESOURCES_DIR+"block.png"));
    private Image hole = new Image(getClass().getResourceAsStream(RESOURCES_DIR+"hole.png"));

    private double boardDIM=620;//Maximum size of board
    private Stage stage;

    /**
     * Initialize main layouts for UI, create model, and load the given config from cmd args
     */
    public void init() {
        model=new TiltModel();
        model.addObserver(this);
        messageBox = new Label();
        gameBoard=new BorderPane();
        mainWindow= new BorderPane();
        model.loadBoardFromFile(getParameters().getRaw().get(0));

    }

    /**
     * Populate the elements, set their sizes, and add them to the stage
     * @param stage the primary stage for this application, onto which
     * the application scene can be set.
     * Applications may create other stages, if needed, but they will not be
     * primary stages.
     * @throws Exception
     */
    @Override
    public void start(Stage stage) throws Exception {
        this.stage=stage;

        //Creates main game board including the tiles and arrow buttons
        gameBoard=makeGameBorderPane();
        mainWindow.setCenter(gameBoard);

        //Side panel with load, reset, and hint buttons
        VBox sidePanel=setSidePanel();
        mainWindow.setRight(sidePanel);
        sidePanel.setAlignment(Pos.CENTER);

        //The text box at top of the screen
        messageBox.setFont(Font.font(24));
        mainWindow.setTop(messageBox);

        //set the window
        Scene scene = new Scene(mainWindow);
        mainWindow.setMaxSize(820,738);

        this.stage.setScene(scene);
        this.stage.setResizable(false);
        this.stage.setTitle("Tilt");
        this.stage.show();
    }

    /**
     * Create the side panel, this includes the load reset, and hint buttons
     * @return sidePanel, the side panel for the UI
     */
    private VBox setSidePanel() {
        VBox sidePanel=new VBox();

        //Create and configure the load button
        Button load=new Button("Load");
        load.setFont(Font.font(18));
        load.setMinWidth(100);
        load.setOnAction(event -> chooseFile());

        //Create and configure the reset button
        Button reset=new Button("Reset");
        reset.setFont(Font.font(18));
        reset.setMinWidth(100);
        reset.setOnAction(event -> model.resetBoard());

        //Create and configure the hint button
        Button hint=new Button("Hint");
        hint.setFont(Font.font(18));
        hint.setMinWidth(100);
        hint.setOnAction(event -> model.getHint());

        sidePanel.getChildren().add(load);
        sidePanel.getChildren().add(reset);
        sidePanel.getChildren().add(hint);

        return sidePanel;
    }

    /**
     * Creates the main game board including the tiles and arrow buttons
     * @return
     */
    private BorderPane makeGameBorderPane(){
        BorderPane gameBoard=new BorderPane();
        //get the tiles, and add them to the center
        board=makeGameGridPane();
        gameBoard.setCenter(board);

        //Left arrow
        Button left=new Button("<");
        left.setOnAction(event -> model.tiltBoard("W"));
        left.setPrefSize(50,model.getSize()*boardDIM/model.getSize());
        left.setFont(Font.font(24));
        gameBoard.setLeft(left);

        //Right arrow
        Button right=new Button(">");
        right.setOnAction(event -> model.tiltBoard("E"));
        right.setPrefSize(50,model.getSize()*boardDIM/model.getSize());
        right.setFont(Font.font(24));
        gameBoard.setRight(right);

        //Up arrow
        Button up=new Button("^");
        up.setOnAction(event -> model.tiltBoard("N"));
        up.setPrefSize(model.getSize()*boardDIM/model.getSize()+100,50);
        up.setFont(Font.font(24));
        gameBoard.setTop(up);
        up.setAlignment(Pos.TOP_CENTER);

        //down arrow
        Button down=new Button("V");
        down.setOnAction(event -> model.tiltBoard("S"));
        down.setPrefSize(model.getSize()*boardDIM/model.getSize()+100,50);
        down.setFont(Font.font(24));
        gameBoard.setBottom(down);
        down.setAlignment(Pos.BOTTOM_CENTER);

        return gameBoard;
    }

    /**
     * Create the grid of tiles, sliders, and blockers
     * @return board The board of tiles, sliders, and blockers
     */
    private GridPane makeGameGridPane(){
        GridPane gameBoard=new GridPane();
        char[][] gameArray = model.getConfigBoard();
        Button b;
        for(int i=0; i< model.getSize(); i++){
            for(int j=0; j< model.getSize(); j++){
                b=new Button();
                //adjusts the size of button to fit in window
                b.setMinSize(boardDIM/model.getSize(),boardDIM/model.getSize());
                gameBoard.add(b,j,i);
                switch (gameArray[i][j]){
                    case 'G'->
                        //sets button image so that button size is same as image size
                        b.setBackground(new Background(
                                new BackgroundImage(greenDisk, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                                        new BackgroundSize(boardDIM/model.getSize(),boardDIM/model.getSize(),false,false,true,true))));
                    case 'B'->
                        //sets button image so that button size is same as image size
                        b.setBackground(new Background(
                                new BackgroundImage(blueDisk,BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                                        new BackgroundSize(boardDIM/model.getSize(),boardDIM/model.getSize(),false,false,true,true))));
                    case '*'->
                        //sets button image so that button size is same as image size
                        b.setBackground(new Background(
                                new BackgroundImage(blocker,BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                                        new BackgroundSize(boardDIM/model.getSize(),boardDIM/model.getSize(),false,false,true,true))));
                    case 'O'->
                        //sets button image so that button size is same as image size
                        b.setBackground(new Background(
                                new BackgroundImage(hole,BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                                        new BackgroundSize(boardDIM/model.getSize(),boardDIM/model.getSize(),false,false,true,true))));
                    default ->
                        //For empty tiles
                        b.setStyle("-fx-background-color: white; -fx-border-color: black;");
                }
            }
        }
        return gameBoard;
    }

    /**
     * Creates a FileChooser window and sends the selected file to the model to be loaded in.
     */
    private void chooseFile(){
        //Create the FileChooser window
        FileChooser fileChooser= new FileChooser();
        fileChooser.setTitle("Load a game board");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")+"/data/tilt"));
        //Get the user selection and send it to the model
        File selected=fileChooser.showOpenDialog(this.stage);
        int startIndex=selected.toString().indexOf("data");
        model.loadBoardFromFile(selected.toString().substring(startIndex));
    }

    /**
     * Update the game board
     */
    private void updateBoard(){
        this.gameBoard=makeGameBorderPane();
        this.board=makeGameGridPane();
        this.gameBoard.setCenter(board);
        this.mainWindow.setCenter(gameBoard);
    }

    /**
     * Get updates from the model
     * @param tiltModel the object that wishes to inform this object
     *                about something that has happened.
     * @param message optional data the server.model can send to the observer
     *
     */
    @Override
    public void update(TiltModel tiltModel, String message) {

        if (message.startsWith("Loaded")){ // game is loaded successfully
            messageBox.setText(message);
            updateBoard();
            return;
        }else if (message.startsWith("Failed")){ //Game failed to load
            messageBox.setText(message);
            updateBoard();
            return;
        } else if (message.startsWith("Hint")) { //Model is reporting a  hint
            messageBox.setText("Next Step");
            updateBoard();
            return;
        } else if (message.startsWith("Solved")) {//Game was already solved when using a hint
            messageBox.setText("Already "+message);
            updateBoard();
            return;
        } else if (message.startsWith("No")) {//No solution when looking for a hint
            messageBox.setText(message);
            updateBoard();
            return;
        } else if (message.startsWith("Illegal")) {//An illegal move is made by the user
            messageBox.setText(message+"  move. A blue slider will fall through the hole!");
            updateBoard();
            return;
        } else if (message.startsWith("RESET")) {//User resets the puzzle
            messageBox.setText("Puzzle Reset");
            updateBoard();
            return;
        }

        if (model.gameOver()) {//checks if game is over.
            messageBox.setText("You Win!");
            updateBoard();
            return;
        }
        messageBox.setText(message);
        updateBoard(); // renders the board

    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java TiltGUI filename");
        } else {
            Application.launch(args);
        }
    }
}
