package puzzles.jam.model;

import puzzles.common.Observer;
import puzzles.common.solver.Configuration;
import puzzles.common.solver.Solver;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class JamModel {
    /** the collection of observers of this model */
    private final List<Observer<JamModel, String>> observers = new LinkedList<>();

    /** the current configuration */
    private JamConfig config;
    private boolean moveMode;
    private int moveModeRow, moveModeCol;
    private String fileName;
    private final Queue<JamConfig> hint;

    /**
     * creates a new jam model
     */
    public JamModel() {
        hint = new LinkedList<>();
    }

    /**
     * Tells the model to load a new file
     * @param fileName file path
     */
    public void loadFile(String fileName) {
        File file = new File(fileName);
        if(!file.exists()) {
            alertObservers("File: " + fileName + " does not exist.");
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file));) {
            String[] sizes = reader.readLine().split(" ");
            int rows = Integer.parseInt(sizes[0]);
            int cols = Integer.parseInt(sizes[1]);
            Car[] cars = new Car[Integer.parseInt(reader.readLine())];
            for (int i = 0; i < cars.length; i++) {
                String line = reader.readLine();
                if(line == null) continue;
                String[] str_data = line.split(" ");
                String letter = str_data[0];
                int[] data = new int[] {
                        Integer.parseInt(str_data[1]),
                        Integer.parseInt(str_data[2]),
                        Integer.parseInt(str_data[3]),
                        Integer.parseInt(str_data[4])

                };
                cars[i] = new Car(letter, data);
            }
            this.config = new JamConfig(rows, cols, cars);
            this.fileName = fileName;
            this.moveMode = false;
            this.hint.clear();
            alertObservers("Successfully loaded file " + file.getName());
        } catch (IOException e) {
            alertObservers("Error opining file: " + file.getName());
        } catch (Exception e) {
            alertObservers("Error parsing file: " + file.getName());
        }
    }

    /**
     * @return if the current model has a loaded game
     */
    public boolean isLoaded() {
        return fileName != null;
    }

    /**
     * @return the current config of the model
     */
    public JamConfig getConfig() {
        return config;
    }

    /**
     * Moves a car with the last selected (row,col) and the new selected (row,col)
     * @param row the new selected row
     * @param col the new selected col
     */
    private void moveCar(int row, int col) {
        int index = getCarIndex(moveModeRow, moveModeCol);
        boolean status;
        moveMode = false;
        if(moveModeRow != row && moveModeCol != col) {
            alertObservers("Illegal Move");
            return;
        } else if(moveModeRow != row) {
            // up down movement
            int amount = Math.abs(moveModeRow - row);
            if(moveModeRow < row) {
                status = down(index, amount);
            } else {
                status = up(index, amount);
            }
        } else if(moveModeCol != col) {
            // left right movement
            int amount = Math.abs(moveModeCol - col);
            if(moveModeCol < col) {
                status = right(index, amount);
            } else {
                status = left(index, amount);
            }
        } else {
            alertObservers("Cannot move car onto itself");
            return;
        }
        if(!status) {
            alertObservers("Illegal move");
        } else if(this.config.isSolution()) {
            if(!this.config.equals(hint.peek())) {
                hint.clear();
            }
            alertObservers("You have won!");
        } else {
            if(!this.config.equals(hint.peek())) {
                hint.clear();
            }
            alertObservers(String.format(
                    "Moved car at (%s,%s) to (%s,%s)", moveModeRow, moveModeCol, row, col
            ));
        }
    }

    /**
     * trys to move a car up
     * @param index the car index
     * @param amount the amount to move
     * @return if the car successfully moved
     */
    private boolean up(int index, int amount) {
        for(int i = 0; i < amount; i ++) {
            if(!config.cars[index].can_up(config.mask)) {
                return i != 0;
            }
            config.cars[index].up(config.mask);
        }
        return true;
    }

    /**
     * trys to move a car down
     * @param index the car index
     * @param amount the amount to move
     * @return if the car successfully moved
     */
    private boolean down(int index, int amount) {
        for(int i = 0; i < amount; i ++) {
            if(!config.cars[index].can_down(config.mask)) {
                return i != 0;
            }
            config.cars[index].down(config.mask);
        }
        return true;
    }

    /**
     * trys to move a car left
     * @param index the car index
     * @param amount the amount to move
     * @return if the car successfully moved
     */
    private boolean left(int index, int amount) {
        for(int i = 0; i < amount; i ++) {
            if(!config.cars[index].can_left(config.mask)) {
                return i != 0;
            }
            config.cars[index].left(config.mask);
        }
        return true;
    }

    /**
     * trys to move a car right
     * @param index the car index
     * @param amount the amount to move
     * @return if the car successfully moved
     */
    private boolean right(int index, int amount) {
        for(int i = 0; i < amount; i ++) {
            if(!config.cars[index].can_right(config.mask)) {
                return i != 0;
            }
            config.cars[index].right(config.mask);
        }
        return true;
    }

    /**
     * Gets the car's index at a given (row,col)
     * @param row the row of the car
     * @param col the col of the car
     * @return the index of the car, -1 if doesn't exist
     */
    private int getCarIndex(int row, int col) {
        char letter = config.mask.getValue(row, col);
        for(int i = 0; i < config.cars.length; i++) {
            if(config.cars[i].getLetter() == letter) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Select a car at a given (row,col)
     * @param row the row to select
     * @param col the col to select
     */
    private void selectCar(int row, int col) {
        char letter = config.mask.getValue(row, col);
        if(letter == Car.NULL) {
            alertObservers("Cannot select an empty space");
            return;
        }
        moveModeRow = row;
        moveModeCol = col;
        moveMode = true;
        alertObservers(
                String.format("Selected car at (%s,%s)", row, col)
        );
    }

    /**
     * Select a tile at a given (row,col)
     * @param row the row to select
     * @param col the col to select
     */
    public void selectTile(int row, int col) {
        if(fileName == null) {
            alertObservers("You have no file loaded");
            return;
        }
        if(config.isSolution()) {
            alertObservers("You have won!");
            return;
        }
        if (config.mask.unsafe(row, col)) {
            alertObservers("That selection is out of bounds");
            return;
        }
        if(!moveMode) {
            selectCar(row, col);
        } else {
            moveCar(row, col);
        }
    }

    /**
     * Get a hint from the model to see what the most efficient next move is
     */
    public void hint() {
        if(fileName == null) {
            alertObservers("You have no file loaded");
            return;
        }
        if(config.isSolution()) {
            alertObservers("You have won!");
            return;
        }
        if(hint.isEmpty()) {
            Solver solver = new Solver();
            Iterable<Configuration> solution = solver.solve(config);
            if(solution == null) {
                alertObservers("No solution could be found");
            } else {
                for (Configuration configuration : solution) {
                    hint.offer((JamConfig) configuration);
                }
                hint.remove();
                System.out.println(hint.size());
                this.config = hint.remove();
                if (this.config.isSolution()) {
                    alertObservers("You have won!");
                } else {
                    alertObservers("Here is a hint!");
                }
            }
        } else {
            this.config = hint.remove();
            if(this.config.isSolution()) {
                alertObservers("You have won!");
            } else {
                alertObservers("Here is a hint!");
            }
        }
    }

    /**
     * Reset the game to the original file
     */
    public void reset() {
        if(fileName == null) {
            alertObservers("You have no file loaded");
            return;
        }
        loadFile(fileName);
    }

    public CarMask getMask() {
        if(fileName == null) {
            return null;
        }
        return config.mask;
    }

    public Car[] getCars() {
        if(fileName == null) {
            return null;
        }
        return config.cars;
    }

    /**
     * The view calls this to add itself as an observer.
     *
     * @param observer the view
     */
    public void addObserver(Observer<JamModel, String> observer) {
        this.observers.add(observer);
    }

    /**
     * The model's state has changed (the counter), so inform the view via
     * the update method
     */
    private void alertObservers(String data) {
        for (var observer : observers) {
            observer.update(this, data);
        }
    }
}
