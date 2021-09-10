/**
 * Name: Lane Hogan
 * Date: 3/23/2021
 * Program #2
 */

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static Socket socket;
    private static DataInputStream inStream;
    private static DataOutputStream outStream;
    private static PrintWriter out;
    private static BufferedReader in;
    private static char[][] board;
    private static int row, col;

    private static void printBoard() {
        System.out.println(board[0][0] + "|" + board[0][1] + "|" + board[0][2]);
        System.out.println("-----");
        System.out.println(board[1][0] + "|" + board[1][1] + "|" + board[1][2]);
        System.out.println("-----");
        System.out.println(board[2][0] + "|" + board[2][1] + "|" + board[2][2]);
        System.out.println("");
    }

    private static void makeMove(Scanner input, PrintWriter out) {
        String move = "";

        do {
            System.out.print("Enter your move (row and column): ");
            move = input.nextLine();
            String[] moves = move.split("\\s+");

            try {
                row = Integer.parseInt(moves[0]);
                col = Integer.parseInt(moves[1]);
            } catch (NumberFormatException e) {}
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

        // connect to the server and get the iostreams
        try {
            socket = new Socket("localhost", 7788);
            inStream = new DataInputStream(socket.getInputStream());
            outStream = new DataOutputStream(socket.getOutputStream());
        } catch (Exception e) {
        }

        out = new PrintWriter(outStream, true);
        in = new BufferedReader(new InputStreamReader(inStream));

        // clear the board
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                board[i][j] = ' ';
            }
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
                    case "WIN":
                        System.out.println("Player wins!");
                        break;
                    case "TIE":
                        System.out.println("There's a tie!");
                        break;
                    }
                } else { // server made a move
                    System.out.println("*** SERVER'S TURN ***");

                    row = Integer.parseInt(data[1]);
                    col = Integer.parseInt(data[2]);

                    board[row][col] = 'X';
                    printBoard();

                    switch (data[3]) {
                    case "LOSS":
                        System.out.println("Computer wins!");
                        break;
                    case "TIE":
                        System.out.println("There's a tie!");
                        break;
                    }
                }

                gameOver = true;
            }
        }

        input.close();
    }
}