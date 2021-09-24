package com.lanehogan.tictactoe;

import com.lanehogan.tictactoe.server.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * The dispatcher calls the server and handles connection
 * to the server.
 */
public class Dispatcher {
    private static ServerSocket port;

    public static void main(String[] args) {
        Server server;
        Socket socket;
        boolean running;

        try {
            port = new ServerSocket(7788);
        } catch (IOException ignored) {}

        running = true;
        while (running)
            try {
                socket = port.accept();
                server = new Server(socket);
                server.start();
            } catch (IOException e) {
                running = false;
            }
    }
}
