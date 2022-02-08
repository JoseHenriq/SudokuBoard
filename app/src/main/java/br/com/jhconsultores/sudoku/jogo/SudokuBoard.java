package br.com.jhconsultores.sudoku.jogo;
/**
* Estudo JH: Intellij Idea 2021.2.1 (Community Edition)
*            D:\Android\Java\SudokuGenerator
*
* - ref: https://github.com/Sphoun/SudokuGenerator
 */

import java.util.Random;

/**
 * Created by scott on 2016-07-08.
 */

public class SudokuBoard {

    public static int [][] board = new int[9][9];

    public int[][] generate()
    {
        board = new int[9][9];

        int a, b = 1;
        for(int i = 0; i < 9; i++)
        {
            a = b;
            for(int j = 0; j < 9; j++)
            {
                if(a<=9)
                {
                    board[i][j] = a;
                    a++;
                }
                else
                {
                    a = 1;
                    board[i][j] = a;
                    a++;
                }
            } //fill row

            b = a + 3;
            // keeps validity aisdjasid
            if(a == 10) b = 4;
            if(b > 9) b = (b%9) + 1;
        }

        return board;

    }

    public static void gen_rand(int check)
    {
        int s1, s2, max = 2, min = 0;
        Random r = new Random();

        for(int i = 0; i < 3; i++)
        {
            s1 = r.nextInt(max - min + 1) + min;

            // ensure different row/cols are selected to swap
            do {
                s2 = r.nextInt(max - min + 1) + min;
            } while(s1==s2);

            max += 3;
            min += 3;

            if(check == 1) swap_rows(s1,s2);
            else if(check == 0) swap_cols(s1,s2);
        }
    }

    // swap single lines between group sections
    public static void swap_rows(int row1, int row2)
    {
        int temp;

        for(int a = 0; a < 9; a++)
        {
            temp = board[row1][a];
            board[row1][a] = board[row2][a];
            board[row2][a] = temp;
        }
    }

    public static void swap_cols(int col1, int col2)
    {
        int temp;

        for(int a = 0; a < 9; a++)
        {
            temp = board[a][col1];
            board[a][col1] = board[a][col2];
            board[a][col2] = temp;
        }
    }

    // swap groups of 3
    public static void swap_row_group(int rows1, int rows2)
    {
        int temp;

        for(int j = 1; j <= 3; j++)
        {
            for(int k = 0; k < 9; k++)
            {
                temp = board[rows1][k];
                board[rows1][k] = board[rows2][k];
                board[rows2][k] = temp;
            }
            rows1++;
            rows2++;
        }
    }

    public static void swap_col_group(int cols1, int cols2)
    {
        int temp;

        for(int j = 1; j <= 3; j++)
        {
            for(int k = 0; k < 9; k++)
            {
                temp = board[k][cols1];
                board[k][cols1] = board[k][cols2];
                board[k][cols2] = temp;
            }
            cols1++;
            cols2++;
        }
    }

    public static void strike_out(int k1,int k2)
    {
        int row_from, row_to;
        int col_from, col_to;
        int rem1, rem2, flag;
        int temp = board[k1][k2];
        int count=9;

        for(int i = 1; i <= 9; i++)
        {
            flag=1;
            for(int j = 0; j < 9; j++)
            {
                if(j != k2)
                {
                    if(i != board[k1][j])
                    {
                        continue;
                    }
                    else
                    {
                        flag=0;
                        break;
                    }
                }
            }
            if(flag == 1)
            {
                for(int c = 0; c < 9; c++)
                {
                    if(c != k1)
                    {
                        if(i!=board[c][k2])
                        {
                            continue;
                        }
                        else
                        {
                            flag=0;
                            break;
                        }
                    }
                }
            }
            if(flag==1)
            {
                rem1=k1%3; rem2=k2%3;
                row_from=k1-rem1;
                row_to=k1+(2-rem1);
                col_from=k2-rem2;
                col_to=k2+(2-rem2);
                for(int c = row_from; c<=row_to; c++)
                {
                    for(int b = col_from; b <= col_to; b++)
                    {
                        if(c != k1 && b != k2)
                        {
                            if(i != board[c][b])continue;
                            else
                            {
                                flag=0;
                                break;
                            }
                        }
                    }
                }
            }
            if(flag==0)
                count--;
        }
        if(count==1)
        {
            board[k1][k2]=0;
        }
    }

    public int[][] getBoard()
    {
        return board;
    }

}
