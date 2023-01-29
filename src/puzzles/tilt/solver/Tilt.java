package puzzles.tilt.solver;

import puzzles.common.solver.Configuration;
import puzzles.common.solver.Solver;
import puzzles.tilt.model.TiltConfig;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * A class to solve tile puzzles
 *
 * @author Victor Rabinovich
 */
public class Tilt {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java Tilt filename");
        }
        else{
            try(BufferedReader in = new BufferedReader(new FileReader(args[0]))){
                //Process the file
                int size= Integer.parseInt(in.readLine());
                char[][] board= new char[size][size];
                for(int i=0; i<size;i++){
                    String[] line = in.readLine().split("\\s+");
                    for(int j=0; j<size;j++){
                        board[i][j]=line[j].charAt(0);
                    }
                }

                //Create initial config and send it to common solver
                Configuration start=new TiltConfig(size,board);
                Solver s=new Solver();
                Iterable<Configuration> path = s.solve(start);

                //Display results
                System.out.println("File: "+args[0]);
                System.out.println(start);;
                System.out.println("Total configs: "+s.getTotalConfigs());
                System.out.println("Unique configs: "+s.getUniqueConfigs());

                if(path==null){
                    System.out.println("No Solution");
                }else {
                    int i = 0;
                    for (Configuration item : path) {
                        System.out.println("Step " + i + ":\n" + item+"\n");
                        i++;
                    }
                }

            }catch (IOException e){

            }

        }
    }
}
