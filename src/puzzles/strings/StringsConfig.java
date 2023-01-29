package puzzles.strings;

import puzzles.common.solver.Configuration;

import java.util.Collection;
import java.util.LinkedList;

/**
 * The configuration class for the strings puzzle.
 *
 * @author Victor Rabinovich
 */

public class StringsConfig implements Configuration {
    private static String start;//The starting string
    private static String end;//The final string
    private String current;//The current string

    /**
     * Initial Constructor for StringsConfig
     * @param start The starting string, also gets set as the current string
     * @param end The target string
     */
    public StringsConfig(String start, String end){
        StringsConfig.start=start;
        StringsConfig.end=end;
        this.current=start;//Set current string to the start
    }

    /**
     * The constructor that is used when creating neighbors
     * @param other A StringsConfig object to be copied
     */
    public StringsConfig(StringsConfig other){
        this.current = other.current;
    }

    /**
     * Determine if current configuration is a solution to the puzzle
     * @return isSolution If current string equals the target string
     */
    public boolean isSolution(){
        return this.current.equals(end);
    }

    /**
     * Check if two StringConfig objects are equal to each other
     * @param other The object to be equated to
     * @return equals, if the two objects are equal
     */
    @Override
    public boolean equals(Object other) {
        if(other instanceof StringsConfig) {
            return this.current.equals(((StringsConfig) other).current);
        }
        return false;
    }

    /**
     * Get the hash code for the configuration
     * @return hashCode- The hash code of the configuration
     */
    @Override
    public int hashCode() {
        return this.current.hashCode();
    }

    /**
     * Get the neighboring strings for the current configuration.
     * *If a letter is already in the correct position, neighbors will be produced such that that letter is not changed*
     * @return neighbors A collection of StringsConfig that are neighbors to the current configuration
     */
    @Override
    public Collection<Configuration> getNeighbors() {
        Collection<Configuration> neighbors = new LinkedList<Configuration>();//Create the list of neighbors

        //Loop through each character in the string
        for(int i=0; i<current.length(); i++){
            /*If character at current position matches character in the same position in target string skip this
            * iteration of the loop*/
            if(this.current.charAt(i)==end.charAt(i)){
                continue;
            }
            String temp=this.current;//Create copy of current for simplicity
            int value=temp.charAt(i);//Letter being evaluated
            int printValue;//The transforms letter

            //Shifts the letter down 1
            if(value-1<65){//For wrap around
                printValue=90;
            }else{
                printValue=value-1;
            }
            StringsConfig c= new StringsConfig(this);
            c.current = temp.substring(0,i)+ (char) printValue +temp.substring(i+1);//Set state of new config
            neighbors.add(c);//Add config to list

            //Shifts letter up 1
            if(value+1>90){//For wrap around
                printValue=65;
            }else{
                printValue=value+1;
            }
            c= new StringsConfig(this);
            c.current = temp.substring(0,i)+ (char) printValue +temp.substring(i+1);//Set state of new config
            neighbors.add(new StringsConfig(c));
        }
        return neighbors;
    }

    /**
     * Get the state of the configuration
     * @return current, The string of the configurations state
     */
    @Override
    public String toString() {
        return this.current;
    }

}
