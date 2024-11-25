package br.edu.ifnmg;

import br.edu.ifnmg.strategy.BackTrackingWithMinimumRemainingValuesSolver;
import br.edu.ifnmg.strategy.BacktrackingSolver;
import br.edu.ifnmg.strategy.BacktrackingWithExclusionSolver;

import java.io.File;
import java.util.ArrayList;


public class Main {
    // Códigos ANSI para cores
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";

    public static RunResults loadSolveAndMeasure(String filePath) {
        Sudoku sudoku = new Sudoku(9, 9);
        if (!sudoku.loadInstanceFromFile(filePath)) {
            System.err.println(RED + "Failed to load the Sudoku grid." + RESET);
        }
//        RunResults result = sudoku.solveWithStrategy(new BacktrackingSolver());
//        RunResults result = sudoku.solveWithStrategy(new BacktrackingWithExclusionSolver());
        RunResults result = sudoku.solveWithStrategy(new BackTrackingWithMinimumRemainingValuesSolver());
        System.out.println("Recursive Calls: " + result.getRecursiveCalls());
        System.out.println("Time: " + PURPLE + result.getTimeElapsed() + " nanoseconds" + RESET + " | " + GREEN + (result.getTimeElapsed() / 1_000_000) + " milliseconds" + RESET);
        return result;
    }

    public static void printTimes(ArrayList<Long> times) {
        System.out.print("[ ");
        for (Long time : times) {
            System.out.print(time + ", ");
        }
        System.out.println(" ]");
    }

    public static void main(String[] args) {
        String folderPath = "instances/95puzzles";

        File folder = new File(folderPath);
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".txt"));

        ArrayList<Long> times = new ArrayList<>();
        if (files != null) {
            System.out.println(YELLOW + "Starting file processing..." + RESET);
            for (File file : files) {
                String filePath = file.getPath();
                System.out.println(CYAN + "Processing: " + RESET + BLUE + filePath + RESET);
                times.add(loadSolveAndMeasure(filePath).getTimeElapsed());
                System.out.println();
            }
            System.out.println(GREEN + "Processing complete!" + RESET);
            printTimes(times);
        } else {
            System.out.println(RED + "Error: No such file or folder." + RESET);
        }
    }
}