package br.com.jhconsultores.utils;

/*
  https://www.geeksforgeeks.org/check-if-given-sudoku-solution-is-valid-or-not/
 */

//--- This code is contributed by akhilsaini
/*
// Driver Code
public static void main(String[] args) {
        int[][] board = {
        { 7, 9, 2, 1, 5, 4, 3, 8, 6 },
        { 6, 4, 3, 8, 2, 7, 1, 5, 9 },
        { 8, 5, 1, 3, 9, 6, 7, 2, 4 },
        { 2, 6, 5, 9, 7, 3, 8, 4, 1 },
        { 4, 8, 9, 5, 6, 1, 2, 7, 3 },
        { 3, 1, 7, 4, 8, 2, 9, 6, 5 },
        { 1, 3, 6, 7, 4, 8, 5, 9, 2 },
        { 9, 7, 4, 2, 1, 5, 6, 3, 8 },
        { 5, 2, 8, 6, 3, 9, 4, 1, 7 } };

        boolean flag = false;            // JH

        //-----------------------------
        flag = isValidSudoku(board);    // JH
        //-----------------------------

        if (flag) {                     // JH
        System.out.println("Valid"); }
        else { System.out.println("Not Valid"); }

    }
*/

// Java program to implement the above approach

//import java.io.*;
import java.util.*;

//--- Classe 'GeeksForGeeks' (GFG)
public class GFG {

    static int N = 9;
    public static int [][] boardGFG = new int[9][9];

    // Function to check if the solution of sudoku puzzle is valid or not
    //public static boolean isValidSudoku(int[][] board)
    public static boolean isValidSudoku()
    {
        // Check if all elements of board[][] stores value in the range[1, 9]
        if (!isInRange(boardGFG)) { return false; }

        // Stores unique value from 1 to N
        boolean[] unique = new boolean[N + 1];

        // Traverse each row of the given array
        for(int i = 0; i < N; i++)
        {

            // Initialize unique[] array to false
            Arrays.fill(unique, false);

            // Traverse each column of current row
            for(int j = 0; j < N; j++)
            {

                // Stores the value of board[i][j]
                int Z = boardGFG[i][j];

                // Commit9.0.182 -> NÃO considera o '0' !!!
                // Check if current row stores duplicate value
                if (Z != 0 && unique[Z]) { return false; }
                unique[Z] = true;
            }
        }

        // Traverse each column of the given array
        for(int i = 0; i < N; i++)
        {
            // Initialize unique[] array to false
            Arrays.fill(unique, false);

            // Traverse each row of current column
            for(int j = 0; j < N; j++)
            {
                // Stores the value of board[j][i]
                int Z = boardGFG[j][i];

                // Commit9.0.182 -> NÃO considera o '0' !!!
                // Check if current column stores duplicate value
                if (Z != 0 && unique[Z]) { return false; }
                unique[Z] = true;
            }
        }

        // Traverse each block of size 3 * 3 in board[][] array
        for(int i = 0; i < N - 2; i += 3)
        {

            // j stores first column of each 3 * 3 block
            for(int j = 0; j < N - 2; j += 3)
            {

                // Initialize unique[] array to false
                Arrays.fill(unique, false);

                // Traverse current block
                for(int k = 0; k < 3; k++)
                {
                    for(int l = 0; l < 3; l++)
                    {

                        // Stores row number of current block
                        int X = i + k;

                        // Stores column number of current block
                        int Y = j + l;

                        // Stores the value of board[X][Y]
                        int Z = boardGFG[X][Y];

                        // Commit9.0.182 -> NÃO considera o '0' !!!
                        // Check if current block stores duplicate value
                        if (Z != 0 && unique[Z]) { return false; }
                        unique[Z] = true;
                    }
                }
            }
        }

        // If all conditions satisfied
        return true;
    }

    // Commit9.0.182 -> comentário: NÃO considera o '0' como inválido!!!
    // Function to check if all elements of the board[][] array store value in the range[0, 9]
    static boolean isInRange(int[][] board)
    {

        // Traverse board[][] array
        for(int i = 0; i < N; i++)
        {
            for(int j = 0; j < N; j++)
            {

                // Check if board[i][j] lies in the range
                if (board[i][j] < 0 || board[i][j] > 9) { return false; }

            }
        }
        return true;
    }

}
