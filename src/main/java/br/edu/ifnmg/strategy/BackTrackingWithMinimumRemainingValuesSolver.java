package br.edu.ifnmg.strategy;

import br.edu.ifnmg.RunResults;
import br.edu.ifnmg.SolverStrategy;
import br.edu.ifnmg.Sudoku;

import java.util.ArrayList;
import java.util.Comparator;


public class BackTrackingWithMinimumRemainingValuesSolver implements SolverStrategy {
    private long recursiveCallCount = 0;

    private ArrayList<Cell> remainingValuesReference;

    private static class Cell {
        private int i;
        private int j;
        private final ArrayList<Integer> values;

        public Cell(int i, int j, ArrayList<Integer> values) {
            this.i = i;
            this.j = j;
            this.values = values;
        }

        @Override
        public String toString() {
            return "Cell[i=" + i + ", j=" + j + ", values=" + values + "]";
        }

    }

    @Override
    public RunResults solve(Sudoku sudoku) {
        long startTime = System.nanoTime();
        remainingValuesReference = new ArrayList<>();
        sudoku.fillCellsPossibilities();
        for (int i = 0; i < sudoku.rows; i++) {
            for (int j = 0; j < sudoku.columns; j++) {
                if (!sudoku.cellsPossibilities[i][j].isEmpty())
                    remainingValuesReference.add(new Cell(i, j, sudoku.cellsPossibilities[i][j]));
            }
        }
        remainingValuesReference.sort(Comparator.comparingInt(c -> c.values.size()));
        boolean successful = solveRecursive(sudoku, 0);
        long endTime = System.nanoTime();
        RunResults result = new RunResults();
        result.setTimeElapsed(endTime - startTime);
        result.setRecursiveCalls(recursiveCallCount);
        result.setSucessful(successful);
        return result;
    }

    private boolean solveRecursive(Sudoku sudoku, int cellIndex) {
        // Se todas as células foram preenchidas, o Sudoku está resolvido
        if (cellIndex == remainingValuesReference.size()) return true;
        recursiveCallCount++;
        // Obtém a célula atual da lista ordenada
        Cell currentCell = remainingValuesReference.get(cellIndex);
        int row = currentCell.i;
        int col = currentCell.j;

        // Testa cada valor possível da célula
        for (int num : currentCell.values) {
            if (sudoku.isValid(row, col, num)) {
                sudoku.cells[row][col] = num; // Atribui o número
                if (solveRecursive(sudoku, cellIndex + 1)) return true; // Avança para a próxima célula
                sudoku.cells[row][col] = 0; // Backtrack
            }
        }
        return false;
    }
}

