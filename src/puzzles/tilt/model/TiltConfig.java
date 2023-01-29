package puzzles.tilt.model;


import puzzles.common.solver.Configuration;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

/**
 * A program that represents a configuration of the Tilt puzzle and can form the following configurations
 * based on the rules of the game
 *
 * @author Victor Rabinovich
 */

public class TiltConfig implements Configuration {
    private static final char G_SLIDER = 'G';
    private static final char B_SLIDER = 'B';
    private static final char HOLE = 'O';
    private static final char EMPTY = '.';
    private static final char BLOCKER = '*';

    protected static int boardSize;//Side length of the board
    private static int NUM_BLUE;//Number of blue sliders the puzzle starts with
    private int numGreen = 0, currentNumBlue = 0;//The number of each type of slider in the current config
    protected char[][] currentBoard;//The board of the current config


    /**
     * Constructor to create the initial config for a setup of the Tilt puzzle
     * @param boardSize The side length of the board
     * @param boardLayout The layout of the game
     */
    public TiltConfig(int boardSize, char[][] boardLayout) {
        TiltConfig.boardSize = boardSize;
        this.currentBoard = boardLayout;

        //Look through the board and count number of sliders
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                switch (boardLayout[i][j]) {
                    case G_SLIDER ->
                        numGreen++;
                    case B_SLIDER ->
                        currentNumBlue++;
                }
            }
        }
        NUM_BLUE = currentNumBlue;
    }

    /**
     * A constructor to create a copy of a given configuration
     * @param other The config to be copied
     */
    private TiltConfig(TiltConfig other) {
        this.numGreen = other.numGreen;
        this.currentNumBlue = other.currentNumBlue;

        //Copy the board
        this.currentBoard = new char[boardSize][boardSize];
        for (int i = 0; i < boardSize; i++) {
            System.arraycopy(other.currentBoard[i], 0, this.currentBoard[i], 0, boardSize);
        }
    }

    /**
     * Determine if the current config is a solution to the puzzle
     * @return isSolution: whether the config is a solution
     */
    @Override
    public boolean isSolution() {
        //Checks is there are any green sliders and if # of blue sliders is still the same as the start
        return (numGreen == 0) && (currentNumBlue == NUM_BLUE);
    }

    /**
     * Creates a collection of the neighboring configurations
     * @return neighbors The collection of neighboring configs
     */
    @Override
    public Collection<Configuration> getNeighbors() {
        Collection<Configuration> neighbors = new LinkedList<>();
        //Neighbor tilting down
        if (southNeighbor() != null) {
            neighbors.add(southNeighbor());
        }
        //Neighbor tilting up
        if (northNeighbor() != null) {
            neighbors.add(northNeighbor());
        }
        //Neighbor tilting left
        if (eastNeighbor() != null) {
            neighbors.add(eastNeighbor());
        }
        //Neighbor tilting right
        if (westNeighbor() != null) {
            neighbors.add(westNeighbor());
        }
        return neighbors;
    }

    /**
     * Get the neighbor resulting from tilting the board up.
     * Used for the solver
     * @return north The neighbor resulting from tilting up
     */
    public Configuration northNeighbor() {
        TiltConfig c = new TiltConfig(this);
        int openIndex;//The index in the column which a slider can slide to

        //loop from top to bottom, right to left
        for (int i = 0; i < boardSize; i++) {
            openIndex = 0;
            for (int j = 0; j < boardSize; j++) {
                switch (c.currentBoard[j][i]) {
                    case BLOCKER -> openIndex = j + 1;//Set next open slot underneath the blocker
                    case HOLE -> openIndex = j;//any slider will just fall through making this an empty tile
                    case B_SLIDER -> {
                        if (c.currentBoard[openIndex][i] == HOLE) {//Illegal move
                            return null;
                        } else {//Move slider
                            c.currentBoard[j][i] = EMPTY;
                            c.currentBoard[openIndex][i] = B_SLIDER;
                            openIndex++;//set open index below slider
                        }
                    }
                    case G_SLIDER -> {
                        c.currentBoard[j][i] = EMPTY;
                        if (c.currentBoard[openIndex][i] == HOLE) {//Remove slider from board if it will fall in hole
                            c.numGreen--;
                        } else {//Just move the slider
                            c.currentBoard[openIndex][i] = G_SLIDER;
                            openIndex++;//set open index below slider
                        }
                    }
                }
            }
        }
        return c;
    }

    /**
     * Get the neighbor resulting from tilting the board down
     * Used for the solver
     * @return south The neighbor resulting from tilting down
     */
    public Configuration southNeighbor() {
        TiltConfig c = new TiltConfig(this);
        int openIndex;//The index in the column which a slider can slide to

        //loop from bottom to top, right to left
        for (int i = 0; i < boardSize; i++) {
            openIndex = boardSize - 1;
            for (int j = boardSize - 1; j >= 0; j--) {
                switch (c.currentBoard[j][i]) {
                    case BLOCKER -> openIndex = j - 1;//Set next open slot atop the blocker
                    case HOLE -> openIndex = j;//any slider will just fall through making this an empty tile
                    case B_SLIDER -> {
                        if (c.currentBoard[openIndex][i] == HOLE) {//Illegal move
                            return null;
                        } else {
                            c.currentBoard[j][i] = EMPTY;
                            c.currentBoard[openIndex][i] = B_SLIDER;
                            openIndex--;//Set open space to above slider
                        }
                    }
                    case G_SLIDER -> {
                        c.currentBoard[j][i] = EMPTY;
                        if (c.currentBoard[openIndex][i] == HOLE) {//Remove slider if it will go through hole
                            c.numGreen--;
                        } else {//move slider
                            c.currentBoard[openIndex][i] = G_SLIDER;
                            openIndex--;//Set open space to above slider
                        }

                    }
                }
            }
        }
        return c;
    }

    /**
     * Get the neighbor resulting from tilting the board right
     * Used for the solver
     * @return east The neighbor resulting from tilting right
     */
    public Configuration eastNeighbor() {
        TiltConfig c = new TiltConfig(this);
        int openIndex;//The index in the row which a slider can slide to

        //loop from left to right, top to bottom
        for (int i = 0; i < boardSize; i++) {
            openIndex = boardSize - 1;
            for (int j = boardSize - 1; j >= 0; j--) {
                switch (c.currentBoard[i][j]) {
                    case BLOCKER -> openIndex = j - 1;//Set next open slot to the left of the blocker
                    case HOLE -> openIndex = j;//any slider will just fall through making this an empty tile
                    case B_SLIDER -> {
                        if (c.currentBoard[i][openIndex] == HOLE) {//Illegal move
                            return null;
                        } else {//Move slider
                            c.currentBoard[i][j] = EMPTY;
                            c.currentBoard[i][openIndex] = B_SLIDER;
                            openIndex--;//Set open space to left of slider
                        }
                    }
                    case G_SLIDER -> {
                        c.currentBoard[i][j] = EMPTY;
                        if (c.currentBoard[i][openIndex] == HOLE) {//Remove slider if it wil fall in hole
                            c.numGreen--;
                        } else {//Move slider
                            c.currentBoard[i][openIndex] = G_SLIDER;
                            openIndex--;//Set open space to left of slider
                        }
                    }
                }
            }
        }
        return c;
    }

    /**
     * Get the neighbor resulting from tilting the board left
     * Used for the solver
     * @return west The neighbor resulting from tilting left
     */
    public Configuration westNeighbor() {
        TiltConfig c = new TiltConfig(this);
        int openIndex;//The index in the row which a slider can slide to

        //loop from right to left, top to bottom
        for (int i = 0; i < boardSize; i++) {
            openIndex = 0;
            for (int j = 0; j < boardSize; j++) {
                switch (c.currentBoard[i][j]) {
                    case BLOCKER -> openIndex = j + 1;//Set next open slot to the right of the blocker
                    case HOLE -> openIndex = j;//any slider will just fall through making this an empty tile
                    case B_SLIDER -> {
                        if (c.currentBoard[i][openIndex] == HOLE) {//Illegal move
                            return null;
                        } else {
                            c.currentBoard[i][j] = EMPTY;
                            c.currentBoard[i][openIndex] = B_SLIDER;
                            openIndex++;//Set open space to right of slider
                        }
                    }
                    case G_SLIDER -> {
                        c.currentBoard[i][j] = EMPTY;
                        if (c.currentBoard[i][openIndex] == HOLE) {//Remove slider if it wil fall in hole
                            c.numGreen--;
                        } else {//Move slider
                            c.currentBoard[i][openIndex] = G_SLIDER;
                            openIndex++;//Set open space to right of slider
                        }
                    }
                }
            }
        }
        return c;
    }

    /**
     * Determine if two configs are equal to each other
     * @param other The object being compared to
     * @return equals If the two objects are equal
     */
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof TiltConfig)) {
            return false;
        }
        for (int i = 0; i < boardSize; i++) {
            //If the row does not match return false
            if (!Arrays.equals(this.currentBoard[i], ((TiltConfig) other).currentBoard[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Get the hash code of the board
     * @return hash The hashcode of the configuration
     */
    @Override
    public int hashCode() {
        //Uses a deep hash on the current board
        return Arrays.deepHashCode(this.currentBoard);
    }

    /**
     * The string of the current board
     * @return toString The string of the current board
     */
    @Override
    public String toString() {
        String board = "";
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                board += currentBoard[i][j] + "\s";
            }
            if (i != boardSize - 1) {
                board += "\n";
            }
        }
        return board;
    }
}