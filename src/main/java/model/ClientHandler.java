package model;

import server.Game;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.Vector;

public class ClientHandler extends Thread{

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

    public Game getGame() {
        return game;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void sendMessage(String text) {
        out.println(text);
        out.flush();
    }






    @Override
    public void run() {
        while (true) {
            String input = in.nextLine();
            if (input.equals("p")) {
                game.playCard(username, getCards().get(0));
//                Integer card = getCards().get(0);
//                game.setCardOnTable(card);
//                getCards().remove(card);
            }
            else {
                try {
                    Integer playedCard = Integer.parseInt(input);
                    if (cards.contains(playedCard)) {
                        //send card to game
                        game.playCard(username, playedCard);
                      //  cards.remove(playedCard);
                    }
                    else {
                        out.println("you don't have this card!");
                        out.flush();
                    }
                } catch (Exception e) {
                    if (input.equals("ninja")) {
                        //play the ninja card
                        game.applyNinja();
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
    }
public class ClientHandler extends Thread{

}
