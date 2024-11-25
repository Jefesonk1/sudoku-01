package br.edu.ifnmg;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Sudoku {
    private boolean isInstanceLoaded = false;
    public int[][] cells;
    public int rows;
    public int columns;
    public ArrayList<Integer>[][] cellsPossibilities;
    public boolean[][][] lookupTablePossibilities;

    Sudoku(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        cells = new int[this.rows][this.columns];
        cellsPossibilities = new ArrayList[this.rows][this.columns];
        lookupTablePossibilities = new boolean[this.rows][this.columns][this.rows + 1];

        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.columns; j++) {
                cellsPossibilities[i][j] = new ArrayList<>();
            }
        }

    }

    public ArrayList<Integer> getValidCandidates(int row, int col) {
        ArrayList<Integer> candidates = new ArrayList<>();
        int sqrtRows = (int) Math.sqrt(this.rows);
        int sqrtCols = (int) Math.sqrt(this.columns);
        boolean[] used = new boolean[this.rows + 1];

        //row
        for (int c = 0; c < this.columns; c++) {
            if (this.cells[row][c] != 0) {
                used[this.cells[row][c]] = true;
            }
        }

        //column
        for (int r = 0; r < this.rows; r++) {
            if (this.cells[r][col] != 0) {
                used[this.cells[r][col]] = true;
            }
        }

        //subgrid
        int startRow = (row / sqrtRows) * sqrtRows;
        int startCol = (col / sqrtCols) * sqrtCols;
        for (int r = startRow; r < startRow + sqrtRows; r++) {
            for (int c = startCol; c < startCol + sqrtCols; c++) {
                if (this.cells[r][c] != 0) {
                    used[this.cells[r][c]] = true;
                }
            }
        }

        // Add numbers 1-9 that are not used to the candidates list
        for (int num = 1; num <= this.rows; num++) {
            if (!used[num]) {
                candidates.add(num);
            }
        }

        return candidates;
    }

    public void fillCellsPossibilities() {
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.columns; j++) {
                if (this.cells[i][j] == 0) {
                    ArrayList<Integer> candidates = getValidCandidates(i, j);
                    cellsPossibilities[i][j].addAll(candidates);
                }
            }
        }
        for (int i = 0; i < cellsPossibilities.length; i++) {
            for (int j = 0; j < cellsPossibilities[i].length; j++) {
                for (int value : cellsPossibilities[i][j]) {
                    lookupTablePossibilities[i][j][value] = true;
                }
            }
        }

    }

    public void printCellsPossibilities() {
        System.out.println("Printing CellsPossibilities:");
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.columns; j++) {
                System.out.print("[" + i + "][" + j + "] = ");
                System.out.println(cellsPossibilities[i][j]);
            }
        }

    }

    public void printLookupTablePossibilities() {
        System.out.println("Printing LookupTablePossibilities:");
        for (int i = 0; i < lookupTablePossibilities.length; i++) {
            for (int j = 0; j < lookupTablePossibilities[i].length; j++) {
                System.out.print("[" + i + "][" + j + "] = [");
                boolean first = true;
                for (int value = 1; value <= lookupTablePossibilities.length; value++) {
                    if (lookupTablePossibilities[i][j][value]) {
                        if (!first) {
                            System.out.print(", ");
                        }
                        System.out.print(value);
                        first = false;
                    }
                }
                System.out.println("]");
            }
        }
    }

    public boolean loadInstanceFromFile(String filePath) {
        try (Scanner input = new Scanner(new File(filePath))) {
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    if (!input.hasNextInt()) {
                        System.err.println("Error reading number at position (" + i + ", " + j + ")");
                        return false;
                    }
                    cells[i][j] = input.nextInt();
                }
            }
            isInstanceLoaded = true;
            return true;
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + filePath);
            return false;
        }
    }

    public void printGrid() {
        if (!isInstanceLoaded) {
            System.out.println("No Sudoku instance loaded.");
            return;
        }
        for (int[] row : cells) {
            for (int num : row) {
                System.out.print(num + " ");
            }
            System.out.println();
        }
    }

    public void formatedPrintGrid() {
        int sqrtRows = (int) Math.sqrt(rows);
        int sqrtColumns = (int) Math.sqrt(columns);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                // Adiciona a barra vertical após cada bloco de sqrtColumns
                if (j % sqrtColumns == 0 && j != 0) {
                    System.out.print(" | ");
                }
                System.out.print(cells[i][j] + " ");
            }
            System.out.println(); // Quebra de linha após cada linha da grade

            // Adiciona uma linha horizontal após cada bloco de sqrtRows
            if (i % sqrtRows == sqrtRows - 1 && i != rows - 1) {
                for (int k = 0; k < columns + sqrtColumns - 1; k++) {
                    System.out.print("-");
                }
                System.out.println();
            }
        }
    }

    public boolean isValid(int row, int col, int num) {
        for (int i = 0; i < columns; i++) {
            if (cells[row][i] == num || cells[i][col] == num) return false;
        }
        int sqrt = (int) Math.sqrt(columns);
        int boxRowStart = row - row % sqrt;
        int boxColStart = col - col % sqrt;

        for (int r = boxRowStart; r < boxRowStart + sqrt; r++) {
            for (int d = boxColStart; d < boxColStart + sqrt; d++) {
                if (cells[r][d] == num) return false;
            }
        }
        return true;
    }

    public RunResults solveWithStrategy(SolverStrategy strategy) {
        RunResults result = strategy.solve(this);
        if (result.isSucessful()) {
            System.out.println("Sudoku solved successfully!");
        } else {
            System.out.println("Failed to solve Sudoku.");
        }
        return result;
    }
}
