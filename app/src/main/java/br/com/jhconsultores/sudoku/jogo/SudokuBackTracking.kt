package br.com.jhconsultores.sudoku.jogo

import kotlin.math.sqrt

/*** Publicado em: https://www.geeksforgeeks.org/sudoku-backtracking-7/
 *   A Backtracking program in Java (convertido para o Kotlin pelo AS - JH) to solve Sudoku problem
 *   This code is contributed by MohanDas  */
object SudokuBackTracking {

    var intNumBackTracking = 0
    var flagZeraNivel      = true

    //--- Função principal
    fun solveSudoku(board: Array<Array<Int>>, n: Int): Boolean {

        if (flagZeraNivel) {

            intNumBackTracking = 0
            flagZeraNivel      = false

        }
        var row = -1
        var col = -1
        var isEmpty = true
        for (i in 0 until n) {
            for (j in 0 until n) {
                if (board[i][j] == 0) {
                    row = i
                    col = j

                    // We still have some remaining missing values in Sudoku
                    isEmpty = false
                    break
                }
            }
            if (!isEmpty) {
                break
            }
        }

        // No empty space left
        if (isEmpty) {
            return true
        }

        // Else for each-row backtrack
        for (num in 1..n) {
            if (isSafe(board, row, col, num)) {

                board[row][col] = num
                intNumBackTracking ++
                //-----------------------------------------------
                if (solveSudoku(board, n))
                //-----------------------------------------------
                {
                    // print(board, n);
                    return true

                } else {

                    // replace it
                    board[row][col] = 0

                }
            }
        }
        return false
    }

    //---
    private fun isSafe(board: Array<Array<Int>>, row: Int, col: Int, num: Int): Boolean {

        // Row has the unique (row-clash)
        for (d in board.indices) {

            // Check if the number we are trying to place is already present in that row.
            // Return false.
            if (board[row][d] == num) {
                return false
            }
        }

        // Column has the unique numbers (column-clash)
        for (r in board.indices) {

            // Check if the number we are trying to place is already present in that column.
            // Return false.
            if (board[r][col] == num) {
                return false
            }
        }

        // Corresponding square has unique number (box-clash)
        val sqrt = sqrt(board.size.toDouble()).toInt()
        val boxRowStart = row - row % sqrt
        val boxColStart = col - col % sqrt
        for (r in boxRowStart until boxRowStart + sqrt) {
            for (d in boxColStart until boxColStart + sqrt) {
                if (board[r][d] == num) {
                    return false
                }
            }
        }

        // if there is no clash, it's safe
        return true
    }

    /*
    //--- Apresenta o jogo resultante
    fun print(board: Array<Array<Int>>, N: Int) {

        // We got the answer, just print it
        for (r in 0 until N) {
            for (d in 0 until N) {
                print(board[r][d])
                print(" ")
            }
            print("\n")
            if ((r + 1) % sqrt(N.toDouble()).toInt() == 0) {
                print("")
            }
        }
    }
    */

    /* Utilização da classe acima
    // Driver Code
    public static void main(String args[])
    {

        int[][] board = new int[][] {
                { 3, 0, 6, 5, 0, 8, 4, 0, 0 },
                { 5, 2, 0, 0, 0, 0, 0, 0, 0 },
                { 0, 8, 7, 0, 0, 0, 0, 3, 1 },
                { 0, 0, 3, 0, 1, 0, 0, 8, 0 },
                { 9, 0, 0, 8, 6, 3, 0, 0, 5 },
                { 0, 5, 0, 0, 9, 0, 6, 0, 0 },
                { 1, 3, 0, 0, 0, 0, 2, 5, 0 },
                { 0, 0, 0, 0, 0, 0, 0, 7, 4 },
                { 0, 0, 5, 2, 0, 6, 3, 0, 0 }
        };
        int N = board.length;

        if (solveSudoku(board, N))
        {
            // print solution
            print(board, N);
        }
        else {
            System.out.println("No solution");
        }
    }
    */

}