package puzzles.tilt.model;

import puzzles.common.Observer;
import puzzles.common.solver.Configuration;
import puzzles.common.solver.Solver;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/**
 * A class to create a model for the tilt game to run under mvc
 *
 * @author Victor Rabinovich
 */
public class TiltModel {
    /** the collection of observers of this model */
    private final List<Observer<TiltModel, String>> observers = new LinkedList<>();

    /** the current configuration */
    private TiltConfig currentConfig;

    private String currentFile;

    /**
     * Create the tilt model
     */
    public TiltModel(){}

    /**
     * Load the given file and create the matching config
     * @param filename The file to be loaded
     * @return loaded If it was successfully loaded
     */
    public boolean loadBoardFromFile(String filename){
        try(BufferedReader in = new BufferedReader(new FileReader(filename))){
            //parse file into a board that can be used to create the config
            int size= Integer.parseInt(in.readLine());
            char[][] board= new char[size][size];
            for(int i=0; i<size;i++){
                String[] line = in.readLine().split("\\s+");
                for(int j=0; j<size;j++){
                    board[i][j]=line[j].charAt(0);
                }
            }
            this.currentConfig = new TiltConfig(size,board);
            alertObservers("Loaded: "+ filename);
            currentFile=filename;
            return true;
        }catch (IOException e){
            alertObservers("Failed to load " + filename);//If not successful
            return false;
        }
    }

    /**
     * Tilt the board in a certain direction
     * @param direction The direction to tilt the board (N,E,S,W)
     */
    public void tiltBoard(String direction){
        direction.toUpperCase();
        switch (direction){
            //Tilt up
            case "N"->{
                TiltConfig temp = (TiltConfig) currentConfig.northNeighbor();
                if(temp!=null){//Valid moves
                    currentConfig=temp;
                    alertObservers("");
                }else{//Illegal move
                    alertObservers("Illegal");
                }
            }
            //Tilt down
            case "S"->{
                TiltConfig temp = (TiltConfig) currentConfig.southNeighbor();
                if(temp!=null){//Valid moves
                    currentConfig=temp;
                    alertObservers("");
                }else{//Illegal move
                    alertObservers("Illegal");
                }
            }
            //Tilt right
            case "E"->{
                TiltConfig temp = (TiltConfig) currentConfig.eastNeighbor();
                if(temp!=null){//Valid moves
                    currentConfig=temp;
                    alertObservers("");
                }else{//Illegal move
                    alertObservers("Illegal");
                }
            }
            //Tilt left
            case "W"->{
                TiltConfig temp = (TiltConfig) currentConfig.westNeighbor();
                if(temp!=null){//Valid moves
                    currentConfig=temp;
                    alertObservers("");
                }else{//Illegal move
                    alertObservers("Illegal");
                }
            }
        }
    }

    /**
     * Find the solution for the puzzle and set the config to the next step in achieving it
     */
    public void getHint(){
        Solver hintSolver=new Solver();
        LinkedList<Configuration> path=(LinkedList<Configuration>) hintSolver.solve(currentConfig);
        if(path==null){//No solution is found
            alertObservers("No Solution");
        } else if (path.size()==1) {//Already solved
            currentConfig=(TiltConfig) path.get(0);
            alertObservers("Solved");
        }else {//NExt step in solution path
            currentConfig=(TiltConfig) path.get(1);
            alertObservers("Hint");
        }

    }

    /**
     * Reset the game to the current file
     */
    public void resetBoard(){
        try(BufferedReader in = new BufferedReader(new FileReader(currentFile))){
            int size= Integer.parseInt(in.readLine());
            char[][] board= new char[size][size];
            for(int i=0; i<size;i++){
                String[] line = in.readLine().split("\\s+");
                for(int j=0; j<size;j++){
                    board[i][j]=line[j].charAt(0);
                }
            }
            this.currentConfig = new TiltConfig(size,board);
        }catch (IOException e){}
        alertObservers("RESET");
    }

    /**
     * Get the board of the config
     * @return board The current config's board
     */
    public char[][] getConfigBoard(){
        return currentConfig.currentBoard;
    }

    /**
     * Get the board dimension
     * @return The side length of the board
     */
    public int getSize(){
        return currentConfig.boardSize;
    }

    /**
     * Print out the board
     */
    public void displayBoard(){
        System.out.println(currentConfig);
    }

    /**
     * Determine if the current config is a solution to the puzzle
     * @return gameOver If the game is over
     */
    public boolean gameOver(){
        return currentConfig.isSolution();
    }

    /**
     * The view calls this to add itself as an observer.
     *
     * @param observer the view
     */
    public void addObserver(Observer<TiltModel, String> observer) {
        this.observers.add(observer);
    }

    /**
     * The model's state has changed (the counter), so inform the view via
     * the update method
     */
    private void alertObservers(String data) {
        for (var observer : observers) {
            observer.update(this, data);
        }
    }
}
