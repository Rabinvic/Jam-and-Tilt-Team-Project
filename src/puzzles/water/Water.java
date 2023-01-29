package puzzles.water;

import puzzles.common.solver.Configuration;
import puzzles.common.solver.Solver;
import java.util.Arrays;

/**
 * Main class for the water buckets puzzle.
 *
 * @author Victor Rabinovich
 */
public class Water {

    /**
     * Run an instance of the water buckets puzzle.
     *
     * @param args [0]: desired amount of water to be collected;
     *             [1..N]: the capacities of the N available buckets.
     */
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println(
                    ("Usage: java Water amount bucket1 bucket2 ...")
            );
        } else {
            //Get all the buckets from the command line arguments
            int[] buckets= new int[args.length-1];
            for(int i=1; i<args.length; i++){
                buckets[i-1]=Integer.parseInt(args[i]);
            }

            WaterConfig start= new WaterConfig(Integer.valueOf(args[0]), buckets);//Create starting configuration

            Solver s= new Solver();
            Iterable<Configuration> path = s.solve(start);

            System.out.print("Amount: "+args[0]);
            System.out.println(", Buckets: "+ Arrays.toString(buckets));

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
