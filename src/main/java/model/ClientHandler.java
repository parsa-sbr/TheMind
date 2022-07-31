package model;

import server.Game;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.Scanner;
import java.util.Vector;

public class ClientHandler extends Thread {
    Vector<Integer> cards = new Vector<>();
    Socket socket;
    Scanner in;
    PrintWriter out;
    String username;
    Game game;
    protected static SecureRandom random = new SecureRandom();
    String token;

    public ClientHandler(Socket socket, String username) throws IOException {
        this.username = username;
        this.socket = socket;
        in = new Scanner(socket.getInputStream());
        out = new PrintWriter(socket.getOutputStream());
        setToken();
    }

    public Socket getSocket() {
        return socket;
    }

    public Vector<Integer> getCards() {
        return cards;
    }

    public void setCards(Vector<Integer> cards) {
        this.cards = cards;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public String getUsername() {
        return username;
    }

    public void sendMessage(String text) {
        out.println(text);
        out.flush();
    }

    public void setToken() {
        token = tokenGenerator();
        sendMessage("TOKEN#" + token);
    }

    @Override
    public void run() {
        while (game.getGameIsAlive().get()) {
            String input;
            try {
                input = in.nextLine();
            } catch (Exception e) {
                continue;
            }
            if (input.split("#")[1].equals(token)) {
                input = input.split("#")[0];
                try {
                    Integer playedCard = Integer.parseInt(input);
                    if (cards.contains(playedCard)) {
                        game.playCard(username, playedCard);
                    } else {
                        out.println("you don't have this card!");
                        out.flush();
                    }
                } catch (Exception e) {
                    if (input.equals("ninja")) {
                        if (game.getNinjas() > 0) {
                            game.playCard(username, -2);
                        }
                        else {
                            out.println("you don't have any ninja!");
                            out.flush();
                        }
                    } else if (input.contains(":)")) {
                        game.sendToAll(username, ":)");
                    } else if (input.contains(":(")) {
                        game.sendToAll(username, ":(");
                    } else if (input.contains(":|")) {
                        game.sendToAll(username, ":|");
                    } else if (input.equals("exit")) {
                        game.removePlayer(this);
                    }
                }
            }
        }
    }

    private synchronized String tokenGenerator() {
        long longToken = Math.abs( random.nextLong() );
        return Long.toString( longToken, 16 );
    }

    public void killConnection() {
        sendMessage("TERMINATE");
        try {
            socket.close();
            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}