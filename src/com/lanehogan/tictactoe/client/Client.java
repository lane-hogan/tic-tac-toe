package com.lanehogan.tictactoe.client;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;

/**
 * A basic client for a Tic-Tac-Toe game using
 * the socket library.
 */
public class Client {
    private static DataInputStream inStream;
    private static DataOutputStream outStream;
    private static char[][] board;
    private static int row, col;

    /**
     * Outputs the game board.
     */
    private static void printBoard() {
        System.out.println(board[0][0] + "|" + board[0][1] + "|" + board[0][2]);
        System.out.println("-----");
        System.out.println(board[1][0] + "|" + board[1][1] + "|" + board[1][2]);
        System.out.println("-----");
        System.out.println(board[2][0] + "|" + board[2][1] + "|" + board[2][2]);
        System.out.println("");
    }

    /**
     * Dispatches a specified move to the server.
     *
     * @param input scanner for user input
     * @param out   object that will be used to send data to the server
     */
    private static void makeMove(Scanner input, PrintWriter out) {
        String move = "";

        do {
            System.out.print("Enter your move (row and column): ");
            move = input.nextLine();
            String[] moves = move.split("\\s+");

            try {
                row = Integer.parseInt(moves[0]);
                col = Integer.parseInt(moves[1]);
            } catch (NumberFormatException ignored) {
            }

        } while (row < 0 || row > 2 || col > 2 || col < 0 || board[row][col] != ' ');

        // dispatch the message to the server and update the board
        out.println("MOVE " + move);
        board[row][col] = 'O';
    }

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        String response = "";
        boolean gameOver = false;
        board = new char[3][3];

        // connect to the server
        try {
            Socket socket = new Socket("localhost", 7788);
            inStream = new DataInputStream(socket.getInputStream());
            outStream = new DataOutputStream(socket.getOutputStream());
        } catch (Exception ignored) {
        }

        PrintWriter out = new PrintWriter(outStream, true);
        BufferedReader in = new BufferedReader(new InputStreamReader(inStream));

        // clear the board
        for (char[] chars : board) {
            Arrays.fill(chars, ' ');
        }

        row = -1;
        col = -1;

        // game loop
        while (!gameOver) {
            try {
                response = in.readLine();
            } catch (IOException e) {
                System.out.println("Some sort of read error on socket in client");
                System.exit(1);
            }

            // catch the output from the server
            String[] data = response.split("\\s+");

            // base case: the player moves first
            if (data.length == 1) {
                makeMove(input, out);
                printBoard();
            } else if (data.length == 3) { // normal move is made by server
                System.out.println("*** SERVER'S TURN ***");

                // update the board with server's move
                row = Integer.parseInt(data[1]);
                col = Integer.parseInt(data[2]);

                board[row][col] = 'X';
                printBoard();

                makeMove(input, out);
                printBoard();
            } else if (data.length == 4) {
                if (data[1].equals("-1")) { // player made a move
                    printBoard();

                    switch (data[3]) {
                        case "WIN" -> System.out.println("Player wins!");
                        case "TIE" -> System.out.println("There's a tie!");
                    }
                } else { // server made a move
                    System.out.println("*** SERVER'S TURN ***");

                    row = Integer.parseInt(data[1]);
                    col = Integer.parseInt(data[2]);

                    board[row][col] = 'X';
                    printBoard();

                    switch (data[3]) {
                        case "LOSS" -> System.out.println("Computer wins!");
                        case "TIE" -> System.out.println("There's a tie!");
                    }
                }

                gameOver = true;
            }
        }

        input.close();
    }
}
