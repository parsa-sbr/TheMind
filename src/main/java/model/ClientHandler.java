package model;

import server.Game;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.Vector;

public class ClientHandler extends Thread{

    private Socket socket;
    private Scanner in;
    private PrintWriter out;
    private Vector<Integer> cards;
    private Game game;
    private String username;

    public ClientHandler(Socket socket, String username) {
        this.socket = socket;
        this.username = username;
        cards = new Vector<>();
        try {
            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            String input = in.nextLine();
            try {
                Integer playedCard = Integer.parseInt(input);
                if (cards.contains(playedCard)) {
                    //send card to game
                    game.playCard(username, playedCard);
                    cards.remove(playedCard);
                }
                else {
                    out.println("you don't have this card!");
                    out.flush();
                }
            } catch (Exception e) {
                if (input.equals("ninja")) {
                    //play the ninja card
                    game.playCard(username, -2);
                }
                else if (input.contains(":)")) {
                    //send :) emoji
                    game.sendToAll(username, ":)");
                }
                else if (input.contains(":(")) {
                    //send :( emoji
                    game.sendToAll(username, ":(");
                }
                else if (input.contains(":|")) {
                    //send :| emoji
                    game.sendToAll(username, ":|");
                }
                else if (input.equals("exit")) {
                    //close the socket and add a bot instead of the player

                    //game.removePlayer(this);
                }
            }
        }
    }

    public void sendMessage(String text) {
        out.println(text);
        out.flush();
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

}
