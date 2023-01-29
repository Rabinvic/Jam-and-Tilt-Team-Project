package puzzles.jam.model;

import puzzles.common.solver.Configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class JamConfig implements Configuration {

    /**
     * the amount of rows and cols in the model
     */
    protected static int rows, cols;

    /**
     * the current config's car maks
     */
    protected final CarMask mask;

    /**
     * the cars on the board
     */
    protected final Car[] cars;

    /**
     * Creates a new config with a given rows, cols, and cars
     * @param rows the amount of rows
     * @param cols the amount of cols
     * @param cars the cars
     */
    public JamConfig(int rows, int cols, Car[] cars) {
        JamConfig.rows = rows;
        JamConfig.cols = cols;
        this.mask = new CarMask(rows, cols);
        for(Car car : cars) {
            this.mask.addCar(car);
        }
        this.cars = cars;
    }

    /**
     * A copy constructor to make a new config from another
     * @param config the config to copy
     */
    public JamConfig(JamConfig config) {
        this.mask = new CarMask(config.mask);
        this.cars = new Car[config.cars.length];
        for(int i = 0; i < cars.length; i ++) {
            this.cars[i] = new Car(config.cars[i]);
        }
    }

    @Override
    public boolean isSolution() {
        for(Car car : cars) {
            if (car.getLetter() == 'X') {
               return car.getCol() == mask.getCols() - 2;
            }
        }
        return false;
    }

    @Override
    public Collection<Configuration> getNeighbors() {
        List<Configuration> neighbors = new ArrayList<>();
        for(int i = 0; i < cars.length; i++) {
            up(this, i, neighbors);
            down(this, i, neighbors);
            left(this, i, neighbors);
            right(this, i, neighbors);
        }
        return neighbors;
    }

    /**
     * try to recursively move a car up and generate configs each space
     * @param config the config to recuse on
     * @param index the index of the car
     * @param neighbors the neighbor array to append to
     */
    private void up(JamConfig config, int index, List<Configuration> neighbors) {
        if(config.cars[index].can_up(config.mask)) {
            JamConfig copy = new JamConfig(config);
            copy.cars[index].up(copy.mask);
            neighbors.add(copy);
            up(copy, index, neighbors);
        }
    }

    /**
     * try to recursively move a car down and generate configs each space
     * @param config the config to recuse on
     * @param index the index of the car
     * @param neighbors the neighbor array to append to
     */
    private void down(JamConfig config, int index, List<Configuration> neighbors) {
        if(config.cars[index].can_down(config.mask)) {
            JamConfig copy = new JamConfig(config);
            copy.cars[index].down(copy.mask);
            neighbors.add(copy);
            down(copy, index, neighbors);
        }
    }

    /**
     * try to recursively move a car left and generate configs each space
     * @param config the config to recuse on
     * @param index the index of the car
     * @param neighbors the neighbor array to append to
     */
    private void left(JamConfig config, int index, List<Configuration> neighbors) {
        if(config.cars[index].can_left(config.mask)) {
            JamConfig copy = new JamConfig(config);
            copy.cars[index].left(copy.mask);
            neighbors.add(copy);
            left(copy, index, neighbors);
        }
    }

    /**
     * try to recursively move a car right and generate configs each space
     * @param config the config to recuse on
     * @param index the index of the car
     * @param neighbors the neighbor array to append to
     */
    private void right(JamConfig config, int index, List<Configuration> neighbors) {
        if(config.cars[index].can_right(config.mask)) {
            JamConfig copy = new JamConfig(config);
            copy.cars[index].right(copy.mask);
            neighbors.add(copy);
            right(copy, index, neighbors);
        }
    }

    /**
     * @return the car mask from the config
     */
    public CarMask getMask() {
        return mask;
    }

    @Override
    public boolean equals(Object other) {
        if(other instanceof JamConfig config) {
            return this.mask.equals(config.mask);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public String toString() {
        return null;
    }
}
