package model;

import server.Game;

import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

public class Bot extends Thread{
    Vector<Integer> cards;
    Game game;
    String username;
    AtomicBoolean play = new AtomicBoolean(false);

    public Bot(String username) {
       this.username = username;
       cards = new Vector<>();
    }

    public Vector<Integer> getCards() {
        return cards;
    }

    public AtomicBoolean getPlay() {
        return play;
    }

    public String getUsername() {
        return username;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    @Override
    public void run() {
        while (game.getGameIsAlive().get()) {
            if (play.get() && getCards().size() != 0) {
                try {
                    long waitingTime = (cards.get(0) - game.getCardOnTable()) * 300L;
                    System.out.println(waitingTime/1000 + "s");
                    Thread.sleep(waitingTime);
                    game.playCard(username, cards.get(0));
                } catch (Exception ignored) {}
            }
        }
    }
}