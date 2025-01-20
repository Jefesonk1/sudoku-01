import math
import random as rd
import numpy as np
import os
import time

board = np.array([
    [4, 0, 0, 0, 0, 0, 8, 0, 5],
    [0, 3, 0, 0, 0, 0, 0, 0, 0],
    [0, 0, 0, 7, 0, 0, 0, 0, 0],
    [0, 2, 0, 0, 0, 0, 0, 6, 0],
    [0, 0, 0, 0, 8, 0, 4, 0, 0],
    [0, 0, 0, 0, 1, 0, 0, 0, 0],
    [0, 0, 0, 6, 0, 3, 0, 7, 0],
    [5, 0, 0, 2, 0, 0, 0, 0, 0],
    [1, 0, 4, 0, 0, 0, 0, 0, 0]
])

def generateInitialSolution(sudoku):
    board = np.copy(sudoku)
    for row in board:
        temp_arr = list(range(1, 10))
        for i in range(9):
            if row[i] != 0:
                temp_arr.remove(row[i])
        for i in range(9):
            if row[i] == 0:
                row[i] = rd.choice(temp_arr)
                temp_arr.remove(row[i])
    return board

def calculate_fitness(board):
    rows_fitness = 0
    cols_fitness = 0
    for i in range(9):
        rows_fitness += 9 - len(set(board[i]))
        cols_fitness += 9 - len(set(board[:, i]))
    return rows_fitness + cols_fitness

def generateNeighbour(board, original_board):
    new_board = board.copy()
    row = rd.randint(0, 8)
    col = rd.randint(0, 8)
    while original_board[row, col] != 0:
        row = rd.randint(0, 8)
        col = rd.randint(0, 8)
    start_row = (row // 3) * 3
    start_col = (col // 3) * 3
    editable_cells = [
        (r, c)
        for r in range(start_row, start_row + 3)
        for c in range(start_col, start_col + 3)
        if original_board[r, c] == 0 and (r, c) != (row, col)
    ]
    if editable_cells:
        swap_row, swap_col = rd.choice(editable_cells)
        new_board[row, col], new_board[swap_row, swap_col] = (
            new_board[swap_row, swap_col],
            new_board[row, col],
        )
    return new_board

def tabuSearch(sudoku, max_iter=1000, tabu_tenure=15):
    original_board = np.copy(sudoku)
    current_solution = generateInitialSolution(sudoku)
    best_solution = np.copy(current_solution)
    best_fitness = calculate_fitness(current_solution)
    tabu_list = []

    for _ in range(max_iter):
        neighbours = []
        for _ in range(50):  # geração de múltiplos vizinhos
            neighbour = generateNeighbour(current_solution, original_board)
            neighbours.append(neighbour)

        # avaliação dos vizinhos, evitando os que estão na lista tabu
        best_neighbour = None
        best_neighbour_fitness = float('inf')
        for neighbour in neighbours:
            neighbour_fitness = calculate_fitness(neighbour)
            if neighbour_fitness < best_neighbour_fitness and (neighbour.tolist() not in tabu_list):
                best_neighbour = neighbour
                best_neighbour_fitness = neighbour_fitness

        # atualização da solução corrente
        if best_neighbour is not None:
            current_solution = best_neighbour
            current_fitness = best_neighbour_fitness

            # atualização da lista tabu
            tabu_list.append(current_solution.tolist())
            if len(tabu_list) > tabu_tenure:
                tabu_list.pop(0)

            # verificação de solução melhor encontrada
            if current_fitness < best_fitness:
                best_solution = np.copy(current_solution)
                best_fitness = current_fitness

        # critério de parada (fitness ideal)
        if best_fitness == 0:
            break

    return best_solution, best_fitness

def load_and_solve_instances(folder_path):
    files = [f for f in os.listdir(folder_path) if f.endswith('.txt')]
    results = []

    for file in files:
        file_path = os.path.join(folder_path, file)
        print(f"Processing file: {file_path}")

        # carregar o Sudoku
        with open(file_path, 'r') as f:
            sudoku = np.array([
                [int(num) for num in line.split()]
                for line in f.readlines()
            ])

        # resolver usando Busca Tabu
        start_time = time.time()
        solution, fitness = tabuSearch(sudoku)
        elapsed_time = time.time() - start_time

        # salvar os resultados
        results.append((file, solution, fitness, elapsed_time))
        print(f"File: {file}, Fitness: {fitness}, Time: {elapsed_time:.4f} seconds")
    return results

def save_solutions(results, output_folder):
    os.makedirs(output_folder, exist_ok=True)
    for file, solution, fitness, elapsed_time in results:
        output_file = os.path.join(output_folder, f"solution_{file}")
        with open(output_file, 'w') as f:
            for row in solution:
                f.write(' '.join(map(str, row)) + '\n')
            f.write(f"# Fitness: {fitness}\n")
            f.write(f"# Time: {elapsed_time:.4f} seconds\n")

if __name__ == "__main__":
    input_folder = "instances/95puzzles"
    output_folder = "tabu_solutions"

    results = load_and_solve_instances(input_folder)

    save_solutions(results, output_folder)
    print(f"Soluções salvas em: {output_folder}")
