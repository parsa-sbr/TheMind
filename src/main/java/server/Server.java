package server;

import model.Bot;
import model.ClientHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.Vector;

public class Server {

    ServerSocket serverSocket;

    public Server() throws IOException {
        serverSocket = new ServerSocket(1234);
    }

    public void init() throws IOException {
        System.out.println("server started.");
        while (true) {
            Vector<ClientHandler> clientHandlers = new Vector<>();
            Vector<Bot> bots = new Vector<>();

            System.out.println("Waiting for a host...");

            Socket socket = serverSocket.accept();
            System.out.println("host has connected");

            Scanner in = new Scanner(socket.getInputStream());
            PrintWriter out = new PrintWriter(socket.getOutputStream());

            out.println("Hello, you're the host.\nWhat is your name? ");
            out.flush();

            String name = in.nextLine();
            ClientHandler host = new ClientHandler(socket, name);
            clientHandlers.add(host);

            out.println("How many players does the game have?");
            out.flush();
            int numberOfPlayers = Integer.parseInt(in.nextLine());

            for (int i = 0; i < numberOfPlayers - 1; i++) {
                Socket tempSocket = serverSocket.accept();
                System.out.println("A player joined.");
                Scanner tempIn = new Scanner(tempSocket.getInputStream());
                PrintWriter tempOut = new PrintWriter(tempSocket.getOutputStream());

                tempOut.println("Hello, What is your name? ");
                tempOut.flush();
                name = tempIn.nextLine();

                //adding player to a list
                ClientHandler player = new ClientHandler(tempSocket, name);
                clientHandlers.add(player);



                out.println("there are " + clientHandlers.size() + " players in the server including you, do want to start the game? ");
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
