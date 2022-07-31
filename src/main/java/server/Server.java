package server;

import model.Bot;
import model.ClientHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

public class Server {

    ServerSocket serverSocket;
    Vector<Game> games = new Vector<>();
    int numOfBots = 1;
    int count = 1;
    AtomicBoolean isFull = new AtomicBoolean(true);

    public Server() throws IOException {
        serverSocket = new ServerSocket(1234);
    }

    public void startGame(Vector<ClientHandler> clients, int numberOfPlayers) {
        Vector<Bot> list = new Vector<>();
        for (int x = 0; x < numberOfPlayers - clients.size(); x++) {
            list.add(new Bot("Bot" + (x+numOfBots++)));
        }
        System.out.println("game is starting...");
        Game currentGame = new Game(clients, list);
        currentGame.setServer(this);

        for (Bot b : currentGame.bots) {
            b.setGame(currentGame);
        }
        for (ClientHandler c : currentGame.clientHandlers) {
            c.setGame(currentGame);
            c.start();
        }
        currentGame.start();
        games.add(currentGame);
        if (list.size() == 0) {
            currentGame.isFull.set(true);
            games.remove(currentGame);
        }
    }

    public void init() throws IOException {
        System.out.println("server started.");
        while (true) {
            games.removeIf(g -> !g.gameIsAlive.get());

            if (games.size() == 0) {
                Vector<ClientHandler> clients = new Vector<>();

                System.out.println("Waiting for a host...");

                Socket socket = serverSocket.accept();
                System.out.println("host has connected");

                ClientHandler newClient = new ClientHandler(socket, "User" + count++);
                clients.add(newClient);

                Scanner in = new Scanner(socket.getInputStream());
                PrintWriter out = new PrintWriter(socket.getOutputStream());

                if (!isFull.get()) {
                    out.println("Hello! Do you want to join a running game?");
                    out.flush();

                    String response = in.nextLine().split("#")[0];
                    if (response.equals("yes")) {
                        for (Game g : games) {
                            if (!g.isFull.get()) {
                                //add the client to game g
                                g.addPlayer(newClient);

                                for (ClientHandler c : g.clientHandlers) {
                                    c.sendMessage(g.updateString(c, g.round));
                                }

                                break;
                            }
                        }
                    }
                    else {
                        out.println("You're the host.\nHow many players does the game have?");
                        out.flush();
                        int numberOfPlayers = Integer.parseInt(in.nextLine().split("#")[0]);

                        out.println("No other player has joined yet. Do you want to start the game?");
                        out.flush();
                        String response1 = in.nextLine().split("#")[0];
                        if (!response1.equals("yes")) {
                            int c = 0;
                            for (int i = count-1; i < numberOfPlayers - 1 + count-1; i++) {
                                Socket tempSocket = serverSocket.accept();
                                System.out.println("A player joined.");
                                clients.add(new ClientHandler(tempSocket, "User" + (i + 1)));
                                c++;

                                out.println("There are " + (i + 1) + " players in the server including you. Do you want to start the game?");
                                out.flush();
                                String response2 = in.nextLine().split("#")[0];
                                if (response2.equals("yes")) {
                                    break;
                                }
                            }
                            count += c;
                        }
                        startGame(clients, numberOfPlayers);
                    }
                }
                else {
                    out.println("Hello! You're the host.\nHow many players does the game have?");
                    out.flush();
                    int numberOfPlayers = Integer.parseInt(in.nextLine().split("#")[0]);

                    out.println("No other player has joined yet. Do you want to start the game?");
                    out.flush();
                    String response1 = in.nextLine().split("#")[0];
                    if (!response1.equals("yes")) {
                        int c = 0;

                        for (int i = count-1; i < numberOfPlayers - 1 + count-1; i++) {
                            Socket tempSocket = serverSocket.accept();
                            System.out.println("A player joined.");
                            clients.add(new ClientHandler(tempSocket, "User" + (i + 1)));
                            c++;

                            out.println("There are " + (i + 1) + " players in the server including you. Do you want to start the game?");
                            out.flush();
                            String response = in.nextLine().split("#")[0];
                            if (response.equals("yes")) {
                                break;
                            }
                        }
                        count += c;
                    }
                    startGame(clients, numberOfPlayers);
                }
            }
            else  {
                if (!isFull.get()) {
                    Vector<ClientHandler> clients = new Vector<>();

                    Socket socket = serverSocket.accept();

                    ClientHandler newClient = new ClientHandler(socket, "User" + count++);
                    clients.add(newClient);

                    Scanner in = new Scanner(socket.getInputStream());
                    PrintWriter out = new PrintWriter(socket.getOutputStream());

                    out.println("Hello! Do you want to join a running game?");
                    out.flush();

                    String response = in.nextLine().split("#")[0];
                    if (response.equals("yes")) {
                        for (Game g : games) {
                            if (!g.isFull.get()) {
                                //add the client to game g
                                g.addPlayer(newClient);
                                for (ClientHandler c : g.clientHandlers) {
                                    c.sendMessage(g.updateString(c, g.round));
                                }

                                break;
                            }
                        }
                    }
                    else {
                        out.println("You're the host.\nHow many players does the game have?");
                        out.flush();
                        int numberOfPlayers = Integer.parseInt(in.nextLine().split("#")[0]);

                        out.println("No other player has joined yet. Do you want to start the game?");
                        out.flush();
                        String response1 = in.nextLine().split("#")[0];
                        if (!response1.equals("yes")) {
                            int c = 0;

                            for (int i = count-1; i < numberOfPlayers - 1 + count-1; i++) {
                                Socket tempSocket = serverSocket.accept();
                                System.out.println("A player joined.");
                                clients.add(new ClientHandler(tempSocket, "User" + (i + 1)));
                                c++;

                                out.println("There are " + (i + 1) + " players in the server including you. Do you want to start the game?");
                                out.flush();
                                String response2 = in.nextLine().split("#")[0];
                                if (response2.equals("yes")) {
                                    break;
                                }
                            }
                            count += c;
                        }
                        startGame(clients, numberOfPlayers);
                    }
                }
                else {
                    Vector<ClientHandler> clients = new Vector<>();

                    System.out.println("Waiting for a host...");

                    Socket socket = serverSocket.accept();
                    System.out.println("host has connected");
                    clients.add(new ClientHandler(socket, "User" + count++));

                    Scanner in = new Scanner(socket.getInputStream());
                    PrintWriter out = new PrintWriter(socket.getOutputStream());

                    out.println("Hello! You're the host.\nHow many players does the game have?");
                    out.flush();
                    int numberOfPlayers = Integer.parseInt(in.nextLine().split("#")[0]);

                    out.println("No other player has joined yet. Do you want to start the game?");
                    out.flush();
                    String response1 = in.nextLine().split("#")[0];
                    if (!response1.equals("yes")) {
                        int c = 0;

                        for (int i = count-1; i < numberOfPlayers - 1 + count-1; i++) {
                            Socket tempSocket = serverSocket.accept();
                            System.out.println("A player joined.");
                            clients.add(new ClientHandler(tempSocket, "User" + (i + 1)));
                            c++;

                            out.println("There are " + (i + 1) + " players in the server including you. Do you want to start the game?");
                            out.flush();
                            String response = in.nextLine().split("#")[0];
                            if (response.equals("yes")) {
                                break;
                            }
                        }
                        count += c;
                    }
                    startGame(clients, numberOfPlayers);
                }
            }
        }
    }
}