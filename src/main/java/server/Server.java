package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {

    ServerSocket serverSocket;

    public Server() throws IOException {
        serverSocket = new ServerSocket(1234);
    }

    public void init() throws IOException {
        System.out.println("server started.");
        while (true) {
            System.out.println("Waiting for a host...");

            Socket socket = serverSocket.accept();
            System.out.println("host has connected");

            Scanner in = new Scanner(socket.getInputStream());
            PrintWriter out = new PrintWriter(socket.getOutputStream());

            out.println("Hello, you're the host.\nhow many players does game have? ");
            out.flush();
            int numberOfPlayers = Integer.parseInt(in.nextLine());

            for (int i = 0; i < numberOfPlayers - 1; i++) {
                Socket tempSocket = serverSocket.accept();
                System.out.println("A player joined.");

                //adding player to a list

                out.println("there are " + (i + 2) + " players in the server including you, do want to start the game? ");
                out.flush();
                String response = in.nextLine();
                if (response.equals("yes")) {
                    break;
                }
            }

            //adding bots to a list

            System.out.println("game is starting...");
            //Starting a game...
        }
    }
}
