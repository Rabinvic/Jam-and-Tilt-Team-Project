package puzzles.jam.model;

import java.util.Arrays;

public class CarMask {

    /**
     * the 2d array containing the mask
     */
    private final char[][] mask;

    /**
     * creates a empty car mask with a given size
     * @param rows the amount of rows
     * @param cols the amount of cols
     */
    protected CarMask(int rows, int cols) {
        this.mask = new char[rows][cols];
    }

    /**
     * A copy constructor to copy a mask from another mask
     * @param mask the mask to copy
     */
    protected CarMask(CarMask mask) {
        int l = mask.mask.length;
        int w = mask.mask[0].length;
        this.mask = new char[l][w];
        for(int x = 0; x < l; x++) {
            System.arraycopy(mask.mask[x], 0, this.mask[x], 0, w);
        }
    }

    /**
     * add a car's values to the mask
     * @param car the car to add
     */
    protected void addCar(Car car) {
        int row_start = car.getRow();
        int col_start = car.getCol();
        for(int row = row_start; row < (car.getOrientation() == Car.Orientation.HORIZONTAL ? row_start + 1 : row_start + car.getLength()); row++) {
            for(int col = col_start; col < (car.getOrientation() == Car.Orientation.VERTICAL ? col_start + 1 : col_start + car.getLength()); col++) {
                mask[row][col] = car.getLetter();
            }
        }
    }

    /**
     * set the mask's value at a given (row,col)
     * @param row the row
     * @param col the col
     * @param value the value to set
     */
    protected void setValue(int row, int col, char value) {
        mask[row][col] = value;
    }

    /**
     * get a value inside the mask
     * @param row the row to get
     * @param col the col to get
     * @return the value at the given (row,col)
     */
    public char getValue(int row, int col) {
        return mask[row][col];
    }

    /**
     * checks if the given (row,col) is out of bounds
     * @param row the row to check
     * @param col the col to check
     * @return if it's out of bounds and unsafe to access
     */
    protected boolean unsafe(int row, int col) {
        return row < 0 || col < 0 || row >= getRows() || col >= getCols();
    }

    /**
     * @return the amount of rows in the mask
     */
    public int getRows() {
        return mask.length;
    }

    /**
     * @return the amount of cols in the mask
     */
    public int getCols() {
        return mask[0].length;
    }

    @Override
    public boolean equals(Object other) {
        if(other instanceof CarMask config) {
            return Arrays.equals(this.mask, config.mask, Arrays::compare);
        }
        return false;
    }

}
