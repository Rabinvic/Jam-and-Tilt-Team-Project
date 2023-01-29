package puzzles.common.solver;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

/**
 * A common BFS solver.
 *
 * @author Victor Rabinovich
 */
public class Solver {
    HashMap<Configuration,Configuration> predecessors;//HashMap of a configuration and the neighbor it came from
    Queue<Configuration> queue;//The configurations to be visited

    private int totalConfigs;//Total number of generated configurations
    private int uniqueConfigs;//Number of unique configs

    /**
     * Create a Solver. Initializes the predecessors HashMap and visitation queue
     */
    public Solver(){
        predecessors=new HashMap<Configuration,Configuration>();
        queue= new LinkedList<Configuration>();
    }

    /**
     * Method to execute a Breadth First Search
     * @param startConfig The starting configuration
     * @return path The path from the start config to target config
     */
    public Iterable<Configuration> solve(Configuration startConfig){
        //Add starting config to the predecessors map and queue, also increase amount of configs
        predecessors.put(startConfig, null);
        queue.offer(startConfig);
        uniqueConfigs++;
        totalConfigs++;

        //Loops until queue is empty or next config in queue is the solution
        while(!queue.isEmpty() && !queue.peek().isSolution()){
            Configuration thisConfig = queue.remove();//Get the first config in queue
            Collection<Configuration> neighbors=thisConfig.getNeighbors();//Get the neighbors to the config
            for(Configuration c : neighbors){
                totalConfigs++;//Add neighbor to total configs
                //If config has not been visited before
                if(!predecessors.containsKey(c)){
                    uniqueConfigs++;//Add to unique config
                    //Add to map and queue
                    predecessors.put(c, thisConfig);
                    queue.offer(c);
                }
            }
        }

        if(queue.isEmpty()){//Loop ended with no solution being found
            return null;
        }else {
            LinkedList<Configuration> path = new LinkedList<Configuration>();//The path to be returned
            Configuration endConfig=queue.remove();//Get the next config that would have been the solution
            path.add(0, endConfig);//Add it to the path
            //Get the predecessors of the end config and add them to the path
            Configuration config= predecessors.get(endConfig);
            while(config!=null){
                path.add(0,config);
                config=predecessors.get(config);
            }
            return path;
        }
    }

    /**
     * Get total num of configs generated
     * @return totalConfigs Number of configurations generated
     */
    public int getTotalConfigs() {
        return totalConfigs;
    }

    /**
     * Get the number of unique configurations that were generated
     * @return uniqueConfigs -  number of unique configs
     */
    public int getUniqueConfigs() {
        return uniqueConfigs;
    }
}
