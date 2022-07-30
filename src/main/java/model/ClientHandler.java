package model;

import server.Game;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.Vector;

public class ClientHandler extends Thread {

    Vector<Integer> cards = new Vector<>();
    Socket socket;
    Scanner in;
    PrintWriter out;
    String username;
    Game game;

    public ClientHandler(Socket socket, String username) throws IOException {
        this.username = username;
        this.socket = socket;
        in = new Scanner(socket.getInputStream());
        out = new PrintWriter(socket.getOutputStream());
    }

    public Vector<Integer> getCards() {
        return cards;
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

    @Override
    public void run() {
        while (game.getGameIsAlive().get()) {
            String input = in.nextLine();
            if (input.equals("p")) {
                game.playCard(username, getCards().get(0));
            } else {
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
                        //close the socket and add a bot instead of the player

                        //game.removePlayer(this);
                    }
                }
            }
        }
    }
}