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

        Vector<ClientHandler> clients = new Vector<>();
        Vector<Bot> bots = new Vector<>();

        System.out.println("server started.");
        while (true) {
            System.out.println("Waiting for a host...");

            Socket socket = serverSocket.accept();
            System.out.println("host has connected");
            clients.add(new ClientHandler(socket, "user1"));

            Scanner in = new Scanner(socket.getInputStream());
            PrintWriter out = new PrintWriter(socket.getOutputStream());

            out.println("Hello, you're the host.\nhow many players does game have? ");
            out.flush();
            int numberOfPlayers = Integer.parseInt(in.nextLine());




            //****** added this part for testing
            out.println("Enter S to start the game");
            out.flush();
            String response2 = in.nextLine();
            if (response2.equals("S") || response2.equals("s")) {
                System.out.println("game is starting...");
                for (int x = 0; x < numberOfPlayers - 1; x++) {
                    bots.add(new Bot(x+1));
                }
                Game currentGame = new Game(clients, bots);
                for (Bot b : currentGame.bots) {
                    b.setGame(currentGame);
                }
                for (ClientHandler c : currentGame.clientHandlers) {
                    c.setGame(currentGame);
                    c.start();
                }
                currentGame.start();
            }
            //******




            for (int i = 0; i < numberOfPlayers - 1; i++) {
                Socket tempSocket = serverSocket.accept();
                System.out.println("A player joined.");
                clients.add(new ClientHandler(tempSocket, "user" + i + 2));
                //adding player to a list

                out.println("there are " + (i + 2) + " players in the server including you, do want to start the game? ");
                out.flush();
                String response = in.nextLine();
                if (response.equals("yes")) {
                    break;
                }
            }

            //adding bots to a list
            for (int x = 0; x < numberOfPlayers - clients.size(); x++) {
                bots.add(new Bot(x+1));
            }

            //Starting a game...
            System.out.println("game is starting...");
            Game currentGame = new Game(clients, bots);
            for (Bot b : currentGame.bots) {
                b.setGame(currentGame);
            }
            for (ClientHandler c : currentGame.clientHandlers) {
                c.setGame(currentGame);
                c.start();
            }
            currentGame.start();

        }
    }
}
