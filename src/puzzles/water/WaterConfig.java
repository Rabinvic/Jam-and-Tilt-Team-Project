package puzzles.water;

import puzzles.common.solver.Configuration;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

/**
 * The configuration class for the water buckets puzzle.
 *
 * @author Victor Rabinovich
 */

public class WaterConfig implements Configuration {
    private static int amount;//Final amount in single bucket
    private static int[] capacities;//Bucket capacities
    private int[] current;//Current state of each bucket

    /**
     * Initial constructor for WaterConfig, takes the array of bucket capacities, instantiates the
     * current array by the length of the argument and sets each bucket to currently hold 0 gallons
     * @param capacities An array containing the capacities of each bucket being used
     */
    public WaterConfig(int amount,int[] capacities){
        this.capacities=capacities;
        WaterConfig.amount=amount;
        this.current=new int[capacities.length];
        //Set each bucket to have a starting state of 0 water
        for(int i=0; i<this.current.length;i++){
            this.current[i]=0;
        }
    }

    /**
     * The constructor that will be used when generating neighbors, takes another instance of WaterConfig
     * @param other A WaterConfig obj that will be copied
     */
    public WaterConfig(WaterConfig other){
        //Hard copying the current array for the object
        this.current=new int[other.current.length];
        System.arraycopy(other.current,0, this.current,0, this.current.length);
    }

    /**
     * Checks if the current WaterConfig is a solution to the problem
     * @return isSolution, if the current configuration is a solution
     */
    @Override
    public boolean isSolution() {
        //Loops through the array of buckets and check is any of them currently holds the desired amount of water
        for(int i=0; i<this.current.length;i++){
            if(this.current[i]==amount) {
                return this.current[i] == amount;
            }
        }
        return false;//When no buckets match
    }

    /**
     * Generates the next possible states for the current configuration, and returns
     * the possible configurations as a Collection.
     *
     * This method will not however create duplicates.
     * @return neighbors A collection of the next set of possible states for the puzzle
     */
    @Override
    public Collection<Configuration> getNeighbors() {
        Collection<Configuration> neighbors= new LinkedList<>();//List of neighbors to be returned
        WaterConfig c;
        for(int i=0;i<this.current.length; i++){//Go through each bucket


            //Completely fill the bucket to its capacity and add it to the list
            if(this.current[i]!=WaterConfig.capacities[i]) {
                c = new WaterConfig(this);
                c.current[i] = WaterConfig.capacities[i];
                neighbors.add(c);
            }

            //Dump the water in the bucket and add new configuration to the list
            if(this.current[i]!=0) {
                c = new WaterConfig(this);
                c.current[i] = 0;
                neighbors.add(c);
            }

            //Pour a bucket into another bucket that isn't itself
            for(int j=0; j<this.current.length; j++){
                if(i==j){continue;}
                int pourAmount=Math.min(this.current[i],(this.capacities[j]-this.current[j]));
                if(pourAmount!=0) {
                    c = new WaterConfig(this);
                    c.current[i] -= pourAmount;
                    c.current[j] += pourAmount;
                    neighbors.add(c);
                }
            }
        }
        return neighbors;
    }

    /**
     * Get the hashCode for the current array
     * @return hashCode, the hash code for the current array
     */
    @Override
    public int hashCode() {
        return Arrays.hashCode(current);
    }

    /**
     * Determines if two WaterConfig objects are equal
     * @param other The object to be equated
     * @return equals, whether the objects are equal
     */
    @Override
    public boolean equals(Object other) {
        if(other instanceof WaterConfig) {
            return Arrays.equals(this.current, ((WaterConfig)other).current);
        }
        return false;
    }

    /**
     * Get the current array as a string
     * @return toString, the Array toString of the current array
     */
    @Override
    public String toString() {
        return Arrays.toString(this.current);
    }
}
