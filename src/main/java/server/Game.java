package server;

import model.Bot;
import model.ClientHandler;

import java.util.Vector;

public class Game extends Thread{

    Vector<ClientHandler> clientHandlers;
    Vector<Bot> bots;
    Vector<Integer> deckOfAll;
    int cardOnTable;
    Integer playedCard = -1;





    public void sendToAll(String username, String message) {
        for (ClientHandler c : clientHandlers) {
            if (!c.getUsername().equals(username)) {
                c.sendMessage(username + ": " + message);
            }
        }
    }

    public void playCard(String username, int card) {
        playedCard = card;

        String played = String.valueOf(card);
        if (card == -2)
            played = "ninja";
        sendToAll(username, "played card " + played);
    }

}
