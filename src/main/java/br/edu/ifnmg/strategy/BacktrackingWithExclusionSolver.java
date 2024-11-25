package br.edu.ifnmg.strategy;

import br.edu.ifnmg.RunResults;
import br.edu.ifnmg.SolverStrategy;
import br.edu.ifnmg.Sudoku;

public class BacktrackingWithExclusionSolver implements SolverStrategy {
    private long recursiveCallCount = 0;
    @Override
    public RunResults solve(Sudoku sudoku) {
        long startTime = System.nanoTime();
        sudoku.fillCellsPossibilities();
        boolean successful = solveRecursive(sudoku, 0, 0);
        long endTime = System.nanoTime();
        RunResults result = new RunResults();
        result.setTimeElapsed(endTime - startTime);
        result.setRecursiveCalls(recursiveCallCount);
        result.setSucessful(successful);
        return result;
    }

    private boolean solveRecursive(Sudoku sudoku, int row, int col) {
        if (row == sudoku.rows) return true; // Sudoku resolvido
        recursiveCallCount++;
        if (col == sudoku.columns) return solveRecursive(sudoku, row + 1, 0);
        if (sudoku.cells[row][col] != 0) return solveRecursive(sudoku, row, col + 1);

        for (int num = 1; num <= sudoku.columns; num++) {
            if (!sudoku.lookupTablePossibilities[row][col][num]) continue;
            if (sudoku.isValid(row, col, num)) {
                sudoku.cells[row][col] = num;
                if (solveRecursive(sudoku, row, col + 1)) return true;
                sudoku.cells[row][col] = 0; // Backtrack
            }
        }
        return false;
    }


}
