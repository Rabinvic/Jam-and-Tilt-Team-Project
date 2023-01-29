package puzzles.jam.model;

/**
 * A class to manage each car on the board
 */
@SuppressWarnings("RedundantIfStatement")
public class Car {

    /**
     * the null char to single that a mask cell is empty and contains no car
     */
    public static final char NULL = '\u0000';

    /**
     * The cars orientation
     */
    private final Orientation orientation;

    /**
     * The cars letter
     */
    private final char letter;

    /**
     * The length of the car
     */
    private final int length;

    /**
     * The position (row,col) of the head of the car
     */
    private int row, col;

    /**
     * Createa new car with a given letter and data formatted straight from the data file
     * @param letter the car's letter
     * @param data the 4 ints directly out of the data file (start row, start col, end row, end col)
     */
    protected Car(String letter, int[] data) {
        if(data.length != 4) {
            throw new IllegalArgumentException("data must me a int array of length 4 [start row, start col, end row, end col]");
        }
        this.letter = letter.toUpperCase().charAt(0);
        if(data[0] == data[2]) {
            this.orientation = Orientation.HORIZONTAL;
            this.length = data[3] - data[1] + 1;
        } else {
            this.orientation = Orientation.VERTICAL;
            this.length = data[2] - data[0] + 1;
        }
        this.row = data[0];
        this.col = data[1];
    }

    /**
     * A copy constructor to copy a car into another
     * @param other the car to copy
     */
    protected Car(Car other) {
        this.orientation = other.orientation;
        this.letter = other.letter;
        this.length = other.length;
        this.row = other.row;
        this.col = other.col;
    }

    /**
     * @param mask the mask to check on
     * @return if the car can move up
     */
    protected boolean can_up(CarMask mask) {
        if(orientation == Orientation.HORIZONTAL) {
            return false;
        }
        if(mask.unsafe(row - 1, col)) {
            return false;
        }
        if(mask.getValue(row - 1, col) != NULL) {
            return false;
        }
        return true;
    }

    /**
     * Move a car up on a given mask
     * @param mask the mask to modify
     */
    protected void up(CarMask mask) {
        mask.setValue(row - 1, col, letter);
        mask.setValue(row + length - 1,  col, NULL);
        row--;
    }

    /**
     * @param mask the mask to check on
     * @return if the car can move down
     */
    protected boolean can_down(CarMask mask) {
        if(orientation == Orientation.HORIZONTAL) {
            return false;
        }
        if(mask.unsafe(row + length, col)) {
            return false;
        }
        if(mask.getValue(row + length, col) != NULL) {
            return false;
        }
        return true;
    }

    /**
     * Move a car down on a given mask
     * @param mask the mask to modify
     */
    protected void down(CarMask mask) {
        mask.setValue(row + length, col, letter);
        mask.setValue(row,  col, NULL);
        row++;
    }

    /**
     * @param mask the mask to check on
     * @return if the car can move left
     */
    protected boolean can_left(CarMask mask) {
        if(orientation == Orientation.VERTICAL) {
            return false;
        }
        if(mask.unsafe(row, col - 1)) {
            return false;
        }
        if(mask.getValue(row, col - 1) != NULL) {
            return false;
        }
        return true;
    }

    /**
     * Move a car left on a given mask
     * @param mask the mask to modify
     */
    protected void left(CarMask mask) {
        mask.setValue(row, col - 1, letter);
        mask.setValue(row,  col + length - 1, NULL);
        col--;
    }

    /**
     * @param mask the mask to check on
     * @return if the car can move right
     */
    protected boolean can_right(CarMask mask) {
        if(orientation == Orientation.VERTICAL) {
            return false;
        }
        if(mask.unsafe(row, col + length)) {
            return false;
        }
        if(mask.getValue(row, col + length) != NULL) {
            return false;
        }
        return true;
    }

    /**
     * Move a car right on a given mask
     * @param mask the mask to modify
     */
    protected void right(CarMask mask) {
        mask.setValue(row, col + length, letter);
        mask.setValue(row,  col, NULL);
        col++;
    }

    /**
     * @return the row of the car
     */
    public int getRow() {
        return row;
    }

    /**
     * @return the col of the car
     */
    public int getCol() {
        return col;
    }

    /**
     * @return the length of the car
     */
    public int getLength() {
        return length;
    }

    /**
     * @return the orientation of the car
     */
    public Orientation getOrientation() {
        return orientation;
    }

    /**
     * @return the letter of the car
     */
    public char getLetter() {
        return letter;
    }

    /**
     * @return the image file path that represents the car
     */
    public String file() {
        return String.format("%s-%s.png", letter, orientation.name);
    }

    /**
     * An enum that represents the car's either VERTICAL or HORIZONTAL orientation
     */
    public enum Orientation {
        VERTICAL("vert"),
        HORIZONTAL("hori");

        /**
         * the orientation representation in the image file name
         */
        public final String name;

        /**
         * creates a new orentation with a given name that is representation in the image file names
         * @param name the that is representation in the image file names
         */
        Orientation(String name) {
            this.name = name;
        }
    }

    @Override
    public boolean equals(Object other) {
        if(other instanceof Car car) {
            return this.row == car.row &&
                    this.col == car.col &&
                    this.letter == car.letter;
        }
        return false;
    }

}
