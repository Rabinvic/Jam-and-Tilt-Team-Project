package puzzles.jam.ptui;

import puzzles.common.Observer;
import puzzles.jam.model.Car;
import puzzles.jam.model.CarMask;
import puzzles.jam.model.JamModel;

import java.util.Arrays;
import java.util.Scanner;

public class JamPTUI implements Observer<JamModel, String> {

    /**
     * The current model of the game
     */
    private JamModel model;

    /**
     * The help message
     */
    private static final String HELP = """
        The following commands are:
            h(int)              -- hint next move
            l(oad) filename     -- load new puzzle file
            s(elect) r c        -- select cell at r, c
            q(uit)              -- quit the game
            r(eset)             -- reset the current game
        """;

    /**
     * Starts the ptui with a given file name
     * @param fileName the file name
     */
    private void start(String fileName) {
        this.model = new JamModel();
        model.addObserver(this);
        System.out.println(HELP);
        model.loadFile(fileName);
    }

    /**
     * Attempts to load a file with the given arguments
     * @param args cmd arguments
     */
    private void load(String[] args) {
        if(args.length < 1) {
            System.out.println("Usage: l(oad) filename");
            command();
        } else {
            model.loadFile(args[0]);
        }
    }

    /**
     * Selects a square in the model, arg 1 is row, arg 2 is col
     * @param args cmd arguments
     */
    private void select(String[] args) {
        if(args.length < 2) {
            System.out.println("Usage: s(elect) r c");
            command();
        } else {
            try {
                int row = Integer.parseInt(args[0]);
                int col = Integer.parseInt(args[1]);
                model.selectTile(row, col);
            } catch (NumberFormatException e) {
                System.out.println("Error: Row and Col must be valid integers");
                command();
            }
        }
    }

    /**
     * Prompts the user for a command input and then try's to execute that command
     */
    private void command() {
        System.out.print("Enter a command: ");
        String input = new Scanner(System.in).nextLine();
        String[] args = input.split(" ");
        String invoke = args[0];
        switch (invoke) {
            case "h", "hint" -> model.hint();
            case "l", "load" -> load(Arrays.copyOfRange(args, 1, args.length));
            case "s", "select" -> select(Arrays.copyOfRange(args, 1, args.length));
            case "q", "quit" -> System.exit(0);
            default -> {
                System.out.println(HELP);
                command();
            }
        }
    }

    /**
     * Prints the board to the standard output given a certin car mask
     * @param mask the car mask
     */
    private void board(CarMask mask) {
        int x_max = mask.getCols();
        int y_max = mask.getRows();
        for(int row = -1; row < x_max + 1; row++) {
            for(int col = -1; col < y_max + 1; col++) {
               String toPrint = " ";
                if((row == -1 || row == x_max) && (col == -1 || col == y_max)) {
                    toPrint += " ";
                } else if(row == -1 || row == y_max) {
                    toPrint += col;
                } else if(col == -1 || col == x_max) {
                    toPrint += row;
                } else {
                    char letter = mask.getValue(row, col);
                    if(letter == Car.NULL) {
                        letter = '.';
                    }
                    toPrint += letter;
                }
                if(col == y_max) {
                    System.out.println(toPrint);
                } else {
                    System.out.print(toPrint);
                }
            }
        }
    }

    /**
     * Update the current screen with the new board and message
     * @param message the current message
     */
    private void show(String message) {
        CarMask mask = model.getMask();
        if(mask != null) {
            board(mask);
        }
        System.out.printf("Message: %s%n", message);
    }

    @Override
    public void update(JamModel jamModel, String message) {
        this.model = jamModel;
        show(message);
        command();
    }

    /**
     * Starts a jam game on the ptui with a given filename as argument 1
     * @param args arguments
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java JamPTUI filename");
        }
        new JamPTUI().start(args[0]);
    }
}
