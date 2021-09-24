package com.lanehogan.tictactoe.server;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.Random;

/**
 * A basic socket server.
 */
public class Server extends Thread {
    private DataInputStream inStream;
    private DataOutputStream outStream;
    private final PrintWriter out;
    private final BufferedReader in;
    private final Random random;
    private final char[][] board;
    private int row, col;

    public Server(Socket socket) {
        random = new Random();
        try {
            inStream = new DataInputStream(socket.getInputStream());
            outStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException ignored) {}

        out = new PrintWriter(outStream, true);
        in = new BufferedReader(new InputStreamReader(inStream));

        board = new char[3][3];

        // clear the board
        for (char[] chars : board) {
            Arrays.fill(chars, ' ');
        }

        row = -1;
        col = -1;
    }

    @Override
    public void run() {
        int counter = 0;
        String response = "";
        boolean gameOver = false;
        boolean turn = true;

        turn = random.nextInt() % 2 == 0;

        if (turn) {
            out.println("NONE");
        }

        // game loop
        while (!gameOver) {
            if (turn) {
                try {
                    response = in.readLine();
                } catch (IOException e) {
                    System.out.println("Some sort of read error on socket in server thread");
                    System.exit(1);
                }

                String[] data = response.split("\\s+");
                row = Integer.parseInt(data[1]);
                col = Integer.parseInt(data[2]);

                board[row][col] = 'O';
                printBoard();
                counter++;

                // game is over - either a win or tie
                if (checkWin() || counter == 9) {
                    gameOver = true;
                    if (checkWin()) {
                        out.println("MOVE -1 -1 WIN");
                    } else {
                        out.println("MOVE -1 -1 TIE");
                    }
                }
            } else {
                System.out.println("*** SERVER'S TURN ***");

                makeMove();
                counter++;
                board[row][col] = 'X';
                printBoard();

                // did the computer win or tie?
                if (checkWin() || counter == 9) {
                    gameOver = true;
                    if (checkWin()) {
                        out.println("MOVE " + row + " " + col + " LOSS");
                    } else {
                        out.println("MOVE " + row + " " + col + " TIE");
                    }
                } else {
                    out.println("MOVE " + row + " " + col);
                }
            }

            turn = !turn;
        }
    }

    /**
     * Randomly selects a move for the server to perform.
     */
    private void makeMove() {
        do {
            row = random.nextInt(3);
            col = random.nextInt(3);
        } while (board[row][col] != ' ');
    }

    /**
     * Returns true if the server won the game. If the
     * server lost, it returns false.
     *
     * @return server won the game.
     */
    private boolean checkWin() {
        for (int i = 0; i < 3; i++) {
            // horizontal win
            if (board[i][0] == board[i][1] && board[i][1] == board[i][2] && board[i][0] != ' ')
                return true;

            // vertical win
            if (board[0][i] == board[1][i] && board[1][i] == board[2][i] && board[0][i] != ' ')
                return true;
        }

        // down-diagonal win
        if (board[0][0] == board[1][1] && board[1][1] == board[2][2] && board[0][0] != ' ')
            return true;

        // up-diagonal win
        return board[2][0] == board[1][1] && board[1][1] == board[0][2] && board[2][0] != ' ';
    }

    /**
     * Outputs the game board.
     */
    private void printBoard() {
        System.out.println(board[0][0] + "|" + board[0][1] + "|" + board[0][2]);
        System.out.println("-----");
        System.out.println(board[1][0] + "|" + board[1][1] + "|" + board[1][2]);
        System.out.println("-----");
        System.out.println(board[2][0] + "|" + board[2][1] + "|" + board[2][2]);
        System.out.println();
    }
}
