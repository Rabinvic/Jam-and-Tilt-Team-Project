package puzzles.tilt.ptui;

import puzzles.common.Observer;
import puzzles.tilt.model.TiltModel;

import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * A class to create a PTUI for the Tilt puzzle
 *
 * @author Victor Rabinovich
 */
public class TiltPTUI implements Observer<TiltModel, String> {
    private TiltModel model;
    private Scanner in;
    private boolean gameOn;//Whether we are currently in an active game

    /**
     * Construct the PTUI and create the model and scanner
     */
    public TiltPTUI(){
        model=new TiltModel();
        model.addObserver(this);
        gameOn=false;
        in=new Scanner(System.in);
    }

    /**
     * Loop to process user inputs when in an active game
     */
    private void gameLoop(){
        while(gameOn) {
            String command = in.nextLine().strip();
            command=command.toUpperCase();
            if (command.equals("Q") || command.equals("QUIT")) {//Quit the game
                System.out.println("Quitting to main menu.");
                gameOn = false;
                System.exit(0);
                return;
            } else if(command.equals("H") || command.equals("HINT")){//Get a hint
                model.getHint();
            } else if (command.startsWith("L") || command.startsWith("LOAD")) {//Load a new board
                String[] commandArray=command.split("\\s");
                model.loadBoardFromFile(commandArray[1].toLowerCase());
            } else if (command.equals("RESET") || command.equals("R")) {//Reset current board
                model.resetBoard();
            } else if(command.startsWith("T") || command.startsWith("TILT")){
                //Process a game move
                try {
                    Scanner s = new Scanner(command);
                    String[] move = s.nextLine().split("\\s");
                    model.tiltBoard(move[1]);
                } catch (Exception e) {
                    printHelperInstructions();
                }
            }else{
                //Shows commands is no proper command is given
                printHelperInstructions();
            }
        }
    }

    /**
     * Print the command instructions
     */
    private void printHelperInstructions(){
        System.out.println("h(int)              -- hint next move\n" +
                "l(oad) filename     -- load new puzzle file\n" +
                "t(ilt) {N|S|E|W}    -- tilt the board in the given direction\n" +
                "q(uit)              -- quit the game\n" +
                "r(eset)             -- reset the current game");
    }

    /**
     * Get updates from the model
     * @param model the object that wishes to inform this object
     *                about something that has happened.
     * @param message optional data the server.model can send to the observer
     *
     */
    @Override
    public void update(TiltModel model, String message) {
        if (message.startsWith("Loaded")){ // game is loaded successfully
            System.out.println(message);
            model.displayBoard();
            System.out.println();
            gameOn=true;
            return;
        }else if (message.startsWith("Failed")){ //Game failed to load
            System.out.println(message);
            model.displayBoard();
            System.out.println();
            return;
        } else if (message.startsWith("Hint")) { //Model is reporting a  hint
            System.out.println("Next Step!");
            model.displayBoard();
            System.out.println();
            return;
        } else if (message.startsWith("Solved")) {//Game was already solved when using a hint
            System.out.println("Already "+message);
            model.displayBoard();
            System.out.println();
            return;
        } else if (message.startsWith("No")) {//No solution when looking for a hint
            System.out.println(message);
            model.displayBoard();
            System.out.println();
            return;
        } else if (message.startsWith("Illegal")) {//An illegal move is made by the user
            System.out.println(message+"  move. A blue slider will fall through the hole!");
            model.displayBoard();
            System.out.println();
            return;
        } else if (message.startsWith("RESET")) {//User resets the puzzle
            System.out.println("Puzzle reset!");
            model.displayBoard();
            System.out.println();
            return;
        }

        if (model.gameOver()) {//checks if game is over.
            model.displayBoard();
            System.out.println("You win. Good for you.");
            gameOn = false;
            return;
        }
        model.displayBoard(); // renders the board
        System.out.println(message);
    }

    /**
     * Starts the PTUI
     */
    public void run(String fileName) {
        model.loadBoardFromFile(fileName);//Load the starting file
        printHelperInstructions();
        //Run the game
        while (true) {
            gameLoop(); // gameplay
        }

    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java TiltPTUI filename");
        }else{
            TiltPTUI ui = new TiltPTUI();
            ui.run(args[0]);
        }
    }
}
