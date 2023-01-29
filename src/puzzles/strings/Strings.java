package puzzles.strings;

import puzzles.common.solver.Configuration;
import puzzles.common.solver.Solver;

/**
 * Main class for the strings puzzle.
 *
 * @author Victor Rabinovich
 */
public class Strings {
    /**
     * Run an instance of the strings puzzle.
     *
     * @param args [0]: the starting string;
     *             [1]: the finish string.
     */
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println(("Usage: java Strings start finish"));
        } else {
            StringsConfig start= new StringsConfig(args[0],args[1]);//Create starting configuration

            Solver s= new Solver();

            Iterable<Configuration> path = s.solve(start);

            System.out.print("Start string: "+args[0]);
            System.out.println(", Final string: "+args[1]);

            System.out.println("Total configs: "+s.getTotalConfigs());
            System.out.println("Unique configs: "+s.getUniqueConfigs());

            //Traverse through the path provided from solve in Solver, if path is null prints no solution
            if(path==null){
                System.out.println("No Solution");
            }else {
                int i = 0;
                for (Configuration item : path) {
                    System.out.println("Step " + i + ": " + item);
                    i++;
                }
            }
        }
    }
}
