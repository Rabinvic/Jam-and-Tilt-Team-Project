package puzzles.jam.solver;

import puzzles.common.solver.Configuration;
import puzzles.common.solver.Solver;
import puzzles.jam.model.Car;
import puzzles.jam.model.CarMask;
import puzzles.jam.model.JamConfig;
import puzzles.jam.model.JamModel;

public class Jam {

    /**
     * Takes in a file name as argument 1 and try's to find the solution
     * @param args arguments
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java Jam filename");
        }
        JamModel model = new JamModel();
        model.loadFile(args[0]);
        if(!model.isLoaded()) {
            System.out.println("Failed to load file: " + args[1]);
        }
        System.out.println("File: " + args[0]);
        board(model.getConfig().getMask());
        Solver solver = new Solver();
        Iterable<Configuration> configs = solver.solve(model.getConfig());
        System.out.println("Total Configs: " + solver.getTotalConfigs());
        System.out.println("Unique Configs: " + solver.getUniqueConfigs());
        if(configs == null) {
            System.out.print("No Solution");
        } else {
            int i = 0;
            for (Configuration config : configs) {
                JamConfig jam = (JamConfig) config;
                System.out.println("Step: " + i);
                board(jam.getMask());
                System.out.println();
                i++;
            }
        }
    }

    /**
     * Prints out a board to the standard output given a certain car mask
     * @param mask the game board car mask
     */
    private static void board(CarMask mask) {
        for (int row = 0; row < mask.getRows(); row++) {
            for (int col = 0; col < mask.getCols(); col++) {
                char value = mask.getValue(row, col);
                if(value == Car.NULL) {
                    System.out.print('.');
                } else {
                    System.out.print(value);
                }
                System.out.print(' ');
            }
            System.out.println();
        }
    }
}