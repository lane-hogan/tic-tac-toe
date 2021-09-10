/**
 * Name: Lane Hogan
 * Date: 3/23/2021
 * Program #2
 */

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Dispatcher {
    private static ServerSocket port;

    public static void main(String[] args) {
        Server server;
        Socket socket;
        try {
            port = new ServerSocket(7788);
        } catch (IOException e) {}

        while (true) {
            try {
                socket = port.accept();
                server = new Server(socket);
                server.start();
            } catch (IOException e) {}
        }
    }
}
