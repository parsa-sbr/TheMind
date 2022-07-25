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

            out.println("Hello, you're the host.\nhow many players does game have: ");
            out.flush();
            int numberOfPlayers = Integer.parseInt(in.nextLine());
            out.println("and how many real player: ");
            out.flush();
            int numberOfHumans = Integer.parseInt(in.nextLine());

            for (int i = 0; i < numberOfHumans - 1; i++) {
                Socket tempSocket = serverSocket.accept();
                System.out.println("A player joined.");
                //adding players to a list
            }

            for (int i = 0; i < numberOfPlayers - numberOfHumans; i++) {
                //adding bots to a list
            }

            System.out.println("game is starting...");
            //Starting a game...
        }
    }
}
