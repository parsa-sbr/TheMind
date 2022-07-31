package server;

import model.Bot;
import model.ClientHandler;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

public class Server {
    ServerSocket serverSocket;
    private int port;
    Vector<Game> games = new Vector<>();
    int numOfBots = 1;
    int count = 1;
    AtomicBoolean isFull = new AtomicBoolean(true);

    public Server() throws IOException {
        setConnection();
        serverSocket = new ServerSocket(port);
    }


    public void startGame(Vector<ClientHandler> clients, int numberOfPlayers) {
        Vector<Bot> list = new Vector<>();
        for (int x = 0; x < numberOfPlayers - clients.size(); x++) {
            list.add(new Bot("Bot" + (x+numOfBots++)));
        }
        System.out.println("game is starting...");
        Game currentGame = new Game(clients, list);
        currentGame.setServer(this);
        for (Bot b : currentGame.bots) b.setGame(currentGame);
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


    private void setConnection() {
        Properties prop=new Properties();
        FileInputStream ip= null;
        try {
            ip = new FileInputStream("src/main/resources/config.properties");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            prop.load(ip);
        } catch (IOException e) {
            e.printStackTrace();
        }

        port = Integer.parseInt(prop.getProperty("port"));
    }


    public void sendMessage(PrintWriter out, String message) {
        out.println(message);
        out.flush();
    }


    public void addClientToAnExistingGame(ClientHandler clientHandler) {
        for (Game g : games) {
            if (!g.isFull.get()) {
                g.addPlayer(clientHandler);
                for (ClientHandler c : g.clientHandlers) c.sendMessage(g.updateString(c, g.round));
                break;
            }
        }
    }


    public void setUpNewGame(Scanner in, PrintWriter out, Vector<ClientHandler> clients) throws IOException {
        sendMessage(out, "You're the host.\nHow many players does the game have?");
        int numberOfPlayers = Integer.parseInt(in.nextLine().split("#")[0]);
        sendMessage(out, "No other player has joined yet. Do you want to start the game?");
        String response1 = in.nextLine().split("#")[0];
        if (!response1.equals("yes")) {
            int c = 0;
            for (int i = count-1; i < numberOfPlayers - 1 + count-1; i++) {
                Socket tempSocket = serverSocket.accept();
                System.out.println("A player joined.");
                clients.add(new ClientHandler(tempSocket, "User" + (i + 1)));
                c++;
                sendMessage(out, "There are " + (i + 1) + " players in the server including you." +
                                                                  " Do you want to start the game?");
                String response2 = in.nextLine().split("#")[0];
                if (response2.equals("yes")) break;
            }
            count += c;
        }
        startGame(clients, numberOfPlayers);
    }


    public ClientHandler waitForHostToJoin(Vector<ClientHandler> clients) throws IOException {
        System.out.println("Waiting for a host...");
        Socket socket = serverSocket.accept();
        System.out.println("host has connected");
        ClientHandler newClient = new ClientHandler(socket, "User" + count++);
        clients.add(newClient);
        return newClient;
    }


    public void askIfThePlayerWantsToJoinAGame(Scanner in, PrintWriter out, Vector<ClientHandler> clients, ClientHandler newClient) {
        sendMessage(out, "Hello! Do you want to join a running game?");
        String response = in.nextLine().split("#")[0];
        if (response.equals("yes")) addClientToAnExistingGame(newClient);
        else {
            try {
                setUpNewGame(in, out, clients);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void createTheVeryFirstGame() throws IOException {
        Vector<ClientHandler> clients = new Vector<>();
        ClientHandler newClient = waitForHostToJoin(clients);
        Scanner in = new Scanner(newClient.getSocket().getInputStream());
        PrintWriter out = new PrintWriter(newClient.getSocket().getOutputStream());
        if (!isFull.get()) {
            askIfThePlayerWantsToJoinAGame(in, out, clients, newClient);
        }
        else setUpNewGame(in, out, clients);
    }


    public void init() throws IOException {
        System.out.println("server started.");
        while (true) {
            games.removeIf(g -> !g.gameIsAlive.get());
            if (games.size() == 0) createTheVeryFirstGame();
            else  {
                if (!isFull.get()) {
                    Vector<ClientHandler> clients = new Vector<>();
                    Socket socket = serverSocket.accept();
                    ClientHandler newClient = new ClientHandler(socket, "User" + count++);
                    clients.add(newClient);
                    Scanner in = new Scanner(socket.getInputStream());
                    PrintWriter out = new PrintWriter(socket.getOutputStream());
                    askIfThePlayerWantsToJoinAGame(in, out, clients, newClient);
                }
                else {
                    Vector<ClientHandler> clients = new Vector<>();
                    ClientHandler newClient = waitForHostToJoin(clients);
                    Scanner in = new Scanner(newClient.getSocket().getInputStream());
                    PrintWriter out = new PrintWriter(newClient.getSocket().getOutputStream());


                    if (games.size() == 0) {
                        if (!isFull.get()) {
                            askIfThePlayerWantsToJoinAGame(in, out, clients, newClient);
                        }
                        else setUpNewGame(in, out, clients);
                    }
                    else if (!isFull.get()) {
                        askIfThePlayerWantsToJoinAGame(in, out, clients, newClient);
                    }
                    else setUpNewGame(in, out, clients);
                    setUpNewGame(in, out, clients);
                }
            }
        }
    }
}